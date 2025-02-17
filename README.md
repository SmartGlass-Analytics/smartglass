placeholder

Helpful Links:
Sprint Backlog: https://bama365-my.sharepoint.com/:x:/g/personal/jacarroll4_crimson_ua_edu/Edc7TZareVBAodooQoiJQwIBargyNA2nGTDA_JL1eZdjbQ?e=Bgc1kp

## Android Virtual Device (AVD) Setup
To ensure consistency across development environments, create the following AVD in Android Studio:
- **Device:** Custom or Pixel 6 (6-inch screen)
- **API Level:** 34 (Android 14, "UpsideDownCake")
- **Resolution:** 1080x2160 (Portrait)
- **RAM:** 8GB
- **Processor:** Qualcomm 6469 Octacore (if available)
- **System Image:** `system-images;android-34;google_apis;x86_64`
- **Emulator Settings:** Customize as needed in Android Studio’s AVD Manager.

### **Alternative: Command-Line AVD Creation**
If you prefer command-line setup:
1. **Install the required system image:**
```sh
sdkmanager "system-images;android-34;google_apis;x86_64"
```
2. **Create the AVD:**
```sh
avdmanager create avd -n MyDevice -k "system-images;android-34;google_apis;x86_64" --device "pixel_6"
```
3. **Launch the emulator:**
```sh
emulator -avd MyDevice
```