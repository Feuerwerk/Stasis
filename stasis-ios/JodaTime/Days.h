//
//  Days.h
//  JodaTime
//
//  Created by Christian Fruth on 30.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalDate;

@interface Days : NSObject

+ (instancetype)daysBetween:(LocalDate *)startDate and:(LocalDate *)stopDate;

@property (nonatomic, readonly) NSInteger days;

@end
