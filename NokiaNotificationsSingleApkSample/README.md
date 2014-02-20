Nokia Notifications API: Single APK sample
==========================================

This Nokia example application demonstrates how to implement a support for
more than one notification services, in this case Nokia Notifications and Google
Cloud Messaging (GCM) APIs, in a single project. The application dynamically
determines which service to use based on the environment it is run in.


Instructions
--------------------------------------------------------------------------------

Import the project to your IDE. The recommended build target for the project is
Platform 4.1.2/API level 16. For testing the application in the emulator,
the selection of the specific API set depends on the notification service you
wish to test:

--------------------- ----------------------------------------------------------
Emulator target       Supported notifications service
--------------------- ----------------------------------------------------------
Nokia X               Nokia Notifications

Google APIs           Google Cloud Messaging

(Plain) Android 4.1.2 None by default (can used for testing the error handling)
--------------------- ----------------------------------------------------------

This sample application requires the helper libraries for both Nokia
Notifications and Google Cloud Messaging to be added to the build path. The
Nokia Notifications API helper library, push.jar, is provided with the SDK
add-on and is located in
`<Android SDK installation folder>\extras\nokia\nokia_x_services\libs\nna`.

**Note:** Google Cloud Messaging helper library (`gcm.jar`) is not provided with
this sample. You need to obtain it from e.g. samples provided with Google APIs.
The packages can be downloaded with Android SDK Manager.

When you run the application, you can set the sender ID dynamically from the
menu. Note that in a real application you would set the sender ID as a constant
in your code, but for testing purposes this sample provides a possibility to
enter the sender ID dynamically.

To obtain the sender ID for Nokia Notifications, register your service with
Nokia Notifications developer console: https://console.push.nokia.com

For instructions how to obtain the sender ID for GCM, see
http://developer.android.com/google/gcm/gs.html

After you have set the sender ID matching your service, select 'Register' from
menu. If registering to the service was successful, your app should now be able
to receive the notifications. Send notifications from the back-end of your
service, or from a developer console (Nokia Push Notifications).


Implementation
--------------------------------------------------------------------------------

Class descriptions:

* Package `com.nokia.example.nnasingleapksample`:
    * `DemoActivity`: The main UI class of the application.
    * `ErrorDialog`: A helper class for displaying a simple error dialog.
    * `GCMIntentService`: The intent service class for GCM.
    * `PromptDialog`: A dialog for entering the sender ID.
    * `PushIntentService`: The intent service class for Nokia Push Notifications.
* Package `com.nokia.example.nnasingleapksample.notifications`:
    * `CommonIntentServiceImpl`: A common implementation for both intent service classes.
    * `CommonUtilities`: Contains application wide constants and helper methods.
    * `NotificationsManager`: Provides a common interface for managing the notification services.

Most of the notification services specific implementation is wrapped in the
`com.nokia.example.nnasingleapksample.notifications` package. The intent service
classes (`GCMIntentService` and `PushIntentService`), however, are located in
the root package, because the callback methods in them would not get called
otherwise and the application would simply not work. The actual implementation
for both intent services is provided by `CommonIntentServiceImpl` class and the
two classes in the root are merely wrappers.

The Android manifest file plays an important role in this project. It declares
the required permissions and defines the intent services. The manifest file
contains declarations and definitions for both Nokia Notifications and GCM.
Should you want to implement some other notification service, you need to add
the service specific permissions and other possible definitions to the manifest
file.

For more information, see the Nokia Notifications API developer documentation.
    

Known issues
--------------------------------------------------------------------------------

None.


License
--------------------------------------------------------------------------------

See the separate license file provided with this project.
