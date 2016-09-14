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
	NSURL *_url;
	NSString *_userName;
	NSString *_password;
}

+ (instancetype _Nonnull)connectionFromUrl:(nonnull NSURL *)url;

- (ConnectionState)state;

- (void)callAsync:(nonnull NSString *)name withArguments:(nonnull JObjectArray *)args returning:(nullable void (^)(id _Nullable))resultHandler error:(nullable void (^)(NSError * _Nonnull))errorHandler;
- (void)setCredentialsForUser:(nonnull NSString *)userName password:(nonnull NSString *)password andParameters:(nullable NSDictionary *)parameters;
- (void)loginUser:(nonnull NSString *)userName password:(nonnull NSString *)password andParameters:(nullable NSDictionary *)parameters returning:(nullable void (^)(AuthenticationResult * _Nonnull, NSDictionary * _Nonnull))resultHandler error:(nullable void (^)(NSError * _Nonnull))errorHandler;

- (void)setHandshakeHandler:(nullable id<HandshakeHandler>)handshakeHandler;
- (void)setDefaultSerializer:(nonnull Class)defaultSerializer;
- (void)registerDefaultSerializerClass:(nonnull Class)serializerClass forClass:(nonnull Class)type;
- (void)registerDefaultSerializer:(nonnull id<Serializer>)serializer forClass:(nonnull Class)type;
- (void)registerAlias:(nonnull NSString *)alias forClass:(nonnull Class)type;
- (void)registerClass:(nonnull Class)type usingSerializer:(nonnull id<Serializer>)serializer;
- (void)registerClass:(nonnull Class)type andIdent:(NSInteger)ident;
- (void)registerClass:(nonnull Class)type usingSerializer:(nonnull id<Serializer>)serializer andIdent:(NSInteger)ident;

@property (readonly, nonnull) NSURL *url;
@property (readonly, nullable) NSString *userName;
@property (readonly, nullable) NSString *password;

@end
