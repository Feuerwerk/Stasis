//
//  StasisAsyncServiceWrapper.m
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "StasisAsyncServiceWrapper.h"
#import "RemoteConnection.h"
#import "Kryo.h"
#import "CTBlockDescription.h"
#import "AsyncServiceDelegate.h"
#import <objc/runtime.h>

typedef void(^RawDelegatePtr)();

@interface StasisAsyncServiceWrapper ()

- (id)initWithProtocol:(id)protocol name:(NSString *)serviceName andConnection:(RemoteConnection *)connection;
- (void)writeArgumentAtIndex:(NSUInteger)index ofInvocation:(NSInvocation *)invocation havingType:(const char *)type toList:(JObjectArray *)arguments;
- (NSString *)methodNameFromInvocation:(NSInvocation *)invocation;

@end

@implementation StasisAsyncServiceWrapper

static NSMethodSignature *getMethodSignatureRecursively(Protocol *protocol, SEL selector)
{
	NSMethodSignature *methodSignature = nil;
	struct objc_method_description methodDescription = protocol_getMethodDescription(protocol, selector, YES, YES);

	if (methodDescription.name == NULL)
	{
		unsigned int count = 0;
		Protocol * __unsafe_unretained *protocolList = protocol_copyProtocolList(protocol, &count);
		
		for (unsigned int i = 0; (methodSignature == nil) && i < count; i++)
		{
			methodSignature = getMethodSignatureRecursively(protocolList[i], selector);
		}

		free(protocolList);
	}
	else
	{
		methodSignature = [NSMethodSignature signatureWithObjCTypes:methodDescription.types];
	}

	return methodSignature;
}

+ (id)createProxy:(Protocol *)protocol forService:(NSString *)serviceName usingConnection:(RemoteConnection *)connection
{
	return [[StasisAsyncServiceWrapper alloc] initWithProtocol:protocol name:serviceName andConnection:connection];
}

- (id)initWithProtocol:(id)protocol name:(NSString *)serviceName andConnection:(RemoteConnection *)connection
{
	// Es gibt kein [super init]
	
	if (self != nil)
	{
		_serviceName = serviceName;
		_connection = connection;
		_protocol = protocol;
		_translatedMethodNames = [NSMutableDictionary new];
		_methodSignatures = [NSMutableDictionary new];
	}

	return self;
}

