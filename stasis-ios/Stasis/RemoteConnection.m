//
//  RemoteConnection.m
//  Stasis
//
//  Created by Christian Fruth on 05.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "RemoteConnection.h"
#import "HttpRemoteConnection.h"

@implementation RemoteConnection

+ (instancetype)connectionFromUrl:(NSURL *)url
{
	NSString *scheme = url.scheme;
	
	if ([scheme isEqualToString:@"http"])
	{
		return [[HttpRemoteConnection alloc] initWithUrl:url];
	}
	
	if ([scheme isEqualToString:@"https"])
	{
		return [[HttpRemoteConnection alloc] initWithUrl:url];
	}
	
	return nil;
}

- (ConnectionState)state
{
	return _state;
}

- (void)callAsync:(NSString *)name withArguments:(JObjectArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)setCredentialsForUser:(NSString *)userName password:(NSString *)password andClientVersion:(SInt32)clientVersion
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)loginUser:(NSString *)userName password:(NSString *)password andClientVersion:(SInt32)clientVersion returning:(void (^)(NSDictionary *))resultHandler error:(void (^)(NSError *))errorHandler
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)setHandshakeHandler:(id<HandshakeHandler>)handshakeHandler
{
   [NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)setDefaultSerializer:(Class)defaultSerializer
{
    [NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)registerDefaultSerializerClass:(Class)serializerClass forClass:(Class)type
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)registerDefaultSerializer:(id<Serializer>)serializer forClass:(Class)type
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)registerAlias:(NSString *)alias forClass:(Class)type
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)registerClass:(Class)type usingSerializer:(id<Serializer>)serializer
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)registerClass:(Class)type andIdent:(NSInteger)ident
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

- (void)registerClass:(Class)type usingSerializer:(id<Serializer>)serializer andIdent:(NSInteger)ident
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ must be overwritten in subclass", NSStringFromSelector(_cmd)];
}

@end
