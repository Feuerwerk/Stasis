//
//  Days.m
//  JodaTime
//
//  Created by Christian Fruth on 30.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Days.h"
#import "LocalDate.h"

@interface Days ()

- (instancetype)initWithDays:(NSInteger)days;

@end

@implementation Days

+ (instancetype)ZERO
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithDays:0];
	}
	
	return retval;
}

+ (instancetype)ONE
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithDays:1];
	}
	
	return retval;
}

+ (instancetype)days:(NSInteger)days
{
	switch (days)
	{
		case 0:
			return [self ZERO];
			
		case 1:
			return [self ONE];
			
		default:
			return [[self alloc] initWithDays:days];
	}
}

+ (instancetype)daysBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSDayCalendarUnit fromDate:startDate.value toDate:stopDate.value options:0];
	return [Days days:components.day];
}

- (instancetype)initWithDays:(NSInteger)days
{
	self = [super init];
	
	if (self != nil)
	{
		_days = days;
	}
	
	return self;
}

- (Days *)plus:(Days *)aDays
{
	return [Days days:_days + aDays->_days];
}

- (Days *)minus:(Days *)aDays
{
	return [Days days:_days - aDays->_days];
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
	
	if ([object isKindOfClass:[Days class]])
	{
		return _days == ((Days *)object)->_days;
	}
	
	return NO;
}

- (BOOL)isEqualToDays:(Days *)aDays
{
	if (aDays == self)
	{
		return YES;
	}
	
	if (aDays == nil)
	{
		return NO;
	}
	
	return _days == aDays->_days;
}

- (NSComparisonResult)compare:(Days *)aDays
{
	if (_days == aDays->_days)
	{
		return NSOrderedSame;
	}
	
	return (_days < aDays->_days) ? NSOrderedAscending : NSOrderedDescending;
}

- (instancetype)negate
{
	return [Days days:-_days];
}

- (void)fill:(NSDateComponents *)components
{
	components.day = _days;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_days];
}

@end
