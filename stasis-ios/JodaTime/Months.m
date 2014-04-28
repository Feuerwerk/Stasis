//
//  Months.m
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Months.h"
#import "LocalDate.h"

@implementation Months

+ (instancetype)monthsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSMonthCalendarUnit fromDate:startDate.date toDate:stopDate.date options:0];
	Months *newMonths = [Months new];
	newMonths->_months = components.month;
	return newMonths;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_months];
}

@end
