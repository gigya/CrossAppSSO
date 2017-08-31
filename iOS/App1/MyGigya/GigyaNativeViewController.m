//
//  GigyaNativeViewController.m
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 10/04/2017.
//  Copyright © 2016 Gheerish Bansoodeb - Gigya INC. All rights reserved.
//

#import "GigyaNativeViewController.h"
#import <GigyaSDK/Gigya.h>

@interface GigyaNativeViewController ()
@end

@implementation GigyaNativeViewController

// Module level vars
NSString *currentRegToken, *currentProvider;
bool regSwitch, resetPwdSwitch, profCompletion;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [[self.loginBtn layer] setBorderWidth:1.0f];
    [[self.loginBtn layer] setBorderColor:[UIColor colorWithRed:(59 / 255.0)
                                                          green:(89 / 255.0)
                                                           blue:(152 / 255.0)
                                                          alpha:1.0]
     .CGColor];
    
    self.loginBtn.frame = CGRectMake(
                                     self.loginBtn.frame.origin.x, self.loginBtn.frame.origin.y - 60,
                                     self.loginBtn.frame.size.width, self.loginBtn.frame.size.height);
    
    regSwitch = NO;
    resetPwdSwitch = NO;
    profCompletion = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewDidAppear:(BOOL)animated {
    self.errorField.hidden = YES;
}

// Gigya Registration as a Service (RaaS) Flows

/* Registration Flow */
- (void)registerFlow {
    // First - Get new registration token from accounts.initRegistration
    GSRequest *request =
    [GSRequest requestForMethod:@"accounts.initRegistration" parameters:nil];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        if (!error) {
            NSLog(@"Success - %@", response);
            NSString *regToken = response[@"regToken"];
            
            // Create Account
            [self registerAccount:regToken];
        } else {
            NSLog(@"error - %@", error.localizedDescription);
            self.errorField.text = error.localizedDescription;
            self.errorField.hidden = NO;
            
            // Check the error code according to the GSErrorCode enum, and
            // handle it.
        }
    }];
}

- (void)registerAccount:(NSString *)regToken {
    
    // Get User input fields
    NSString *firstName = self.firstNameField.text;
    NSString *lastName = self.lastNameField.text;
    NSString *email = self.emailField.text;
    NSString *password = self.passwordField.text;
    NSString *tnc;
    
    if (self.tncField.on) {
        tnc = @"true";
    } else {
        tnc = @"false";
    }
    
    NSString *profileData =
    [NSString stringWithFormat:@"{ 'firstName' : '%@' , 'lastName' : '%@' }",
     firstName, lastName];
    
    NSString *customData = [NSString stringWithFormat:@"{ 'terms' : '%@' }", tnc];
    
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    [parameters setObject:email forKey:@"email"];
    [parameters setObject:password forKey:@"password"];
    [parameters setObject:regToken forKey:@"regToken"];
    [parameters setObject:profileData forKey:@"profile"];
    [parameters setObject:customData forKey:@"data"];
    
    GSRequest *request =
    [GSRequest requestForMethod:@"accounts.register" parameters:parameters];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        
        if (!error) {
            NSLog(@"Success - %@", response);
            
            // Finalize the registration
            [self finalizeRegistration:regToken];
        } else if (error.code == GSErrorAccountPendingRegistration) {
            // Get updated RegToken to use in subsequent calls
            NSString *newRegToken = response[@"regToken"];
            NSDictionary *profile = response[@"profile"];
            [self profileCompletionFlow:newRegToken isEmailPresent:[[profile objectForKey:@"email"] length] > 0];
        }
    }];
}

/* Finalize Registration */
- (void)finalizeRegistration:(NSString *)regToken {
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    [parameters setObject:regToken forKey:@"regToken"];
    GSRequest *request =
    [GSRequest requestForMethod:@"accounts.finalizeRegistration"
                     parameters:parameters];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        if (!error) {
            NSLog(@"Success - %@", response);
            
            // Login the user
            [self loginFlow:self.emailField.text password:self.passwordField.text];
        } else {
            NSLog(@"error - %@", error.localizedDescription);
            self.errorField.text = error.localizedDescription;
            self.errorField.hidden = NO;
            
            // Check the error code according to the GSErrorCode enum, and
            // handle it.
        }
    }];
}

