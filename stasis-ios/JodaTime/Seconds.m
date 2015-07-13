//
//  Seconds.m
//  JodaTime
//
//  Created by Christian Fruth on 24.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Seconds.h"
#import "LocalDateTime.h"

@interface Seconds ()

- (instancetype)initWithSeconds:(NSInteger)seconds;

@end

@implementation Seconds

+ (instancetype)ZERO
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithSeconds:0];
	}
	
	return retval;
}

+ (instancetype)ONE
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithSeconds:1];
	}
	
	return retval;
}

+ (instancetype)seconds:(NSInteger)seconds
{
	switch (seconds)
	{
		case 0:
			return [self ZERO];
			
		case 1:
			return [self ONE];
			
		default:
			return [[self alloc] initWithSeconds:seconds];
	}
}

+ (instancetype)secondsBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSSecondCalendarUnit fromDate:startDate.value toDate:stopDate.value options:0];
	return [Seconds seconds:components.second];
}

- (instancetype)initWithSeconds:(NSInteger)seconds
{
	self = [super init];
	
	if (self != nil)
	{
		_seconds = seconds;
	}
	
	return self;
}

- (Seconds *)plus:(Seconds *)aSeconds
{
	return [Seconds seconds:_seconds + aSeconds->_seconds];
}

- (Seconds *)minus:(Seconds *)aSeconds
{
	return [Seconds seconds:_seconds - aSeconds->_seconds];
}

- (BOOL)isEqual:(id)object
{
	if (object == self)
	{
		return YES;
	}
	
	if (object == nil)
	{
		return NO;
	}
	
	if ([object isKindOfClass:[Seconds class]])
	{
		return _seconds == ((Seconds *)object)->_seconds;
	}
	
	return NO;
}

- (BOOL)isEqualToSeconds:(Seconds *)aSeconds
{
	if (aSeconds == self)
	{
		return YES;
	}
	
	if (aSeconds == nil)
	{
		return NO;
	}
	
	return _seconds == aSeconds->_seconds;
}

- (NSComparisonResult)compare:(Seconds *)aSeconds
{
	if (_seconds == aSeconds->_seconds)
	{
		return NSOrderedSame;
	}
	
	return (_seconds < aSeconds->_seconds) ? NSOrderedAscending : NSOrderedDescending;
}

- (instancetype)negate
{
	return [Seconds seconds:-_seconds];
}

- (void)fill:(NSDateComponents *)components
{
	components.second = _seconds;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_seconds];
}

@end
