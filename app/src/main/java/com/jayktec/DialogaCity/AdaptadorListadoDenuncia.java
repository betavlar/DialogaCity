package com.jayktec.DialogaCity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alexander on 02-Sep-15.
 */
public class AdaptadorListadoDenuncia extends ArrayAdapter<ListadoDenunciaItem> {
    Context miContexto;
    int recursoLayout;
    ArrayList<ListadoDenunciaItem> arrDatos = null;

    public AdaptadorListadoDenuncia(Context contexto,int recurso, ArrayList<ListadoDenunciaItem> arreglo){
        super(contexto,recurso,arreglo);
        this.recursoLayout = recurso;
        this.miContexto = contexto;
        this.arrDatos = arreglo;
    }

    //@Override
    public View getView(int posicion,View converV, ViewGroup padre){
        View linea = converV;
        ContenedorItem contenedor;

        if (linea == null){
            LayoutInflater inflador = ((Activity)miContexto).getLayoutInflater();
            linea = inflador.inflate(recursoLayout,padre,false);

            contenedor = new ContenedorItem();
            contenedor.Icono = (ImageView)linea.findViewById(R.id.IconoListadoItem);
            contenedor.txtDescrip = (TextView)linea.findViewById(R.id.TextoListadoItem);
            contenedor.idItem = (TextView)linea.findViewById(R.id.TextoIDItem);
            contenedor.txtValoracion = (TextView)linea.findViewById(R.id.TextoValoracion);
            contenedor.Estado = (ImageView)linea.findViewById(R.id.IconoEstadoItem);
            contenedor.FechaDenuncia = (TextView)linea.findViewById(R.id.etiqFecha);

            linea.setTag(contenedor);
        }
        else {
            contenedor = (ContenedorItem)linea.getTag();
        }

        ListadoDenunciaItem miItem = arrDatos.get(posicion);
        contenedor.txtDescrip.setText(miItem.TextoDenuncia);
        contenedor.Icono.setImageResource(miItem.ImagenDenuncia);
        contenedor.idItem.setText("" + miItem.IDDenuncia);
        contenedor.txtValoracion.setText(""+miItem.TextoValoracion+"/5");
        contenedor.Estado.setImageResource(miItem.ImagenEstado);
        contenedor.FechaDenuncia.setText(""+miItem.FechaDenuncia);

        return linea;
    }

    static class ContenedorItem
    {
        ImageView Icono;
        TextView txtDescrip;
        TextView idItem;
        TextView txtValoracion;
        ImageView Estado;
        TextView FechaDenuncia;
    }

}