/* Profile Completion Flow */
- (void)profileCompletionFlow:(NSString *)regToken isEmailPresent: (bool)isEmailPresent {
    profCompletion = YES;
    
    // check required fields. In this example, email address is a
    // required field
    // Get new regToken from response to be used in subsequent calls
    currentRegToken = regToken;
    if(!isEmailPresent){
        UIAlertView *av =
        [[UIAlertView alloc] initWithTitle:@"Profile Completion Page"
                                   message:@"Please enter your email address:"
                                  delegate:self
                         cancelButtonTitle:@"Cancel"
                         otherButtonTitles:@"Save", nil];
        [av setAlertViewStyle:UIAlertViewStylePlainTextInput];
        
        // Alert style customization
        [[av textFieldAtIndex:0] setPlaceholder:@"Email Address"];
        [av show];
    }
    else{
        [self finalizeRegistration:regToken];
    }
}

/* Login Flow */
- (void)loginFlow:(NSString *)loginId password:(NSString *)password {
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    [parameters setObject:loginId forKey:@"loginID"];
    [parameters setObject:password forKey:@"password"];
    
    GSRequest *request =
    [GSRequest requestForMethod:@"accounts.login" parameters:parameters];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        if (!error) {
            NSLog(@"Success - %@", response);
            // Success! Use the response object.
            
        } else {
            NSLog(@"error - %@", error.localizedDescription);
            self.errorField.text = error.localizedDescription;
            self.errorField.hidden = NO;
            
            // Check the error code according to the GSErrorCode enum, and handle it.
        }
    }];
}

/* Social Login Flow */
- (void)socialLogin:(NSString *)provider {
    self.errorField.hidden = YES;
    
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    [parameters setObject:@"saveProfileAndFail" forKey:@"x_conflictHandling"];
    
    NSLog(@"Provider is: %@", provider);
    currentProvider = provider;
    
    [Gigya loginToProvider:provider
                parameters:parameters
                      over:self
         completionHandler:^(GSUser *user, NSError *error) {
             
             if (!error) {
                 // Login was successful
                 NSLog(@"%@", user);
             } else {
                 // Handle errors
                 NSLog(@"%@", error);
                 
                 // profile completion / required fields
                 if (error.code == GSErrorAccountPendingRegistration) {
                     // Get new regToken from response to be used in subsequent calls
                     NSString *regToken = error.userInfo[@"regToken"];
                     [self profileCompletionFlow:regToken isEmailPresent:NO];
                 }
                 
                 // account linking error codes
                 if (error.code == 200010 || error.code == 403043) {
                     NSString *regToken = error.userInfo[@"regToken"];
                     // Get new regToken from response to be used in subsequent calls
                     [self accountLinkingFlow:regToken];
                 }
             }
         }];
}

/* Account Linking Flow */
- (void)accountLinkingFlow:(NSString *)regToken {
    currentRegToken = regToken;
    
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    [params setObject:regToken forKey:@"regToken"];
    
    GSRequest *request =
    [GSRequest requestForMethod:@"accounts.getConflictingAccount"
                     parameters:params];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        if (!error) {
            NSLog(@"Success - %@", response);
            // Success! Use the response object.
            
            NSDictionary *providers = response[@"conflictingAccount"];
            NSArray *availableProviders = providers[@"loginProviders"];
            
            UIAlertController *alert = [UIAlertController
                                        alertControllerWithTitle:@"Accounts Linking"
                                        message:@"You have previously logged in "
                                        @"with a different account. To "
                                        @"link your accounts, please "
                                        @"re-authenticate using the "
                                        @"following options:"
                                        preferredStyle:UIAlertControllerStyleAlert];
            
            // build UI for account to be linked to
            for (int i = 0; i < availableProviders.count; i++) {
                
                NSString *prov = [availableProviders objectAtIndex:i];
                
                if ([prov isEqualToString:@"site"]) {
                    UIAlertAction *siteLogin = [UIAlertAction
                                                actionWithTitle:@"Email/Pwd"
                                                style:UIAlertActionStyleDefault
                                                handler:^(UIAlertAction *action) {
                                                    
                                                    UIAlertView *av = [[UIAlertView alloc]
                                                                       initWithTitle:@"Login"
                                                                       message:@"Login with your "
                                                                       @"username and password"
                                                                       delegate:self
                                                                       cancelButtonTitle:@"Cancel"
                                                                       otherButtonTitles:@"Login", nil];
                                                    [av setAlertViewStyle:
                                                     UIAlertViewStyleLoginAndPasswordInput];
                                                    
                                                    // Alert style customization
                                                    [[av textFieldAtIndex:1] setSecureTextEntry:YES];
                                                    [[av textFieldAtIndex:0]
                                                     setPlaceholder:@"Email Address"];
                                                    [[av textFieldAtIndex:1] setPlaceholder:@"Password"];
                                                    [av show];
                                                    
                                                }];
                    
                    [alert addAction:siteLogin];
                    
                } else {
                    UIAlertAction *socialProvider = [UIAlertAction
                                                     actionWithTitle:prov
                                                     style:UIAlertActionStyleDefault
                                                     handler:^(UIAlertAction *action) {
                                                         
                                                         NSMutableDictionary *parameters =
                                                         [NSMutableDictionary dictionary];
                                                         [parameters setObject:@"link" forKey:@"loginMode"];
                                                         [parameters setObject:regToken forKey:@"regToken"];
                                                         
                                                         [Gigya loginToProvider:prov
                                                                     parameters:parameters
                                                                           over:self
                                                              completionHandler:^(GSUser *_Nullable user,
                                                                                  NSError *_Nullable error) {
                                                                  if (error.code == 200009) {
                                                                      self.errorField.text =
                                                                      @"Accounts successfully linked.";
                                                                      self.errorField.hidden = NO;
                                                                      
                                                                      // Login the user
                                                                      [self socialLogin:currentProvider];
                                                                  }
                                                              }];
                                                     }];
                    [alert addAction:socialProvider];
                }
            }
            
            [self presentViewController:alert animated:YES completion:nil];
            
        } else {
            NSLog(@"error - %@", error.localizedDescription);
            self.errorField.text = error.localizedDescription;
            self.errorField.hidden = NO;
            
            // Check the error code according to the GSErrorCode, and
            // handle it.
        }
    }];
}

