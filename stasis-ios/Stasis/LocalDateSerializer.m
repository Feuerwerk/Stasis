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

- (BOOL)acceptsNull
{
	return YES;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	LocalDate *date = value;
	[output writeULong:date.millis];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	UInt64 millis = [input readULong];
	return [LocalDate dateFromMillis:millis];
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
