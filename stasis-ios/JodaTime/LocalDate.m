//
//  LocalDate.m
//  JodaTime
//
//  Created by Christian Fruth on 08.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalDate.h"
#import "LocalDateTime.h"
#import "LocalTime.h"

@implementation LocalDate

static NSDateFormatter *descriptionFormatter = nil;

+ (instancetype)date
{
	return [[LocalDate alloc] init];
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

+ (instancetype)dateFromYear:(NSInteger)year weekOfYear:(NSInteger)weekOfYear andDayOfWeek:(NSInteger)dayOfWeek
{
	NSDateComponents *components = [NSDateComponents new];
	
	components.yearForWeekOfYear = year;
	components.weekOfYear = weekOfYear;
	components.weekday = dayOfWeek;
	
	return [[LocalDate alloc] initFromComponents:components];
}

- (id)init
{
	return [self initFromDate:[NSDate date]];
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
		_value = [[NSCalendar currentCalendar] dateFromComponents:components];
	}
	
	return self;
}

- (LocalDate *)plusDays:(NSInteger)days
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = days;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)plusWeeks:(NSInteger)weeks
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = weeks * 7;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)plusMonths:(NSInteger)months
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.month = months;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)plusYears:(NSInteger)years
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.year = years;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)minusDays:(NSInteger)days
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = -days;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)minusWeeks:(NSInteger)weeks
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = -weeks * 7;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)minusMonths:(NSInteger)months
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.month = -months;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)minusYears:(NSInteger)years
{
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.year = -years;
	return [LocalDate dateFromDate:[[NSCalendar currentCalendar] dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)withDayOfWeek:(NSInteger)dayOfWeek
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSUInteger firstWeekday = calendar.firstWeekday;
	NSInteger weekDay = [calendar components:NSWeekdayCalendarUnit fromDate:_value].weekday;
	NSDateComponents *offsetComponents = [NSDateComponents new];
	offsetComponents.day = (dayOfWeek >= firstWeekday) ? dayOfWeek - weekDay : dayOfWeek + 7 - firstWeekday;
	return [LocalDate dateFromDate:[calendar dateByAddingComponents:offsetComponents toDate:_value options:0]];
}

- (LocalDate *)withDayOfMonth:(NSInteger)dayOfMonth
{
	NSDateComponents *components = [[NSCalendar currentCalendar] components:NSYearCalendarUnit | NSMonthCalendarUnit |  NSDayCalendarUnit fromDate:_value];
	components.day = dayOfMonth;
	return [[LocalDate alloc] initFromComponents:components];
}

- (LocalDate *)withDayOfYear:(NSInteger)dayOfYear
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSYearCalendarUnit fromDate:_value];
	
	components.day = dayOfYear;
	components.month = 1;
	
	return [[LocalDate alloc] initFromComponents:components];
}

- (UInt64)millis
{
	return (UInt64)(_value.timeIntervalSince1970 * 1000);
}

- (NSDate *)value
{
	return _value;
}

- (NSInteger)year
{
	return [[NSCalendar currentCalendar] components:NSYearCalendarUnit fromDate:_value].year;
}

- (NSInteger)monthOfYear
{
	return [[NSCalendar currentCalendar] components:NSMonthCalendarUnit fromDate:_value].month;
}

- (NSInteger)dayOfMonth
{
	return [[NSCalendar currentCalendar] components:NSDayCalendarUnit fromDate:_value].day;
}

- (WeekDays)dayOfWeek
{
	return (WeekDays)[[NSCalendar currentCalendar] components:NSWeekdayCalendarUnit fromDate:_value].weekday;
}

- (NSInteger)dayOfYear
{
	return [[NSCalendar currentCalendar] ordinalityOfUnit:NSDayCalendarUnit inUnit:NSYearCalendarUnit forDate:_value];
}

- (NSInteger)weekyear
{
	return [[NSCalendar currentCalendar] components:NSYearForWeekOfYearCalendarUnit fromDate:_value].yearForWeekOfYear;
}

- (NSInteger)weekOfWeekyear
{
	return [[NSCalendar currentCalendar] components:NSWeekOfYearCalendarUnit fromDate:_value].weekOfYear;
}

- (NSString *)description
{
	if (descriptionFormatter == nil)
	{
		descriptionFormatter = [NSDateFormatter new];
		descriptionFormatter.dateFormat = @"yyyy-MM-dd";
	}
	
	return [descriptionFormatter stringFromDate:_value];
}

- (NSString *)debugDescription
{
	return _value.description;
}

- (NSComparisonResult)compare:(LocalDate *)aDate
{
	return [_value compare:aDate.value];
}

- (BOOL)isBefore:(LocalDate *)aDate
{
	return [_value compare:aDate.value] == NSOrderedAscending;
}

- (BOOL)isBeforeOrEqual:(LocalDate *)aDate
{
	return [_value compare:aDate.value] != NSOrderedDescending;
}

- (BOOL)isAfter:(LocalDate *)aDate
{
	return [_value compare:aDate.value] == NSOrderedDescending;
}

- (BOOL)isAfterOrEqual:(LocalDate *)aDate
{
	return [_value compare:aDate.value] != NSOrderedAscending;
}

- (BOOL)isEqual:(id)object
{
	if (object == nil)
	{
		return NO;
	}
	
	if (object == self)
	{
		return YES;
	}
	
	if (![object isKindOfClass:[LocalDate class]])
	{
		return NO;
	}
	
	LocalDate *aDate = (LocalDate *)object;
	
	return [_value isEqualToDate:aDate.value];
}

- (BOOL)isEqualToDate:(LocalDate *)aDate
{
	return [_value isEqualToDate:aDate.value];
}

- (LocalDateTime *)atTime:(LocalTime *)time
{
	return [LocalDateTime dateFromDate:self andTime:time];
}

- (LocalDateTime *)atStartOfDay
{
	return [LocalDateTime dateFromDate:self andTime:[LocalTime midnight]];
}

@end
