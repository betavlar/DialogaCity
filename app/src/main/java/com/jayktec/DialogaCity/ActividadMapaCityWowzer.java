package com.jayktec.DialogaCity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.here.android.mpa.cluster.ClusterLayer;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class ActividadMapaCityWowzer extends ActionBarActivity  {
    private Map miMapa = null;
    private ClusterLayer clusterLayer;
    private MapFragment fragmentoMapa = null;
    private BDControler bdlocal;
    private ArrayList<Denuncia> arregloDenuncias;
    private ArrayList<Denuncia> arregloDenunciasForaneas;
    private String usuarioWowzer;
    private int iDUsuarioWow;
    private Dialog ventanaAgregarDenuncia;
    private int Notificacion;
    private DataActualReceiver actualizaMarcador;
    //Variables para manejo del mapa
    private GPSTracker ubicacionGPS;
    private Context miContexto;
    private Double miLatitud;
    private Double miLongitud;

    //FOTOS
    private Bitmap fotoDenuncia;
    private static final int SOLICITUD_DE_FOTO = 0;
    private ByteArrayOutputStream streamDatosFotos;

    //variables del broadcasting
    private IntentFilter filter;
    private  DataActualReceiver rcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Notificacion= getIntent().getIntExtra("Notificacion",0);

        setContentView(R.layout.activity_actividad_mapa_city_wowzer);
        miContexto = ActividadMapaCityWowzer.this;
        bdlocal = new BDControler(this, 1);
        Integer idDenunciaLast = bdlocal.devolverIdUltimaDenuncia();
       // Log.i("ULTIMA DENUNCIA:", " " + idDenunciaLast.toString());

        usuarioWowzer = bdlocal.devolverAliasUsrLogueado();
        iDUsuarioWow = bdlocal.devolverIDUsrLogueado();
        // comprobar que hay un usuario conectado o enviarlo a loguearse obligatoriamente
        if (usuarioWowzer=="X")
        {
            finish();
            Intent inicio = new Intent(getApplicationContext(), com.jayktec.DialogaCity.LoginCityWowzer.class);
            inicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(inicio);

        }

        Integer control = bdlocal.devolverIdUltimaDenuncia();
        //Log.i("ws ulden:", " " + control);
        Integer DenWow = 0;
        /*
        DenunciaMysqlWs cargaDenuncia = new DenunciaMysqlWs(ActividadMapaCityWowzer.this);
        // se verifica si es la primera vez , si es asi se deben traer todos los registros
        int temp=iDUsuarioWow;
        int temp1=control;

        if(bdlocal.primeraVez())
        {
            temp1=0;
            temp = 0;
        }
         Integer [] lista = {temp1,temp};

        cargaDenuncia.execute(lista);
*/
        arregloDenuncias = new ArrayList<>();
        ubicacionGPS = new GPSTracker(this);
        ubicarPosicion();
        //yle 12/11/2015
        //Crear clusterLayer para poner los marcadores alli

        fragmentoMapa = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentoMapa);
        fragmentoMapa.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    miMapa = fragmentoMapa.getMap();
                    PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
                    fragmentoMapa.getMapGesture().addOnGestureListener(new MyOnGestureListener());

                    clusterLayer= new ClusterLayer();
                    //clusterLayer.setTheme(BasicClusterStyle);
                    miMapa.addClusterLayer(clusterLayer);
                    SimpleDateFormat laHora = new SimpleDateFormat("HH");
                    int hora = Integer.parseInt(laHora.format(Calendar.getInstance().getTime()));
                    if (hora > 17 || hora < 7) {
                        miMapa.setMapScheme(Map.Scheme.NORMAL_NIGHT);
                    } else {
                        miMapa.setMapScheme(Map.Scheme.NORMAL_DAY);
                    }
                    //miMapa.setCenter(new GeoCoordinate(miLatitud, miLongitud, 0.0), Map.Animation.NONE);
                    //revisar si viene producto de una notificacion
                    //Log.i("not ", "" + Notificacion);
                    //Log.i("not lat  ",""+ getIntent().getDoubleExtra("Latitud",0) );
                    //Log.i("not long",""+ getIntent().getDoubleExtra("Longitud",0) );

                    if (Notificacion==1)
                    {
                        Notificacion=0;
                        miMapa.setCenter(new GeoCoordinate(getIntent().getDoubleExtra("Latitud",miLatitud),getIntent().getDoubleExtra("Longitud",miLongitud), 0.0), Map.Animation.NONE);

                    }

                    else
                    {
                        miMapa.setCenter(new GeoCoordinate(ubicacionGPS.getMiLatitud(), ubicacionGPS.getMiLongitud(), 0.0), Map.Animation.NONE);
                    }
                    miMapa.setZoomLevel(16.5);
                    //yle 13/102015 para que hacer dos lecturas si al final es to do

                    //arregloDenuncias = bdlocal.obtenerTodasLasDenuncias(iDUsuarioWow);
                    //arregloDenunciasForaneas = bdlocal.obtenerTodasLasDenuncias();
                    //arregloDenunciasForaneas = bdlocal.obtenerTodasLasDenunciasOtros(iDUsuarioWow);

                    arregloDenuncias = bdlocal.obtenerTodasLasDenuncias();

                    for (int i=0;i< arregloDenuncias.size();i++){
                        colocarMarcador(arregloDenuncias.get(i).getLatitud(),
                                arregloDenuncias.get(i).getLongitud(),
                                //28/09/2015 yle
                                //11-10-2015 AME devolver la cochinada que hizo el chino
                                arregloDenuncias.get(i).getIdDenuncia(),
                                //arregloDenuncias.get(i).getIdDenWow(),
                                arregloDenuncias.get(i).getWowzer(),
                                arregloDenuncias.get(i).getTipoDenuncia(),
                                arregloDenuncias.get(i).getEstadoDenuncia());

                    }

                    /*for (int J = 0; J < arregloDenunciasForaneas.size(); J++) {
                        colocarMarcador(arregloDenunciasForaneas.get(J).getLatitud(),
                                arregloDenunciasForaneas.get(J).getLongitud(),
                                //28/09/2015 yle
                                //11-10-2015 AME devolver la cochinada que hizo el chino
                                arregloDenunciasForaneas.get(J).getIdDenuncia(),
                                //arregloDenunciasForaneas.get(i).getIdDenWow(),
                                arregloDenunciasForaneas.get(J).getWowzer(),
                                arregloDenunciasForaneas.get(J).getTipoDenuncia(),
                                arregloDenunciasForaneas.get(J).getEstadoDenuncia());

                    }*/

                } else {
                    Log.i("HERE SDK 3.0:", "Error al cargar el Mapa");
                }

            }
        });
        ImageButton botonWoozie = (ImageButton) findViewById(R.id.botonMenu);
        botonWoozie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubicarPosicion();
                levantarDialogoMenu(miLatitud, miLongitud);
            }
        });
        ImageButton botonListar = (ImageButton) findViewById(R.id.botonListarDenuncias);
        botonListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle grupoDatos = new Bundle();
                grupoDatos.putInt("IDWowzer", iDUsuarioWow);
                grupoDatos.putString("AliasWowzer", usuarioWowzer);
                Intent miIntent = new Intent(getApplicationContext(), com.jayktec.DialogaCity.ListadoDenuncias.class);
                miIntent.putExtras(grupoDatos);
                miIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(miIntent);

            }

        });
        ImageButton botonConfiguracion = (ImageButton)findViewById(R.id.botonMenuOpciones);
        botonConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConfig = new Intent(getApplicationContext(), com.jayktec.DialogaCity.MenuConfiguracion.class);
                intentConfig.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intentConfig);
            }
        });

        //yle 9-11/2015
    //    comprobarServicios();
