package com.jayktec.DialogaCity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


/**
 * Created by Usuario on 16-10-2015.
 */
public class ServicioMusica  extends Service {
    MediaPlayer reproductor;

    @Override
    public void onCreate() {
        reproductor = MediaPlayer.create(this, R.raw.wowzer);
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {

        onStart();
        return START_STICKY;

    }

    public void onStart() {

        reproductor.start();

    }


    @Override
    public void onDestroy() {
        reproductor.stop();
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }
}
