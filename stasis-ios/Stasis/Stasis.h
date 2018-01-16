//
//  Stasis.h
//  Stasis
//
//  Created by Christian Fruth on 06.03.13.
//  Copyright (c) 2013 Boxx IT Solutions e.K. All rights reserved.
//

#import "Kryo/Kryo.h"
#import "RemoteConnection.h"
#import "SerializableException.h"
#import "MessageException.h"
#import "ExceptionError.h"
#import "StasisAsyncServiceWrapper.h"

#define VERSION_NUMBER_KEY @"version.number"
#define VERSION_MISSMATCH_KEY @"version.missmatch"
#define REDIRECT_PATH_KEY @"redirect.path"
#define SERIALIZER_HINT_KEY @"serializer.hint"
