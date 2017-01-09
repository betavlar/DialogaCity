package com.jayktec.DialogaCity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Alexander on 28-Aug-15.
 * Clase utilizada para el manejo de la localizacion del dispositivo
 */
public class GPSTracker extends Service implements LocationListener{
    private Context miContexto;
    private Location miLocalizacion;
    private double miLatitud;
    private double miLongitud;
    private boolean gpsActivo;
    private Boolean netActivo;
    private boolean hayLocalizacion;
    protected LocationManager agenteLocalizador;

    // The minimum distance to change Updates in meters
    private static final long DISTANCIA_MINIMA_PARA_ACTUALIZAR = 1;

    // The minimum time between updates in milliseconds
    private static final long TIEMPO_MINIMO_PARA_ACTUALIZAR = 1;

    public GPSTracker(){}

    public GPSTracker(Context contexto){
        this.miContexto = contexto;
    }

    public boolean obtenerLocalizacionOnline(){
        try{
            Log.i("GPSTracker:", "ONLINE");
            agenteLocalizador = (LocationManager) miContexto.getSystemService(LOCATION_SERVICE);
            gpsActivo = agenteLocalizador.isProviderEnabled(LocationManager.GPS_PROVIDER);
            netActivo = agenteLocalizador.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gpsActivo && !netActivo){
                hayLocalizacion = false;
            }
            else{
                hayLocalizacion = true;
                if(netActivo){
                    agenteLocalizador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            TIEMPO_MINIMO_PARA_ACTUALIZAR,
                            DISTANCIA_MINIMA_PARA_ACTUALIZAR,
                            this);
                    if(agenteLocalizador != null){
                        miLocalizacion = agenteLocalizador.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(miLocalizacion != null){
                            this.miLatitud = miLocalizacion.getLatitude();
                            this.miLongitud = miLocalizacion.getLongitude();
                            Log.i("GPS_T","RED");
                        }
                    }
                }
                else{
                    agenteLocalizador.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            TIEMPO_MINIMO_PARA_ACTUALIZAR,
                            DISTANCIA_MINIMA_PARA_ACTUALIZAR, this);
                    if(agenteLocalizador != null){
                        miLocalizacion = agenteLocalizador.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(miLocalizacion != null){
                            this.miLatitud = miLocalizacion.getLatitude();
                            this.miLongitud = miLocalizacion.getLongitude();
                            Log.i("GPS_T","GPS");
                        }
                    }
                }
            }
        }
        catch (Exception ErrorGPS){
            ErrorGPS.printStackTrace();
        }
        return hayLocalizacion;
    }

    /**
     * Busca la ultima ubicacion conocida del telefono si no puedo encontrarla
     */
    public void obtenerLocalizacionOffline(){
        Log.i("GPSTracker:", "OFFLINE");
        LocationManager servicio = (LocationManager)getSystemService(LOCATION_SERVICE);
        //23 /20/2015 yle
        // Comprobamos si está disponible el proveedor GPS.
        if (!servicio.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            mostrarAvisoGpsDeshabilitado();
        }

        Criteria criteria = new Criteria();
        String provider = servicio.getBestProvider(criteria,false);
        miLocalizacion = servicio.getLastKnownLocation(provider);
        miLatitud = miLocalizacion.getLatitude();
        miLongitud = miLocalizacion.getLongitude();
    }


    private void mostrarAvisoGpsDeshabilitado() {
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(callGPSSettingIntent);

    }

    /**
     * Detiene el servicio GPS
     */
    public void detenerGPS(){
        if (agenteLocalizador != null){
            agenteLocalizador.removeUpdates(GPSTracker.this);
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public double getMiLatitud() {
        return this.miLatitud;
    }

    public double getMiLongitud() {
        return this.miLongitud;
    }
}
