//
//  GigyaAccountManagement.h
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 06/11/2016.
//  Copyright © 2016 Gheerish Bansoodeb. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GigyaSDK/Gigya.h>
#import "LandingTabBarController.h"

@interface GigyaAccountManagement : NSObject<GSAccountsDelegate>

@property (strong,nonatomic) LandingTabBarController *landingView;

-(void)validateGigyaSession;

@end
