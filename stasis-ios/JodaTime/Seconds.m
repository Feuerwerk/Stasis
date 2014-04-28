//
//  Seconds.m
//  JodaTime
//
//  Created by Christian Fruth on 24.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Seconds.h"
#import "LocalDateTime.h"

@implementation Seconds

+ (instancetype)secondsBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSSecondCalendarUnit fromDate:startDate.date toDate:stopDate.date options:0];
	Seconds *newSeconds = [Seconds new];
	newSeconds->_seconds = components.second;
	return newSeconds;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_seconds];
}

@end
