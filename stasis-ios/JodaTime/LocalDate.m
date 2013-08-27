//
//  LocalDate.m
//  JodaTime
//
//  Created by Christian Fruth on 08.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalDate.h"

@implementation LocalDate

static NSDateFormatter *descriptionFormatter = nil;

+ (instancetype)date
{
	return [[LocalDate alloc] initFromDate:[NSDate date]];
}

+ (instancetype)dateFromDate:(NSDate *)date
{
    if (date == nil)
    {
        return nil;
    }

	return [[LocalDate alloc] initFromDate:date];
}

+ (instancetype)dateFromMillis:(UInt64)millis
{
	return [[LocalDate alloc] initFromDate:[NSDate dateWithTimeIntervalSince1970:millis / 1000]];
}

+ (instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth
{
	NSDateComponents *components = [NSDateComponents new];
	
	components.year = year;
	components.month = monthOfYear;
	components.day = dayOfMonth;
	
	return [[LocalDate alloc] initFromComponents:components];
}

- (id)initFromDate:(NSDate *)date
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit fromDate:date];
	return [self initFromComponents:components];
}

- (id)initFromComponents:(NSDateComponents *)components
{
	self = [super init];
	
	if (self != nil)
	{
		_date = [[NSCalendar currentCalendar] dateFromComponents:components];
	}
	
	return self;
}

- (LocalDate *)plusDays:(NSInteger)days
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = days;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)plusWeeks:(NSInteger)weeks
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = weeks * 7;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)plusMonths:(NSInteger)months
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.month = months;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)plusYearsDays:(NSInteger)years
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.year = years;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)minusDays:(NSInteger)days
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = -days;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)minusWeeks:(NSInteger)weeks
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = -weeks * 7;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)minusMonths:(NSInteger)months
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.month = -months;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)minusYearsDays:(NSInteger)years
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.year = -years;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)withDayOfWeek:(NSInteger)dayOfWeek
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSUInteger firstWeekday = calendar.firstWeekday;
	NSInteger weekDay = [calendar components:NSWeekdayCalendarUnit fromDate:_date].weekday;
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = (dayOfWeek >= firstWeekday) ? dayOfWeek - weekDay : dayOfWeek + 7 - firstWeekday;
	return [LocalDate dateFromDate:[calendar dateByAddingComponents:offsetComponents toDate:_date options:0]];
}

- (LocalDate *)withDayOfMonth:(NSInteger)dayOfMonth
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit fromDate:_date];
	components.month = dayOfMonth;
	return [[LocalDate alloc] initFromComponents:components];
}

- (LocalDate *)withDayOfYear:(NSInteger)dayOfYear
{
	return nil;
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
	return [[NSCalendar currentCalendar] components:NSYearCalendarUnit fromDate:_date].year;
}

- (NSInteger)monthOfYear
{
	return [[NSCalendar currentCalendar] components:NSMonthCalendarUnit fromDate:_date].month;
}

- (NSInteger)dayOfMonth
{
	return [[NSCalendar currentCalendar] components:NSDayCalendarUnit fromDate:_date].day;
}

- (NSInteger)dayOfWeek
{
	return [[NSCalendar currentCalendar] components:NSWeekdayCalendarUnit fromDate:_date].weekday;
}

- (NSInteger)weekOfWeekyear
{
	return [[NSCalendar currentCalendar] components:NSWeekOfYearCalendarUnit fromDate:_date].weekOfYear;
}

- (NSString *)description
{
	if (descriptionFormatter == nil)
	{
		descriptionFormatter = [NSDateFormatter new];
		descriptionFormatter.dateFormat = @"yyyy-MM-dd";
	}
	
	return [descriptionFormatter stringFromDate:_date];
}

- (NSString *)debugDescription
{
	return _date.description;
}

- (NSComparisonResult)compare:(LocalDate *)aDate
{
	return [_date compare:aDate.date];
}

- (BOOL)isBefore:(LocalDate *)aDate
{
	return [_date compare:aDate.date] == NSOrderedAscending;
}

- (BOOL)isAfter:(LocalDate *)aDate
{
	return [_date compare:aDate.date] == NSOrderedDescending;
}

@end