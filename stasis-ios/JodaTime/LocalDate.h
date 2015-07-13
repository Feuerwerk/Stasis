//
//  LocalDate.h
//  JodaTime
//
//  Created by Christian Fruth on 08.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WeekDays.h"

@class LocalDateTime;
@class LocalTime;

@interface LocalDate : NSObject
{
	NSDate *_value;
}

+ (instancetype)date;
+ (instancetype)dateFromMillis:(UInt64)millis;
+ (instancetype)dateFromDate:(NSDate *)date;
+ (instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth;
+ (instancetype)dateFromYear:(NSInteger)year weekOfYear:(NSInteger)weekOfYear andDayOfWeek:(NSInteger)dayOfWeek;

- (id)init;
- (id)initFromDate:(NSDate *)date;
- (id)initFromComponents:(NSDateComponents *)components;

- (LocalDate *)plusDays:(NSInteger)days;
- (LocalDate *)plusWeeks:(NSInteger)weeks;
- (LocalDate *)plusMonths:(NSInteger)months;
- (LocalDate *)plusYears:(NSInteger)years;

- (LocalDate *)minusDays:(NSInteger)days;
- (LocalDate *)minusWeeks:(NSInteger)weeks;
- (LocalDate *)minusMonths:(NSInteger)months;
- (LocalDate *)minusYears:(NSInteger)years;

- (LocalDate *)withDayOfWeek:(NSInteger)dayOfWeek;
- (LocalDate *)withDayOfMonth:(NSInteger)dayOfMonth;
- (LocalDate *)withDayOfYear:(NSInteger)dayOfYear;

@property (nonatomic, readonly) UInt64 millis;
@property (nonatomic, readonly) NSDate *value;
@property (nonatomic, readonly) NSInteger year;
@property (nonatomic, readonly) NSInteger monthOfYear;
@property (nonatomic, readonly) NSInteger dayOfMonth;
@property (nonatomic, readonly) WeekDays dayOfWeek;
@property (nonatomic, readonly) NSInteger dayOfYear;
@property (nonatomic, readonly) NSInteger weekyear;
@property (nonatomic, readonly) NSInteger weekOfWeekyear;

- (NSComparisonResult)compare:(LocalDate *)aDate;
- (BOOL)isBefore:(LocalDate *)aDate;
- (BOOL)isAfter:(LocalDate *)aDate;
- (BOOL)isBeforeOrEqual:(LocalDate *)aDate;
- (BOOL)isAfterOrEqual:(LocalDate *)aDate;
- (BOOL)isEqual:(id)object;
- (BOOL)isEqualToDate:(LocalDate *)aDate;

- (LocalDateTime *)atTime:(LocalTime *)time;
- (LocalDateTime *)atStartOfDay;

@end
