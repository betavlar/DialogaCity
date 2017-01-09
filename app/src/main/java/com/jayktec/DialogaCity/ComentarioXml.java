package com.jayktec.DialogaCity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Usuario on 26-08-2015.
 */
public class ComentarioXml implements KvmSerializable

{
    private Comentario com;

    public ComentarioXml(Comentario comentario)
    {
        com= comentario;

    }
    public ComentarioXml()
    {

    }

    public Comentario getComentario(){
        return com;
    }

    public void setComentario(Comentario comentario)
    {
        com=comentario;
    }


    public Object getProperty(int arg0) {
        switch(arg0) {
            case 0:
                return com.get_comentario();

            case 1:
                return "";//com.get_fecha();
            case 2:
                return 0;//com.getId_comentario();
            case 3:
                return com.getId_denuncia();
            case 4:
                return 0;//com.getIdUsuario().toString();

            case 5:
                //todo agregar el valor correcto del usuario
                return 3;//com.getId_Wow().toString();
            case  6:
                return "";//com.getLoginUsuario().toString();
            case 7:
                return false;//com.getUsuarioWeb().toString();

        }
        return null;
    }


    public int getPropertyCount() {
        return 8;
    }



    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo propertyInfo) {
        switch (index) {
            case 0:
                propertyInfo.name = "comentario";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;

            case 1:
                propertyInfo.name = "fechaCreacion";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 2:
                propertyInfo.name = "id";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 3:
                propertyInfo.name = "idDenuncia";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 4:
                propertyInfo.name = "idUsuario";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 5:
                propertyInfo.name = "idWowzer";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 6:
                propertyInfo.name = "loginUsuario";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 7:
                propertyInfo.name = "usuarioWeb";
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                break;
            default:
                break;

        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                com.set_comentario(value.toString());
                break;
            case 2:
                com.setId_comentario(Integer.parseInt(value.toString()));
                break;
            case 1:
                com.set_fecha(value.toString());
                break;
            case 3:
                com.setId_denuncia(Integer.parseInt(value.toString()));
                break;
            case 4:
                com.setIdUsuario((Integer.parseInt(value.toString())));
                break;
            case 5:
                com.setId_wow((Integer.parseInt(value.toString())));
                break;
            case 6:
                com.setLoginUsuario(value.toString());
                break;
            case 7:
                com.setUsuarioWeb(Boolean.valueOf(value.toString()));
                break;

            default:
                break;
        }

    }





}
