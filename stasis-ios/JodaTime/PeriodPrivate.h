//
//  Period+Private.h
//  JodaTime
//
//  Created by Christian Fruth on 24.11.14.
//  Copyright (c) 2014 Boxx IT Solutions GmbH. All rights reserved.
//

@class NSDateComponents;

@interface Period (Private)

- (void)fill:(NSDateComponents *)components;

@end
