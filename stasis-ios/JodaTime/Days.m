//
//  Days.m
//  JodaTime
//
//  Created by Christian Fruth on 30.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Days.h"
#import "LocalDate.h"

@implementation Days

+ (instancetype)daysBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSDayCalendarUnit fromDate:startDate.date toDate:stopDate.date options:0];
	Days *newDays = [Days new];
	newDays->_days = components.day;
	return newDays;
}

@end
