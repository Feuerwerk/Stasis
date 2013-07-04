//
//  ExceptionError.m
//  Stasis
//
//  Created by Christian Fruth on 07.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "ExceptionError.h"

@implementation ExceptionError

static NSString *const ExceptionErrorDomain = @"exceptionErrorDomain";

+ (instancetype)errorWithException:(NSException *)exception
{
	return [[ExceptionError alloc] initWithException:exception];
}

- (id)initWithException:(NSException *)exception
{
	self = [super initWithDomain:ExceptionErrorDomain code:0 userInfo:nil];
	
	if (self != nil)
	{
		_exception = exception;
	}
	
	return self;
}

- (NSString *)localizedDescription
{
	return self.description;
}

- (NSString *)description
{
	return _exception.description;
}

@end
