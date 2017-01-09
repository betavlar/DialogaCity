package com.jayktec.DialogaCity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Usuario on 03-11-2015.
 */
public class BootServicio extends BroadcastReceiver
        {


            @Override
public void onReceive(Context context, Intent intent) {


                if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

                    BDControler bdlocal= new BDControler(context,1);
                    if(!bdlocal.primeraVez()) {
                        Intent pushIntent = new Intent(context, ServicioSincronizacion.class);
                        context.startService(pushIntent);

                        Intent pushIntent1 = new Intent(context, ServicioNotificacion.class);
                        context.startService(pushIntent1);
                    }
                }


        }


        }
