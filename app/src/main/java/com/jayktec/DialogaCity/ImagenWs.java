package com.jayktec.DialogaCity;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * Created by Alexander on 01-Sep-15.
 */
public class ImagenWs extends AsyncTask<String,Void, Boolean> {

    private Byte[] imagen;
    public Activity actividad;
    private boolean trabajando= true;
    public Byte[] getImagen() {
        return imagen;
    }

    public boolean getTrabajando(){return trabajando;}
    /**
     * constructor que recibe la actividad para poder grabar e la Base de Datos
     * @param act
     */
    public ImagenWs(Activity act){
        this.actividad = act;
    }

    /**
     * Constructor por Defecto
     */
    public ImagenWs(){
    }

    /**
     * Metodo asincrono para trabajar el WS de modo Background
     * @param denuncias id Den Wow en modo paramtro
     * @return resultado del WS
     */
    protected Boolean doInBackground(String... denuncias) {
        SoapObject resultsRequestSOAP;
        String denuncia = denuncias[0];
        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:80/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "EnviarImagen";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:80/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("idDenuncia", Integer.parseInt(denuncia));

        Log.i("ws", request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            Log.i("ws", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            Log.i("ws", "esperando");
            Object  response=  envelope.getResponse();
            Log.i("ws", "RECIBI ALGO");

                Log.i("ws", "trajo algo");
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                String mensaje=resultsRequestSOAP.getProperty(0).toString();
                Log.i("ws", "voy a convertirlo en imagen");
                byte[] result = Base64.decode(mensaje);
                if (result!=null)
                {
                    BDControler BDLocal = new BDControler(actividad.getApplicationContext(),1);
                    BDLocal.guardarImagen(Integer.parseInt(denuncia), result);
                    BDLocal.close();
                }

                   trabajando=false;
            Log.i("ws", "exito");
        }
        catch (Exception e)
        {

            Log.i("ws EXPLOTO",e.getMessage().toString());
            trabajando=false;

        }
        trabajando=false;

        return  trabajando=false;

    }


    protected void onPostExecute(Boolean result) {

        if (result)
        {

        }
        else
        {

        }
        trabajando=false;

    }




}