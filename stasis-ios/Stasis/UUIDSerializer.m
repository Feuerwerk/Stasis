//
// Created by Christian Fruth on 08.05.14.
// Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import "UUIDSerializer.h"
#import "KryoInput.h"
#import "KryoOutput.h"

@implementation UUIDSerializer

typedef union
{
	struct
	{
		SInt64 mostSignificant;
		SInt64 leastSignificant;
	} bits;
	uuid_t bytes;
} raw_uuid;

- (BOOL)acceptsNull
{
	return YES;
}

- (BOOL)isFinal:(Class)type
{
	return YES;
}

- (void)write:(Kryo *)kryo value:(id)value to:(KryoOutput *)output
{
	NSUUID *uuid = value;
	raw_uuid u;

	[uuid getUUIDBytes:u.bytes];
	[output writeLong:OSSwapInt64(u.bits.mostSignificant)];
	[output writeLong:OSSwapInt64(u.bits.leastSignificant)];
}

- (id)read:(Kryo *)kryo withClass:(Class)type from:(KryoInput *)input
{
	raw_uuid u;
	SInt64 mostSignificant = [input readLong];
	SInt64 leastSignificant = [input readLong];

	u.bits.mostSignificant = OSSwapInt64(mostSignificant);
	u.bits.leastSignificant = OSSwapInt64(leastSignificant);

	return [[NSUUID alloc] initWithUUIDBytes:u.bytes];
}

- (NSString *)getClassName:(Class)type
{
	return @"java.util.UUID";
}

@end