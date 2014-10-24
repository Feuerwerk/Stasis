//
//  MethodCall.m
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "MethodCall.h"
#import "MethodCallSerializer.h"

@implementation MethodCall

+ (instancetype)methodCall:(NSString *)name withArguments:(NSArray *)args assumingAuthenticated:(BOOL)assumeAuthenticated
{
	MethodCall *methodCall = [[self alloc] init];
	methodCall.name = name;
	methodCall.args = args;
	methodCall.assumeAuthenticated = assumeAuthenticated;
	return methodCall;
}

+ (NSString *)serializingAlias
{
	return @"de.boxxit.statis.MethodCall";
}

+ (Class)defaultSerializer
{
	return [MethodCallSerializer class];
}

@end
