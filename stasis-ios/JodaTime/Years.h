//
//  Years.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Period.h"

@class LocalDate;

@interface Years : Period<NSCopying>

@property (nonatomic, readonly) NSInteger years;

+ (nonnull instancetype)ZERO;
+ (nonnull instancetype)ONE;

+ (nonnull instancetype)years:(NSInteger)years;
+ (nonnull instancetype)yearsBetween:(nonnull LocalDate *)startDate and:(nonnull LocalDate *)stopDate;

- (nonnull Years *)plus:(nonnull Years *)aYears;
- (nonnull Years *)minus:(nonnull Years *)aYears;

- (BOOL)isEqualToYears:(nonnull Years *)aYears;
- (NSComparisonResult)compare:(nonnull Years *)aYears;

@end
