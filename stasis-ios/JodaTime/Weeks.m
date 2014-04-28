//
//  Weeks.m
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Weeks.h"
#import "LocalDate.h"

@implementation Weeks

+ (instancetype)weeksBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSWeekCalendarUnit fromDate:startDate.date toDate:stopDate.date options:0];
	Weeks *newWeeks = [Weeks new];
	newWeeks->_weeks = components.week;
	return newWeeks;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_weeks];
}

@end
