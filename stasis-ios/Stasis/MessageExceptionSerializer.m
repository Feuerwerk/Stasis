//
//  SerializableExceptionSerializer.m
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "MessageExceptionSerializer.h"
#import "MessageException.h"
#import "Kryo.h"

@implementation MessageExceptionSerializer

- (BOOL)acceptsNull
{
	return NO;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	MessageException *exception = value;
	[kryo writeNullableObject:exception.name withClass:[NSString class] to:output];
	[kryo writeNullableObject:exception.reason withClass:[NSString class] to:output];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	NSString *ident = [kryo readNullableObject:input ofClass:[NSString class]];
	NSString *message = [kryo readNullableObject:input ofClass:[NSString class]];
	return [MessageException exceptionWithIdent:ident andMessage:message];
}

@end
