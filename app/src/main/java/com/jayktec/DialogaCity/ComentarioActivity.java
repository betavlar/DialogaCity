package com.jayktec.DialogaCity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ComentarioActivity extends Activity {
    private String denuncia;

    private int idDenuncia;
    private int idDenunciaWs;
    private int iDUsuarioWow;
    private String misComentarios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);
        Button botonCrear= (Button)findViewById(R.id.botonCrearComentario);
        final EditText textoComentario = (EditText)findViewById(R.id.TextComentarioNuevo);
        denuncia= getIntent().getStringExtra("denuncia");
        final int usuarioWowzer= getIntent().getIntExtra("UsuarioWowzer",0);

        Bundle datosEntrada = getIntent().getExtras();
        idDenuncia = datosEntrada.getInt("IDDenuncia");
        iDUsuarioWow = datosEntrada.getInt("IDWowzer");
        idDenunciaWs = datosEntrada.getInt("IDDenunciaWS");
        misComentarios = datosEntrada.getString("ComentDenuncia");
        //Log.i("com elid",""+idDenuncia);
        //Log.i("com idUsuarioWow",""+iDUsuarioWow);
        //Log.i("com iddenws",""+idDenunciaWs);
        //Log.i("com com"," "+misComentarios);

        botonCrear.setOnClickListener(new View.OnClickListener() {
            String Denuncia=denuncia;
            int UsuarioWowzer=usuarioWowzer;
            @Override
            public void onClick(View v) {
              String Denuncias =Denuncia;
             Comentario comentario= new  Comentario ();
                comentario.setId_denuncia(Integer.parseInt(Denuncias));
                comentario.setId_wow(UsuarioWowzer);
                String texto =textoComentario.getText().toString();
                comentario.set_comentario(texto);
                Comentario [] lista ={comentario};
             //   Log.i("comAct:","creado el comentario");
                ComentarioWs nuevoComentario= new ComentarioWs();
                nuevoComentario.execute(lista);
                finish();

                Bundle datosComentarios = new Bundle();

                BDControler bdlocal = new BDControler(ComentarioActivity.this,1);
                datosComentarios.putInt("IDDenuncia", idDenuncia);
                datosComentarios.putInt("IDWowzer", iDUsuarioWow);
                datosComentarios.putInt("IDDenunciaWS", idDenunciaWs);
                datosComentarios.putString("ComentDenuncia",misComentarios);
                Intent miIntent = new Intent(getApplicationContext(), com.jayktec.DialogaCity.ListadoComentarios.class);
                miIntent.putExtras(datosComentarios);
                miIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(miIntent);

        }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comentario, menu);
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
