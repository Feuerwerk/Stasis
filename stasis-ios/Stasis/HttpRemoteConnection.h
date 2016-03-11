//
//  HttpRemoteConnection.h
//  Stasis
//
//  Created by Christian Fruth on 05.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RemoteConnection.h"

@class Kryo;
@class KryoInput;
@class KryoOutput;
@class HTTPCookieStorage;

@interface HttpRemoteConnection : RemoteConnection
{
	Kryo *_kryo;
	KryoOutput *_output;
	KryoInput *_input;
	NSDictionary *_parameters;
	NSString *_activeUserName;
	NSString *_activePassword;
	NSDictionary *_activeParameters;
	BOOL _gzipAvailable;
	id<HandshakeHandler> _handshakeHandler;
	HTTPCookieStorage *_cookieStorage;
}

@property (nonatomic, readonly) HTTPCookieStorage *cookieStorage;

- (id)initWithUrl:(NSURL *)url;

@end
