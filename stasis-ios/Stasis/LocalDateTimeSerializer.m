//
//  LocalDateTimeSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 10.04.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalDateTimeSerializer.h"
#import "../JodaTime/LocalDateTime.h"
#import "Kryo.h"

@implementation LocalDateTimeSerializer

static NSTimeZone *gmtTimeZone;

__attribute__((constructor)) static void initialize()
{
	gmtTimeZone = [NSTimeZone timeZoneWithName:@"GMT"];
}

- (BOOL)acceptsNull
{
	return NO;
}

- (BOOL)isFinal:(Class)type
{
	return YES;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	LocalDateTime *date = value;
	NSInteger gmtSec = [gmtTimeZone secondsFromGMTForDate:date.value];
	NSInteger locSec = [[NSTimeZone localTimeZone] secondsFromGMTForDate:date.value];
	NSTimeInterval secDelta = locSec - gmtSec;
	UInt64 millis = (date.value.timeIntervalSince1970 + secDelta) * 1000;
	
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
	
	return [LocalDateTime dateFromDate:bDate];
}

- (NSString *)getClassName:(Class)type
{
	return @"org.joda.time.LocalDateTime";
}

@end
