//
//  MethodCall.h
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Kryo/SerializationAnnotation.h"

@class JObjectArray;

@interface MethodCall : NSObject<SerializationAnnotation>

@property (nonatomic, copy) NSString *name;
@property (nonatomic, assign) BOOL assumeAuthenticated;
@property (nonatomic, strong) NSArray *args;

+ (instancetype)methodCall:(NSString *)name withArguments:(NSArray *)args assumingAuthenticated:(BOOL)assumeAuthenticated;

@end
