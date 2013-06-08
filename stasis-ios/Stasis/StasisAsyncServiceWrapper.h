//
//  StasisAsyncServiceWrapper.h
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void(^ErrorHandler)(NSError *error);

@class RemoteConnection;

@interface StasisAsyncServiceWrapper : NSProxy
{
	NSString *_serviceName;
	NSMutableDictionary *_translatedMethodNames;
	NSMutableDictionary *_methodSignatures;
	RemoteConnection *_connection;
	Protocol *_protocol;
}

@property (nonatomic, copy) ErrorHandler defaultErrorHandler;

+ (id)createProxy:(id)protocol forService:(NSString *)serviceName usingConnection:(RemoteConnection *)connection;

-(BOOL)conformsToProtocol:(Protocol*)aProtocol;
-(BOOL)isKindOfClass:(Class)aClass;
-(void)forwardInvocation:(NSInvocation *)invocation;
- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector;

@end
