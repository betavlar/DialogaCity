package com.jayktec.DialogaCity;

import android.util.Log;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.KvmSerializable;

import org.ksoap2.serialization.PropertyInfo;
import java.nio.charset.Charset;

import java.util.Hashtable;


/**
 * Created by Alexander on 02-Sep-15.
 */
public class DenunciaXml  implements KvmSerializable {
    private Denuncia den;

    public DenunciaXml(Denuncia denuncia)
    {
        den= denuncia;

    }
    public DenunciaXml()
    {

    }

    public Denuncia getDenucia(){
        return den;
    }

    public void setDenuncia(Denuncia denuncia)
    {
        den=denuncia;
    }


    public Object getProperty(int arg0) {
        switch(arg0) {
            case 0:
                return den.getComentarios().toString();
            case 1:
                return den.getEstadoDenuncia();
            case 2:
                return den.getFechaDenuncia();
            case 3:
                if (den.getImagen()==null)
                {
                    Log.i("ws","imagen vacia");

                    return "";
                }
                else
                {
                    Log.i("ws","imagen llena");

                    return Base64.encode(den.getImagen());

                }

            case 4:
                if (den.getImagen()==null)
                {
                    Log.i("ws","imagen vacia");

                    return false;
                }
                else
                {
                    Log.i("ws","imagen llena");

                    return true;
                }
            case 5:
                return den.getIdDenuncia();
            case  6:
                return den.getLatitud().toString();
            case 7:
                return den.getWowzer();
            case 8:
                return den.getLongitud().toString();
            case 9:
                return 1;//den.getMunicipio
            case 10:
                return den.getTipoDenuncia();
            case 11:
                return 0;//usuario del municipo
            case 12:
                return den.getUsuarioWeb();// usuario web
            case 13:
                return 0;//valoracion
            case 14:
                return 1;//den.getVersion();
            case 15:
                return den.getIdWowzer();

        }
        return null;
    }


    public int getPropertyCount() {
        return 16;
    }



    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo propertyInfo) {
        switch (index) {
            case 0:
                propertyInfo.name = "descripcion";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 1:
                propertyInfo.name = "estado";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 2:
                propertyInfo.name = "fecha";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 3:
                propertyInfo.name = "imgByteFoto";
                propertyInfo.type =  byte[].class;
                break;
            case 4:
                propertyInfo.name = "imgFoto";
                propertyInfo.type =  PropertyInfo.BOOLEAN_CLASS;
                break;

            case 5:
                propertyInfo.name = "id";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 6:
                propertyInfo.name = "latitud";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 7:
                propertyInfo.name = "loginUsuario";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 8:
                propertyInfo.name = "longitud";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 9:
                propertyInfo.name = "municipio";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 10:
                propertyInfo.name = "tipo";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 11:
                propertyInfo.name = "usuario";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 12:
                propertyInfo.name = "usuarioWeb";
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                break;

            case 13:
                propertyInfo.name = "valoracion";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 14:
                propertyInfo.name = "version";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;
            case 15:
                propertyInfo.name = "wowzer";
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                break;

            default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                den.setComentarios(value.toString());
                break;
            case 1:
                den.setEstadoDenuncia(value.toString());
                break;
            case 2:
                den.setFechaDenuncia(value.toString());
                break;
            case 3:
                den.setImagen(value.toString().getBytes(Charset.forName("UTF-8")));
                break;
            case 4:
                den.setIdDenWow(Integer.parseInt(value.toString()));
                break;
            case 5:
                den.setLatitud(Double.parseDouble(value.toString()));
                break;

            case 6:
                den.setWowzer(value.toString());
                break;

            case 7:
                den.setLongitud(Double.parseDouble(value.toString()));
                break;
            case 8:
                //todo municipio
                break;

            case 9:
                den.setTipoDenuncia(Integer.parseInt(value.toString()));
                break;
            case 10:
                den.setWowzer(value.toString());
                //Usuario Web;
                break;
            case 11:
                den.setUsuarioWeb(Boolean.getBoolean(value.toString()));
                break;

            case 12:
                //valoracion
                den.setRating(Double.parseDouble(value.toString()));
                break;
            case 13:
                //version
                den.setVersion(Integer.parseInt(value.toString()));
                break;

            case 14:
                //usuario Mobil
                den.setIdWowzer(Integer.parseInt(value.toString()));
                break;
            default:
                break;
        }

    }





}