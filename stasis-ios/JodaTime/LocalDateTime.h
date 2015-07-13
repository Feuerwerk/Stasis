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

@interface LocalDateTime : NSObject
{
	NSDate *_value;
}

+ (instancetype)date;
+ (instancetype)dateFromMillis:(UInt64)millis;
+ (instancetype)dateFromDate:(NSDate *)date;
+ (instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth;
+ (instancetype)dateFromDate:(LocalDate *)date andTime:(LocalTime *)time;

- (id)initFromDate:(NSDate *)date;
- (id)initFromComponents:(NSDateComponents *)components;

- (LocalDateTime *)plus:(Period *)period;
- (LocalDateTime *)plusDays:(NSInteger)days;
- (LocalDateTime *)plusWeeks:(NSInteger)weeks;
- (LocalDateTime *)plusMonths:(NSInteger)months;
- (LocalDateTime *)plusYears:(NSInteger)years;
- (LocalDateTime *)plusHours:(NSInteger)hours;
- (LocalDateTime *)plusMinutes:(NSInteger)minutes;
- (LocalDateTime *)plusSeconds:(NSInteger)seconds;

- (LocalDateTime *)minus:(Period *)period;
- (LocalDateTime *)minusDays:(NSInteger)days;
- (LocalDateTime *)minusWeeks:(NSInteger)weeks;
- (LocalDateTime *)minusMonths:(NSInteger)months;
- (LocalDateTime *)minusYears:(NSInteger)years;
- (LocalDateTime *)minusHours:(NSInteger)hours;
- (LocalDateTime *)minusMinutes:(NSInteger)minutes;
- (LocalDateTime *)minusSeconds:(NSInteger)seconds;

- (LocalDateTime *)withDayOfWeek:(NSInteger)dayOfWeek;
- (LocalDateTime *)withDayOfMonth:(NSInteger)dayOfMonth;
- (LocalDateTime *)withDayOfYear:(NSInteger)dayOfYear;
- (LocalDateTime *)withHourOfDay:(NSInteger)hourOfDay;
- (LocalDateTime *)withMinuteOfHour:(NSInteger)minuteOfHour;
- (LocalDateTime *)withSecondOfMinute:(NSInteger)secondOfMinute;

@property (nonatomic, readonly) UInt64 millis;
@property (nonatomic, readonly) NSDate *value;
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

- (NSComparisonResult)compare:(LocalDateTime *)aDate;
- (BOOL)isBefore:(LocalDateTime *)aDate;
- (BOOL)isAfter:(LocalDateTime *)aDate;

@end
