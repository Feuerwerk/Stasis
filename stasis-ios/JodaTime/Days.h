//
//  Days.h
//  JodaTime
//
//  Created by Christian Fruth on 30.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Period.h"

@class LocalDate;

@interface Days : Period

@property (nonatomic, readonly) NSInteger days;

+ (instancetype)ZERO;
+ (instancetype)ONE;

+ (instancetype)days:(NSInteger)days;
+ (instancetype)daysBetween:(LocalDate *)startDate and:(LocalDate *)stopDate;

- (Days *)plus:(Days *)aDays;
- (Days *)minus:(Days *)aDays;

- (BOOL)isEqualToDays:(Days *)aDays;
- (NSComparisonResult)compare:(Days *)aDays;

@end
