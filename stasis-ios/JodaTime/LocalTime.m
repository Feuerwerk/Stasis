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
	NSDate *_date;
}

static NSDateFormatter *descriptionFormatter = nil;

+ (instancetype)time
{
	return [[LocalTime alloc] init];
}

+ (instancetype)timeFromDate:(NSDate *)date
{
	return [[LocalTime alloc] initFromDate:date];
}

+ (instancetype)timeFromHour:(NSInteger)hourOfDay
{
	NSDateComponents *components = [NSDateComponents new];
	components.hour = hourOfDay;
	return [[LocalTime alloc] initFromComponents:components];
}

+ (instancetype)timeFromHour:(NSInteger)hourOfDay minute:(NSInteger)minuteOfHour
{
	NSDateComponents *components = [NSDateComponents new];
	components.hour = hourOfDay;
	components.minute = minuteOfHour;
	return [[LocalTime alloc] initFromComponents:components];
}

+ (instancetype)timeFromHour:(NSInteger)hourOfDay minute:(NSInteger)minuteOfHour second:(NSInteger)secondOfMinute
{
	NSDateComponents *components = [NSDateComponents new];
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
	return [self initFromComponents:[[NSCalendar currentCalendar] components:NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:date]];
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
		_date = date;
	}
	
	return self;
}

- (LocalTime *)plusHours:(NSInteger)hours
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.hour = hours;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalTime *)plusMinutes:(NSInteger)minutes
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.minute = minutes;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalTime *)plusSeconds:(NSInteger)seconds
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.second = seconds;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalTime *)minusHours:(NSInteger)hours
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.hour = -hours;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalTime *)minusMinutes:(NSInteger)minutes
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.minute = -minutes;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalTime *)minusSeconds:(NSInteger)seconds
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.second = -seconds;
	return [[LocalTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalTime *)withHourOfDay:(NSInteger)hourOfDay
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit fromDate:_date];
	components.hour = hourOfDay;
	return [[LocalTime alloc] initFromComponents:components];
}

- (LocalTime *)withMinuteOfHour:(NSInteger)minuteOfHour
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit fromDate:_date];
	components.minute = minuteOfHour;
	return [[LocalTime alloc] initFromComponents:components];
}

- (LocalTime *)withSecondOfMinute:(NSInteger)secondOfMinute
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit fromDate:_date];
	components.second = secondOfMinute;
	return [[LocalTime alloc] initFromComponents:components];
}


- (UInt64)millis
{
	return _date.timeIntervalSince1970 * 1000;
}

- (NSDate *)date
{
	return _date;
}

- (NSInteger)hourOfDay
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSHourCalendarUnit fromDate:_date];
	return components.hour;
}

- (NSInteger)minuteOfHour;
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSMinuteCalendarUnit fromDate:_date];
	return components.minute;
}

- (NSInteger)secondOfMinute
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSSecondCalendarUnit fromDate:_date];
	return components.second;
}

- (NSString *)description
{
	if (descriptionFormatter == nil)
	{
		descriptionFormatter = [NSDateFormatter new];
		descriptionFormatter.dateFormat = @"HH:mm:ss";
	}
	
	return [descriptionFormatter stringFromDate:_date];
}

- (NSString *)debugDescription
{
	return _date.description;
}

- (NSComparisonResult)compare:(LocalTime *)aTime
{
	return [_date compare:aTime.date];
}

- (BOOL)isBefore:(LocalTime *)aTime
{
	return [_date compare:aTime.date] == NSOrderedAscending;
}

- (BOOL)isAfter:(LocalTime *)aTime
{
	return [_date compare:aTime.date] == NSOrderedDescending;
}

@end
