//
//  LocalTimeSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 25.06.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "LocalTimeSerializer.h"
#import "../JodaTime/LocalTime.h"
#import "KryoInput.h"
#import "KryoOutput.h"

@implementation LocalTimeSerializer

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
	LocalTime *time = value;
	[output writeULong:time.millisOfDay];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	UInt64 millisOfDay = [input readULong];
	return [LocalTime timeFromMillisOfDay:millisOfDay];
}

- (NSString *)getClassName:(Class)type
{
	return @"org.joda.time.LocalTime";
}

@end
