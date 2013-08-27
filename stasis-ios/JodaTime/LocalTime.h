//
//  LocalTime.h
//  JodaTime
//
//  Created by Christian Fruth on 07.05.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LocalTime : NSObject

+ (instancetype)time;
+ (instancetype)timeFromMillisOfDay:(UInt64)millisOfDay;
+ (instancetype)timeFromDate:(NSDate *)date;
+ (instancetype)timeFromHour:(NSInteger)hourOfDay;
+ (instancetype)timeFromHour:(NSInteger)hourOfDay minute:(NSInteger)minuteOfHour;
+ (instancetype)timeFromHour:(NSInteger)hourOfDay minute:(NSInteger)minuteOfHour second:(NSInteger)secondOfMinute;

- (id)init;
- (id)initFromDate:(NSDate *)date;

- (LocalTime *)plusHours:(NSInteger)hours;
- (LocalTime *)plusMinutes:(NSInteger)minutes;
- (LocalTime *)plusSeconds:(NSInteger)seconds;

- (LocalTime *)minusHours:(NSInteger)hours;
- (LocalTime *)minusMinutes:(NSInteger)minutes;
- (LocalTime *)minusSeconds:(NSInteger)seconds;

- (LocalTime *)withHourOfDay:(NSInteger)hourOfDay;
- (LocalTime *)withMinuteOfHour:(NSInteger)minuteOfHour;
- (LocalTime *)withSecondOfMinute:(NSInteger)secondOfMinute;

- (UInt64)millisOfDay;
- (NSDate *)date;
- (NSInteger)hourOfDay;
- (NSInteger)minuteOfHour;
- (NSInteger)secondOfMinute;

- (NSComparisonResult)compare:(LocalTime *)aTime;
- (BOOL)isBefore:(LocalTime *)aTime;
- (BOOL)isAfter:(LocalTime *)aTime;

@end