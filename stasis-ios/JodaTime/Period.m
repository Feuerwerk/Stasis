//
//  Period.m
//  JodaTime
//
//  Created by Christian Fruth on 24.11.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Period.h"

@implementation Period

- (instancetype)negate
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ is an unsupported operation", NSStringFromSelector(_cmd)];
	return nil;
}

- (void)setComponent:(NSDateComponents *)components
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ is an unsupported operation", NSStringFromSelector(_cmd)];
}

@end
