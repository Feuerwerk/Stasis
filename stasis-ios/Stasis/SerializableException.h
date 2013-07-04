//
//  SerializableException.h
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SerializationAnnotation.h"

@interface SerializableException : NSException<SerializationAnnotation>

+ (instancetype)exceptionWithIdent:(NSString *)ident andMessage:(NSString *)message;
- (id)initWithIdent:(NSString *)ident andMessage:(NSString *)message;

@end
