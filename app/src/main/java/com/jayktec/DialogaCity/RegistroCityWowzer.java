package com.jayktec.DialogaCity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


public class RegistroCityWowzer extends ActionBarActivity {
    private Registro registro;
    private EditText ETNombre;
    private EditText ETApellido;
    private EditText ETEmail;
    private EditText ETPassword;
    private EditText ETIdUsuario;
    private Spinner SpEdoCivil;
    private RadioButton RadioHombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_city_wowzer);
        Button botonRegresar = (Button)findViewById(R.id.BotonRegistroCancelar);

        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent miIntent = new Intent(getApplicationContext(), com.jayktec.DialogaCity.LoginCityWowzer.class);
                startActivity(miIntent);

            }
        });
        Button botonRegistrar = (Button)findViewById(R.id.BotonRegistroOk);
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registro = new Registro();
                ETNombre = (EditText)findViewById(R.id.RegistroNombre);
                ETApellido = (EditText)findViewById(R.id.RegistroApellido);
                ETEmail = (EditText)findViewById(R.id.RegistroEmail);
                ETPassword = (EditText)findViewById(R.id.RegistroPassword);
                ETIdUsuario = (EditText)findViewById(R.id.RegistroID);
                SpEdoCivil = (Spinner)findViewById(R.id.estadoCivil);
                RadioHombre = (RadioButton)findViewById(R.id.radioHombre);

                registro.setNombre(ETNombre.getText().toString());
                registro.setApellido(ETApellido.getText().toString());
                registro.setEmail(ETEmail.getText().toString());
                registro.setPassword(ETPassword.getText().toString());
                registro.setId_wowzer(ETIdUsuario.getText().toString());

                //TODO verificar el Spinner
                //SpEdoCivil.g
                registro.setEstado_civil("S");
                if (RadioHombre.isChecked()){
                    registro.setSexo("M");
                }
                else{
                    registro.setSexo("F");
                }
                Log.i("nombre:", registro.getNombre());
                Log.i("Apellido:", registro.getApellido());
                Log.i("Email:", registro.getEmail());
                Log.i("Password:", registro.getPassword());
                Log.i("Alias:", registro.getId_wowzer());
                Log.i("Estado Civil:", registro.getEstado_civil());
                Log.i("Sexo:", registro.getSexo());
                Registro[] arrRegistro = {registro};

                RegistroWs registroWs = new RegistroWs(RegistroCityWowzer.this);
                registroWs.execute(arrRegistro);

                while (registroWs.isTrabajando() ) {
                    //esperando que finalice el ws
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                        }
                    }, 100);
                }



               if(registroWs.isResult()){
                    //TODO autologin o regresar???
                   Toast toast1 =
                           Toast.makeText(getApplicationContext(),
                                   "Registrado exitosamente", Toast.LENGTH_SHORT);

                   toast1.show();
                   finish();
                }
                else
               {
                    //TODO toast ... estas tostado
                   Toast toast1 =
                           Toast.makeText(getApplicationContext(),
                                   "Problemas con el registro intente nuevamente", Toast.LENGTH_LONG);

                   ETEmail.setText("");
                   ETPassword.setText("");

                   toast1.show();

                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_registro_city_wowzer, menu);
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
