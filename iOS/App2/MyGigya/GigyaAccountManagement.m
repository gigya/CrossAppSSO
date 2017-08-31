//
//  GigyaAccountManagement.m
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 30/08/2017.
//  Copyright © 2017 Gheerish Bansoodeb. All rights reserved.
//

#import "AppDelegate.h"
#import "ContentViewController.h"
#import "GigyaAccountManagement.h"
#import "KeychainHelper.h"
#import <GigyaSDK/GSSession.h>
#import <GigyaSDK/Gigya.h>

@implementation GigyaAccountManagement

KeychainHelper *keychainHelper;

- (id)init {
  self = [super init];
  if (self) {
    keychainHelper = [[KeychainHelper alloc] init];
  }
  return self;
}
// Accounts Delegates
- (void)accountDidLogin:(GSAccount *)account {
  NSLog(@"Account Did Login: %@", account);
  app.userAccount = account;
  [self storeGigyaSessionForSSO:[account objectForKey:@"UID"]];
  [self navToLandingPage];
}

- (void)accountDidLogout {
  NSLog(@"Account Did Logout");
  app.userAccount = nil;
  [keychainHelper deleteFromKeychain];
}

- (void)navToLandingPage {
  UIStoryboard *storyboard =
      [UIStoryboard storyboardWithName:@"Main" bundle:nil];
  ContentViewController *myVC = (ContentViewController *)[storyboard
      instantiateViewControllerWithIdentifier:@"ContentViewController"];
  [self.landingView presentViewController:myVC animated:YES completion:nil];
}

- (void)storeGigyaSessionForSSO:(NSString *)UID {
  [Gigya getSessionWithCompletionHandler:^(GSSession *_Nullable session) {
    NSLog(@"%@-%@-%@-%@", session.token, session.secret, session.info,
          session.lastLoginProvider);

    NSDictionary *sessionInfo = @{
      @"token" : session.token,
      @"secret" : session.secret,
      @"apiKey" : session.info.APIKey,
      @"lastLoginProvider" : session.lastLoginProvider,
    };
    NSMutableData *data = [[NSMutableData alloc] init];
    NSKeyedArchiver *archiver =
        [[NSKeyedArchiver alloc] initForWritingWithMutableData:data];
    [archiver encodeObject:sessionInfo forKey:@"GSSession"];
    [archiver finishEncoding];

    [keychainHelper saveInKeychain:data];
  }];
}

// Account state management
- (void)validateGigyaSession {

  NSDictionary *sessionDic = [keychainHelper retrieveFromKeychain];

  if (sessionDic) {
    GSSession *sess = [[GSSession alloc] init];
    sess = [sess initWithSessionToken:[sessionDic objectForKey:@"token"]
                               secret:[sessionDic objectForKey:@"secret"]];
    GSSessionInfo *sessionInfo = [[GSSessionInfo alloc] init];
    sessionInfo.APIKey = [sessionDic objectForKey:@"apiKey"];
    sess.info = sessionInfo;
    sess.lastLoginProvider = [sessionDic objectForKey:@"lastLoginProvider"];
    [Gigya setSession:sess];
  } else if ([Gigya isSessionValid]) {
    // Single Log Out
    [Gigya logout];
  }

  if ([Gigya isSessionValid]) {
    // Logged in
    // Get user info (if already registerd)

    NSMutableDictionary *userAction6 = [NSMutableDictionary dictionary];
    [userAction6 setObject:@"loginIDs" forKey:@"include"];

    GSRequest *request = [GSRequest requestForMethod:@"accounts.getAccountInfo"
                                          parameters:userAction6];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
      if (!error) {
        NSLog(@"Account Did Login: %@", response);

        NSDictionary *userProfile = response[@"loginIDs"];
        NSString *nickname = [userProfile[@"emails"] objectAtIndex:0];
        // User was logged in

        UIAlertController *alert = [UIAlertController
            alertControllerWithTitle:@"Login"
                             message:[@"User is already logged in: "
                                         stringByAppendingString:nickname]
                      preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *logoutBtn =
            [UIAlertAction actionWithTitle:@"Log Out"
                                     style:UIAlertActionStyleDefault
                                   handler:^(UIAlertAction *action) {
                                     [Gigya logout];
                                     app.userAccount = nil;
                                   }];
        [alert addAction:logoutBtn];

        UIAlertAction *continueBtn = [UIAlertAction
            actionWithTitle:@"Continue"
                      style:UIAlertActionStyleDefault
                    handler:^(UIAlertAction *action) {
                      UIStoryboard *storyboard =
                          [UIStoryboard storyboardWithName:@"Main" bundle:nil];
                      ContentViewController *myVC =
                          (ContentViewController *)[storyboard
                              instantiateViewControllerWithIdentifier:
                                  @"ContentViewController"];
                      app.userAccount = (GSAccount *)response;
                      [self.landingView presentViewController:myVC
                                                     animated:YES
                                                   completion:nil];

                    }];
        [alert addAction:continueBtn];

        [self.landingView presentViewController:alert
                                       animated:YES
                                     completion:nil];
      } else {
        NSLog(@"Error getUserInfo: %@", error);
      }
    }];
  } else {
    NSLog(@"not valid");
  }
}

@end
