//
//  AppDelegate.m
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 20/08/2017.
//  Copyright © 2017 Gheerish Bansoodeb. All rights reserved.
//

#import "AppDelegate.h"
#import <GigyaSDK/Gigya.h>

AppDelegate *app;

@interface AppDelegate ()
@end

@implementation AppDelegate

- (id)init {
    self = [super init];
    if (self) {
        app = self;
    }
    
    return self;
}

- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    [Gigya
     initWithAPIKey:
     @"3_n6HDyxNJWZ9C6j1JcQrexeRptHoiCayyzLQ0pXGd05WwXoYNaZAC83wQ2F7kCLWN"
     application:application
     launchOptions:launchOptions
     APIDomain:@"eu1.gigya.com"];
    
    return YES;
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    [Gigya handleDidBecomeActive];
}

- (BOOL)application:(UIApplication *)app
            openURL:(NSURL *)url
            options:(NSDictionary<NSString *, id> *)options {
    return [Gigya handleOpenURL:url app:app options:options];
}

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation {
    return [Gigya handleOpenURL:url
                    application:application
              sourceApplication:sourceApplication
                     annotation:annotation];
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state.
    // This can occur for certain types of temporary interruptions (such as an
    // incoming phone call or SMS message) or when the user quits the application
    // and it begins the transition to the background state. Use this method to
    // pause ongoing tasks, disable timers, and throttle down OpenGL ES frame
    // rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate
    // timers, and store enough application state information to restore your
    // application to its current state in case it is terminated later. If your
    // application supports background execution, this method is called instead of
    // applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state;
    // here you can undo many of the changes made on entering the background.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if
    // appropriate. See also applicationDidEnterBackground:.
}



@end
