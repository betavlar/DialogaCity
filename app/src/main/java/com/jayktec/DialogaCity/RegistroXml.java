package com.jayktec.DialogaCity;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by Alexander on 20-Sep-15.
 */
public class RegistroXml  implements KvmSerializable {

    private Registro registro;

    public RegistroXml(){

    }
    public RegistroXml(Registro reg){
        registro = reg;
    }

    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return registro.getPassword();
            case 1:
                return registro.getEmail();
            case 2:
                return registro.getEstado_civil();
            case 3:
               return ""; // facebook
            case 4:
                return "2000-01-01"; // fecha de nacimiento
            case 5:
                return "";// id del wowzer
            case 6:
                return registro.getId_wowzer();
            case 7:
                return registro.getNombre();
            case 8:
                return 0; // puntos
            case 9:
                return registro.getApellido();

            case 10:
                return registro.getSexo();
            case 11:
                return"" ; // segundo nombre
            case 12:
                return"" ; // segundo apellido
            case 13:
                return"" ; // twitter
            case 14:
                return 0 ; //super heroe
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 15;
    }

    @Override
    public void setProperty(int i, Object objeto) {
        switch (i){
            case 0:
                registro.setNombre(objeto.toString());
                break;
            case 1:
                registro.setApellido(objeto.toString());
                break;
            case 2:
                registro.setEmail(objeto.toString());
                break;
            case 3:
                registro.setPassword(objeto.toString());
                break;
            case 4:
                registro.setId_wowzer(objeto.toString());
                break;
            case 5:
                registro.setEstado_civil(objeto.toString());
                break;
            case 6:
                registro.setSexo(objeto.toString());
                break;
            default:break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i){
            case 0:
                propertyInfo.name = "clave";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 1:
                propertyInfo.name = "email";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 2:
                propertyInfo.name = "estadoSent";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 3:
                propertyInfo.name = "facebook";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 4:
                propertyInfo.name = "fechaNacimiento";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 5:
                propertyInfo.name = "id";
                propertyInfo.type = propertyInfo.INTEGER_CLASS;
                break;

            case 6:
                propertyInfo.name = "login";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 7:
                propertyInfo.name = "papellido";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 8:
                propertyInfo.name = "pnombre";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 9:
                propertyInfo.name = "puntos";
                propertyInfo.type = propertyInfo.INTEGER_CLASS;
                break;
            case 10:
                propertyInfo.name = "sapellido";
                propertyInfo.type = propertyInfo.INTEGER_CLASS;
                break;

            case 11:
                propertyInfo.name = "sexo";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 12:
                propertyInfo.name = "snombre";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 13:
                propertyInfo.name = "twitter";
                propertyInfo.type = propertyInfo.STRING_CLASS;
                break;
            case 14:
                propertyInfo.name = "wowSuper";
                propertyInfo.type = propertyInfo.INTEGER_CLASS;
                break;


            default: break;
        }

    }
}
