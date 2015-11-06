
README for Slick, a coding exercise for Slack

Build Instructions
==================
1. Build the debug version (no certs)
./gradlew assembleDebug

2. Install the output
adb install -r build/outputs/apk/Slick-1.0-debug.apk

The exercise
=============
Create an app that displays a list of all users on a Slack team using the results of a call to `https://api.slack.com/methods/users.list`.
1. Upon clicking on a user row, the app should drill into the user's profile.
2. On the individual profile page, you should show the person's
- picture,
- username,
- real name, and
- title.
Other profile fields are optional.

Addn'l Requirements:
* The app should work offline from a fresh open (e.g. force close and opening the app in airplane mode should still work fine after one previous launch).
* The persistence implementation does not matter, but the app should ideally be written in such a way that you could swap out implementations at a later date.
* You need only support API 15 and above.
* You are encouraged to use any 3rd party libraries that you deem appropriate. Please provide a brief explanation of why you chose to use each of the libraries you end up using.
* Any design details are also up to you. You will not be intensely scrutinized for design choices (this is an engineering role!).
* The app should look and feel like something you are proud of. Feel free to have some fun :)

Design:
=============
MemberImageLoader and MemberListLoader are the async task classes that would handle loading from remote and local caching of the JSON file and images.

For offline work, images are cached to the application’s storage directory, and the member list is cached to a String preference. 

model - Member, Profile
views - MemberDetailActivity, MemberListActivity
tasks - MemberImageLoader, MemberListLoader
utils - AndroidHelper, FileHelper, PrefsHelper 


3rd Party Library Choices:
==========================
Gson - for JSON treatment. Though not as fast as Jackson, Gson has a very easy to use API that allows serialization/deserialization
Picasso - Image loading library that handles async loading and saving to file


Known Issue
================
Member images aren’t properly scaled for the screen resolution.
(because I opted for pretty sizes vs. sharp images). 

Solution: make more sizes available. 
Thumbnail (48dp) in xxxhdpi is image_192, xxhdpi is 144px, xhdpi is 96px, etc
Profile Image (192dp) in xxxhdpi is 768 px, etc) 

Member
getThumbnailUri(Context context) 
getImageUri(Context context) 




