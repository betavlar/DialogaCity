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
public class LoginWs extends AsyncTask<String,Void, Boolean> {

    private int idRegistro;
    private int nivel;
    private boolean trabajando= true;
    private String sexo;
    private String alias;

    public boolean isTrabajando() {
        return trabajando;
    }

    public int getNivel() {
        return nivel;
    }

    public String getAlias() {
        return alias;
    }

    public String getSexo() {
        return sexo;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean result = false;
        SoapObject resultsRequestSOAP;
        String login = params[0];
        String clave = params[1];

        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "VerificacionLogin";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("login", login);
        request.addProperty("clave", clave);


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

                    Log.i("ws wowzer id:", " "+Integer.parseInt(ic.getProperty("id_usuario").toString()));

                    idRegistro= Integer.parseInt(ic.getProperty("id_usuario").toString());
                    alias=(ic.getProperty("mensaje").toString());
                    nivel= Integer.parseInt(ic.getProperty("idGenerico").toString());

                    if ( Integer.parseInt(ic.getProperty("version").toString())==1)
                    {
                        sexo="M";
                    }
                    else
                    {
                        sexo="F";
                    }

                    result = true;
                }
            }
            Log.i("ws", "exito");
            trabajando=false;
        }
        catch (Exception e)
        {
            trabajando=false;

            //Log.i("ws EXPLOTO",e.getMessage());
            result = false;
        }
        trabajando=false;

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