- (void)linkAccounts:(NSString *)loginID password:(NSString *)password {
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    [parameters setObject:loginID forKey:@"loginID"];
    [parameters setObject:password forKey:@"password"];
    [parameters setObject:currentRegToken forKey:@"regToken"];
    [parameters setObject:@"link" forKey:@"loginMode"];
    
    GSRequest *request = [GSRequest requestForMethod:@"accounts.login"
                                          parameters:parameters];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        if (!error || error.code == 200009) {
            NSLog(@"Success - %@", response);
            // Success! Use the response object.
            self.errorField.text = @"Accounts successfully linked.";
            self.errorField.hidden = NO;
            [self socialLogin:currentProvider];
            
        } else {
            NSLog(@"error - %@", error.localizedDescription);
            self.errorField.text = error.localizedDescription;
            self.errorField.hidden = NO;
            
            // Check the error code according to the GSErrorCode enum, and handle
            // it.
        }
    }];
}

/* Password Reset Flow */
- (void)passwordResetFlow:(NSString *)emailAddress {
    NSMutableDictionary *userAction = [NSMutableDictionary dictionary];
    [userAction setObject:emailAddress forKey:@"loginID"];
    [userAction setObject:emailAddress forKey:@"email"];
    
    GSRequest *request = [GSRequest requestForMethod:@"accounts.resetPassword"
                                          parameters:userAction];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        if (!error) {
            NSLog(@"Success - %@", response);
            // Success! Use the response object.
            self.errorField.text = @"Please check your email to reset your password";
            self.errorField.hidden = NO;
            
            self.passwordField.hidden = NO;
            self.btnRegister.hidden = NO;
            [self.btnRegister setTitle:@"Don't have an account?"
                              forState:UIControlStateNormal];
            self.loginBtn.titleLabel.text = @"LOGIN";
            self.forgotPwBtn.hidden = NO;
            
            self.pwdSeparator.hidden = NO;
            resetPwdSwitch = NO;
            
        } else {
            NSLog(@"error - %@", error.localizedDescription);
            self.errorField.text = error.localizedDescription;
            self.errorField.hidden = NO;
            
            // Check the error code according to the GSErrorCode, and handle it.
        }
    }];
}

