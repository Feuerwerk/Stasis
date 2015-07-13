//
//  LocalTime.m
//  JodaTime
//
//  Created by Christian Fruth on 07.05.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalTime.h"

@interface LocalTime ()

- (id)initFromUnsafeDate:(NSDate *)date;
- (id)initFromComponents:(NSDateComponents *)components;

@end

@implementation LocalTime
{
	NSDate *_value;
}

static NSDateFormatter *descriptionFormatter = nil;

+ (instancetype)midnight
{
	static LocalTime *_midnight = nil;
	static dispatch_once_t once = 0;
	dispatch_once(&once, ^{ _midnight = [LocalTime timeFromMillisOfDay:0]; });
	return _midnight;
}

+ (instancetype)time
{
	return [[LocalTime alloc] init];
}

+ (instancetype)timeFromMillisOfDay:(UInt64)millisOfDay
{
	NSDateComponents *components = [NSDateComponents new];
	UInt32 seconds = (UInt32)(millisOfDay / 1000);
	UInt32 minutes = seconds / 60;
	components.year = 1970;
	components.month = 1;
	components.day = 1;
	components.hour = minutes / 60;
	components.minute = minutes % 60;
	components.second = seconds % 60;
	return [[LocalTime alloc] initFromComponents:components];
}

+ (instancetype)timeFromDate:(NSDate *)date
{
	if (date == nil)
    {
        return nil;
    }

    return [[LocalTime alloc] initFromDate:date];
}

+ (instancetype)timeFromHour:(NSInteger)hourOfDay
{
	NSDateComponents *components = [NSDateComponents new];
	components.year = 1970;
	components.month = 1;
	components.day = 1;
	components.hour = hourOfDay;
	components.minute = 0;
	components.second = 0;
	return [[LocalTime alloc] initFromComponents:components];
}

+ (instancetype)timeFromHour:(NSInteger)hourOfDay minute:(NSInteger)minuteOfHour
{
	NSDateComponents *components = [NSDateComponents new];
	components.year = 1970;
	components.month = 1;
	components.day = 1;
	components.hour = hourOfDay;
	components.minute = minuteOfHour;
	components.second = 0;
	return [[LocalTime alloc] initFromComponents:components];
}

+ (instancetype)timeFromHour:(NSInteger)hourOfDay minute:(NSInteger)minuteOfHour second:(NSInteger)secondOfMinute
{
	NSDateComponents *components = [NSDateComponents new];
	components.year = 1970;
	components.month = 1;
	components.day = 1;
	components.hour = hourOfDay;
	components.minute = minuteOfHour;
	components.second = secondOfMinute;
	return [[LocalTime alloc] initFromComponents:components];
}

- (id)init
{
	return [self initFromDate:[NSDate date]];
}

- (id)initFromDate:(NSDate *)date
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:date];
	components.year = 1970;
	components.month = 1;
	components.day = 1;
	return [self initFromComponents:components];
}

- (id)initFromComponents:(NSDateComponents *)components
{
	return [self initFromUnsafeDate:[[NSCalendar currentCalendar] dateFromComponents:components]];
}

- (id)initFromUnsafeDate:(NSDate *)date
{
	self = [super init];
	
	if (self != nil)
	{
		_value = date;
	}
	
	return self;
}

- (LocalTime *)plusHours:(NSInteger)hours
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.hour = hours;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalTime *)plusMinutes:(NSInteger)minutes
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.minute = minutes;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalTime *)plusSeconds:(NSInteger)seconds
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.second = seconds;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalTime *)minusHours:(NSInteger)hours
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.hour = -hours;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalTime *)minusMinutes:(NSInteger)minutes
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.minute = -minutes;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalTime *)minusSeconds:(NSInteger)seconds
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.second = -seconds;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalTime *)withHourOfDay:(NSInteger)hourOfDay
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:_value];
	components.hour = hourOfDay;
	return [[LocalTime alloc] initFromComponents:components];
}

- (LocalTime *)withMinuteOfHour:(NSInteger)minuteOfHour
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit | NSSecondCalendarUnit fromDate:_value];
	components.minute = minuteOfHour;
	return [[LocalTime alloc] initFromComponents:components];
}

- (LocalTime *)withSecondOfMinute:(NSInteger)secondOfMinute
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:_value];
	components.second = secondOfMinute;
	return [[LocalTime alloc] initFromComponents:components];
}


- (UInt64)millisOfDay
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:_value];
	UInt64 millisOfDay = (UInt64)components.hour;
	millisOfDay = millisOfDay * 60 + components.minute;
	millisOfDay = millisOfDay * 60 + components.second;
	return millisOfDay * 1000;
}

- (NSDate *)value
{
	return _value;
}

- (NSInteger)hourOfDay
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit fromDate:_value];
	return components.hour;
}

- (NSInteger)minuteOfHour;
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSMinuteCalendarUnit fromDate:_value];
	return components.minute;
}

- (NSInteger)secondOfMinute
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSSecondCalendarUnit fromDate:_value];
	return components.second;
}

- (NSString *)description
{
	if (descriptionFormatter == nil)
	{
		descriptionFormatter = [NSDateFormatter new];
		descriptionFormatter.dateFormat = @"HH:mm:ss";
	}
	
	return [descriptionFormatter stringFromDate:_value];
}

- (NSString *)debugDescription
{
	return _value.description;
}

- (NSComparisonResult)compare:(LocalTime *)aTime
{
	return [_value compare:aTime.value];
}

- (BOOL)isBefore:(LocalTime *)aTime
{
	return [_value compare:aTime.value] == NSOrderedAscending;
}

- (BOOL)isBeforeOrEqual:(LocalTime *)aTime
{
	return [_value compare:aTime.value] != NSOrderedDescending;
}

- (BOOL)isAfter:(LocalTime *)aTime
{
	return [_value compare:aTime.value] == NSOrderedDescending;
}

- (BOOL)isAfterOrEqual:(LocalTime *)aTime
{
	return [_value compare:aTime.value] != NSOrderedAscending;
}

@end
