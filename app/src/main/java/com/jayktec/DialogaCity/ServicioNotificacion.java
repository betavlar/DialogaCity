package com.jayktec.DialogaCity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yisheng on 02-Sep-15.
 */
public class ServicioNotificacion extends Service {
    private Timer timer = new Timer();
    private static final long UPDATE_INTERVAL = 20000;
    public static final int APP_ID_NOTIFICATION = 0;
    private NotificationCompat.Builder mensaje;


    public ServicioNotificacion() {
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

      //  Log.i("sv not", "inicio ");

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                       // Log.i("sv not","bucle ");
                        BDControler bdlocal= new BDControler(ServicioNotificacion.this,1);
                        if (!bdlocal.primeraVez()){
                        ArrayList<Notificacion> arregloNotificacion = bdlocal.obtenerNotificaciones();


                        if (arregloNotificacion.size()>0)
                        {
                            NotificationManager nm = (NotificationManager)  getSystemService(NOTIFICATION_SERVICE);

                            for (int i=0;i< arregloNotificacion.size();i++){

                                mensaje= new NotificationCompat.Builder(ServicioNotificacion.this);  ;

                                Intent actividad= new Intent(ServicioNotificacion.this,DenunciaActivity.class);

                                //actividad.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                actividad.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Bundle grupoDatos = new Bundle();
                                Denuncia midenuncia = bdlocal.obtenerLaDenunciaWOW(arregloNotificacion.get(i).getIdDenWow());
                                grupoDatos.putInt("idDenuncia", midenuncia.getIdDenuncia());
                                grupoDatos.putInt("Notificacion",1);
                                grupoDatos.putString("wowzer", midenuncia.getWowzer());
                                grupoDatos.putString("AliasWowzer", bdlocal.devolverAliasUsrLogueado());
                                actividad.putExtras(grupoDatos);


                               PendingIntent pendingIntent=  PendingIntent.getActivity(ServicioNotificacion.this,0,actividad,PendingIntent.FLAG_UPDATE_CURRENT);

                                 mensaje.setContentIntent(pendingIntent);


                                String aviso="Wowzer Wow te contesto la alcaldia";
                                Uri sonido = RingtoneManager.getActualDefaultRingtoneUri(ServicioNotificacion.this,R.raw.notificacion);

                                Bitmap icono = BitmapFactory.decodeResource(null, R.drawable.wowzie);
                                // Personalizacion
                                mensaje.setSmallIcon(R.drawable.wowzie);// pequeno superior
                                mensaje.setTicker(aviso); // Mensaje cuando aparece
                                mensaje.setWhen(System.currentTimeMillis()); // Hora que mostramos en la notificacion
                                //todo personalizar las notificaciones
                                mensaje.setContentTitle("DialgogaCity Denuncia:");
                                mensaje.setContentText(arregloNotificacion.get(i).getMensaje().toString());
                                mensaje.setContentInfo("WoW"); // Informacion breve-extra
                                mensaje.setSound(sonido);
                                mensaje.setLargeIcon(icono);

                                bdlocal.actualizarNotificacion(arregloNotificacion.get(i).getIdDenWow());

                                // Creamos la notificacion (tenemos un objeto Notification.Builder)
                                Notification n = mensaje.build();
                                // Llamo al Manager de notificaciones y le mando mi notificacion con un ID.
                                n.flags=Notification.FLAG_AUTO_CANCEL;
                                nm.notify(i, n);

                                }

                        }
                        }
                        bdlocal.close();}
                },
                0,
                UPDATE_INTERVAL);
    }
}
