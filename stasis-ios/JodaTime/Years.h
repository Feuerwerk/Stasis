//
//  Years.h
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalDate;

@interface Years : NSObject

+ (instancetype)yearsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate;

@property (nonatomic, readonly) NSInteger years;

@end
