//
//  Weeks.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

# import "Period.h"

@class LocalDate;

@interface Weeks : Period<NSCopying>

@property (nonatomic, readonly) NSInteger weeks;

+ (nonnull instancetype)ZERO;
+ (nonnull instancetype)ONE;

+ (nonnull instancetype)weeks:(NSInteger)weeks;
+ (nonnull instancetype)weeksBetween:(nonnull LocalDate *)startDate and:(nonnull LocalDate *)stopDate;

- (nonnull Weeks *)plus:(nonnull Weeks *)aWeeks;
- (nonnull Weeks *)minus:(nonnull Weeks *)aWeeks;

- (BOOL)isEqualToWeeks:(nonnull Weeks *)aDays;
- (NSComparisonResult)compare:(nonnull Weeks *)aDays;

@end
