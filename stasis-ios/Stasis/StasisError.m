//
//  StasisError.m
//  Stasis
//
//  Created by Christian Fruth on 25.10.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "StasisError.h"

@implementation StasisError

- (id)initWithDomain:(NSString *)domain code:(NSInteger)code userInfo:(NSDictionary *)dict
{
	self = [super initWithDomain:domain code:code userInfo:dict];
	return self;
}


+ (instancetype)errorWithDomain:(NSString *)domain code:(NSInteger)code userInfo:(NSDictionary *)dict
{
	return [[StasisError alloc] initWithDomain:domain code:code userInfo:dict];
}


- (NSString *)localizedDescription
{
	NSString *key = [NSString stringWithFormat:@"%@#%d", self.domain, self.code];
	NSBundle *bundle = [NSBundle bundleWithURL:[[NSBundle mainBundle] URLForResource:@"StasisResources" withExtension:@"bundle"]];
	NSString *localizedDescription = [bundle localizedStringForKey:key value:@"" table:@"StasisErrors"];
	
	if (localizedDescription != nil)
	{
		return localizedDescription;
	}
	
	return super.localizedDescription;
}

@end
