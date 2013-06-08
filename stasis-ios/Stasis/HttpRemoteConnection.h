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

@interface HttpRemoteConnection : RemoteConnection
{
	NSURL *_url;
	Kryo *_kryo;
	KryoOutput *_output;
	KryoInput *_input;
	NSString *_userName;
	NSString *_password;
	NSString *_activeUserName;
	NSString *_activePassword;
}

- (id)initWithUrl:(NSURL *)url;

@end
