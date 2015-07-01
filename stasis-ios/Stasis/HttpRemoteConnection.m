//
//  HttpRemoteConnection.m
//  Stasis
//
//  Created by Christian Fruth on 05.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "HttpRemoteConnection.h"
#import "Stasis.h"
#import "SerializableException.h"
#import "AuthenticationMissmatchException.h"
#import "ExceptionError.h"
#import "LocalDate.h"
#import "LocalDateSerializer.h"
#import "HandshakeHandler.h"
#import "AuthenticationResult.h"
#import "MethodCall.h"
#import "MethodResult.h"
#import "HTTPCookieStorage.h"
#import "StasisError.h"
#import "LoginResult.h"
#import <zlib.h>

@interface HttpRemoteConnection ()

- (void)invokeServiceFunction:(NSString *)name withArguments:(NSArray *)args handshakeHandler:(id<HandshakeHandler>)handshakeHandler andTryCount:(int)tryCount returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler;
- (void)invokeLoginForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters handshakeHandler:(id<HandshakeHandler>)handshakeHandler returning:(void (^)(AuthenticationResult *, NSDictionary *))resultHandler error:(void (^)(NSError *))errorHandler;
- (void)invokeLoginForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters followingFunction:(NSString *)name withArguments:(NSArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler;

@end

@interface KryoOutput (gzip)

- (NSData *)toGzippedData;

@end

@implementation KryoOutput (gzip)

static const NSUInteger ChunkSize = 16384;

- (NSData *)toGzippedData
{
	if (self.position == 0)
	{
		return [NSData data];
	}
	
	z_stream stream;

	stream.zalloc = Z_NULL;
	stream.zfree = Z_NULL;
	stream.opaque = Z_NULL;
	stream.avail_in = (uint)self.position;
	stream.next_in = (Bytef *)self.buffer;
	stream.total_out = 0;
	stream.avail_out = 0;

	if (deflateInit2(&stream, Z_DEFAULT_COMPRESSION, Z_DEFLATED, 31, 8, Z_DEFAULT_STRATEGY) == Z_OK)
	{
		NSMutableData *data = [NSMutableData dataWithLength:ChunkSize];

		while (stream.avail_out == 0)
		{
			if (stream.total_out >= data.length)
			{
				data.length += ChunkSize;
			}

			stream.next_out = (uint8_t *)data.mutableBytes + stream.total_out;
			stream.avail_out = (uInt)(data.length - stream.total_out);
			deflate(&stream, Z_FINISH);
		}

		deflateEnd(&stream);
		data.length = stream.total_out;

		return data;
	}

	return nil;
}

@end

@implementation HttpRemoteConnection

static NSString * const CONTENT_TYPE_KEY = @"Content-Type";
static NSString * const CONTENT_ENCODING_KEY = @"Content-Encoding";
static NSString * const CONTENT_TYPE_VALUE = @"application/x-stasis";
static NSString * const REQUEST_METHOD = @"POST";
static NSString * const LOGIN_FUNCTION = @"login";
static NSString * const GZIP_ENCODING = @"gzip";
static NSString * const CONNECTION_ERROR_DOMAIN = @"httpRemoteConnectionError";
static const NSInteger ERROR_AUTHENTICATION_MISSMATCH = 100;
static const NSInteger ERROR_AUTHENTICATION_FAILED = 101;
static const NSInteger ERROR_UNKNOWN_CONTENT_TYPE = 102;

BOOL isUsingGzipEncoding(NSString *headerValue)
{
	NSArray *chunks = [headerValue componentsSeparatedByString: @","];

	for (NSString *chunk in chunks)
	{
		NSRange offset = [chunk rangeOfString:@";"];
		NSString *encoding = chunk;
		
		if (offset.location != NSNotFound)
		{
			encoding = [encoding substringToIndex:offset.location];
		}
		
		encoding = [encoding stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];

		if ([encoding caseInsensitiveCompare:GZIP_ENCODING] == NSOrderedSame)
		{
			return YES;
		}
	}
	
	return NO;
}

- (id)initWithUrl:(NSURL *)url
{
	self = [super init];
	
	if (self != nil)
	{
		_url = url;
		_kryo = [Kryo new];
		_output = [[KryoOutput alloc] initWithBufferSize:256 untilMaximum:-1];
		_input = [[KryoInput alloc] init];
		_state = Unconnected;
		_userName = nil;
		_password = nil;
		_parameters = nil;
		_gzipAvailable = NO;
		_cookieStorage = [HTTPCookieStorage new];
	}
	
	return self;
}

- (void)callAsync:(NSString *)name withArguments:(NSArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler
{
	if ((_state != Authenticated) && (_userName != nil) && (_password != nil))
	{
		// Die Verbindung noch nicht authentifiziert ist, aber Credentials vorliegen zuerst einloggen
		[self invokeLoginForUser:_userName password:_password andParameters:_parameters followingFunction:name withArguments:args returning:resultHandler error:errorHandler];
	}
	else
	{
		// Die Service-Funktion ausführen
		[self invokeServiceFunction:name withArguments:args handshakeHandler:_handshakeHandler andTryCount:1 returning:resultHandler error:^(NSError *error)
		{
			// Ausführung der Service-Funktion ist fehlgeschlagen
			if ([error.domain isEqualToString:CONNECTION_ERROR_DOMAIN] && (error.code == ERROR_AUTHENTICATION_MISSMATCH))
			{
				assert(_activeUserName != nil);
				assert(_activePassword != nil);
				
				[self invokeLoginForUser:_activeUserName password:_activePassword andParameters:_activeParameters followingFunction:name withArguments:args returning:resultHandler error:errorHandler];
			}
			else
			{
				errorHandler(error);
			}
		}];
	}
}

- (void)loginUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters returning:(void (^)(AuthenticationResult *, NSDictionary *))resultHandler error:(void (^)(NSError *))errorHandler
{
	_userName = userName;
	_password = password;
	_parameters = parameters;

	[self invokeLoginForUser:userName password:password andParameters:parameters handshakeHandler:_handshakeHandler returning:resultHandler error:errorHandler];
}

- (void)setCredentialsForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters
{
	_userName = userName;
	_password = password;
	_parameters = parameters;
	_state = Connected;
}

- (void)setHandshakeHandler:(id<HandshakeHandler>)handshakeHandler
{
	_handshakeHandler = handshakeHandler;
}

- (void)setDefaultSerializer:(Class)defaultSerializer
{
    [_kryo setDefaultSerializer:defaultSerializer];
}

- (void)registerDefaultSerializerClass:(Class)serializerClass forClass:(Class)type
{
	[_kryo registerDefaultSerializerClass:serializerClass forClass:type];
}

- (void)registerDefaultSerializer:(id<Serializer>)serializer forClass:(Class)type
{
	[_kryo registerDefaultSerializer:serializer forClass:type];
}

- (void)registerAlias:(NSString *)alias forClass:(Class)type
{
	[_kryo registerAlias:alias forClass:type];
}

- (void)registerClass:(Class)type usingSerializer:(id<Serializer>)serializer
{
	[_kryo registerClass:type usingSerializer:serializer];
}

- (void)registerClass:(Class)type andIdent:(NSInteger)ident
{
	[_kryo registerClass:type andIdent:ident];
}

- (void)registerClass:(Class)type usingSerializer:(id<Serializer>)serializer andIdent:(NSInteger)ident
{
	[_kryo registerClass:type usingSerializer:serializer andIdent:ident];
}

- (void)invokeLoginForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters followingFunction:(NSString *)name withArguments:(NSArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler
{
	// Die Verbindung noch nicht authentifiziert ist, aber Credentials vorliegen zuerst einloggen
	[self invokeLoginForUser:userName password:password andParameters:parameters handshakeHandler:_handshakeHandler returning:^(AuthenticationResult *authenticationResult, NSDictionary *loginResult)
	{
		if (authenticationResult != AuthenticationResult.AUTHENTICATED)
		{
			// Die Authentifizierung ist fehlgeschlagen
			errorHandler([StasisError errorWithDomain:CONNECTION_ERROR_DOMAIN code:ERROR_AUTHENTICATION_FAILED userInfo:loginResult]);
			return;
		}
		 
		// Die Service-Funktion ausführen
		[self invokeServiceFunction:name withArguments:args handshakeHandler:_handshakeHandler andTryCount:1 returning:resultHandler error:errorHandler];
	} error:errorHandler];
}

- (void)invokeServiceFunction:(NSString *)name withArguments:(NSArray *)args handshakeHandler:(id<HandshakeHandler>)handshakeHandler andTryCount:(int)tryCount returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler
{
	[_output clear];
	[_kryo writeObject:[MethodCall methodCall:name withArguments:args assumingAuthenticated:self.state == Authenticated] to:_output];
	
	__weak HttpRemoteConnection *weakSelf = self;
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:_url];
	
	[request addValue:CONTENT_TYPE_VALUE forHTTPHeaderField:CONTENT_TYPE_KEY];
	request.cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
	request.HTTPShouldHandleCookies = NO;
	request.HTTPMethod = REQUEST_METHOD;
	
	if (_gzipAvailable)
	{
		[request setValue:GZIP_ENCODING forHTTPHeaderField:CONTENT_ENCODING_KEY];
		request.HTTPBody = [_output toGzippedData];
	}
	else
	{
		request.HTTPBody = [_output toData];
	}
	
	[_cookieStorage handleCookiesInRequest:request];
	
	NSLog(@"Send request to %@", name);
	
	[NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error)
	 {
		if (error == nil)
		{
			NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
			_gzipAvailable |= isUsingGzipEncoding(httpResponse.allHeaderFields[CONTENT_ENCODING_KEY]);
			
			[_cookieStorage handleCookiesInResponse:httpResponse];
			
			if (data.length == 0)
			{
				return;
			}
			
			if (![CONTENT_TYPE_VALUE isEqualToString:response.MIMEType])
			{
				BOOL handled = NO;
				
				if (handshakeHandler != nil)
				{
					handled = [handshakeHandler handleResponse:response withData:data forConnection:weakSelf tryCount:tryCount returning:^(NSError *error) {
						if (error == nil)
						{
							[weakSelf invokeServiceFunction:name withArguments:args handshakeHandler:handshakeHandler andTryCount:tryCount + 1 returning:resultHandler error:errorHandler];
						}
						else
						{
							errorHandler(error);
						}
					}];
				}
				
				if (!handled)
				{
					NSLog(@"Response with MimeType %@ can't be handled", response.MIMEType);
					errorHandler([StasisError errorWithDomain:CONNECTION_ERROR_DOMAIN code:ERROR_UNKNOWN_CONTENT_TYPE userInfo:nil]);
				}
				
				return;
			}
			 
			@try
			{
				_input.buffer = data;
				MethodResult *methodResult = [_kryo readObject:_input ofClass:[MethodResult class]];
				
				if (_state == Unconnected)
				{
					_state = Connected;
				}
				
				switch (methodResult.type)
				{
					case MethodResultVoid:
					case MethodResultValue:
						@try
						{
							resultHandler(methodResult.result);
						}
						@catch (NSException *ex)
						{
							NSLog(@"ResultHandler for %@ threw exception: %@", name, ex.description);
							NSLog(@"StackTrace: %@", ex.callStackSymbols);
						}
						break;

					case MethodResultException:
					{
						NSError *transformedError;
						
						if ([methodResult.result isKindOfClass:[AuthenticationMissmatchException class]])
						{
							transformedError = [StasisError errorWithDomain:CONNECTION_ERROR_DOMAIN code:ERROR_AUTHENTICATION_MISSMATCH userInfo:nil];
						}
						else
						{
							transformedError = [ExceptionError errorWithException:(NSException *)methodResult.result];
						}
						
						errorHandler(transformedError);
						break;
					}
				}
			}
			@catch (NSException *ex)
			{
				NSLog(@"Request %@ returned, but throw exception: %@", name, ex.description);
				NSLog(@"StackTrace: %@", ex.callStackSymbols);
				errorHandler([ExceptionError errorWithException:ex]);
			}
			@finally
			{
				_input.buffer = nil;
			}
		 }
		 else
		 {
			 NSLog(@"Request %@ returned with error: %@", name, error.localizedDescription);
			 errorHandler(error);
		 }
	 }];
}

- (void)invokeLoginForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters handshakeHandler:(id<HandshakeHandler>)handshakeHandler returning:(void (^)(AuthenticationResult *, NSDictionary *))resultHandler error:(void (^)(NSError *))errorHandler
{
	if (parameters == nil)
	{
		parameters = [NSDictionary dictionary];
	}
	
	NSArray *args = [NSArray arrayWithObjects:userName, password, parameters, nil];
	
	[self invokeServiceFunction:LOGIN_FUNCTION withArguments:args handshakeHandler:handshakeHandler andTryCount:1 returning:^(id result)
	{
		LoginResult *loginResult = (LoginResult *)result;

		if (loginResult.authenticationResult == AuthenticationResult.AUTHENTICATED)
		{
			NSMutableDictionary *newParameters = [NSMutableDictionary new];
			[newParameters addEntriesFromDictionary:parameters];
			[newParameters addEntriesFromDictionary:loginResult.loginResponse];
			 
			_activeUserName = userName;
			_activePassword = password;
			_activeParameters = newParameters;
			_state = Authenticated;
		}
		 
		resultHandler(loginResult.authenticationResult, loginResult.loginResponse);
	} error:errorHandler];
}

@end
