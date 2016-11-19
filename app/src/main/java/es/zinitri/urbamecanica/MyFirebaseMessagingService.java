package es.zinitri.urbamecanica;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.zinitri.urbamecanica.utils.Constants;


/**
 * Created by victor on 9/11/16.
 * Captura la notificacion de Firebase cuando estas dentro de la aplicacion y la vuelve a enviar
 * para que se muestre en la barra de notificaciones
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_culi)
                        .setContentTitle(Constants.EQUIPO)
                        .setContentText(remoteMessage.getNotification().getBody());

        Intent notIntent = new Intent(this, MainActivity.class);

        PendingIntent contIntent = PendingIntent.getActivity(this, 0, notIntent, 0);

        mBuilder.setContentIntent(contIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
