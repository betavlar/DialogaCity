package com.jayktec.DialogaCity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ListadoDenuncias extends Activity {
    private BDControler bdlocal;
    private ArrayList<ListadoDenunciaItem> arregloListadoDeItem = new ArrayList<ListadoDenunciaItem>();
    private ListView miListView;
    private int iDUsuarioWow;
    private String UserWowzer;
    private ArrayList<Denuncia> arregloDenuncias = new ArrayList<Denuncia>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle datosEntrada = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_denuncias);

        bdlocal = new BDControler(this, 1);
        iDUsuarioWow = datosEntrada.getInt("IDWowzer");  //bdlocal.devolverIDUsrLogueado();

        arregloDenuncias = bdlocal.obtenerTodasLasDenuncias(iDUsuarioWow);

        for (int i = 0; i < arregloDenuncias.size(); i++){
            ListadoDenunciaItem listadoItem = new ListadoDenunciaItem();

            //listadoItem.TextoDenuncia = arregloDenuncias.get(i).getIdDenuncia().toString();
            Integer tipo = arregloDenuncias.get(i).getTipoDenuncia();
            //todo hay que hacer el catalogo de tipo vs el mensaje
            /* TEXTO de la denuncia
            switch (tipo.intValue()){
                case 1: listadoItem.TextoDenuncia = "Semaforo";
                    break;
                case 2: listadoItem.TextoDenuncia = "Cloaca";
                    break;
                case 3: listadoItem.TextoDenuncia = "Calle";
                    break;
                case 4: listadoItem.TextoDenuncia = "Arbol";
                    break;
                case 5: listadoItem.TextoDenuncia = "Alumbrado";
                    break;
                case 6: listadoItem.TextoDenuncia = "Basura";
                    break;
            }
            */
            listadoItem.TextoDenuncia = arregloDenuncias.get(i).getComentarios();


//
            listadoItem.IDDenuncia = arregloDenuncias.get(i).getIdDenuncia().intValue();
            switch (tipo.intValue()){
                case 1:
                    listadoItem.ImagenDenuncia = R.drawable.semaforo;
                    break;
                case 2:
                    listadoItem.ImagenDenuncia = R.drawable.agua;
                    break;
                case 3:
                    listadoItem.ImagenDenuncia = R.drawable.viapublica;
                    break;
                case 4:
                    listadoItem.ImagenDenuncia = R.drawable.poste;
                    break;
                case 5:
                    listadoItem.ImagenDenuncia = R.drawable.iluminacion;
                    break;
                case 6:
                    listadoItem.ImagenDenuncia = R.drawable.malolor;
                    break;
              }
            listadoItem.idWow=arregloDenuncias.get(i).getIdDenWow().intValue();

          //  listadoItem.ImagenDenuncia=arregloDenuncias.get(i).getImagen().;

            listadoItem.TextoValoracion= arregloDenuncias.get(i).getRating().toString();
            int posicionT = arregloDenuncias.get(i).getFechaDenuncia().indexOf("T");
            if (posicionT <= 0){
                listadoItem.FechaDenuncia = arregloDenuncias.get(i).getFechaDenuncia();
            }
            else{
                listadoItem.FechaDenuncia = arregloDenuncias.get(i).getFechaDenuncia().substring(0,posicionT);
            }


            String elEstado = arregloDenuncias.get(i).getEstadoDenuncia();
            if (elEstado.isEmpty()){
                elEstado = "X";
            }
            switch (elEstado){
                case "P":
                    listadoItem.ImagenEstado = R.drawable.pendiente;
                    break;
                case "P1":
                    listadoItem.ImagenEstado = R.drawable.enproceso1;
                    break;
                case "P2":
                    listadoItem.ImagenEstado = R.drawable.enproceso2;
                    break;
                case "R":
                    listadoItem.ImagenEstado = R.drawable.realizado;
                    break;
                default:
                    listadoItem.ImagenEstado = R.drawable.pendiente;
                    break;
            }
            arregloListadoDeItem.add(listadoItem);
        }

        AdaptadorListadoDenuncia miAdaptador = new AdaptadorListadoDenuncia(this,R.layout.listado_denuncia_item,arregloListadoDeItem);

        miListView = (ListView)findViewById(R.id.ListaDeDenuncias);
        View cabecera = (View)getLayoutInflater().inflate(R.layout.cabecera_listado,null);
        miListView.addHeaderView(cabecera);
        miListView.setAdapter(miAdaptador);
        miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position >=1){
                    ListadoDenunciaItem elItem = (ListadoDenunciaItem)parent.getItemAtPosition(position);
                    Bundle datosComentarios = new Bundle();
                    datosComentarios.putInt("IDDenuncia", elItem.IDDenuncia);
                    datosComentarios.putInt("IDWowzer", iDUsuarioWow);
                    datosComentarios.putInt("IDDenunciaWS",elItem.idWow);
                    datosComentarios.putString("ComentDenuncia",elItem.TextoDenuncia);
                    datosComentarios.putString("FechaDenuncia",elItem.FechaDenuncia);
                    Intent miIntent = new Intent(getApplicationContext(),com.jayktec.DialogaCity.ListadoComentarios.class);
                    miIntent.putExtras(datosComentarios);
                    startActivity(miIntent);

                }
            }
        });


        ImageButton botonCerrar = (ImageButton)findViewById(R.id.BotonCerrarListado);
        botonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Intent miIntent = new Intent(getApplicationContext(),laboratorios.mova.hereame.MainActivity.class);
                // startActivity(miIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_listado_denuncias, menu);
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