/* Set Account Info Flow */
- (void)setAccountInfo:(NSString *)profileData
              regToken:(nullable NSString *)regToken {
    
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    [parameters setObject:profileData forKey:@"profile"];
    
    if (regToken) {
        [parameters setObject:regToken forKey:@"regToken"];
        [parameters setObject:@"saveProfileAndFail" forKey:@"conflictHandling"];
    }
    
    GSRequest *request = [GSRequest requestForMethod:@"accounts.setAccountInfo"
                                          parameters:parameters];
    [request sendWithResponseHandler:^(GSResponse *response, NSError *error) {
        if (!error) {
            NSLog(@"Success - %@", response);
            // Success! Use the response object.
            // Finalize Reg if regtoken is present
            if (regToken) {
                [self finalizeRegistration:regToken];
            }
        }
        
        else if (error.code == 200010 || error.code == 403043) {
            [self accountLinkingFlow:regToken];
        } else {
            NSLog(@"error - %@", error.localizedDescription);
            self.errorField.text = error.localizedDescription;
            self.errorField.hidden = NO;
            
            // Check the error code according to the GSErrorCode, and handle it.
        }
    }];
}

// Alert Box handlers

- (void)alertView:(UIAlertView *)alertView
clickedButtonAtIndex:(NSInteger)buttonIndex {
    
    if (buttonIndex == 1) {
        if (profCompletion) {
            NSString *profileData =
            [NSString stringWithFormat:@"{ 'email' : '%@'  }",
             [alertView textFieldAtIndex:0].text];
            //  NSString *customData = @"{ 'terms' : 'true' }";
            
            [self setAccountInfo:profileData
                        regToken:currentRegToken];
            profCompletion = NO;
        } else {
            [self linkAccounts:[alertView textFieldAtIndex:0].text
                      password:[alertView textFieldAtIndex:1].text];
        }
    }
}

// UI Tap events

- (IBAction)nativeTapped:(id)sender {
    self.errorField.hidden = YES;
    
    if (!regSwitch && !resetPwdSwitch) {
        // Login Flow
        [self loginFlow:self.emailField.text password:self.passwordField.text];
    } else if (resetPwdSwitch) {
        // password reset flow
        [self passwordResetFlow:self.emailField.text];
    } else {
        // Register
        [self registerFlow];
    }
}

- (IBAction)registerTapped:(id)sender {
    self.errorField.hidden = YES;
    
    if (!regSwitch && !resetPwdSwitch) {
        
        self.firstNameField.hidden = NO;
        self.lastNameField.hidden = NO;
        self.uiSeperator.hidden = NO;
        self.tncField.hidden = NO;
        self.tncLabel.hidden = NO;
        self.forgotPwBtn.hidden = YES;
        
        [self.btnRegister setTitle:@"Have an account already?"
                          forState:UIControlStateNormal];
        self.loginBtn.titleLabel.text = @"SIGN";
        
        self.loginBtn.frame = CGRectMake(
                                         self.loginBtn.frame.origin.x, self.loginBtn.frame.origin.y + 60,
                                         self.loginBtn.frame.size.width, self.loginBtn.frame.size.height);
        
        regSwitch = YES;
    } else {
        
        self.firstNameField.hidden = YES;
        self.lastNameField.hidden = YES;
        self.uiSeperator.hidden = YES;
        self.tncField.hidden = YES;
        self.tncLabel.hidden = YES;
        self.forgotPwBtn.hidden = NO;
        self.passwordField.hidden = NO;
        self.pwdSeparator.hidden = NO;
        
        [self.btnRegister setTitle:@"Don't have an account?"
                          forState:UIControlStateNormal];
        self.loginBtn.titleLabel.text = @"LOGIN";
        
        if (!resetPwdSwitch)
            self.loginBtn.frame = CGRectMake(
                                             self.loginBtn.frame.origin.x, self.loginBtn.frame.origin.y - 60,
                                             self.loginBtn.frame.size.width, self.loginBtn.frame.size.height);
        
        regSwitch = NO;
        resetPwdSwitch = NO;
    }
}

- (IBAction)forgotPwdTapped:(id)sender {
    
    self.passwordField.hidden = YES;
    self.pwdSeparator.hidden = YES;
    self.btnRegister.hidden = NO;
    self.loginBtn.titleLabel.text = @"EMAIL";
    self.forgotPwBtn.hidden = YES;
    [self.btnRegister setTitle:@"Back to Login" forState:UIControlStateNormal];
    
    resetPwdSwitch = YES;
}

- (IBAction)twitterTapped:(id)sender {
    [self socialLogin:@"twitter"];
}

- (IBAction)fbTapped:(id)sender {
    [self socialLogin:@"facebook"];
}

- (IBAction)googleTapped:(id)sender {
    [self socialLogin:@"googleplus"];
}

- (IBAction)linkedTapped:(id)sender {
    [self socialLogin:@"linkedin"];
}

@end
