package com.jayktec.DialogaCity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Usuario on 12-11-2015.
 */
public class DataActualReceiver extends BroadcastReceiver {
    private int id_denuncia;

    public int getId_denuncia() {
        return id_denuncia;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("REFRESH_DATA_INTENT")) {
            //Do stuff - maybe update my view based on the changed DB contents
            id_denuncia=intent.getIntExtra("id",0);

Log.i("breceiver",""+id_denuncia);
        }
    }
}
