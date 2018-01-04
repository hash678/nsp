package abbasi.com.nixor.misc;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Anjum on 7/22/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

        private static final String TAG = "MyFirebaseIIDService";

        @Override
        public void onTokenRefresh() {

            //Getting registration token
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            //Displaying token on logcat
            Log.d(TAG, "Refreshed token: " + refreshedToken);

        }

        private void sendRegistrationToServer(String token) {
            //You can implement this method to store the token on your server
            //Not required for current project
        }
}
