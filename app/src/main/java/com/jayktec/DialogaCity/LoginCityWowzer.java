package com.jayktec.DialogaCity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginCityWowzer extends Activity {
    private BDControler bdlocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_city_wowzer);
        //15/10/2015 inicia la musica todo cambiar a un formato mas liviano como .ogg
//        startService(new Intent(LoginCityWowzer.this, ServicioMusica.class));

        bdlocal = new BDControler(this,1);
        String idUsuario = bdlocal.devolverAliasUsrLogueado();

        if(!idUsuario.equals("X")){
            Intent miIntent = new Intent(getApplicationContext(),com.jayktec.DialogaCity.ActividadMapaCityWowzer.class);
            startActivity(miIntent);
        }

        Button botonAceptar = (Button)findViewById(R.id.loginBotonAceptar);
        Button botonRegistro = (Button)findViewById(R.id.LoginBotonRegistrarse);
        final EditText textoEmail = (EditText)findViewById(R.id.loginTextoEmail);
        final EditText textoPassword = (EditText)findViewById(R.id.loginTextoPassword);

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo quitar el registro de prueba
                if((textoEmail.getText().toString().equals("1")) && (textoPassword.getText().toString().equals("2"))){
                    //if (1 ==1){
                    if(bdlocal.registrarUsuarioLogeado(19,"gatico69","M",1) == "OK"){
                        Intent miIntent = new Intent(getApplicationContext(),com.jayktec.DialogaCity.ActividadMapaCityWowzer.class);
                        startActivity(miIntent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Problemas para Guardar los", Toast.LENGTH_LONG).show();
                        textoEmail.setText("");
                        textoPassword.setText("");
                    }

                }
                else{
                    LoginWs ingreso= new LoginWs();
                    String[] datos={textoEmail.getText().toString(),textoPassword.getText().toString()};

                    ingreso.execute(datos);
                    while (ingreso.isTrabajando() ) {
//        Log.i("lCom SUCIIO fin:",listado.getTrabajando().toString());
                        //esperando que finalice el ws
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                            }
                        }, 100);
                    }


                    if (ingreso.getIdRegistro()==0)

                    {
                        Toast.makeText(getApplicationContext(), "Datos Invalidos", Toast.LENGTH_LONG).show();
                        textoEmail.setText("");
                        textoPassword.setText("");
                        textoEmail.hasFocus();
                    }
                    else
                    {
                        Log.i("ws registrar usuario","por defecto");
                        if(bdlocal.registrarUsuarioLogeado(
                                    ingreso.getIdRegistro(),
                                    ingreso.getAlias(),
                                    ingreso.getSexo(),
                                    ingreso.getNivel()
                            ) == "OK")
                            {
                                Intent miIntent = new Intent(getApplicationContext(),com.jayktec.DialogaCity.ActividadMapaCityWowzer.class);
                                startActivity(miIntent);
                            }
                        else{
                            Toast.makeText(getApplicationContext(), "Problemas para Guardar los", Toast.LENGTH_LONG).show();
                            textoEmail.setText("");
                            textoPassword.setText("");
                        }
                    }
                }
            }
        });
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent miIntent = new Intent(getApplicationContext(),com.jayktec.DialogaCity.RegistroCityWowzer.class);

                startActivity(miIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login_city_wowzer, menu);
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
