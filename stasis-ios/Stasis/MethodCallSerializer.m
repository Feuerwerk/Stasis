//
//  MethodCallSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "MethodCallSerializer.h"
#import "MethodCall.h"
#import "Kryo/Kryo.h"

@implementation MethodCallSerializer

- (BOOL)acceptsNull
{
	return NO;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	MethodCall *methodCall = value;
	NSArray *args = methodCall.args;
	NSUInteger argCount = args.count;
	
	[output writeString:methodCall.name];
	[output writeBoolean:methodCall.assumeAuthenticated];
	[output writeInt:(SInt32)argCount optimizePositive:YES];
	
	for (NSUInteger i = 0; i < argCount; ++i)
	{
		[kryo writeClassAndObject:args[i] to:output];
	}
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	
	MethodCall *methodCall = [MethodCall new];
	[kryo reference:methodCall];
	
	NSString *name = [input readString];
	bool assumeAuthenticated = [input readBoolean];
	int argCount = [input readIntOptimizePositive:YES];
	NSMutableArray *args = [NSMutableArray arrayWithCapacity:argCount];
	
	for (int i = 0; i < argCount; ++i)
	{
		[args addObject:[kryo readClassAndObject:input]];
	}
	
	methodCall.name = name;
	methodCall.assumeAuthenticated = assumeAuthenticated;
	methodCall.args = args;
	
	return methodCall;
}

@end
