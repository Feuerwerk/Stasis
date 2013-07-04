//
//  ExceptionError.h
//  Stasis
//
//  Created by Christian Fruth on 07.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ExceptionError : NSError

@property (nonatomic, readonly) NSException *exception;

+ (instancetype)errorWithException:(NSException *)exception;
- (id)initWithException:(NSException *)exception;

@end
