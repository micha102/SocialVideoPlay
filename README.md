# SocialVideoPlay - Android Video Player for Social Media Links

SocialVideoPlay is an Android application that intercepts video links from popular social media platforms (such as Facebook, Instagram, Tiktok, and X/Twitter), and plays the videos directly in the app using **yt-dlp** and **ExoPlayer**. This app is designed to provide a cleaner and distraction-free viewing experience by removing popups and forcing login prompts, while also helping protect user privacy by not logging you in or tracking your viewing history.

## Features

- **Video Playback from Social Media**: Intercepts and plays video and reel links from Facebook, Instagram, Tiktok, and X/Twitter.
- **Video Looping**: Video will loop until the end exits.
- **yt-dlp Integration**: Utilizes `yt-dlp` (a powerful YouTube downloader and video extraction tool) wrapped with Chaquopy to handle the video extraction.
- **ExoPlayer for Playback**: Ensures smooth video playback across platforms using ExoPlayer.
- **No Login/Tracking**: Avoids the need to log into social media accounts, preserving your privacy and blocking annoying popups that ask for registration or login.
- **Intercept Video Links**: The app automatically intercepts video links shared via other apps or browsers.
- **Watch History**: Lists the history of watched videos and allows you to clear history with a long press.
- **Customizable Link Interception**: Users can configure which links to open with the app via Android settings (Settings > Apps > SocialVideoPlay > Open by default > Add link).

## Supported Social Medias

- **Facebook**
- **Instagram**
- **Tiktok**
- **X/Twitter** (limited support)

> **Note:** X/Twitter video links may not work consistently due to difficulties in differentiating between statuses with video content versus plain text or images.

## Installation

1. Clone or download the repository:
    ```bash
    git clone https://github.com/micha102/socialvideoplay.git
    ```

2. Open the project in Android Studio.

3. Build and run the project on your Android device.

4. Install the APK on your Android device or use Android Studio's Run feature to deploy it to your phone.

## Setup

Once installed, the app needs to be configured to intercept video links:

1. Go to **Settings > Apps** on your Android device.
2. Select **SocialVideoPlay**.
3. Tap **Open by default**.
4. Tap **Add link**, and select the social media links you want to intercept (e.g., Facebook, Instagram, Tiktok, X/Twitter).
   - You can uncheck X/Twitter if you prefer not to intercept X links.

> **Important:** The app does not support Android App Links verification, so manual configuration is required for link interception.

## Usage

1. **Intercepting Video Links**: 
    - When a video link is shared from a browser or any other app, the app will prompt you to open the link with **SocialVideoPlay**.
    - Once selected, the app will extract and play the video using ExoPlayer, without downloading it.

2. **Viewing Watch History**: 
    - Open the app, and it will show a history of watched videos.
    - To delete a video from the history, long-click on the entry to remove it.

## Privacy

This app doesn't track anything nor requires  to log in to any social media account. It is designed to avoid big Social Media tracking by not visiting their web pages stuffed with JS and other tracking means.

## Limitations

- **X/Twitter**: Because X/Twitter does not differentiate clearly between statuses with video and those with just text or images, video playback from X/Twitter is **not guaranteed** to work reliably.
  
- **Link Interception**: As the app cannot verify Android App Links for external domains (such as Facebook, Instagram, etc.), users must manually configure the app to intercept the links via the settings.

## Contributing

Contributions are welcome! If you encounter bugs or want to add more features, feel free to open an issue or submit a pull request.

### Steps to contribute:
1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Submit a pull request with a detailed description of the changes.

## Acknowledgements

- **yt-dlp** - Video downloader and extraction library.
- **Chaquopy** - Allows integration of Python code in Android applications.
- **ExoPlayer** - A powerful media player for Android.

## Support

If you run into any issues or have questions, feel free to open an issue on GitHub, and we'll do our best to help!
