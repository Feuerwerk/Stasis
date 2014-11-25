//
//  MinutesSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 24.11.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "MinutesSerializer.h"
#import "Minutes.h"
#import "Kryo.h"
#import "KryoInput.h"
#import "KryoOutput.h"

@implementation MinutesSerializer

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
	Minutes *minutes = value;
	[output writeInt:(SInt32)minutes.minutes optimizePositive:YES];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	SInt32 minutes = [input readIntOptimizePositive:YES];
	Minutes *gurke = [Minutes minutes:minutes];
	[kryo reference:gurke];
	return gurke;
}

- (NSString *)getClassName:(Class)type
{
	return @"org.joda.time.Minutes";
}

@end
