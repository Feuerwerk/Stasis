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
#import "HTTPCookieStorage.h"
#import "StasisError.h"

@interface HttpRemoteConnection ()

- (void)invokeServiceFunction:(NSString *)name withArguments:(JObjectArray *)args handshakeHandler:(id<HandshakeHandler>)handshakeHandler andTryCount:(int)tryCount extractResult:(BOOL)extractResult returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler;
- (void)invokeLoginForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters handshakeHandler:(id<HandshakeHandler>)handshakeHandler returning:(void (^)(AuthenticationResult *, NSDictionary *))resultHandler error:(void (^)(NSError *))errorHandler;
- (void)invokeLoginForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters followingFunction:(NSString *)name withArguments:(JObjectArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler;

@end

@implementation HttpRemoteConnection

static NSString * const CONTENT_TYPE_KEY = @"Content-Type";
static NSString * const CONTENT_TYPE_VALUE = @"application/x-stasis";
static NSString * const REQUEST_METHOD = @"POST";
static NSString * const LOGIN_FUNCTION = @"login";
static NSString * const CONNECTION_ERROR_DOMAIN = @"httpRemoteConnectionError";
static const NSInteger ERROR_AUTHENTICATION_MISSMATCH = 100;
static const NSInteger ERROR_AUTHENTICATION_FAILED = 101;
static const NSInteger ERROR_UNKNOWN_CONTENT_TYPE = 102;

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
		_cookieStorage = [HTTPCookieStorage new];
	}
	
	return self;
}

- (void)callAsync:(NSString *)name withArguments:(JObjectArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler
{
	if ((_state != Authenticated) && (_userName != nil) && (_password != nil))
	{
		// Die Verbindung noch nicht authentifiziert ist, aber Credentials vorliegen zuerst einloggen
		[self invokeLoginForUser:_userName password:_password andParameters:_parameters followingFunction:name withArguments:args returning:resultHandler error:errorHandler];
	}
	else
	{
		// Die Service-Funktion ausführen
		[self invokeServiceFunction:name withArguments:args handshakeHandler:_handshakeHandler andTryCount:1 extractResult:YES returning:resultHandler error:^(NSError *error)
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

- (void)invokeLoginForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters followingFunction:(NSString *)name withArguments:(JObjectArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler
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
		[self invokeServiceFunction:name withArguments:args handshakeHandler:_handshakeHandler andTryCount:1 extractResult:YES returning:resultHandler error:errorHandler];
	} error:errorHandler];
}

- (void)invokeServiceFunction:(NSString *)name withArguments:(JObjectArray *)args handshakeHandler:(id<HandshakeHandler>)handshakeHandler andTryCount:(int)tryCount extractResult:(BOOL)extractResult returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler
{
	[_output clear];
	[_kryo writeObject:name to:_output];
	[_kryo writeObject:[JBoolean boolWithValue:self.state == Authenticated] to:_output]; // Dem Server mitteilen ob wir davon ausgehen, dass wir bereits authentifiziert sind
	[_kryo writeObject:args to:_output];
	
	__weak HttpRemoteConnection *weakSelf = self;
	NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:_url];
	
	[request addValue:CONTENT_TYPE_VALUE forHTTPHeaderField:CONTENT_TYPE_KEY];
	request.cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData;
	request.HTTPShouldHandleCookies = NO;
	request.HTTPMethod = REQUEST_METHOD;
	request.HTTPBody = [_output toData];
	
	[_cookieStorage handleCookiesInRequest:request];
	
	NSLog(@"Send request to %@", name);
	
	[NSURLConnection sendAsynchronousRequest:request queue:[NSOperationQueue mainQueue] completionHandler:^(NSURLResponse *response, NSData *data, NSError *error)
	 {
		if (error == nil)
		{
			[_cookieStorage handleCookiesInResponse:(NSHTTPURLResponse *)response];
			
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
							[weakSelf invokeServiceFunction:name withArguments:args handshakeHandler:handshakeHandler andTryCount:tryCount + 1 extractResult:extractResult returning:resultHandler error:errorHandler];
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
				JObjectArray *result = [_kryo readObject:_input ofClass:[JObjectArray class]];
				id resultValue = nil;
				 
				if (result.count != 0)
				{
					resultValue = [result objectAtIndex:0];
					//NSLog(@"Request %@ returned with: %@", name, resultValue);
					NSLog(@"Request %@ returned", name);
				 
					if ([resultValue isKindOfClass:[NSException class]])
					{
						NSError *transformedError;

						if ([resultValue isKindOfClass:[AuthenticationMissmatchException class]])
						{
							transformedError = [StasisError errorWithDomain:CONNECTION_ERROR_DOMAIN code:ERROR_AUTHENTICATION_MISSMATCH userInfo:nil];
						}
						else
						{
							transformedError = [ExceptionError errorWithException:resultValue];
						}
					
						errorHandler(transformedError);
						return;
					}
				}
				
				if (_state == Unconnected)
				{
					_state = Connected;
				}
				
				@try
				{
					resultHandler(extractResult ? resultValue : result);
				}
				@catch (NSException *ex)
				{
					NSLog(@"ResultHandler for %@ threw exception: %@", name, ex.description);
				}
			}
			@catch (NSException *ex)
			{
				NSLog(@"Request %@ returned, but throw exception: %@", name, ex.description);
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
	
	JObjectArray *args = [JObjectArray arrayWithObjects:userName, password, parameters, nil];
	
	[self invokeServiceFunction:LOGIN_FUNCTION withArguments:args handshakeHandler:handshakeHandler andTryCount:1 extractResult:NO returning:^(id result)
	{
		JObjectArray *resultValues = (JObjectArray *)result;
		AuthenticationResult *authenticationResult = [resultValues objectAtIndex:0];
		NSDictionary *response = [resultValues objectAtIndex:1];

		if (authenticationResult == AuthenticationResult.AUTHENTICATED)
		{
			NSMutableDictionary *newParameters = [NSMutableDictionary new];
			[newParameters addEntriesFromDictionary:parameters];
			[newParameters addEntriesFromDictionary:response];
			 
			_activeUserName = userName;
			_activePassword = password;
			_activeParameters = newParameters;
			_state = Authenticated;
		}
		 
		resultHandler(authenticationResult, response);
	} error:errorHandler];
}

@end
