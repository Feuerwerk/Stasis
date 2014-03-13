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

+ (instancetype)exceptionWithType:(NSString *)type message:(NSString *)message andStackSymbols:(NSArray *)stackSymbols
{
	return [[SerializableException alloc] initWithType:type message:message andStackSymbols:stackSymbols];
}

- (id)initWithType:(NSString *)type message:(NSString *)message andStackSymbols:(NSArray *)stackSymbols
{
	self = [super initWithName:type reason:message userInfo:nil];
	
	if (self != nil)
	{
		_stackSymbols = stackSymbols;
	}
	
	return self;
}

- (NSArray *)callStackSymbols
{
	return _stackSymbols;
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
