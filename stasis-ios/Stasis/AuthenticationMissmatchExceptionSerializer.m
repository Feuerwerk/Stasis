//
//  AuthenticationMissmatchExceptionSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "AuthenticationMissmatchExceptionSerializer.h"
#import "AuthenticationMissmatchException.h"
#import "Kryo.h"

@implementation AuthenticationMissmatchExceptionSerializer

- (BOOL)acceptsNull
{
	return NO;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	AuthenticationMissmatchException *exception = value;
	[output writeBoolean:exception.authenticated];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	bool authenticated = [input readBoolean];
	return [AuthenticationMissmatchException exceptionWithAuthenticated:authenticated];
}

@end
