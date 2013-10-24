//
//  HTTPCookieStorage.h
//  Stasis
//
//  Created by Christian Fruth on 23.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface HTTPCookieStorage : NSObject<NSCoding, NSCopying>

- (NSArray *)cookiesForURL:(NSURL *)theURL;
- (void)setCookie:(NSHTTPCookie *)aCookie;

/**
 Removes all stored cookies from this storage
 */

-(void)reset;

-(void)loadCookies:(id)cookies;
-(void)handleCookiesInRequest:(NSMutableURLRequest *)request;
-(void)handleCookiesInResponse:(NSHTTPURLResponse *)response;

@end


@interface HTTPCookieStorage (HTTPCookieStorage) <NSCoding>

@end