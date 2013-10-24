//
//  LocalDateTime.h
//  JodaTime
//
//  Created by Christian Fruth on 10.04.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WeekDays.h"

@interface LocalDateTime : NSObject
{
	NSDate *_date;
}

+ (instancetype)date;
+ (instancetype)dateFromMillis:(UInt64)millis;
+ (instancetype)dateFromDate:(NSDate *)date;
+ (instancetype)dateFromYear:(NSInteger)year month:(NSInteger)monthOfYear andDay:(NSInteger)dayOfMonth;

- (id)initFromDate:(NSDate *)date;
- (id)initFromComponents:(NSDateComponents *)components;

- (LocalDateTime *)plusDays:(NSInteger)days;
- (LocalDateTime *)plusWeeks:(NSInteger)weeks;
- (LocalDateTime *)plusMonths:(NSInteger)months;
- (LocalDateTime *)plusYears:(NSInteger)years;
- (LocalDateTime *)plusHours:(NSInteger)hours;
- (LocalDateTime *)plusMinutes:(NSInteger)minutes;
- (LocalDateTime *)plusSeconds:(NSInteger)seconds;

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

- (UInt64)millis;
- (NSDate *)date;
- (NSInteger)year;
- (NSInteger)monthOfYear;
- (NSInteger)dayOfMonth;
- (WeekDays)dayOfWeek;
- (NSInteger)dayOfYear;
- (NSInteger)weekOfWeekyear;
- (NSInteger)hourOfDay;
- (NSInteger)minuteOfHour;
- (NSInteger)secondOfMinute;

- (NSComparisonResult)compare:(LocalDateTime *)aDate;
- (BOOL)isBefore:(LocalDateTime *)aDate;
- (BOOL)isAfter:(LocalDateTime *)aDate;

@end
