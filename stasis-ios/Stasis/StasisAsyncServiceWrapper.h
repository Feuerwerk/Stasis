//
//  StasisAsyncServiceWrapper.h
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RemoteConnection;
@protocol AsyncServiceDelegate;

@interface StasisAsyncServiceWrapper : NSProxy
{
	NSString *_serviceName;
	NSMutableDictionary *_translatedMethodNames;
	NSMutableDictionary *_methodSignatures;
	RemoteConnection *_connection;
	Protocol *_protocol;
}

@property (nonatomic, weak) id<AsyncServiceDelegate> delegate;

+ (id)createProxy:(Protocol *)protocol forService:(NSString *)serviceName usingConnection:(RemoteConnection *)connection;
+ (id)createProxy:(Protocol *)protocol forService:(NSString *)serviceName usingConnection:(RemoteConnection *)connection withDelegate:(id<AsyncServiceDelegate>)delegate;

-(BOOL)conformsToProtocol:(Protocol*)aProtocol;
-(BOOL)isKindOfClass:(Class)aClass;
-(void)forwardInvocation:(NSInvocation *)invocation;
- (NSMethodSignature *)methodSignatureForSelector:(SEL)aSelector;

@end
