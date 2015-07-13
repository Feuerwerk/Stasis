//
//  Weeks.m
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Weeks.h"
#import "LocalDate.h"

@interface Weeks ()

- (instancetype)initWithWeeks:(NSInteger)weeks;

@end

@implementation Weeks

+ (instancetype)ZERO
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithWeeks:0];
	}
	
	return retval;
}

+ (instancetype)ONE
{
	static id retval = nil;
	
	if (retval == nil)
	{
		retval = [[self alloc] initWithWeeks:1];
	}
	
	return retval;
}

+ (instancetype)weeks:(NSInteger)weeks
{
	switch (weeks)
	{
		case 0:
			return [self ZERO];
			
		case 1:
			return [self ONE];
			
		default:
			return [[self alloc] initWithWeeks:weeks];
	}
}

+ (instancetype)weeksBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSWeekCalendarUnit fromDate:startDate.value toDate:stopDate.value options:0];
	return [Weeks weeks:components.week];
}

- (instancetype)initWithWeeks:(NSInteger)weeks
{
	self = [super init];
	
	if (self != nil)
	{
		_weeks = weeks;
	}
	
	return self;
}

- (Weeks *)plus:(Weeks *)aWeeks
{
	return [Weeks weeks:_weeks + aWeeks->_weeks];
}

- (Weeks *)minus:(Weeks *)aWeeks
{
	return [Weeks weeks:_weeks - aWeeks->_weeks];
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
	
	if ([object isKindOfClass:[Weeks class]])
	{
		return _weeks == ((Weeks *)object)->_weeks;
	}
	
	return NO;
}

- (BOOL)isEqualToWeeks:(Weeks *)aWeeks
{
	if (aWeeks == self)
	{
		return YES;
	}
	
	if (aWeeks == nil)
	{
		return NO;
	}
	
	return _weeks == aWeeks->_weeks;
}

- (NSComparisonResult)compare:(Weeks *)aWeeks
{
	if (_weeks == aWeeks->_weeks)
	{
		return NSOrderedSame;
	}
	
	return (_weeks < aWeeks->_weeks) ? NSOrderedAscending : NSOrderedDescending;
}

- (instancetype)negate
{
	return [Weeks weeks:-_weeks];
}

- (void)fill:(NSDateComponents *)components
{
	components.week = _weeks;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_weeks];
}

@end
