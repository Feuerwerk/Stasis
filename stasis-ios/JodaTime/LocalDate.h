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

@interface LocalDate : NSObject<NSCopying>
{
	NSDate *_value;
}

+ (nonnull instancetype)date;
+ (nonnull instancetype)dateFromMillis:(UInt64)millis;
+ (nonnull instancetype)dateFromDate:(nonnull NSDate *)date;
+ (nonnull instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth;
+ (nonnull instancetype)dateFromYear:(NSInteger)year weekOfYear:(NSInteger)weekOfYear andDayOfWeek:(NSInteger)dayOfWeek;

- (nonnull id)init;
- (nonnull id)initFromDate:(nonnull NSDate *)date;
- (nonnull id)initFromComponents:(nonnull NSDateComponents *)components;

- (nonnull LocalDate *)plusDays:(NSInteger)days;
- (nonnull LocalDate *)plusWeeks:(NSInteger)weeks;
- (nonnull LocalDate *)plusMonths:(NSInteger)months;
- (nonnull LocalDate *)plusYears:(NSInteger)years;

- (nonnull LocalDate *)minusDays:(NSInteger)days;
- (nonnull LocalDate *)minusWeeks:(NSInteger)weeks;
- (nonnull LocalDate *)minusMonths:(NSInteger)months;
- (nonnull LocalDate *)minusYears:(NSInteger)years;

- (nonnull LocalDate *)withDayOfWeek:(NSInteger)dayOfWeek;
- (nonnull LocalDate *)withDayOfMonth:(NSInteger)dayOfMonth;
- (nonnull LocalDate *)withDayOfYear:(NSInteger)dayOfYear;

@property (nonatomic, readonly) UInt64 millis;
@property (nonatomic, readonly, nonnull) NSDate *value;
@property (nonatomic, readonly) NSInteger year;
@property (nonatomic, readonly) NSInteger monthOfYear;
@property (nonatomic, readonly) NSInteger dayOfMonth;
@property (nonatomic, readonly) WeekDays dayOfWeek;
@property (nonatomic, readonly) NSInteger dayOfYear;
@property (nonatomic, readonly) NSInteger weekyear;
@property (nonatomic, readonly) NSInteger weekOfWeekyear;

- (NSComparisonResult)compare:(nonnull LocalDate *)aDate;
- (BOOL)isBefore:(nonnull LocalDate *)aDate;
- (BOOL)isAfter:(nonnull LocalDate *)aDate;
- (BOOL)isBeforeOrEqual:(nonnull LocalDate *)aDate;
- (BOOL)isAfterOrEqual:(nonnull LocalDate *)aDate;
- (BOOL)isEqual:(nullable id)object;
- (BOOL)isEqualToDate:(nonnull LocalDate *)aDate;

- (nonnull LocalDateTime *)atTime:(nonnull LocalTime *)time;
- (nonnull LocalDateTime *)atStartOfDay;

@end
