//
//  LocalDateSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 08.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalDateSerializer.h"
#import "LocalDate.h"
#import "Kryo.h"

@implementation LocalDateSerializer

static NSTimeZone *gmtTimeZone;

__attribute__((constructor)) static void initialize()
{
	gmtTimeZone = [NSTimeZone timeZoneWithName:@"GMT"];
}

- (BOOL)acceptsNull
{
	return YES;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	LocalDate *date = value;
	NSInteger gmtSec = [gmtTimeZone secondsFromGMTForDate:date.date];
	NSInteger locSec = [[NSTimeZone localTimeZone] secondsFromGMTForDate:date.date];
	NSTimeInterval secDelta = locSec - gmtSec;
	UInt64 millis = (date.date.timeIntervalSince1970 + secDelta) * 1000;

	[output writeULong:millis];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	// Zeit in GMT in lokale Zeitzone wandeln
	UInt64 millis = [input readULong];
	NSTimeInterval secondsSince1970 = millis / 1000;
	NSDate *aDate = [NSDate dateWithTimeIntervalSince1970:secondsSince1970];
	
	NSInteger gmtSec = [gmtTimeZone secondsFromGMTForDate:aDate];
	NSInteger locSec = [[NSTimeZone localTimeZone] secondsFromGMTForDate:aDate];
	NSTimeInterval secDelta = gmtSec - locSec;
	
	NSDate *bDate = [[NSDate alloc] initWithTimeInterval:secDelta sinceDate:aDate];
	
	return [LocalDate dateFromDate:bDate];
}

- (NSString *)getClassName:(Class)type
{
	return @"org.joda.time.LocalDate";
}

- (BOOL)isFinal:(Class)type
{
	return YES;
}

@end
