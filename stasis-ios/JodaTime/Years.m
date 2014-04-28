//
//  Years.m
//  JodaTime
//
//  Created by Christian Fruth on 28.04.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "Years.h"
#import "LocalDate.h"

@implementation Years

+ (instancetype)yearsBetween:(LocalDate *)startDate and:(LocalDate *)stopDate
{
	NSCalendar *calendar = [NSCalendar currentCalendar];
	NSDateComponents *components = [calendar components:NSYearCalendarUnit fromDate:startDate.date toDate:stopDate.date options:0];
	Years *newYears = [Years new];
	newYears->_years = components.year;
	return newYears;
}

- (NSString *)description
{
	return [NSString stringWithFormat:@"%li", (long)_years];
}

@end
