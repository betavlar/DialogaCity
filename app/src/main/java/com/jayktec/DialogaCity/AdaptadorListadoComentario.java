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
public class AdaptadorListadoComentario extends ArrayAdapter<ListadoComentarioItem> {
    Context miContexto;
    int recursoLayout;
    ArrayList<ListadoComentarioItem> arrDatos = null;

    public AdaptadorListadoComentario(Context contexto, int recurso, ArrayList<ListadoComentarioItem> arreglo){
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
            contenedor.txtDescrip = (TextView)linea.findViewById(R.id.TextoComentarioItem);
            contenedor.idItem = (TextView)linea.findViewById(R.id.TextoIDComentarioItem);
            contenedor.Usuario = (TextView)linea.findViewById(R.id.comentarioItemTextoUsuario);
            contenedor.Fecha = (TextView)linea.findViewById(R.id.comentarioItemTextoFecha);

            linea.setTag(contenedor);
        }
        else {
            contenedor = (ContenedorItem)linea.getTag();
        }

        ListadoComentarioItem miItem = arrDatos.get(posicion);
        contenedor.txtDescrip.setText(miItem.TextoDenuncia);
        contenedor.Icono.setImageResource(miItem.ImagenDenuncia);
        contenedor.idItem.setText("" + miItem.IDDenuncia);
        contenedor.Usuario.setText(miItem.Usuario);
        contenedor.Fecha.setText(miItem.Fecha);
        return linea;
    }

    static class ContenedorItem
    {
        ImageView Icono;
        TextView txtDescrip;
        TextView idItem;
        TextView Usuario;
        TextView Fecha;

    }

}
