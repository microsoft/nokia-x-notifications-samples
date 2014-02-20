Nokia Notifications API: Helper library sample
==============================================

This sample application uses the helper library (`push.jar`) to communicate with
the Nokia Notifications service. It allows a single sender ID to be specified,
registered and unregistered. The registration ID fetched from the service is
stored on the clipboard so that it can be shared and used for testing the
notification service. Received notifications are shown in the notification bar.


Instructions
--------------------------------------------------------------------------------

1. Import the project to your IDE and compile it. Remember to set the build
   target as Platform 4.1.2/API level 16 with Nokia X services API set. Note
   that you do need to have Nokia X add-ons installed to your SDK.
2. Install the sample application to your device or run the app in the emulator.
3. Launch the sample application and select Menu -> Set sender ID.
4. Input the sender ID you registered in Nokia Developer console. By default the
   application uses "anna-example" as the sender ID.
    * If you first want to just test the push notification system, you can keep
      the default "anna-example" sender ID. The developer console allows you to
      send notifications to that service without a need to register your own.
5. Select Menu -> Register.
6. Once the registration is successful, the registration ID is shown in the UI.
   The ID is also automatically copied to device clipboard. Depending on your
   environment, if you have LogCat output available in your IDE, you can copy
   the ID from that output. To copy the ID from the clipboard follow these
   steps:
    * Open [pastebin.com](http://pastebin.com) on emulator/device browser.
    * Select paste in New paste (requires long key press to activate the
      Paste pop-up menu).
    * Press Submit button.
    * Check the created URL on the emulator/device browser for the new paste,
      e.g. `http://pastebin.com/sREjJELE`.
    * Open the URL in browser in the PC, which you are about to use to send the
      notification, and copy the registration ID.
7. Navigate to the developer console and select the sender ID of your choice.
8. Copy the registration ID to the place reserved for it.
9. Enter payload (optional) and click Push.


Implementation
--------------------------------------------------------------------------------

Class descriptions:

* Package `com.nokia.example.nnahelperlibrarysample`:
    * `CommonUtilities`: Contains application wide constants and helper methods.
    * `DemoActivity`: The main UI class of the application.
    * `PromptDialog`: A dialog for entering the sender ID.
    * `PushIntentService`: The intent service class implementation.
    
For more information, see the Nokia Notifications API developer documentation.


Known issues
--------------------------------------------------------------------------------

None.


License
--------------------------------------------------------------------------------

See the separate license file provided with this project.
