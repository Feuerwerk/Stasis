//
//  Years.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Period.h"

@class LocalDate;

@interface Years : Period

@property (nonatomic, readonly) NSInteger years;

+ (instancetype)ZERO;
+ (instancetype)ONE;

+ (instancetype)years:(NSInteger)years;
+ (instancetype)yearsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate;

- (Years *)plus:(Years *)aYears;
- (Years *)minus:(Years *)aYears;

- (BOOL)isEqualToYears:(Years *)aYears;
- (NSComparisonResult)compare:(Years *)aYears;

@end
