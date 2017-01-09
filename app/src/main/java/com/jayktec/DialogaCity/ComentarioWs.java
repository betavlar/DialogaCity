package com.jayktec.DialogaCity;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * Created by Alexander on 01-Sep-15.
 */
public class ComentarioWs extends AsyncTask<Comentario,Void, Boolean> {

    private int idComentario;

    public int getIdComentario() {
        return idComentario;
    }

    protected Boolean doInBackground(Comentario... params) {
        boolean result = false;
        SoapObject resultsRequestSOAP;
        Comentario comentario = params[0];
        final String NAMESPACE = "http://AppMobile/";
        final String URL = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "CrearComentario";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        //Log.i("WS_COMENT", URL);
        int lista = 0;

        Log.i("WS_COMENT:", comentario.get_comentario());

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        ComentarioXml mensaje = new ComentarioXml(comentario);
        request.addProperty("comentario", mensaje);


       Log.i("WS_COMENT", "ya anadi las propiedades");
        Log.i("WS_COMENT", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        envelope.addMapping(SOAP_ACTION, "comentario", new ComentarioXml().getClass());

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            //Log.i("WS_COMENT", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            //Log.i("WS_COMENT", "esperando");
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            //Log.i("WS_COMENT", "RECIBI ALGO");

            Object obj = envelope.bodyIn;
            if (obj == null) {
               // Log.i("WS_COMENT", "nulo");

            } else {
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                //Log.i("WS_COMENT", "trajo algo");


                lista = resultsRequestSOAP.getPropertyCount();
                //Log.i("WS_COMENT", "total de objetos:" + lista);

                for (int i = 0; i < lista; i++) {
                   // Log.i("WS_COMENT", "objeto:" + i);

                    SoapObject ic = (SoapObject) resultsRequestSOAP.getProperty(i);

                   // Log.i("WS_COMENT id", ic.getProperty("idGenerico").toString());
                    idComentario = Integer.parseInt(ic.getProperty("idGenerico").toString());
                    result = true;
                }
            }
            Log.i("WS_COMENT", "exito");
        }
        catch (NullPointerException e)
        {
          //  Log.i("ws EXPLOTO", e.getMessage().toString());
            result = false;
        }
        catch (Exception e) {

            Log.i("WS_COMENT EXPLOTO", e.getMessage().toString());
            result = false;
        }

        return result;
    }


    protected void onPostExecute(Boolean result) {

        if (result) {

        } else {
            idComentario = 0;

        }
    }


}