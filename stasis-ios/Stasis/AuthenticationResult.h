//
//  AuthenticationResult.h
//  Stasis
//
//  Created by Christian Fruth on 17.08.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Enum.h"
#import "SerializationAnnotation.h"

@interface AuthenticationResult : Enum<SerializationAnnotation>

+ (instancetype)UNAUTHENTICATED;
+ (instancetype)AUTHENTICATED;
+ (instancetype)VERSIONMISSMATCH;

@end
