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

@interface Minutes : Period<NSCopying>

@property (nonatomic, readonly) NSInteger minutes;

+ (nonnull instancetype)ZERO;
+ (nonnull instancetype)ONE;
+ (nonnull instancetype)TWO;

+ (nonnull instancetype)minutes:(NSInteger)minutes;
+ (nonnull instancetype)minutesBetween:(nonnull LocalDateTime *)startDate and:(nonnull LocalDateTime *)stopDate;
+ (nonnull instancetype)minutesOfTimeBetween:(nonnull LocalTime *)startTime and:(nonnull LocalTime *)stopTime;

- (nonnull Minutes *)plus:(nonnull Minutes *)aMinutes;
- (nonnull Minutes *)minus:(nonnull Minutes *)aMinutes;

- (BOOL)isBefore:(nonnull Minutes *)aMinutes;
- (BOOL)isAfter:(nonnull Minutes *)aMinutes;
- (BOOL)isBeforeOrEqual:(nonnull Minutes *)aMinutes;
- (BOOL)isAfterOrEqual:(nonnull Minutes *)aMinutes;
- (BOOL)isEqual:(nullable id)object;
- (BOOL)isEqualToMinutes:(nonnull Minutes *)aMinutes;

- (NSComparisonResult)compare:(nonnull Minutes *)aMinutes;

@end
