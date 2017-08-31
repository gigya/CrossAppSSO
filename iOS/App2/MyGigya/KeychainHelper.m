//
//  KeychainHelper.m
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 31/08/2017.
//  Copyright © 2017 Gheerish Bansoodeb. All rights reserved.
//

#import "KeychainHelper.h"

// replace this with your app's AppId.KeychainGroup [see Keychain sharing in Capabilities]
const NSString *ACCESSGROUP = @"T48CNFXFQH.GigyaSSO";
const NSString *KEY = @"GigyaSSO";
const NSString *SERVICE = @"GigyaSDK";

@implementation KeychainHelper

- (void)saveInKeychain:(NSData *)data {

  // NOTE: keychain access group is defined in Capabilities in Project settings
  // - only apps in the same group can use the below

  NSDictionary *secItem = @{
    (__bridge id)kSecClass : (__bridge id)kSecClassGenericPassword,
    (__bridge id)kSecAttrService : (NSString *)SERVICE,
    (__bridge id)kSecAttrAccount : (NSString *)KEY,
    (__bridge id)kSecAttrAccessGroup : (NSString *)ACCESSGROUP,
    (__bridge id)
    kSecAttrAccessible : (__bridge id)kSecAttrAccessibleWhenUnlocked,
    (__bridge id)kSecValueData : data,
  };

  SecItemDelete((CFDictionaryRef)secItem);

  CFTypeRef result = NULL;
  OSStatus status = SecItemAdd((__bridge CFDictionaryRef)secItem, &result);
  if (status == errSecSuccess) {
    NSLog(@"value saved");
  } else {
    NSLog(@"error: %ld", (long)status);
  }
}

- (NSDictionary *)retrieveFromKeychain {
  NSString *keyToSearchFor = @"GigyaSSO";
  NSString *service = @"GigyaSDK";

  NSDictionary *query = @{
    (__bridge id)kSecClass : (__bridge id)kSecClassGenericPassword,
    (__bridge id)kSecAttrService : service,
    (__bridge id)kSecAttrAccount : keyToSearchFor,
    (__bridge id)kSecReturnData : (__bridge id)kCFBooleanTrue,
  };

  CFTypeRef result = NULL;

  OSStatus results =
      SecItemCopyMatching((__bridge CFDictionaryRef)query, &result);

  NSData *data = (__bridge_transfer NSData *)result;

  if (results == errSecSuccess) {

    NSKeyedUnarchiver *unarchiver =
        [[NSKeyedUnarchiver alloc] initForReadingWithData:data];
    NSDictionary *dataDic = [unarchiver decodeObjectForKey:@"GSSession"];
    [unarchiver finishDecoding];
    return dataDic;

  } else {
    NSLog(@"error: %ld", (long)results);
    return nil;
  }
}

- (void)deleteFromKeychain {
  NSDictionary *secItem = @{
    (__bridge id)kSecClass : (__bridge id)kSecClassGenericPassword,
    (__bridge id)kSecAttrService : (NSString *)SERVICE,
    (__bridge id)kSecAttrAccount : (NSString *)KEY,
    (__bridge id)kSecAttrAccessGroup : (NSString *)ACCESSGROUP,
    (__bridge id)
    kSecAttrAccessible : (__bridge id)kSecAttrAccessibleWhenUnlocked,
  };
  SecItemDelete((CFDictionaryRef)secItem);
}

@end
