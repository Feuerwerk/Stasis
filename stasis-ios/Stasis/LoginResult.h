//
//  LoginResult.h
//  Stasis
//
//  Created by Christian Fruth on 24.10.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Kryo/SerializationAnnotation.h"

@class AuthenticationResult;

@interface LoginResult : NSObject<SerializationAnnotation>

@property (nonatomic, strong) AuthenticationResult *authenticationResult;
@property (nonatomic, strong) NSDictionary *loginResponse;

@end
