//
//  Months.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalDate;

@interface Months : NSObject

+ (instancetype)monthsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate;

@property (nonatomic, readonly) NSInteger months;

@end
