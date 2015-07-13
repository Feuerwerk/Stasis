//
//  Months.m
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Months.h"
#import "LocalDate.h"

@interface Months ()

- (instancetype)initWithMonths:(NSInteger)months;

@end

@implementation Months

+ (instancetype)ZERO
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithMonths:0];
	}
	
	return retval;
}

+ (instancetype)ONE
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithMonths:1];
	}
	
	return retval;
}

+ (instancetype)months:(NSInteger)months
{
	switch (months)
	{
		case 0:
			return [self ZERO];
			
		case 1:
			return [self ONE];
			
		default:
			return [[self alloc] initWithMonths:months];
	}
}

+ (instancetype)monthsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSMonthCalendarUnit fromDate:startDate.value toDate:stopDate.value options:0];
	return [Months months:components.month];
}

- (instancetype)initWithMonths:(NSInteger)months
{
	self = [super init];
	
	if (self != nil)
	{
		_months = months;
	}
	
	return self;
}

- (Months *)plus:(Months *)aMonths
{
	return [Months months:_months + aMonths->_months];
}

- (Months *)minus:(Months *)aMonths
{
	return [Months months:_months - aMonths->_months];
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
	
	if ([object isKindOfClass:[Months class]])
	{
		return _months == ((Months *)object)->_months;
	}
	
	return NO;
}

- (BOOL)isEqualToMonths:(Months *)aMonths
{
	if (aMonths == self)
	{
		return YES;
	}
	
	if (aMonths == nil)
	{
		return NO;
	}
	
	return _months == aMonths->_months;
}

- (NSComparisonResult)compare:(Months *)aMonths
{
	if (_months == aMonths->_months)
	{
		return NSOrderedSame;
	}
	
	return (_months < aMonths->_months) ? NSOrderedAscending : NSOrderedDescending;
}

- (instancetype)negate
{
	return [Months months:-_months];
}

- (void)fill:(NSDateComponents *)components
{
	components.month = _months;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_months];
}

@end
