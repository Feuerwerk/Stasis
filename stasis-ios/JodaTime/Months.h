//
//  Months.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Period.h"

@class LocalDate;

@interface Months : Period<NSCopying>

@property (nonatomic, readonly) NSInteger months;

+ (nonnull instancetype)ZERO;
+ (nonnull instancetype)ONE;

+ (nonnull instancetype)months:(NSInteger)months;
+ (nonnull instancetype)monthsBetween:(nonnull LocalDate *)startDate and:(nonnull LocalDate *)stopDate;

- (nonnull Months *)plus:(nonnull Months *)aMonths;
- (nonnull Months *)minus:(nonnull Months *)aMonths;

- (BOOL)isEqualToMonths:(nonnull Months *)aMonths;
- (NSComparisonResult)compare:(nonnull Months *)aMonths;

@end
