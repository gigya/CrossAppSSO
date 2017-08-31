//
//  GigyaPluginViewController.m
//  MyGigya
//
//  Created by Gheerish Bansoodeb on 06/11/2016.
//  Copyright © 2016 Gheerish Bansoodeb. All rights reserved.
//

#import "GigyaPluginViewController.h"
#import <MediaPlayer/MediaPlayer.h>
@interface GigyaPluginViewController ()

@end

@implementation GigyaPluginViewController

UIWebView *webView;
GSPluginView *pluginView;

- (void)viewDidLoad {
    [super viewDidLoad];
    [self showGigyaComponent];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

// Gigya Plugin view integration

- (void)showGigyaComponent {
    NSMutableDictionary *params = [NSMutableDictionary dictionary];
    [params setObject:@"Default-RegistrationLogin" forKey:@"screenSet"];
    
    CGRect region = CGRectMake(0, 0, 375, 567);
    
    pluginView = [[GSPluginView alloc] initWithFrame:region];
    pluginView.delegate = self;
    pluginView.showLoginProgress = YES;
    [pluginView loadPlugin:@"accounts.screenSet" parameters:params];
    [self.view addSubview:pluginView];
}

// Plugin Delegates

- (void)pluginView:(GSPluginView *)pluginView firedEvent:(NSDictionary *)event {
    NSLog(@"Plugin event from %@ - %@", pluginView.plugin,
          [event objectForKey:@"eventName"]);
}

- (void)pluginView:(GSPluginView *)pluginView
finishedLoadingPluginWithEvent:(NSDictionary *)event {
    NSLog(@"Finished loading plugin: %@", pluginView.plugin);
}

- (void)pluginView:(GSPluginView *)pluginView
  didFailWithError:(NSError *)error {
    NSLog(@"Plugin error: %@", [error localizedDescription]);
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [GSWebBridge webViewDidStartLoad:webView];
}

//  Helper
-(UIWebView*)getPluginWebView{
    //  GET UNDERLYING WEB-VIEW DISPLAYING THE PLUGIN
    //  Sample code to find Gigya's plugin view
    //  underlying web-view container
    for (int i = 0; i < pluginView.subviews.count; i++) {
        UIView *thisView = [pluginView.subviews objectAtIndex:i];
        if ([thisView isKindOfClass:[UIWebView class]]) {
            return (UIWebView*)thisView;
        }
    }
    return nil;
}

@end
