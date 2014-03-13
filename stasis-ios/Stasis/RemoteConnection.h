//
//  RemoteConnection.h
//  Stasis
//
//  Created by Christian Fruth on 05.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol Serializer;

typedef enum { Unconnected, Connected, Authenticated } ConnectionState;

@class JObjectArray;
@class AuthenticationResult;
@protocol HandshakeHandler;

@interface RemoteConnection : NSObject
{
	@protected
	ConnectionState _state;
}

+ (instancetype)connectionFromUrl:(NSURL *)url;

- (ConnectionState)state;

- (void)callAsync:(NSString *)name withArguments:(JObjectArray *)args returning:(void (^)(id))resultHandler error:(void (^)(NSError *))errorHandler;
- (void)setCredentialsForUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters;
- (void)loginUser:(NSString *)userName password:(NSString *)password andParameters:(NSDictionary *)parameters returning:(void (^)(AuthenticationResult *, NSDictionary *))resultHandler error:(void (^)(NSError *))errorHandler;

- (void)setHandshakeHandler:(id<HandshakeHandler>)handshakeHandler;
- (void)setDefaultSerializer:(Class)defaultSerializer;
- (void)registerDefaultSerializerClass:(Class)serializerClass forClass:(Class)type;
- (void)registerDefaultSerializer:(id<Serializer>)serializer forClass:(Class)type;
- (void)registerAlias:(NSString *)alias forClass:(Class)type;
- (void)registerClass:(Class)type usingSerializer:(id<Serializer>)serializer;
- (void)registerClass:(Class)type andIdent:(NSInteger)ident;
- (void)registerClass:(Class)type usingSerializer:(id<Serializer>)serializer andIdent:(NSInteger)ident;

@end
