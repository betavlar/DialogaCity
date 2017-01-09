package com.jayktec.DialogaCity;

/**
 * Created by Alexander on 02-Sep-15.
 */
public class ListadoComentarioItem {
    public int ImagenDenuncia;
    public String TextoDenuncia;
    public int IDDenuncia;
    public int idWow;
    public String Usuario;
    public int ImagenEstado;
    public String Fecha;

    public ListadoComentarioItem(){
        super();
    }

    public ListadoComentarioItem(int imgTipo, String texto, int id, int IdWow, String usuario, int imgEstado, String fecha){
        super();
        ImagenDenuncia = imgTipo;
        TextoDenuncia = texto;
        IDDenuncia = id;
        idWow=IdWow;
        Usuario = usuario;
        ImagenEstado = imgEstado;
        Fecha=fecha;
    }
}
