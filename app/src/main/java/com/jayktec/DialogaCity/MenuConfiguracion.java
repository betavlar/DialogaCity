package com.jayktec.DialogaCity;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class MenuConfiguracion extends ActionBarActivity {

    private BDControler bdlocal;
    private String[] datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_configuracion);
        bdlocal= new BDControler(this,1);
        datos=bdlocal.devolverDatosLogueado();

        TextView usuario= (TextView)  findViewById(R.id.etiqDatosLogin);
        TextView nivel= (TextView)  findViewById(R.id.etiqDatosNivel);
        Log.i("datos 0",datos[0]);
        Log.i("datos 1",datos[1]);
        Log.i("datos 2",datos[2]);

        usuario.setText(datos[0]);
        nivel.setText(datos[2]);

        Button BotonSalir= (Button) findViewById(R.id.botonConfSalir);
        BotonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bdlocal.limpiarIDUsrLogueadoConexion();
                MediaPlayer     reproductor = MediaPlayer.create(MenuConfiguracion.this, R.raw.triste);
                reproductor.start();
                finish();
                Intent intentLogin = new Intent(getApplicationContext(), com.jayktec.DialogaCity.LoginCityWowzer.class);
                startActivity(intentLogin);

            }
        });

        Button botonCredito = (Button) findViewById(R.id.botonConfCredito);
        botonCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog miVentanaCredito = new Dialog(MenuConfiguracion.this);
                miVentanaCredito.requestWindowFeature(Window.FEATURE_NO_TITLE);
                miVentanaCredito.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                miVentanaCredito.setContentView(R.layout.ventana_credito);
                Button botonSalida = (Button) miVentanaCredito.findViewById(R.id.botonVentana1);
                botonSalida.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        miVentanaCredito.dismiss();

                    }
                });
                miVentanaCredito.show();


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_configuracion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
