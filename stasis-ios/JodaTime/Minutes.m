//
//  Minutes.m
//  JodaTime
//
//  Created by Christian Fruth on 17.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Minutes.h"
#import "LocalDateTime.h"
#import "LocalTime.h"

@interface Minutes ()

- (instancetype)initWithMinutes:(NSInteger)minutes;

@end

@implementation Minutes

+ (instancetype)ZERO
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithMinutes:0];
	}

	return retval;
}

+ (instancetype)ONE
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithMinutes:1];
	}
	
	return retval;
}

+ (instancetype)TWO
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithMinutes:2];
	}
	
	return retval;
}

+ (instancetype)minutes:(NSInteger)minutes
{
	switch (minutes)
	{
		case 0:
			return [self ZERO];
			
		case 1:
			return [self ONE];
			
		case 2:
			return [self TWO];
			
		default:
			return [[self alloc] initWithMinutes:minutes];
	}
}

+ (instancetype)minutesBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSMinuteCalendarUnit fromDate:startDate.value toDate:stopDate.value options:0];
	return [Minutes minutes:components.minute];
}

+ (instancetype)minutesOfTimeBetween:(LocalTime *)startTime and:(LocalTime *)stopTime
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSMinuteCalendarUnit fromDate:startTime.value toDate:startTime.value options:0];
	return [Minutes minutes:components.minute];
}

- (instancetype)initWithMinutes:(NSInteger)minutes;
{
	self = [super init];
	
	if (self != nil)
	{
		_minutes = minutes;
	}
	
	return self;
}

- (Minutes *)plus:(Minutes *)aMinutes
{
	return [Minutes minutes:_minutes + aMinutes->_minutes];
}

- (Minutes *)minus:(Minutes *)aMinutes
{
	return [Minutes minutes:_minutes - aMinutes->_minutes];
}

- (BOOL)isBefore:(Minutes *)aMinutes
{
	return _minutes < aMinutes->_minutes;
}

- (BOOL)isAfter:(Minutes *)aMinutes
{
	return _minutes > aMinutes->_minutes;
}

- (BOOL)isBeforeOrEqual:(Minutes *)aMinutes
{
	return _minutes <= aMinutes->_minutes;
}

- (BOOL)isAfterOrEqual:(Minutes *)aMinutes
{
	return _minutes >= aMinutes->_minutes;
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
	
	if ([object isKindOfClass:[Minutes class]])
	{
		return _minutes == ((Minutes *)object)->_minutes;
	}
	
	return NO;
}

- (BOOL)isEqualToMinutes:(Minutes *)aMinutes
{
	if (aMinutes == self)
	{
		return YES;
	}
	
	if (aMinutes == nil)
	{
		return NO;
	}
	
	return _minutes == aMinutes->_minutes;
}

- (NSComparisonResult)compare:(Minutes *)aMinutes
{
	if (_minutes == aMinutes->_minutes)
	{
		return NSOrderedSame;
	}

	return (_minutes < aMinutes->_minutes) ? NSOrderedAscending : NSOrderedDescending;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_minutes];
}

- (instancetype)negate
{
	return [Minutes minutes:-_minutes];
}

- (void)fill:(NSDateComponents *)components
{
	components.minute = _minutes;
}

@end
