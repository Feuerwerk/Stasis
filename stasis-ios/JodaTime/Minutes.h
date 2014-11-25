//
//  Minutes.h
//  JodaTime
//
//  Created by Christian Fruth on 17.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Period.h"

@class LocalDateTime;
@class LocalTime;

@interface Minutes : Period

@property (nonatomic, readonly) NSInteger minutes;

+ (instancetype)ZERO;
+ (instancetype)ONE;
+ (instancetype)TWO;

+ (instancetype)minutes:(NSInteger)minutes;
+ (instancetype)minutesBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate;
+ (instancetype)minutesOfTimeBetween:(LocalTime *)startTime and:(LocalTime *)stopTime;

- (Minutes *)plus:(Minutes *)aMinutes;
- (Minutes *)minus:(Minutes *)aMinutes;

- (BOOL)isBefore:(Minutes *)aMinutes;
- (BOOL)isAfter:(Minutes *)aMinutes;
- (BOOL)isBeforeOrEqual:(Minutes *)aMinutes;
- (BOOL)isAfterOrEqual:(Minutes *)aMinutes;
- (BOOL)isEqual:(id)object;
- (BOOL)isEqualToMinutes:(Minutes *)aMinutes;

- (NSComparisonResult)compare:(Minutes *)aMinutes;

@end
