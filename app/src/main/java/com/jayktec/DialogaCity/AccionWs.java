package com.jayktec.DialogaCity;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by yisheng on 20-Oct-15.
 */
public class AccionWs extends AsyncTask<Integer,Void, Boolean> {

    private int idRegistro;

    public int getIdRegistro() {
        return idRegistro;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        boolean result = false;
        SoapObject resultsRequestSOAP;
        int wowzer = params[0];
        int denuncia = params[1];
        int accion= params[2];

        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "accionWowzer";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("wowzer", wowzer);
        request.addProperty("denuncia", denuncia);
        request.addProperty("accion", accion);

        Log.i("ws", "ya anadi las propiedades");
        Log.i("ws", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);


        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            Log.i("ws", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            Log.i("ws", "esperando");
            SoapObject resSoap =(SoapObject)envelope.getResponse();
            Log.i("ws", "RECIBI ALGO");

            Object obj = envelope.bodyIn;
            if (obj==null)
            {
                Log.i("ws", "nulo");

            }
            else {
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                Log.i("ws", "trajo algo");


                lista = resultsRequestSOAP.getPropertyCount();
                Log.i("ws", "total de objetos:" + lista);

                for (int i = 0; i < lista; i++) {
                    Log.i("ws", "objeto:" + i);

                    SoapObject ic = (SoapObject) resultsRequestSOAP.getProperty(i);
                    Log.i("ws accion id:", " "+Integer.parseInt(ic.getProperty("idGenerico").toString()));

                    idRegistro= Integer.parseInt(ic.getProperty("idGenerico").toString());


                    result = true;
                }
            }
            Log.i("ws", "exito");
        }
        catch (Exception e)
        {

            //Log.i("ws EXPLOTO",e.getMessage());
            result = false;
        }
        return result;
    }

    protected void onPostExecute(Boolean result) {

        if (result)
        {

        }
        else
        {
            idRegistro=0;
        }
    }

}
