//
//  Months.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Period.h"

@class LocalDate;

@interface Months : Period

@property (nonatomic, readonly) NSInteger months;

+ (instancetype)ZERO;
+ (instancetype)ONE;

+ (instancetype)months:(NSInteger)months;
+ (instancetype)monthsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate;

- (Months *)plus:(Months *)aMonths;
- (Months *)minus:(Months *)aMonths;

- (BOOL)isEqualToMonths:(Months *)aMonths;
- (NSComparisonResult)compare:(Months *)aMonths;

@end
