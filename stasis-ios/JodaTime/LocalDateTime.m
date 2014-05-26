//
//  LocalDateTime.m
//  JodaTime
//
//  Created by Christian Fruth on 10.04.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalDateTime.h"
#import "LocalDate.h"
#import "LocalTime.h"

@interface LocalDateTime ()

- (id)initFromUnsafeDate:(NSDate *)date;
- (id)initFromComponents:(NSDateComponents *)components;

@end

@implementation LocalDateTime

static NSDateFormatter *descriptionFormatter = nil;

+ (instancetype)date
{
	return [[LocalDateTime alloc] initFromDate:[NSDate date]];
}

+ (instancetype)dateFromDate:(NSDate *)date
{
    if (date == nil)
    {
        return nil;
    }

	return [[LocalDateTime alloc] initFromDate:date];
}

+ (instancetype)dateFromDate:(LocalDate *)date andTime:(LocalTime *)time
{
	NSDateComponents *dateComponents = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit fromDate:date.date];
	NSDateComponents *timeComponents = [[NSCalendar currentCalendar] components:NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:time.date];

	dateComponents.hour = timeComponents.hour;
	dateComponents.minute = timeComponents.minute;
	dateComponents.second = timeComponents.second;

	return [[LocalDateTime alloc] initFromComponents:dateComponents];
}

+ (instancetype)dateFromMillis:(UInt64)millis
{
	return [[LocalDateTime alloc] initFromDate:[NSDate dateWithTimeIntervalSince1970:millis / 1000]];
}

+ (instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth
{
	NSDateComponents *components = [NSDateComponents new];
	
	components.year = year;
	components.month = monthOfYear;
	components.day = dayOfMonth;
	
	return [[LocalDateTime alloc] initFromComponents:components];
}

- (id)init
{
	return [self initFromDate:[NSDate date]];
}

- (id)initFromDate:(NSDate *)date
{
	return [self initFromComponents:[[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:date]];
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

- (LocalDateTime *)plusDays:(NSInteger)days
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = days;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)plusWeeks:(NSInteger)weeks
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = weeks * 7;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)plusMonths:(NSInteger)months
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.month = months;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)plusYears:(NSInteger)years
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.year = years;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)plusHours:(NSInteger)hours
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.hour = hours;
	return [[LocalDateTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)plusMinutes:(NSInteger)minutes
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.minute = minutes;
	return [[LocalDateTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)plusSeconds:(NSInteger)seconds
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.second = seconds;
	return [[LocalDateTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)minusDays:(NSInteger)days
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = -days;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)minusWeeks:(NSInteger)weeks
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = -weeks * 7;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)minusMonths:(NSInteger)months
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.month = -months;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)minusYears:(NSInteger)years
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.year = -years;
	return [LocalDateTime dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)minusHours:(NSInteger)hours
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.hour = -hours;
	return [[LocalDateTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)minusMinutes:(NSInteger)minutes
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.minute = -minutes;
	return [[LocalDateTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)minusSeconds:(NSInteger)seconds
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.second = -seconds;
	return [[LocalDateTime alloc] initFromUnsafeDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)withDayOfWeek:(NSInteger)dayOfWeek
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSUInteger firstWeekday = calendar.firstWeekday;
	NSInteger weekDay = [calendar components:NSWeekdayCalendarUnit fromDate:_date].weekday;
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = (dayOfWeek >= firstWeekday) ? dayOfWeek - weekDay : dayOfWeek + 7 - firstWeekday;
	return [LocalDateTime dateFromDate:[calendar dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDateTime *)withDayOfMonth:(NSInteger)dayOfMonth
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:_date];
	components.month = dayOfMonth;
	return [[LocalDateTime alloc] initFromComponents:components];
}

- (LocalDateTime *)withDayOfYear:(NSInteger)dayOfYear
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSYearCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:_date];
	
	components.day = dayOfYear;
	components.month = 1;

	return [[LocalDateTime alloc] initFromComponents:components];
}

- (LocalDateTime *)withHourOfDay:(NSInteger)hourOfDay
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit fromDate:_date];
	components.hour = hourOfDay;
	return [[LocalDateTime alloc] initFromComponents:components];
}

- (LocalDateTime *)withMinuteOfHour:(NSInteger)minuteOfHour
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit | NSHourCalendarUnit | NSSecondCalendarUnit fromDate:_date];
	components.minute = minuteOfHour;
	return [[LocalDateTime alloc] initFromComponents:components];
}

- (LocalDateTime *)withSecondOfMinute:(NSInteger)secondOfMinute
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit fromDate:_date];
	components.second = secondOfMinute;
	return [[LocalDateTime alloc] initFromComponents:components];
}

- (UInt64)millis
{
	return _date.timeIntervalSince1970 * 1000;
}

- (NSDate *)date
{
	return _date;
}

- (NSInteger)year
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit fromDate:_date];
	return components.year;
}

- (NSInteger)monthOfYear
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSMonthCalendarUnit fromDate:_date];
	return components.month;
}

- (NSInteger)dayOfMonth
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSDayCalendarUnit fromDate:_date];
	return components.day;
}

- (WeekDays)dayOfWeek
{
	return (WeekDays)[[NSCalendar currentCalendar] components:NSWeekdayCalendarUnit fromDate:_date].weekday;
}

- (NSInteger)dayOfYear
{
	return [[NSCalendar currentCalendar] ordinalityOfUnit:NSDayCalendarUnit inUnit:NSYearCalendarUnit forDate:_date];
}

- (NSInteger)weekOfWeekyear
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSWeekOfYearCalendarUnit fromDate:_date];
	return components.weekOfYear;
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
		descriptionFormatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
	}
	
	return [descriptionFormatter stringFromDate:_date];
}

- (NSString *)debugDescription
{
	return _date.description;
}

- (NSComparisonResult)compare:(LocalDateTime *)aDate
{
	return [_date compare:aDate.date];
}

- (BOOL)isBefore:(LocalDateTime *)aDate
{
	return [_date compare:aDate.date] == NSOrderedAscending;
}

- (BOOL)isAfter:(LocalDateTime *)aDate
{
	return [_date compare:aDate.date] == NSOrderedDescending;
}

@end
