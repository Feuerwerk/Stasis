//
//  StasisError.h
//  Stasis
//
//  Created by Christian Fruth on 25.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface StasisError : NSError

- (id)initWithDomain:(NSString *)domain code:(NSInteger)code userInfo:(NSDictionary *)dict;
+ (instancetype)errorWithDomain:(NSString *)domain code:(NSInteger)code userInfo:(NSDictionary *)dict;

@end
