package com.webservice.findmyandroid.util;

import android.content.Context;
import android.content.Intent;

/** 
 * <p>Description: [常量]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public final class Constants {
    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    public static final String SERVER_URL = "http://191.168.1.105:8088/WebService/gcm/gcmRegister.action?method=registration";

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "293248959336";

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */
    public  static final String DISPLAY_MESSAGE_ACTION =
            "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public  static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    /**
     * This is for the result request code
     */
    public static int START_ACTIVITY_FOR_RESULT_REQUEST_CODE = 0;
    
    public static final String DROID_USERNAME = "DROID_USERNAME";
    public static final String DROID_PASSWORD = "DROID_PASSWORD";
    public static final String DROID_ALIAS = "DROID_ALIAS";
}
