//
//  HandshakeHandler.h
//  Stasis
//
//  Created by Christian Fruth on 26.07.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RemoteConnection;

@protocol HandshakeHandler <NSObject>

- (BOOL)handleResponse:(NSURLResponse *)response withData:(NSData *)data forConnection:(RemoteConnection *)connection tryCount:(int)tryCount returning:(void (^)(NSError *))resultHandler;

@end
