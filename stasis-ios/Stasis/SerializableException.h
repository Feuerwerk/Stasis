//
//  SerializableException.h
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Kryo/SerializationAnnotation.h"

@interface SerializableException : NSException<SerializationAnnotation>
{
	NSArray *_stackSymbols;
}

+ (instancetype)exceptionWithType:(NSString *)type message:(NSString *)message andStackSymbols:(NSArray *)stackSymbols;
- (id)initWithType:(NSString *)type message:(NSString *)message andStackSymbols:(NSArray *)stackSymbols;

@end
