# AndroidDemo

The demo is optimised for Android Studio. It comprises of:
- Plugin view implementation of RaaS
- Native implementation of all RaaS flows including accounts linking, password reset.
- Social Login for all 3 methods – configured and working. (Google, Facebook and Twitter providers are available for native)
- Webbridge implementation

Just adjust the values of the strings file (path: app/src/main/res/values/strings.xml):
- gigya_api_key : your api key
- api_domain : au1, eu1, ru1 or us1
- facebook_app_id : your app id for Facebook

**Important - Google login** : in order to authorize Google login, you will need to generate a signing certificate (otherwise, the error "Operation cancelled" will appear). Please follow this guide : https://developers.google.com/drive/android/auth#generate_the_signing_certificate_fingerprint_and_register_your_application.
If no Google+ app is set up, please read: http://developers.gigya.com/display/GD/Google