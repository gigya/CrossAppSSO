## About this project

This code repository is provided on an "as-is" basis for demonstration purposes. For further information on the Gigya platform and APIs, please reference http://developers.gigya.com

This project shows a custom implementation on how Single-Sign-On (SSO) can be achieved from one app to another, leveraging Gigya's iOS SDK.<br/>

This project is not officially Gigya, therefore, PLEASE DO NOT CONTACT GIGYA SUPPORT ABOUT IT.


## Demo features<br/>
-> Keychain implementation for Gigya SSO<br/>
-> Session management through Gigya SDK<br/>
-> Gigya Registration-as-a-Service (RaaS) features and flows<br/>
-> Native, plugin and web-view options

## Installation

A pod file is included in this project, which configures Gigya, Facebook and Google native SDKs. For the purpose of this demo, a test Gigya API key, Facebook and Google application keys are being used from a test account. <br/>

<strong>Please do not use any of these api keys and provider ids in Production codes - as it is configured for demo purposes only.</strong><br/>
From Terminal, run
```
pod install
```
Depending on your xcode configuration, you may get a message from cocoa pods indicating to use '$(inherited)' in 'Other Linker Flags' in Build Settings. This is important in order to avoide compilation linking issues.

## Gigya References

[Gigya iOS SDK installation](https://developers.gigya.com/display/GD/iOS)<br/>
[Gigya iOS Reference Documentation](https://developers.gigya.com/display/GD/iOS+SDK+Reference)<br/>
[Gigya iOS SDK change log](https://developers.gigya.com/display/GD/iOS+SDK+Change+Log)

## Native Social Login

The info.plist file contains all necessary LSApplicationQueriesSchemes and URLSchemes in order for Google and Facebook to make use of their own native sdk for login. 
The following link describes in more details on configuration present in this project: [Native Login for social providers](https://developers.gigya.com/display/GD/iOS#iOS-ConfiguringNativeLogin)
