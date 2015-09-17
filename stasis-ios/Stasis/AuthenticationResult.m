//
//  AuthenticationResult.m
//  Stasis
//
//  Created by Christian Fruth on 17.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "AuthenticationResult.h"

@implementation AuthenticationResult

ENUM_ELEMENT(UNAUTHENTICATED, 0)
ENUM_ELEMENT(AUTHENTICATED, 1)
ENUM_ELEMENT(INVALID, 2)

+ (NSString *)serializingAlias
{
	return @"de.boxxit.stasis.AuthenticationResult";
}


@end
