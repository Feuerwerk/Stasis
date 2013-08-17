//
//  AuthenticationResult.m
//  Stasis
//
//  Created by Christian Fruth on 17.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "AuthenticationResult.h"

@implementation AuthenticationResult

ENUM_ELEMENT(UNAUTHENTICATED, 0, nil)
ENUM_ELEMENT(AUTHENTICATED, 1, nil)
ENUM_ELEMENT(VERSIONMISSMATCH, 2, nil)

+ (NSString *)serializingAlias
{
	return @"de.boxxit.stasis.AuthenticationResult";
}


@end
