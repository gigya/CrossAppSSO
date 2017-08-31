//
//  KeychainHelper.h
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 31/08/2017.
//  Copyright © 2017 Gheerish Bansoodeb. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface KeychainHelper : NSObject
- (void)saveInKeychain:(NSData *)data;
- (NSDictionary *)retrieveFromKeychain;
- (void)deleteFromKeychain;
@end
