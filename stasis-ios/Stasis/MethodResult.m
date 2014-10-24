//
//  MethodResult.m
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "MethodResult.h"
#import "MethodResultSerializer.h"

@implementation MethodResult

+ (NSString *)serializingAlias
{
	return @"de.boxxit.statis.MethodResult";
}

+ (Class)defaultSerializer
{
	return [MethodResultSerializer class];
}

@end
