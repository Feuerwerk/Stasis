//
//  LocalDate.h
//  JodaTime
//
//  Created by Christian Fruth on 08.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum
{
	SUNDAY = 1,
	MONDAY = 2,
	TUESDAY = 3,
	WEDNESDAY = 4,
	THURSDAY = 5,
	FRIDAY = 6,
	SATURDAY = 7
} WeekDays;

@interface LocalDate : NSObject
{
	NSDate *_date;
}

+ (instancetype)date;
+ (instancetype)dateFromMillis:(UInt64)millis;
+ (instancetype)dateFromDate:(NSDate *)date;
+ (instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth;

- (id)initFromDate:(NSDate *)date;
- (id)initFromComponents:(NSDateComponents *)components;

- (LocalDate *)plusDays:(NSInteger)days;
- (LocalDate *)plusWeeks:(NSInteger)weeks;
- (LocalDate *)plusMonths:(NSInteger)months;
- (LocalDate *)plusYearsDays:(NSInteger)years;

- (LocalDate *)minusDays:(NSInteger)days;
- (LocalDate *)minusWeeks:(NSInteger)weeks;
- (LocalDate *)minusMonths:(NSInteger)months;
- (LocalDate *)minusYearsDays:(NSInteger)years;

- (LocalDate *)withDayOfWeek:(NSInteger)dayOfWeek;
- (LocalDate *)withDayOfMonth:(NSInteger)dayOfMonth;
- (LocalDate *)withDayOfYear:(NSInteger)dayOfYear;

- (UInt64)millis;
- (NSDate *)date;
- (NSInteger)year;
- (NSInteger)monthOfYear;
- (NSInteger)dayOfMonth;
- (NSInteger)weekOfWeekyear;

- (NSComparisonResult)compare:(LocalDate *)aDate;
- (BOOL)isBefore:(LocalDate *)aDate;
- (BOOL)isAfter:(LocalDate *)aDate;

@end
