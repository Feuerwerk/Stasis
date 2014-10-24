//
//  MethodResultSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "MethodResultSerializer.h"
#import "MethodResult.h"
#import "Kryo/Kryo.h"

@implementation MethodResultSerializer

- (BOOL)acceptsNull
{
	return NO;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	MethodResult *methodResult = value;
	
	[output writeInt:(SInt32)methodResult.type optimizePositive:YES];
	
	switch (methodResult.type)
	{
		case MethodResultVoid:
			break;
			
		case MethodResultValue:
		case MethodResultException:
			[kryo writeClassAndObject:methodResult.result to:output];
			break;
	}
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	
	MethodResult *methodResult = [MethodResult new];
	[kryo reference:methodResult];
	
	MethodResultType resultType = (MethodResultType)[input readIntOptimizePositive:YES];
	NSObject *result = nil;
	
	switch (resultType)
	{
		case MethodResultVoid:
			break;
			
		case MethodResultValue:
		case MethodResultException:
			result = [kryo readClassAndObject:input];
			break;
	}
	
	methodResult.type = resultType;
	methodResult.result = result;
	
	return methodResult;
}

@end