-(void)forwardInvocation:(NSInvocation *)invocation
{
	// Name und Argumente für den Service-Aufruf zusammenstellen
	NSString *name = [self methodNameFromInvocation:invocation];
	NSMethodSignature* signature = [invocation methodSignature];
	NSUInteger argumentCount = [signature numberOfArguments];
	JObjectArray *arguments = [JObjectArray arrayWithCapacity:argumentCount - 3];
	
	for (NSUInteger i = 2; i < argumentCount - 1; i++)
	{
		const char *type = [signature getArgumentTypeAtIndex:i];
		[self writeArgumentAtIndex:i ofInvocation:invocation havingType:type toList:arguments];
	}
	
	// Datentyp des Rückgabe-Handlers auswerten
	__unsafe_unretained RawDelegatePtr delegate = nil;
	[invocation getArgument:&delegate atIndex:argumentCount - 1];

	// Abfrage an Server senden
	if ((_delegate != nil) && [_delegate respondsToSelector:@selector(serviceCallWillBegin)])
	{
		[_delegate serviceCallWillBegin];
	}

	RawDelegatePtr retainedDelegate = delegate;
	[_connection callAsync:name withArguments:arguments returning:^(id result) { [self handleResult:result forDelegate:retainedDelegate];	} error:^(NSError *error) { [self handleError:error]; }];

	if ((_delegate != nil) && [_delegate respondsToSelector:@selector(serviceCallDidBegin)])
	{
		[_delegate serviceCallDidBegin];
	}
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector
{
	if ((aSelector != _cmd) && ![NSStringFromSelector(aSelector) hasPrefix:@"_cf"])
	{
		NSNumber *selectorKey = [NSNumber numberWithInteger:(NSInteger)sel_getName(aSelector)];
		NSMethodSignature *signature = [_methodSignatures objectForKey:selectorKey];

		if (signature == nil)
		{
			signature = getMethodSignatureRecursively(_protocol, aSelector);

			if (signature != nil)
			{
				[_methodSignatures setObject:signature forKey:selectorKey];
			}
		}

		return signature;
	}
	
	return nil;
}

-(BOOL)conformsToProtocol:(Protocol*)aProtocol;
{
	if (_protocol == aProtocol)
	{
		return YES;
	}
	
	return [super conformsToProtocol:aProtocol];
}

-(BOOL)isKindOfClass:(Class)aClass;
{
	return (aClass == [self class]) || (aClass == [NSProxy class]);

}

- (void)writeArgumentAtIndex:(NSUInteger)index ofInvocation:(NSInvocation *)invocation havingType:(const char *)type toList:(JObjectArray *)arguments
{
	if (strcmp(type, @encode(bool)) == 0)
	{
		bool value;
		[invocation getArgument:&value atIndex:index];
		[arguments addObject:[JBoolean boolWithValue:value]];
	}
	else if (strcmp(type, @encode(SInt8)) == 0)
	{
		SInt8 value;
		[invocation getArgument:&value atIndex:index];
		[arguments addObject:[JByte byteWithValue:value]];
	}
	else if (strcmp(type, @encode(SInt16)) == 0)
	{
		SInt16 value;
		[invocation getArgument:&value atIndex:index];
		[arguments addObject:[JShort shortWithValue:value]];
	}
	else if (strcmp(type, @encode(SInt32)) == 0)
	{
		SInt32 value;
		[invocation getArgument:&value atIndex:index];
		[arguments addObject:[JInteger intWithValue:value]];
	}
	else if (strcmp(type, @encode(SInt64)) == 0)
	{
		SInt64 value;
		[invocation getArgument:&value atIndex:index];
		[arguments addObject:[JLong longWithValue:value]];
	}
	else if (strcmp(type, @encode(float)) == 0)
	{
		float value;
		[invocation getArgument:&value atIndex:index];
		[arguments addObject:[JFloat floatWithValue:value]];
	}
	else if (strcmp(type, @encode(double)) == 0)
	{
		double value;
		[invocation getArgument:&value atIndex:index];
		[arguments addObject:[JDouble doubleWithValue:value]];
	}
	else if (strcmp(type, @encode(id)) == 0)
	{
		__unsafe_unretained id value;
		[invocation getArgument:&value atIndex:index];
		
		if (value != nil)
		{
			[arguments addObject:value];
		}
		else
		{
			[arguments addObject:[NSNull null]];
		}
	}
	else
	{
		[NSException raise:NSInvalidArchiveOperationException format:@"Unsupported type %s", type];
	}
}

- (NSString *)methodNameFromInvocation:(NSInvocation *)invocation
{
	NSString *selectorName = NSStringFromSelector([invocation selector]);
	NSString *methodName = [_translatedMethodNames objectForKey:selectorName];
	
	if (methodName == nil)
	{
		NSRange range = [selectorName rangeOfString:@":"];
		
		if (range.location == NSNotFound)
		{
			[NSException raise:NSInvalidArgumentException format:@"Selector \"%@\" is invalid", selectorName];
		}
		
		NSMutableString *nameBuffer = [NSMutableString new];
		
		[nameBuffer appendString:_serviceName];
		[nameBuffer appendString:@"."];
		[nameBuffer appendString:[selectorName substringToIndex:range.location]];

		methodName = [NSString stringWithString:nameBuffer];
		
		[_translatedMethodNames setObject:methodName forKey:selectorName];
	}

	return methodName;
}

- (void)handleResult:(id)result forDelegate:(RawDelegatePtr)delegate
{
	if ((_delegate != nil) && [_delegate respondsToSelector:@selector(serviceCallWillFinish)])
	{
		[_delegate serviceCallWillFinish];
	}

	CTBlockDescription *blockDescription = [[CTBlockDescription alloc] initWithBlock:delegate];
	NSMethodSignature *blockSignature = blockDescription.blockSignature;
	NSUInteger blockArgumentCount = blockSignature.numberOfArguments;

	if (blockArgumentCount == 1)
	{
		void (^voidDelegate)() = delegate;
		voidDelegate();
	}
	else if (blockArgumentCount == 2)
	{
		const char *type = [blockSignature getArgumentTypeAtIndex:1];

		if (strcmp(type, @encode(bool)) == 0)
		{
			void (^boolDelegate)(bool) = (void (^)(bool))delegate;

			if (![result isKindOfClass:[JNumeric class]])
			{
				[NSException raise:NSInvalidArgumentException format:@"Return value is not numeric"];
			}

			JNumeric *numeric = result;
			boolDelegate(numeric.boolValue);
		}
		else if (strcmp(type, @encode(SInt8)) == 0)
		{
			void (^byteDelegate)(SInt8) = (void (^)(SInt8))delegate;

			if (![result isKindOfClass:[JNumeric class]])
			{
				[NSException raise:NSInvalidArgumentException format:@"Return value is not numeric"];
			}

			JNumeric *numeric = result;
			byteDelegate(numeric.byteValue);
		}
		else if (strcmp(type, @encode(SInt16)) == 0)
		{
			void (^shortDelegate)(SInt16) = (void (^)(SInt16))delegate;

			if (![result isKindOfClass:[JNumeric class]])
			{
				[NSException raise:NSInvalidArgumentException format:@"Return value is not numeric"];
			}

			JNumeric *numeric = result;
			shortDelegate(numeric.shortValue);
		}
		else if (strcmp(type, @encode(SInt32)) == 0)
		{
			void (^intDelegate)(SInt32) = (void (^)(SInt32))delegate;

			if (![result isKindOfClass:[JNumeric class]])
			{
				[NSException raise:NSInvalidArgumentException format:@"Return value is not numeric"];
			}

			JNumeric *numeric = result;
			intDelegate(numeric.intValue);
		}
		else if (strcmp(type, @encode(SInt64)) == 0)
		{
			void (^longDelegate)(SInt64) = (void (^)(SInt64))delegate;

			if (![result isKindOfClass:[JNumeric class]])
			{
				[NSException raise:NSInvalidArgumentException format:@"Return value is not numeric"];
			}

			JNumeric *numeric = result;
			longDelegate(numeric.longValue);
		}
		else if (strcmp(type, @encode(float)) == 0)
		{
			void (^floatDelegate)(float) = (void (^)(float))delegate;

			if (![result isKindOfClass:[JNumeric class]])
			{
				[NSException raise:NSInvalidArgumentException format:@"Return value is not numeric"];
			}

			JNumeric *numeric = result;
			floatDelegate(numeric.floatValue);
		}
		else if (strcmp(type, @encode(double)) == 0)
		{
			void (^doubleDelegate)(double) = (void (^)(double))delegate;

			if (![result isKindOfClass:[JNumeric class]])
			{
				[NSException raise:NSInvalidArgumentException format:@"Return value is not numeric"];
			}

			JNumeric *numeric = result;
			doubleDelegate(numeric.doubleValue);
		}
		else if (strcmp(type, @encode(id)) == 0)
		{
			void (^objectDelegate)(id) = (void (^)(id))delegate;
			objectDelegate(result);
		}
		else if (type[0] == '@')
		{
			void (^objectDelegate)(id) = (void (^)(id))delegate;
			objectDelegate(result);
		}
		else
		{
			[NSException raise:NSInvalidArchiveOperationException format:@"Unsupported type %s", type];
		}
	}
	else
	{
		[NSException raise:NSInvalidArgumentException format:@"Block must have one parameter or no parameter at all"];
	}

	if ((_delegate != nil) && [_delegate respondsToSelector:@selector(serviceCallDidFinish)])
	{
		[_delegate serviceCallDidFinish];
	}
}

- (void)handleError:(NSError *)error
{
	NSLog(@"Error received: %@", error.localizedDescription);

	if (_delegate != nil)
	{
		if ([_delegate respondsToSelector:@selector(serviceCallWillFinish)])
		{
			[_delegate serviceCallWillFinish];
		}

		if ([_delegate respondsToSelector:@selector(serviceCallFailed:)])
		{
			[_delegate serviceCallFailed:error];
		}

		if ([_delegate respondsToSelector:@selector(serviceCallDidFinish)])
		{
			[_delegate serviceCallDidFinish];
		}
	}
}

@end
