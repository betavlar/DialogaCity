package com.jayktec.DialogaCity;

/**
 * Created by Alexander on 02-Sep-15.
 */
public class ListadoDenunciaItem {
    public int ImagenDenuncia;
    public String TextoDenuncia;
    public int IDDenuncia;
    public int idWow;
    public String TextoValoracion;
    public int ImagenEstado;
    public String FechaDenuncia;

    public ListadoDenunciaItem(){
        super();
    }

    public ListadoDenunciaItem(int imgTipo,String texto,int id, int IdWow,String valoracion, int imgEstado,String fecha){
        super();
        ImagenDenuncia = imgTipo;
        TextoDenuncia = texto;
        IDDenuncia = id;
        idWow=IdWow;
        TextoValoracion = valoracion;
        ImagenEstado = imgEstado;
        FechaDenuncia = fecha;
    }
}
