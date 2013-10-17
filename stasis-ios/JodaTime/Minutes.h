//
//  Minutes.h
//  JodaTime
//
//  Created by Christian Fruth on 17.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalDateTime;

@interface Minutes : NSObject

+ (instancetype)minutesBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate;

@property (nonatomic, readonly) NSInteger minutes;

@end
