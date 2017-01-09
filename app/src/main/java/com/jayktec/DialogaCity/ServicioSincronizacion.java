package com.jayktec.DialogaCity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yisheng Leon Espinoza on 02-Sep-15.
 */
public class ServicioSincronizacion extends Service {
    private Timer timer = new Timer();
    private static final long UPDATE_INTERVAL = 100000;
    public static final int APP_ID_NOTIFICATION = 0;
    private NotificationCompat.Builder mensaje;
    private boolean actualizado=false;
    public static final String REFRESH_DATA_INTENT="REFRESH_DATA_INTENT";

    public ServicioSincronizacion() {
    }

    @Override

    public void onCreate()
    {
        super.onCreate();
        onStart();

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {

        super.onDestroy();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if((flags & START_FLAG_RETRY) != 0){

            // El servicio se ha reiniciado
            onStart();
        } else {
            // Iniciar el proceso de fondo
            onStart();

        }
        return Service.START_STICKY;
    }

    public void onStart() {

        startService();

    }


    private void startService() {
//        Log.i("sv sin","inicio ");

        timer.scheduleAtFixedRate(
                new TimerTask() {
                      public void run() {
  //                        Log.i("sv sin","bucle");
                        BDControler bdlocal = new BDControler(ServicioSincronizacion.this, 1);
                        if (!bdlocal.primeraVez()) {
                            ArrayList<Denuncia> arregloDenuncias = bdlocal.obtenerTodasLasDenuncias();
                            int wowzer = bdlocal.devolverIDUsrLogueado();
                            if (arregloDenuncias.size() > 0) {
                                for (int i = 0; i < arregloDenuncias.size(); i++) {

                                    Denuncia registro = arregloDenuncias.get(i);
                                    Integer[] idDenWow = {registro.getIdDenWow(), registro.getVersion(), wowzer};
                                   // Log.i("sv sin", "emp:" + registro.getIdDenWow());
                                   // Log.i("sv sin", "ver:" + registro.getVersion());
                                   // Log.i("sv sin", "wow:" + wowzer);
                                    ActualizaDenunciaWs actualiza = new ActualizaDenunciaWs(bdlocal);
                                    actualiza.execute(idDenWow);
                                    actualizado = true;


                                }


                            }

                            Intent broadcastIntent = new Intent();
                           // broadcastIntent.putExtra("id", Integer.parseInt(ic.getProperty("id").toString());
                            broadcastIntent.setAction(REFRESH_DATA_INTENT);
                            sendBroadcast(broadcastIntent);
                            bdlocal.close();

                        }
                    }
                },
                0,
                UPDATE_INTERVAL);
    }
}
