//
//  HTTPCookieStorage.m
//  Stasis
//
//  Created by Christian Fruth on 23.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "HTTPCookieStorage.h"

@interface HTTPCookieStorage() <NSCoding>

/*
 Cookie storage is stored in the order of domain -> path -> name
 This one stores cookies that are subdomain specific
 */
@property (nonatomic, strong, readonly) NSMutableDictionary *subdomainCookies;

/*
 Cookie storage is stored in the order of domain -> path -> name
 This one stores cookies global for a domain.
 */
@property (nonatomic, strong, readonly) NSMutableDictionary *domainGlobalCookies;

@end


@implementation HTTPCookieStorage

@synthesize subdomainCookies = _subdomainCookies;
@synthesize domainGlobalCookies = _domainGlobalCookies;

void findCookies(NSString *cookiePath, NSString *domainKey, NSDictionary* domainStorage, NSMutableArray *resultCookies)
{
	NSMutableDictionary *pathStorage = [domainStorage objectForKey:domainKey];
	
	if (pathStorage == nil)
	{
		return;
	}
	
	for (NSString *path in pathStorage)
	{
		if ([path isEqualToString:@"/"] || [cookiePath hasPrefix:path])
		{
			NSMutableDictionary *nameStorage = [pathStorage objectForKey:path];
			[resultCookies addObjectsFromArray:[nameStorage allValues]];
		}
	}
}


- (void)setCookie:(NSHTTPCookie *)aCookie
{
	// only domain names are case insensitive
	NSString *domain = [[aCookie domain] lowercaseString];
	NSString *path = [aCookie path];
	NSString *name = [aCookie name];
	NSMutableDictionary *domainStorage = [domain hasPrefix:@"."] ? self.domainGlobalCookies : self.subdomainCookies;
	NSMutableDictionary *pathStorage = [domainStorage objectForKey:domain];

	if (pathStorage == nil)
	{
		pathStorage = [NSMutableDictionary new];
		[domainStorage setObject:pathStorage forKey:domain];
	}

	NSMutableDictionary *nameStorage = [pathStorage objectForKey:path];

	if (nameStorage == nil)
	{
		nameStorage = [NSMutableDictionary new];
		[pathStorage setObject:nameStorage forKey:path];
	}

	[nameStorage setObject:aCookie forKey:name];
}


- (NSArray *)cookiesForURL:(NSURL *)theURL
{
	NSMutableArray *resultCookies = [NSMutableArray new];
	NSString *cookiePath = [theURL path];
	NSString *cookieDomain = [[theURL host] lowercaseString];

	findCookies(cookiePath, cookieDomain, self.subdomainCookies, resultCookies);

	// delete the fist subdomain
	NSRange range = [cookieDomain rangeOfString:@"."];

	if (range.location != NSNotFound)
	{
		NSString *globalDomain = [cookieDomain substringFromIndex:range.location];
		findCookies(cookiePath, globalDomain, self.domainGlobalCookies, resultCookies);
	}
	
	return resultCookies;
}


- (void)loadCookies:(id)cookies
{
	for (NSHTTPCookie *cookie in cookies)
	{
		[self setCookie:cookie];
	}
}


- (void)handleCookiesInRequest:(NSMutableURLRequest *)request
{
	NSURL *url = request.URL;
	NSArray *cookies = [self cookiesForURL:url];
	NSDictionary *headers = [NSHTTPCookie requestHeaderFieldsWithCookies:cookies];
	NSUInteger count = [headers count];
	__unsafe_unretained id keys[count];
	__unsafe_unretained id values[count];

	[headers getObjects:values andKeys:keys];
    
	for (NSUInteger i = 0; i < count; ++i)
	{
		[request setValue:values[i] forHTTPHeaderField:keys[i]];
	}
}

- (void)handleCookiesInResponse:(NSHTTPURLResponse *)response
{
	NSURL *url = response.URL;
	NSArray *cookies = [NSHTTPCookie cookiesWithResponseHeaderFields:response.allHeaderFields forURL:url];
	
	//[self loadCookies:cookies];
}

#pragma mark Property Access

- (NSMutableDictionary *)subdomainCookies
{
	if (!_subdomainCookies)
	{
		_subdomainCookies = [NSMutableDictionary new];
	}

	return _subdomainCookies;
}


-(NSMutableDictionary *)domainGlobalCookies
{
	if (_domainGlobalCookies == nil)
	{
		_domainGlobalCookies = [NSMutableDictionary new];
	}

	return _domainGlobalCookies;
}


- (void)reset
{
	[self.subdomainCookies removeAllObjects];
	[self.domainGlobalCookies removeAllObjects];
}

#pragma mark NSCoding

- (id)initWithCoder:(NSCoder *)aDecoder
{
	self = [self init];
	
	if (self != nil)
	{
		_domainGlobalCookies = [aDecoder decodeObjectForKey:@"domainGlobalCookies"];
		_subdomainCookies = [aDecoder decodeObjectForKey:@"subdomainCookies"];
	}

	return self;
}


- (void)encodeWithCoder:(NSCoder *)aCoder
{
	if (_domainGlobalCookies != nil)
	{
		[aCoder encodeObject:_domainGlobalCookies forKey:@"domainGlobalCookies"];
	}

	if (_subdomainCookies != nil)
	{
		[aCoder encodeObject:_subdomainCookies forKey:@"subdomainCookies"];
	}
}

#pragma mark NSCopying

- (id)copyWithZone:(NSZone *)zone
{
	HTTPCookieStorage *copy = [[[self class] allocWithZone:zone] init];

	if (copy != nil)
	{
		copy->_subdomainCookies = [self.subdomainCookies mutableCopy];
		copy->_domainGlobalCookies = [self.domainGlobalCookies mutableCopy];
	}

	return copy;
}

@end


@implementation NSHTTPCookie (HTTPCookieStorage)

- (id)initWithCoder:(NSCoder *)aDecoder
{
	NSDictionary* cookieProperties = [aDecoder decodeObjectForKey:@"cookieProperties"];
	
	if (![cookieProperties isKindOfClass:[NSDictionary class]])
	{
		// cookies are always immutable, so there's no point to return anything here if its properties cannot be found.
		return nil;
	}

	self = [self initWithProperties:cookieProperties];
	
	return self;
}


- (void)encodeWithCoder:(NSCoder *)aCoder
{
	NSDictionary* cookieProperties = self.properties;

	if (cookieProperties != nil)
	{
		[aCoder encodeObject:cookieProperties forKey:@"cookieProperties"];
	}
}

@end
