package com.jayktec.DialogaCity;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * Created by yisheng 28-Sep-15.
 */
public class ActualizaDenunciaWs extends AsyncTask<Integer,Void, Boolean>  {

    private int version;
    private int estado;
    private int creador;
    private boolean actualizado;
    private BDControler bdCelular;



    public ActualizaDenunciaWs(BDControler bdCelular) {
        this.bdCelular = bdCelular;
    }


    public boolean isActualizado() {
        return actualizado;
    }

    public int getVersion() {
        return version;
    }

    public int getEstado() {
        return estado;
    }

    protected Boolean doInBackground(Integer... params) {
        boolean result = false;
        SoapObject resultsRequestSOAP;
        int idDenuncia = params[0];
        int versionCel = params[1];
        int wowzer = params[2];
        Denuncia registro= new Denuncia();
        final String NAMESPACE = "http://AppMobile/";
        final String URL = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "consultarDenunciaWowzerEspecifica";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        //Log.i("ws", URL);
        int lista = 0;
        actualizado=false;
        //Log.i("ws idDenuncia:", "" + idDenuncia);

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("idDenuncia", idDenuncia);


        //Log.i("ws", "ya anadi las propiedades");
        //Log.i("ws", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        version=0;
        estado=0;
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            //Log.i("ws", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            //Log.i("ws", "esperando");
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            //Log.i("ws", "RECIBI ALGO");

            Object obj = envelope.bodyIn;
            if (obj == null) {
                Log.i("ws", "nulo");

            } else {
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                //Log.i("ws", "trajo algo");


                lista = resultsRequestSOAP.getPropertyCount();
               // Log.i("ws", "total de objetos:" + lista);

                for (int i = 0; i < lista; i++) {
                 //   Log.i("ws", "objeto:" + i);

                    SoapObject ic = (SoapObject) resultsRequestSOAP.getProperty(i);
                    registro.setComentarios(ic.getProperty("descripcion").toString());
                    registro.setEstadoDenuncia(ic.getProperty("estado").toString());
                    registro.setFechaDenuncia(ic.getProperty("fecha").toString());
                    registro.setIdDenWow(Integer.parseInt(ic.getProperty("id").toString()));
                   //Log.i("ws sin den:", ic.getProperty("id").toString());

                    registro.setTipoDenuncia(Integer.parseInt(ic.getProperty("tipo").toString()));
                    registro.setLongitud(Double.parseDouble(ic.getProperty("longitud").toString()));
                    registro.setLatitud(Double.parseDouble(ic.getProperty("latitud").toString()));
                    registro.setRating(Double.parseDouble(ic.getProperty("valoracion").toString()));
                   //
                    registro.setWowzer(ic.getProperty("loginUsuario").toString());
                    registro.setIdWowzer(Integer.parseInt(ic.getProperty("wowzer").toString()));
                    registro.setVersion(Integer.parseInt(ic.getProperty("version").toString()));

                    version = Integer.parseInt(ic.getProperty("version").toString());
                    estado= Integer.parseInt(ic.getProperty("estado").toString());
                    creador=Integer.parseInt(ic.getProperty("wowzer").toString());
                    if (estado==4 || estado==5)
                    {
                        actualizado=true;
                        Log.i("ws sin", "eliminando denuncias");
                        bdCelular.eliminarDenuncia(idDenuncia);
                        //todo  eliminar los marcadores en el mapa
                    }
                    else
                    if(version != versionCel)
                    {
                        actualizado=true;
                        //Log.i("ws sin", "versiones diferentes");
                        bdCelular.actualizaDenunciaSinImagen(registro, 2);
                            if (creador== wowzer)
                            {
                                //Log.i("ws sin", "inserto notificacion");
                                //Log.i("ws not", ic.getProperty("descripcion").toString());
                                bdCelular.insertarNotificacion(Integer.parseInt(ic.getProperty("id").toString()),
                                ic.getProperty("descripcion").toString());
                            }

                    }
                    result = true;
                }
            }
          Log.i("ws", "exito");
        }
        catch (NullPointerException e)
        {
          //  Log.i("ws EXPLOTO", e.getMessage().toString());
            result = false;
        }
        catch (Exception e) {

            Log.i("ws EXPLOTO", e.getMessage().toString());
            result = false;
        }

        return result;
    }


    protected void onPostExecute(Boolean result) {

        if (result) {

        } else {

        }
    }


}