//
//  Minutes.m
//  JodaTime
//
//  Created by Christian Fruth on 17.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Minutes.h"
#import "LocalDateTime.h"
#import "LocalTime.h"

@implementation Minutes

+ (instancetype)minutesBetween:(LocalDateTime *)startDate and:(LocalDateTime *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSMinuteCalendarUnit fromDate:startDate.date toDate:stopDate.date options:0];
	Minutes *newMinutes = [Minutes new];
	newMinutes->_minutes = components.minute;
	return newMinutes;
}

+ (instancetype)minutesOfTimeBetween:(LocalTime *)startTime and:(LocalTime *)stopTime
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSMinuteCalendarUnit fromDate:startTime.date toDate:startTime.date options:0];
	Minutes *newMinutes = [Minutes new];
	newMinutes->_minutes = components.minute;
	return newMinutes;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_minutes];
}

@end