/*
        filter = new IntentFilter();
        filter.addAction(ServicioSincronizacion.REFRESH_DATA_INTENT);
        rcv = new DataActualReceiver();
        registerReceiver(rcv, filter);
*/
     }

    @Override
    public void onResume() {
        super.onResume();
    //  registerReceiver(rcv,filter);
        if (fragmentoMapa.isResumed()) {
            PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
        }
        if (bdlocal.devolverAliasUsrLogueado()=="X")
        {
            finish();
            Intent inicio = new Intent(getApplicationContext(), com.jayktec.DialogaCity.LoginCityWowzer.class);
            inicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(inicio);

        }

    }

    @Override
    public void onPause() {
        PositioningManager.getInstance().stop();
        ubicacionGPS.detenerGPS();
        super.onPause();
        //yle 9-11/2015
        comprobarServicios();
        // limpiar notificaciones
        bdlocal.depurarNotificaciones();
  //      unregisterReceiver(rcv);


    }

    @Override
    public void onDestroy() {
        //PositioningManager.getInstance().removeListener();
        miMapa = null;
        // limpiar notificaciones
        bdlocal.depurarNotificaciones();

        super.onDestroy();
    }

    /**
     * Metodo Utilizado para Ubicar la latitud o logintud del Telefono
     * Si no se encuentra busca la ultima posicion conocida
     */
    private void ubicarPosicion() {
        if (ubicacionGPS.obtenerLocalizacionOnline()) {
            miLatitud = ubicacionGPS.getMiLatitud();
            miLongitud = ubicacionGPS.getMiLongitud();
         //   Log.i("GPS_T", "Online");
        } else {
            ubicacionGPS.obtenerLocalizacionOffline();
            miLatitud = ubicacionGPS.getMiLatitud();
            miLongitud = ubicacionGPS.getMiLongitud();
            Log.i("GPS_T", "Offline");
        }
    }

    /**
     * Activa el Dialogo que muestra el menu
     * @param latitud  Latitud de donde se va a centrar el mapa y colocar el Marcador
     * @param longitud Longtidu en el cual se va a centrar el mapa y colocar el Marcador
     */
    private void levantarDialogoMenu(Double latitud, Double longitud) {
        miMapa.setCenter(new GeoCoordinate(latitud, longitud, 0.0), Map.Animation.NONE);
        miMapa.setZoomLevel(16.5);
/* tipos de denuncias

SEMAFORO	1
CLOACA	2
CALLE	3
ARBOL	4
ALUMBRADO	5
BASURA	6
Recreacion 7
actividad deportiva 8
actividad cultural 9
    */
        final Dialog miVentanaMenu = new Dialog(ActividadMapaCityWowzer.this);
        miVentanaMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        miVentanaMenu.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        miVentanaMenu.setContentView(R.layout.ventana_menu);
        Button botonAgua = (Button) miVentanaMenu.findViewById(R.id.botonAgua);
        Button botonSemaforo = (Button) miVentanaMenu.findViewById(R.id.botonSemaforo);
        botonSemaforo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miVentanaMenu.dismiss();
                agregarDenuncia(1, R.drawable.semaforo);
            }
        });
        botonAgua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miVentanaMenu.dismiss();
                agregarDenuncia(2, R.drawable.agua);
            }
        });
        Button botonViaPublica = (Button) miVentanaMenu.findViewById(R.id.botonViaPublica);
        botonViaPublica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miVentanaMenu.dismiss();
                agregarDenuncia(3, R.drawable.viapublica);
            }
        });
        Button botonPoste = (Button) miVentanaMenu.findViewById(R.id.botonPoste);
        botonPoste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miVentanaMenu.dismiss();
                agregarDenuncia(4, R.drawable.poste);
            }
        });
        Button botonIluminacion = (Button) miVentanaMenu.findViewById(R.id.botonIluminacion);
        botonIluminacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miVentanaMenu.dismiss();
                agregarDenuncia(5, R.drawable.iluminacion);
            }
        });
        Button botonMalOlor = (Button) miVentanaMenu.findViewById(R.id.botonMalOlor);
        botonMalOlor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miVentanaMenu.dismiss();
                agregarDenuncia(6, R.drawable.malolor);
            }
        });
        miVentanaMenu.show();
    }

    /**
     * Este metodo levanta el dialogo requerido para registrar una denuncia en la Base de datos
     *
     * @param tipoDenuncia   Descripcion del tipo de Denuncia a Agregar
     * @param imagenDenuncia Recibe el ID drawable del tipo de Denuncia/Alerta
     */
    private void agregarDenuncia(int tipoDenuncia, int imagenDenuncia) {
        ventanaAgregarDenuncia = new Dialog(ActividadMapaCityWowzer.this);
        final int laDenuncia = tipoDenuncia;
        String tituloDenuncia = "";
        fotoDenuncia = null;
        ventanaAgregarDenuncia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ventanaAgregarDenuncia.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ventanaAgregarDenuncia.setContentView(R.layout.ventana_agregar_denuncia);
        final Button botonGuardarDenuncia = (Button) ventanaAgregarDenuncia.findViewById(R.id.btnV21);
        Button botonFoto = (Button) ventanaAgregarDenuncia.findViewById(R.id.btnV22);
        ImageButton botonCerrar = (ImageButton) ventanaAgregarDenuncia.findViewById(R.id.BotonCerrarAgregaDenuncia);
        TextView TVMarcoDenuncia = (TextView) ventanaAgregarDenuncia.findViewById(R.id.textoVentanaDenuncia);
        switch (tipoDenuncia) {
            case 1:
                tituloDenuncia = "Semaforo";
                break;
            case 2:
                tituloDenuncia = "Cloaca";
                break;
            case 3:
                tituloDenuncia = "Calle";
                break;
            case 4:
                tituloDenuncia = "Arbol";
                break;
            case 5:
                tituloDenuncia = "Alumbrado";
                break;
            case 6:
                tituloDenuncia = "Basura";
                break;
        }
        TVMarcoDenuncia.setText("ALERTA: " + tituloDenuncia);
        TVMarcoDenuncia.setCompoundDrawablesWithIntrinsicBounds(imagenDenuncia, 0, 0, 0);

        botonGuardarDenuncia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText TVComentarios = (EditText) ventanaAgregarDenuncia.findViewById(R.id.textoEditableV2);
                Long idDen;
                SimpleDateFormat formatoFecha = new SimpleDateFormat("MM-dd-yyyy");
                Denuncia unaDenuncia = new Denuncia();
                unaDenuncia.setIdWowzer(iDUsuarioWow);
              //  Log.i("ID_USUARIO:", "" + iDUsuarioWow);
                unaDenuncia.setIdDenWow(0);
                unaDenuncia.setWowzer(usuarioWowzer);
                unaDenuncia.setFechaDenuncia(formatoFecha.format(new Date()));
                unaDenuncia.setLatitud(miLatitud);
                unaDenuncia.setLongitud(miLongitud);
                unaDenuncia.setTipoDenuncia(laDenuncia);
                unaDenuncia.setEstadoDenuncia("P");
                unaDenuncia.setComentarios(TVComentarios.getText().toString());
                if (fotoDenuncia != null) {
                    streamDatosFotos = new ByteArrayOutputStream();
                    fotoDenuncia.compress(Bitmap.CompressFormat.JPEG, 100, streamDatosFotos);
                    byte[] arregloBytes = streamDatosFotos.toByteArray();
                    unaDenuncia.setImagen(arregloBytes);
                }
                unaDenuncia.setRating(0.0);
                unaDenuncia.setFecha_ult_actualizacion(formatoFecha.format(new Date()));
                idDen = bdlocal.insertarDenuncia(unaDenuncia);
                MediaPlayer reproductor = MediaPlayer.create(ActividadMapaCityWowzer.this, R.raw.alegre);
                reproductor.start();

                //28/09/2015 yle
                // Cambios YLe y ame
                unaDenuncia.setIdDenuncia(idDen.intValue());
                DenunciaWs guardaDenunciaMysql = new DenunciaWs(ActividadMapaCityWowzer.this);
                Denuncia[] lista = {unaDenuncia};
                guardaDenunciaMysql.execute(lista);
               // Log.i("ID Adentro:", "" + idDen.intValue());
                //28/09/2015 yle

                colocarMarcador(miLatitud, miLongitud, idDen.intValue(), usuarioWowzer, laDenuncia, "P");

                            //Fin YLE
                ventanaAgregarDenuncia.dismiss();
            }
        });

        botonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               intentCamara.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivityForResult(intentCamara, SOLICITUD_DE_FOTO);
            }
        });

        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ventanaAgregarDenuncia.dismiss();
            }
        });
        ventanaAgregarDenuncia.show();
    }

