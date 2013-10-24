//
//  Seconds.h
//  JodaTime
//
//  Created by Christian Fruth on 24.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalDateTime;

@interface Seconds : NSObject

+ (instancetype)secondsBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate;

@property (nonatomic, readonly) NSInteger seconds;

@end
