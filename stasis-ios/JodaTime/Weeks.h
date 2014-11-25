//
//  Weeks.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

# import "Period.h"

@class LocalDate;

@interface Weeks : Period

@property (nonatomic, readonly) NSInteger weeks;

+ (instancetype)ZERO;
+ (instancetype)ONE;

+ (instancetype)weeks:(NSInteger)weeks;
+ (instancetype)weeksBetween:(LocalDate *)startDate and:(LocalDate *)stopDate;

- (Weeks *)plus:(Weeks *)aWeeks;
- (Weeks *)minus:(Weeks *)aWeeks;

- (BOOL)isEqualToWeeks:(Weeks *)aDays;
- (NSComparisonResult)compare:(Weeks *)aDays;

@end