private void actualizarMarcador()
{
//miMapa.removeMapObject();

    //MapObject marcador= miMapa.
}

    /**
     * Metodo Comun para colocar un marcador en el mapa
     *
     * @param latitud       Latitud del Marcador
     * @param longitud      Longitud del Marcador
     * @param idDenuncia    ID de la denuncia en la BD local
     * @param usuarioWowzer usuario del telefono que agrego la denuncia
     * @param tipo          tipo de la denuncia para identificar el marcador
     * @param estado        Estado de la denuncia
     */
    private void colocarMarcador(Double latitud, Double longitud, int idDenuncia, String usuarioWowzer, int tipo, String estado) {
       // Log.i("COLOCAR_M:", "Lat:" + latitud + " Lon:" + longitud + " ID:" + idDenuncia + " WOW" + usuarioWowzer);
        //Log.i("COLOCAR_M:", "tipo:" + tipo + " estado:" + estado);
        MapMarker marcador = new MapMarker();
        marcador.setCoordinate(new GeoCoordinate(latitud, longitud, 0.0));
        Image miIcono = new Image();
        try {
/* tipos de denuncias

SEMAFORO	1
CLOACA	2
CALLE	3
ARBOL	4 poste???
ALUMBRADO	5
BASURA	6

Recreacion 7
actividad deportiva 8
actividad cultural 9
    */

/*tipos de estado
'1', 'Creada'
'2', 'En Atención'
'3', 'Procesada'
'4', 'Cerrada'
'5', 'Anulada'

             */

            if (estado.equals("3")) {
                switch (tipo) {
                    case 1:
                        miIcono.setImageResource(R.drawable.semaforo_hotspot_cerrado);
                        break;
                    case 2:
                        miIcono.setImageResource(R.drawable.agua_hotspot_cerrado);
                        break;
                    case 3:
                        miIcono.setImageResource(R.drawable.viapublica_hotspot_cerrado);
                        break;
                    case 4:
                        miIcono.setImageResource(R.drawable.poste_hotspot_cerrado);
                        break;
                    case 5:
                        miIcono.setImageResource(R.drawable.iluminacion_hotspot_cerrado);
                        break;
                    case 6:
                        miIcono.setImageResource(R.drawable.malolor_hotspot_cerrado);
                        break;
                    case 7 :
                        miIcono.setImageResource(R.drawable.recreacion);
                    case 8 :
                        miIcono.setImageResource(R.drawable.deportes);
                    case 9 :
                        miIcono.setImageResource(R.drawable.arteycultura);
                }
            } else {
                switch (tipo) {
                    case 1:
                        miIcono.setImageResource(R.drawable.semaforo_hotspot_activo);
                        break;
                    case 2:
                        miIcono.setImageResource(R.drawable.agua_hotspot_activo);
                        break;
                    case 3:
                        miIcono.setImageResource(R.drawable.viapublica_hotspot_activo);
                        break;
                    case 4:
                        miIcono.setImageResource(R.drawable.poste_hotspot_cerrado);
                        break;
                    case 5:
                        miIcono.setImageResource(R.drawable.iluminacion_hotspot_activo);
                        break;
                    case 6:
                        miIcono.setImageResource(R.drawable.malolor_hotspot_activo);
                        break;
                    case 7 :
                        miIcono.setImageResource(R.drawable.recreacion);
                    case 8 :
                        miIcono.setImageResource(R.drawable.deportes);
                    case 9 :
                        miIcono.setImageResource(R.drawable.arteycultura);

                }
            }
            marcador.setIcon(miIcono);

        } catch (IOException e) {
            e.printStackTrace();
        }
        marcador.setDescription("#ID#" + idDenuncia + "#WOWZER#" + usuarioWowzer);
        marcador.setVisible(true);
        //miMapa.addMapObject(marcador);
        clusterLayer.addMarker(marcador);
    }

    /**
     * Maneja el proceso para guardar la foto en la Base de datos
     *
     * @param requestCode Codigo de Solicitud de foto
     * @param resultCode  Codigo resultado de la actividad Tomar Foto
     * @param data        Data de la Imagen
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView TVSiNo = (TextView) ventanaAgregarDenuncia.findViewById(R.id.etiquetaSINOAgregaDenuncia);
        if (requestCode == SOLICITUD_DE_FOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            fotoDenuncia = (Bitmap) extras.get("data");
            TVSiNo.setText("Si");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_actividad_mapa_city_wowzer, menu);
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

    /**
     * Metodo requerido para manejar los gestos del Mapa
     * Seleccion
     * Zoom
     * PAN IN y OUT
     * etc.
     */
    private class MyOnGestureListener implements MapGesture.OnGestureListener {
        @Override
        public void onPanStart() {

        }

        @Override
        public void onPanEnd() {

        }

        @Override
        public void onMultiFingerManipulationStart() {

        }

        @Override
        public void onMultiFingerManipulationEnd() {

        }

        @Override
        public boolean onMapObjectsSelected(List<ViewObject> list) {
            ViewObject objetoMapa;
            String datosIdWowzer;
            int idDenuncia;
            String wowzer;

            TextView TVId;
            TextView TVFecha;
            TextView TVComentario;
            TextView TVTipo;
            TextView TVEstado;
            TextView TVConsultaImagen;
            TextView TVEtiqTipo;
            TextView TVNo;
            TextView TVSi;
            ImageView ImViFotoDenuncia;
            ImageView ImViImagenProgreso;

            Denuncia unaDenuncia;
            for (int i = 0; i < list.size(); i++) {
                objetoMapa = list.get(i);
                if (objetoMapa.getBaseType().toString() == "USER_OBJECT") {
                    MapMarker hotspot = (MapMarker) objetoMapa;
                    datosIdWowzer = hotspot.getDescription();
                    idDenuncia = obtenerIdDenuncia(datosIdWowzer);
                    wowzer = obtenerUsuarioDenuncia(datosIdWowzer);
                    Intent miDenuncia = new Intent(getApplicationContext(), com.jayktec.DialogaCity.DenunciaActivity.class);
                    Bundle grupoDatos = new Bundle();
                    grupoDatos.putInt("Notificacion", 0);
                    grupoDatos.putInt("IDUsuarioWow", iDUsuarioWow);
                    grupoDatos.putInt("idDenuncia", idDenuncia);
                    grupoDatos.putString("wowzer", wowzer);

                    grupoDatos.putString("AliasWowzer", usuarioWowzer);
                    miDenuncia.putExtras(grupoDatos);
                    miDenuncia.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(miDenuncia);

                }
            }
            return false;
        }

        @Override
        public boolean onTapEvent(PointF pointF) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF pointF) {
            return false;
        }

        @Override
        public void onPinchLocked() {

        }

        @Override
        public boolean onPinchZoomEvent(float v, PointF pointF) {
            return false;
        }

        @Override
        public void onRotateLocked() {

        }

        @Override
        public boolean onRotateEvent(float v) {
            return false;
        }

        @Override
        public boolean onTiltEvent(float v) {
            return false;
        }

        @Override
        public boolean onLongPressEvent(PointF pointF) {
            miLatitud = miMapa.pixelToGeo(pointF).getLatitude();
            miLongitud = miMapa.pixelToGeo(pointF).getLongitude();
            levantarDialogoMenu(miLatitud, miLongitud);
            return false;
        }

        @Override
        public void onLongPressRelease() {

        }

        @Override
        public boolean onTwoFingerTapEvent(PointF pointF) {
            return false;
        }
    }

    /**
     * Obtiene el ID de la Denuncia cargada en la descripcion del Marcador
     *
     * @param datosIdUsr String que contiene los datos del ID de la alerta y el alias del wowzer en el Marcador
     * @return ID de la Denuncia/Alerta
     */
    private int obtenerIdDenuncia(String datosIdUsr) {
        int posHasta = datosIdUsr.indexOf("#WOWZER#");
        return Integer.parseInt(datosIdUsr.substring(4, posHasta));
    }

    /**
     * Obtiene el USUARIO/WOWZER que hizo la denuncia agregado en un Marker
     *
     * @param datosIdUsr String que contiene los datos del ID de la alerta y el alias del wowzer en la sesion del dispositivo
     * @return Alias del WOWZER en el marcador
     */
    private String obtenerUsuarioDenuncia(String datosIdUsr) {
        return datosIdUsr.substring(8 + datosIdUsr.indexOf("#WOWZER#"), datosIdUsr.length());
    }

    /**
     * Revisa si los servicios de la aplicacion se estan ejecutando y de no ser asi los enciende
     */
    private void comprobarServicios()
    {
        Class[] lista={ServicioSincronizacion.class,ServicioNotificacion.class};

        for (int i=0 ;i<lista.length;i++ )
        {
            if (!isMyServiceRunning(lista[i]))
            {
                // Log.i("enc serv",lista[i].toString());
                encenderServicio(lista[i]);
            }
        }
    }

    /**
     * Revisa si se esta ejecutando o no un servicio
     * @param serviceClass recibe el nombre del servicio para saber si esta funcionando
     * @return devuelve verdader o falso según sea el caso
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enciendo un servicio
     * @param serviceClass nombre del servicio a encender
     * @return el estado del servicio.
     */
    private boolean encenderServicio(Class<?> serviceClass){
        try {
            Intent service = new Intent(this, serviceClass);
            service.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startService(service);
            return   true;
        }
        catch (Exception e)
        {
            Log.i("error enc serv", e.getMessage().toString());
            return false;
        }
    }

    /**
     * Actualiza los marcadores actualizados
     * @param id_denuncia
     */
    public void cambiarMarcador(int id_denuncia) {

                boolean encontrado=false;
                Collection<MapMarker> marcadores= clusterLayer.getMarkers();
                Iterator<MapMarker> bucleMarcadores= marcadores.iterator();
                MapMarker marcador = new MapMarker();
                while   (bucleMarcadores.hasNext()|| encontrado)
                {
                    marcador= bucleMarcadores.next();
                    String descripcion=marcador.getDescription();
//                    marcador.setDescription("#ID#" + idDenuncia + "#WOWZER#" + usuarioWowzer);
                    Log.i("descripcion",descripcion);
                    descripcion=descripcion.substring(0,3);
                    Log.i("descripcion",descripcion);
                    int idmarcador=Integer.parseInt(descripcion.substring(0,descripcion.indexOf("#")));
                    if (idmarcador==id_denuncia)
                    {
                        encontrado=true;
                    }

                }

                if (encontrado==true)
                {
                    clusterLayer.removeMarker(marcador);
                    Denuncia denuncia= bdlocal.obtenerLaDenuncia(id_denuncia);
                    colocarMarcador(denuncia.getLatitud(),denuncia.getLongitud(),id_denuncia,denuncia.getWowzer(),denuncia.getTipoDenuncia(),denuncia.getEstadoDenuncia());
                }


            }
}


