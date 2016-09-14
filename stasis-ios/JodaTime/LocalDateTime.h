//
//  LocalDateTime.h
//  JodaTime
//
//  Created by Christian Fruth on 10.04.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WeekDays.h"

@class LocalDate;
@class LocalTime;
@class Period;

@interface LocalDateTime : NSObject<NSCopying>
{
	NSDate *_value;
}

+ (nonnull instancetype)date;
+ (nonnull instancetype)dateFromMillis:(UInt64)millis;
+ (nonnull instancetype)dateFromDate:(nonnull NSDate *)date;
+ (nonnull instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth;
+ (nonnull instancetype)dateFromDate:(nonnull LocalDate *)date andTime:(nonnull LocalTime *)time;

- (nonnull id)initFromDate:(nonnull NSDate *)date;
- (nonnull id)initFromComponents:(nonnull NSDateComponents *)components;

- (nonnull LocalDateTime *)plus:(nonnull Period *)period;
- (nonnull LocalDateTime *)plusDays:(NSInteger)days;
- (nonnull LocalDateTime *)plusWeeks:(NSInteger)weeks;
- (nonnull LocalDateTime *)plusMonths:(NSInteger)months;
- (nonnull LocalDateTime *)plusYears:(NSInteger)years;
- (nonnull LocalDateTime *)plusHours:(NSInteger)hours;
- (nonnull LocalDateTime *)plusMinutes:(NSInteger)minutes;
- (nonnull LocalDateTime *)plusSeconds:(NSInteger)seconds;

- (nonnull LocalDateTime *)minus:(nonnull Period *)period;
- (nonnull LocalDateTime *)minusDays:(NSInteger)days;
- (nonnull LocalDateTime *)minusWeeks:(NSInteger)weeks;
- (nonnull LocalDateTime *)minusMonths:(NSInteger)months;
- (nonnull LocalDateTime *)minusYears:(NSInteger)years;
- (nonnull LocalDateTime *)minusHours:(NSInteger)hours;
- (nonnull LocalDateTime *)minusMinutes:(NSInteger)minutes;
- (nonnull LocalDateTime *)minusSeconds:(NSInteger)seconds;

- (nonnull LocalDateTime *)withDayOfWeek:(NSInteger)dayOfWeek;
- (nonnull LocalDateTime *)withDayOfMonth:(NSInteger)dayOfMonth;
- (nonnull LocalDateTime *)withDayOfYear:(NSInteger)dayOfYear;
- (nonnull LocalDateTime *)withHourOfDay:(NSInteger)hourOfDay;
- (nonnull LocalDateTime *)withMinuteOfHour:(NSInteger)minuteOfHour;
- (nonnull LocalDateTime *)withSecondOfMinute:(NSInteger)secondOfMinute;

@property (nonatomic, readonly) UInt64 millis;
@property (nonatomic, readonly, nonnull) NSDate *value;
@property (nonatomic, readonly) NSInteger year;
@property (nonatomic, readonly) NSInteger monthOfYear;
@property (nonatomic, readonly) NSInteger dayOfMonth;
@property (nonatomic, readonly) WeekDays dayOfWeek;
@property (nonatomic, readonly) NSInteger dayOfYear;
@property (nonatomic, readonly) NSInteger weekyear;
@property (nonatomic, readonly) NSInteger weekOfWeekyear;
@property (nonatomic, readonly) NSInteger hourOfDay;
@property (nonatomic, readonly) NSInteger minuteOfHour;
@property (nonatomic, readonly) NSInteger secondOfMinute;

- (NSComparisonResult)compare:(nonnull LocalDateTime *)aDate;
- (BOOL)isBefore:(nonnull LocalDateTime *)aDate;
- (BOOL)isAfter:(nonnull LocalDateTime *)aDate;
- (BOOL)isEqual:(nullable id)object;
- (BOOL)isEqualToDateTime:(nonnull LocalDateTime *)aDateTime;

@end
