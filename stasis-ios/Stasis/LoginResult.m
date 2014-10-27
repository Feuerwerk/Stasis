//
//  LoginResult.m
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "LoginResult.h"

@implementation LoginResult

+ (NSArray *)loginResponseGenerics
{
	return [NSArray arrayWithObjects:[NSString class], [NSObject class], nil];
}

+ (NSString *)serializingAlias
{
	return @"de.boxxit.stasis.LoginResult";
}

@end
