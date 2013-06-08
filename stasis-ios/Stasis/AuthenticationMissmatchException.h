//
//  AuthenticationMissmatchException.h
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SerializationAnnotation.h"

@interface AuthenticationMissmatchException : NSException<SerializationAnnotation>

+ (instancetype)exceptionWithAuthenticated:(BOOL) authenticated;
- (id)initWithAuthenticated:(BOOL) authenticated;

@property (nonatomic, readonly) BOOL authenticated;

@end
