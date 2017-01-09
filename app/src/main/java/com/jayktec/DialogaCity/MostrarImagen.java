package com.jayktec.DialogaCity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MostrarImagen extends ActionBarActivity {
    private byte[] imagenDenuncia;
    private Bitmap img;
    private String comentario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle datosEntrada = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_imagen);

        imagenDenuncia = datosEntrada.getByteArray("IMAGEN");
        comentario = datosEntrada.getString("COMENTARIO");
        ImageView IVImagenDenuncia = (ImageView)findViewById(R.id.MostrarImagenVentanaImagen);
        TextView TVComentario = (TextView)findViewById(R.id.MostrarImgEtiqDetalle);
        TVComentario.setText(comentario);
        img = BitmapFactory.decodeByteArray(imagenDenuncia,0,imagenDenuncia.length);
        IVImagenDenuncia.setImageBitmap(img);
        IVImagenDenuncia.setVisibility(View.VISIBLE);

        Button botonAceptar = (Button)findViewById(R.id.MostrarImagenBotonOK);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_mostrar_imagen, menu);
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
