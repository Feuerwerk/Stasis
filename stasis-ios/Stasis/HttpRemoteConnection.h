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
	NSURL *_url;
	Kryo *_kryo;
	KryoOutput *_output;
	KryoInput *_input;
	NSString *_userName;
	NSString *_password;
	SInt32 _clientVersion;
	NSString *_activeUserName;
	NSString *_activePassword;
	SInt32 _activeClientVersion;
	id<HandshakeHandler> _handshakeHandler;
	HTTPCookieStorage *_cookieStorage;
}

- (id)initWithUrl:(NSURL *)url;

@end
