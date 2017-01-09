package com.jayktec.DialogaCity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Usuario on 02-10-2015.
 */
public class AnuncioActivity extends Activity {

    private String denuncia;
    private BDControler bdlocal= new BDControler(AnuncioActivity.this,1);
    private int Notificacion;
    private int idDenuncia;
    private String wowzer;
    private int iDUsuarioWow;
    private String usuarioWowzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idDenuncia= getIntent().getIntExtra("idDenuncia", 0);
        wowzer= getIntent().getStringExtra("wowzer");
        iDUsuarioWow= getIntent().getIntExtra("IDUsuarioWow",0);
        if (Notificacion==1)
        {
            MediaPlayer mp=     MediaPlayer.create(AnuncioActivity.this,R.raw.feliz);
            mp.start();
        }
        usuarioWowzer=  bdlocal.devolverAliasUsrLogueado();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.ventana_consultar_anuncio);

        TextView TVFecha = (TextView) findViewById(R.id.VentanaConsultaTextoFecha);
        Typeface letra = Typeface.createFromAsset(getAssets(), "Multicolore.otf");
        TVFecha.setTypeface(letra);
        TextView TVComentario = (TextView) findViewById(R.id.VentanaConsultaTextoComentario);
        TVComentario.setTypeface(letra);
        TextView TVEstado = (TextView) findViewById(R.id.VentanaConsultaTextoEstado);
        ImageView ImViFotoDenuncia = (ImageView) findViewById(R.id.VentanaConsultaImagen);
        TextView TVConsultaImagen = (TextView) findViewById(R.id.VentanaConsultaBuscarImagen);
        TextView TVNo = (TextView) findViewById(R.id.VentanaConsultaImgNo);
        TextView TVSi = (TextView) findViewById(R.id.VentanaConsultaImgSi);
        TVConsultaImagen.setTypeface(letra);
        TVConsultaImagen.setEnabled(false);
        TVNo.setEnabled(true);
        TVNo.setVisibility(View.VISIBLE);
        TVSi.setEnabled(false);
        TVSi.setVisibility(View.INVISIBLE);

        TextView TVEtiqTipo = (TextView) findViewById(R.id.VentanaConsultaEtiqIcono);
        ImageView ImViImagenProgreso = (ImageView) findViewById(R.id.VentanaConsultaImagenProgreso);
        TVEtiqTipo.setTypeface(letra)        ;
        final Denuncia unaDenuncia = bdlocal.obtenerLaDenuncia(idDenuncia, wowzer);
       // Log.i("img iddenuncia",""+idDenuncia);
       // Log.i("img wowzer", "" + wowzer);


        if (unaDenuncia.getIdDenuncia() == null) {
            Toast.makeText(getApplicationContext(), "Problemas para Obtener datos del Marcados", Toast.LENGTH_LONG).show();
        } else {
           // Log.i("MP MARCADOR:","ID:"+unaDenuncia.getIdDenuncia()+" WOW:"+unaDenuncia.getIdDenWow()+" FeHo:"+unaDenuncia.getFechaDenuncia());
            final String eliDenWow = unaDenuncia.getIdDenWow().toString();
            final String elID = unaDenuncia.getIdDenuncia().toString();
            final String elComentario = unaDenuncia.getComentarios();
            final Double elRating = unaDenuncia.getRating();
            final byte[] imagenDenuncia = unaDenuncia.getImagen();
            int posicion = unaDenuncia.getFechaDenuncia().indexOf("T");
            if (posicion <= 0){
                TVFecha.setText(unaDenuncia.getFechaDenuncia());
            }
            else{
                TVFecha.setText(unaDenuncia.getFechaDenuncia().substring(0,posicion));
            }
            TVComentario.setText(unaDenuncia.getComentarios());
            String elEstado = unaDenuncia.getEstadoDenuncia();


            if (imagenDenuncia != null) {
                TVConsultaImagen.setEnabled(true);
                TVNo.setEnabled(false);
                TVNo.setVisibility(View.INVISIBLE);
                TVSi.setEnabled(true);
                TVSi.setVisibility(View.VISIBLE);
                TVConsultaImagen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog ventanaImagen1 = new Dialog(AnuncioActivity.this);
                        ventanaImagen1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        ventanaImagen1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        ventanaImagen1.setContentView(R.layout.ventana_mostrar_imagen);
                        ImageView IVImagenDenuncia = (ImageView) ventanaImagen1.findViewById(R.id.VentanaImgImagen);
                        TextView TVComentario = (TextView) ventanaImagen1.findViewById(R.id.VentanaImgEtiqDetalle);
                        TVComentario.setText(elComentario);
                        Bitmap img = BitmapFactory.decodeByteArray(imagenDenuncia, 0, imagenDenuncia.length);
                        IVImagenDenuncia.setImageBitmap(img);
                        Button botonVentanaImagen = (Button) ventanaImagen1.findViewById(R.id.VentanaImgBotonOK);
                        botonVentanaImagen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ventanaImagen1.dismiss();
                            }
                        });
                        IVImagenDenuncia.setVisibility(View.VISIBLE);
                        ventanaImagen1.show();


                    }
                });
            } else {
                ImagenWs imagenWs = new ImagenWs(AnuncioActivity.this);
                String[] denuncias = {eliDenWow};
                imagenWs.execute(denuncias);

                while (imagenWs.getTrabajando()) {
                    //        Log.i("lCom SUCIIO fin:",listado.getTrabajando().toString());
                    //esperando que finalice el ws
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                        }
                    }, 50);
                }
                final byte[] foto = bdlocal.obtenerLaDenunciaWOW(Integer.parseInt(eliDenWow)).getImagen();

                if (foto != null) {
                    TVConsultaImagen.setEnabled(true);
                    TVNo.setEnabled(false);
                    TVNo.setVisibility(View.INVISIBLE);
                    TVSi.setEnabled(true);
                    TVSi.setVisibility(View.VISIBLE);
                    TVConsultaImagen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final Dialog ventanaImagen = new Dialog(AnuncioActivity.this);
                            ventanaImagen.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            ventanaImagen.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            ventanaImagen.setContentView(R.layout.ventana_mostrar_imagen);
                            ImageView IVImagenDenuncia = (ImageView) ventanaImagen.findViewById(R.id.VentanaImgImagen);
                            TextView TVComentario = (TextView) ventanaImagen.findViewById(R.id.VentanaImgEtiqDetalle);
                            TVComentario.setText(elComentario);
                            Bitmap img = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                            IVImagenDenuncia.setImageBitmap(img);
                            Button botonVentanaImagen = (Button) ventanaImagen.findViewById(R.id.VentanaImgBotonOK);
                            botonVentanaImagen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ventanaImagen.dismiss();
                                }
                            });
                            IVImagenDenuncia.setVisibility(View.VISIBLE);
                            ventanaImagen.show();

                        }
                    });
                } else {
                    // NO HAY IMAGEN y punto!!!
                    Log.i("IMG", "no hay imagen carajo");
                    TVConsultaImagen.setEnabled(false);
                    TVNo.setEnabled(true);
                    TVNo.setVisibility(View.VISIBLE);
                    TVSi.setEnabled(false);
                    TVSi.setVisibility(View.INVISIBLE);
                }
                //todo falta una imagen por defecto de no hay foto.....
                // }


            }


            Button botonValorar = (Button) findViewById(R.id.botonVentana2);
            botonValorar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               //     finish();
                    mostrarVentanaRating(elID, elComentario, elRating);
                }
            });
            //TODO Ver si es necesario desactivar el valorar segun el estado
            //botonValorar.setEnabled(false);
            ;
            Button botonCompartir = (Button) findViewById(R.id.boton_compartir);
            botonCompartir.setOnClickListener((new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            Compartir compartirDenuncia = new Compartir(AnuncioActivity.this, bdlocal.obtenerLaDenuncia(Integer.parseInt(elID), wowzer));
                            compartirDenuncia.compartirTwitter();
                        }
                    })
            )
            ;

        }

        Button botonAceptar = (Button) findViewById(R.id.botonVentana1);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                finish();

                                            }
                                        }
        );


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    private void mostrarVentanaRating(String idDenuncia, String descripcion, Double rating) {
        TextView TVIdDenuncia;
        TextView TVComentario;
        final RatingBar barraEstrellas;
        final String elID = idDenuncia;

        final Dialog miVentanaRating = new Dialog(AnuncioActivity.this);
        miVentanaRating.requestWindowFeature(Window.FEATURE_NO_TITLE);
        miVentanaRating.setContentView(R.layout.ventana_valorar_denuncia);
        miVentanaRating.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TVIdDenuncia = (TextView) miVentanaRating.findViewById(R.id.TextoIdDenuncuaVentanaValora);
        TVComentario = (TextView) miVentanaRating.findViewById(R.id.TextoDescripcionVetanaValora);
        barraEstrellas = (RatingBar) miVentanaRating.findViewById(R.id.BarraEstrellasVentanaConsulta);


        TVIdDenuncia.setText(idDenuncia);
        TVComentario.setText(descripcion);
        barraEstrellas.setRating(rating.floatValue());

        Button botonValorar = (Button) miVentanaRating.findViewById(R.id.BotonValorarVentanaValorar);
        botonValorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String RTN = String.valueOf(barraEstrellas.getRating());
                final Double elRating = Double.valueOf(RTN);
                bdlocal.actualizarValoracion(elID, usuarioWowzer, elRating);
                miVentanaRating.dismiss();
            }
        });
        Button botonCancelar = (Button) miVentanaRating.findViewById(R.id.BotonCancelarVentanaValorar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miVentanaRating.dismiss();
            }
        });
        miVentanaRating.show();
    }


}
