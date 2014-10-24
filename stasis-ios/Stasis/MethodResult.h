//
//  MethodResult.h
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Kryo/SerializationAnnotation.h"

typedef enum { MethodResultVoid = 0, MethodResultValue = 1, MethodResultException = 2 } MethodResultType;

@interface MethodResult : NSObject<SerializationAnnotation>

@property (nonatomic, assign) MethodResultType type;
@property (nonatomic, strong) NSObject *result;

@end
