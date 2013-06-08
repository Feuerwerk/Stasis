//
//  AuthenticationMissmatchException.m
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "AuthenticationMissmatchException.h"
#import "AuthenticationMissmatchExceptionSerializer.h"

@implementation AuthenticationMissmatchException

+ (instancetype)exceptionWithAuthenticated:(BOOL) authenticated
{
	return [[AuthenticationMissmatchException alloc] initWithAuthenticated:authenticated];
}

- (id)initWithAuthenticated:(BOOL) authenticated
{
	self = [super initWithName:@"StasisException" reason:@"Authentication missmatched" userInfo:nil];
	
	if (self != nil)
	{
		_authenticated = authenticated;
	}
	
	return self;
}

+ (NSString *)serializingAlias
{
	return @"de.boxxit.statis.AuthenticationMissmatchException";
}

+ (Class)defaultSerializer
{
	return [AuthenticationMissmatchExceptionSerializer class];
}

@end
