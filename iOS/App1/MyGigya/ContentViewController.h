//
//  ContentViewController.h
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 06/11/2016.
//  Copyright © 2016 Gheerish Bansoodeb. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <GigyaSDK/Gigya.h>

@interface ContentViewController : UIViewController<GSPluginViewDelegate>
- (IBAction)logoutTapped:(id)sender;
- (IBAction)viewProfileTapped:(id)sender;
- (IBAction)commentsTapped:(id)sender;
- (IBAction)shareTapped:(id)sender;
- (IBAction)reactTapped:(id)sender;

@property (weak, nonatomic) IBOutlet UIWebView *webView;

@end
