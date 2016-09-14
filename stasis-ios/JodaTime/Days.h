//
//  Days.h
//  JodaTime
//
//  Created by Christian Fruth on 30.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Period.h"

@class LocalDate;

@interface Days : Period<NSCopying>

@property (nonatomic, readonly) NSInteger days;

+ (nonnull instancetype)ZERO;
+ (nonnull instancetype)ONE;

+ (nonnull instancetype)days:(NSInteger)days;
+ (nonnull instancetype)daysBetween:(nonnull LocalDate *)startDate and:(nonnull LocalDate *)stopDate;

- (nonnull Days *)plus:(nonnull Days *)aDays;
- (nonnull Days *)minus:(nonnull Days *)aDays;

- (BOOL)isEqualToDays:(nonnull Days *)aDays;
- (NSComparisonResult)compare:(nonnull Days *)aDays;

@end
