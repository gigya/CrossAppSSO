//
//  AppDelegate.h
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 20/08/2017.
//  Copyright © 2017 Gheerish Bansoodeb. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <GigyaSDK/Gigya.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>
@property (strong, nonatomic) UIWindow *window;
@property (strong,nonatomic) GSAccount *userAccount;

@end

extern AppDelegate *app;
