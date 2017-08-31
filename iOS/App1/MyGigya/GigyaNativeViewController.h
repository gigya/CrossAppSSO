//
//  GigyaNativeViewController.h
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 06/11/2016.
//  Copyright © 2016 Gheerish Bansoodeb. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MediaPlayer/MediaPlayer.h>

@interface GigyaNativeViewController : UIViewController <UIAlertViewDelegate>

@property (weak, nonatomic) IBOutlet UITextField *emailField;
@property (weak, nonatomic) IBOutlet UITextField *passwordField;
@property (weak, nonatomic) IBOutlet UITextField *firstNameField;
@property (weak, nonatomic) IBOutlet UITextField *lastNameField;

@property (weak, nonatomic) IBOutlet UILabel *errorField;
@property (weak, nonatomic) IBOutlet UILabel *tncLabel;


@property (weak, nonatomic) IBOutlet UIView *contView;
@property (weak, nonatomic) IBOutlet UIView *uiSeperator;
@property (weak, nonatomic) IBOutlet UIView *pwdSeparator;

@property (weak, nonatomic) IBOutlet UISwitch *tncField;

@property (weak, nonatomic) IBOutlet UIButton *forgotPwBtn;
@property (weak, nonatomic) IBOutlet UIButton *btnRegister;
@property (weak, nonatomic) IBOutlet UIButton *loginBtn;

- (IBAction)nativeTapped:(id)sender;
- (IBAction)registerTapped:(id)sender;
- (IBAction)forgotPwdTapped:(id)sender;
- (IBAction)twitterTapped:(id)sender;


@end
