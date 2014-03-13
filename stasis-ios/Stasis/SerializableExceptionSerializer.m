//
//  SerializableExceptionSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "SerializableExceptionSerializer.h"
#import "SerializableException.h"
#import "Kryo.h"

@implementation SerializableExceptionSerializer

- (BOOL)acceptsNull
{
	return NO;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	[NSException raise:NSInternalInconsistencyException format:@"%@ is an unsupported operation", NSStringFromSelector(_cmd)];
}

- (id)read:(Kryo *)kryo withClass:(Class)clazz from:(KryoInput *)input
{
	NSString *type = [input readString];
	NSString *message = [input readString];
	SInt32 stackLength = [input readIntOptimizePositive:YES];
	NSMutableArray *stackSymbols = [NSMutableArray arrayWithCapacity:stackLength];
	
	for (SInt32 i = 0; i < stackLength; ++i)
	{
		NSString *className = [input readString];
		NSString *methodName = [input readString];
		NSString *fileName = [input readString];
		SInt32 lineNumber = [input readIntOptimizePositive:YES];
		NSString *stackSymbol = [NSString stringWithFormat:@"%@.%@(%@:%d)", className, methodName, fileName, (int)lineNumber];
		
		[stackSymbols addObject:stackSymbol];
	}

	return [SerializableException exceptionWithType:type message:message andStackSymbols:stackSymbols];
}

@end
