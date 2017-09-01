//
//  LandingTabBarController.m
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 06/11/2016.
//  Copyright © 2016 Gheerish Bansoodeb. All rights reserved.
//

#import "LandingTabBarController.h"
#import <GigyaSDK/Gigya.h>
#import "ContentViewController.h"
#import "AppDelegate.h"

@interface LandingTabBarController ()

@end

@implementation LandingTabBarController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    app.gigyaAccount.landingView = self;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
