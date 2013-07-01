//
//  LocalDateTimeSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 10.04.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalDateTimeSerializer.h"
#import "LocalDateTime.h"
#import "Kryo.h"

@implementation LocalDateTimeSerializer

- (BOOL)acceptsNull
{
	return YES;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	LocalDateTime *date = value;
	UInt64 millis = date.millis + NSTimeZone.defaultTimeZone.secondsFromGMT * 1000;

	[output writeULong:millis];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	UInt64 millis = [input readULong] - NSTimeZone.defaultTimeZone.secondsFromGMT * 1000;

	return [LocalDateTime dateFromMillis:millis];
}

- (NSString *)getClassName:(Class)type
{
	return @"org.joda.time.LocalDateTime";
}

- (BOOL)isFinal:(Class)type
{
	return YES;
}

@end
