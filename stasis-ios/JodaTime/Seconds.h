//
//  Seconds.h
//  JodaTime
//
//  Created by Christian Fruth on 24.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Period.h"

@class LocalDateTime;

@interface Seconds : Period

@property (nonatomic, readonly) NSInteger seconds;

+ (instancetype)ZERO;
+ (instancetype)ONE;

+ (instancetype)seconds:(NSInteger)seconds;
+ (instancetype)secondsBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate;

- (Seconds *)plus:(Seconds *)aSeconds;
- (Seconds *)minus:(Seconds *)aSeconds;

- (BOOL)isEqualToSeconds:(Seconds *)aSeconds;
- (NSComparisonResult)compare:(Seconds *)aSeconds;

@end
