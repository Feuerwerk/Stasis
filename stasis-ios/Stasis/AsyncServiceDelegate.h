//
// Created by Christian Fruth on 21.05.14.
// Copyright (c) 2014 MEP24web Software GmbH. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol AsyncServiceDelegate <NSObject>
@optional

- (void)serviceCallWillBegin;
- (void)serviceCallDidBegin;
- (void)serviceCallWillFinish;
- (void)serviceCallDidFinish;
- (void)serviceCallFailed:(NSError *)error;

@end