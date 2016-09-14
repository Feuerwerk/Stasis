//
//  Seconds.h
//  JodaTime
//
//  Created by Christian Fruth on 24.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Period.h"

@class LocalDateTime;

@interface Seconds : Period<NSCopying>

@property (nonatomic, readonly) NSInteger seconds;

+ (nonnull instancetype)ZERO;
+ (nonnull instancetype)ONE;

+ (nonnull instancetype)seconds:(NSInteger)seconds;
+ (nonnull instancetype)secondsBetween:(nonnull LocalDateTime *)startDate and:(nonnull LocalDateTime *)stopDate;

- (nonnull Seconds *)plus:(nonnull Seconds *)aSeconds;
- (nonnull Seconds *)minus:(nonnull Seconds *)aSeconds;

- (BOOL)isEqualToSeconds:(nonnull Seconds *)aSeconds;
- (NSComparisonResult)compare:(nonnull Seconds *)aSeconds;

@end
