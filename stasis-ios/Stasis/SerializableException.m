//
//  SerializableException.m
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "SerializableException.h"
#import "SerializableExceptionSerializer.h"

@implementation SerializableException

+ (instancetype)exceptionWithIdent:(NSString *)ident andMessage:(NSString *)message
{
	return [[SerializableException alloc] initWithIdent:ident andMessage:message];
}

- (id)initWithIdent:(NSString *)ident andMessage:(NSString *)message
{
	return [super initWithName:ident reason:message userInfo:nil];
}

+ (NSString *)serializingAlias
{
	return @"de.boxxit.statis.SerializableException";
}

+ (Class)defaultSerializer
{
	return [SerializableExceptionSerializer class];
}

@end
