//
//  Years.m
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Years.h"
#import "LocalDate.h"

@interface Years ()

- (instancetype)initWithYears:(NSInteger)years;

@end

@implementation Years

+ (instancetype)ZERO
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithYears:0];
	}
	
	return retval;
}

+ (instancetype)ONE
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithYears:1];
	}
	
	return retval;
}

+ (instancetype)years:(NSInteger)years
{
	switch (years)
	{
		case 0:
			return [self ZERO];
			
		case 1:
			return [self ONE];
			
		default:
			return [[self alloc] initWithYears:years];
	}
}

+ (instancetype)yearsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSYearCalendarUnit fromDate:startDate.value toDate:stopDate.value options:0];
	return [Years years:components.year];
}

- (instancetype)initWithYears:(NSInteger)years
{
	self = [super init];
	
	if (self != nil)
	{
		_years = years;
	}
	
	return self;
}

- (Years *)plus:(Years *)aYears
{
	return [Years years:_years + aYears->_years];
}

- (Years *)minus:(Years *)aYears
{
	return [Years years:_years - aYears->_years];
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
	
	if ([object isKindOfClass:[Years class]])
	{
		return _years == ((Years *)object)->_years;
	}
	
	return NO;
}

- (BOOL)isEqualToYears:(Years *)aYears
{
	if (aYears == self)
	{
		return YES;
	}
	
	if (aYears == nil)
	{
		return NO;
	}
	
	return _years == aYears->_years;
}

- (NSComparisonResult)compare:(Years *)aYears
{
	if (_years == aYears->_years)
	{
		return NSOrderedSame;
	}
	
	return (_years < aYears->_years) ? NSOrderedAscending : NSOrderedDescending;
}

- (instancetype)negate
{
	return [Years years:-_years];
}

- (void)fill:(NSDateComponents *)components
{
	components.year = _years;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_years];
}

@end
