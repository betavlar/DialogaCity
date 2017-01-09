package com.jayktec.DialogaCity;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;


/**
 * Created by Alexander on 01-Sep-15.
 */
public class DenunciaWs extends AsyncTask<Denuncia,Void, Boolean> {

    private int idDenuncia;
    public Activity actividad;

    /**
     * constructor que recibe la actividad para poder grabar e la Base de Datos
     * @param act
     */
    public DenunciaWs(Activity act){
        this.actividad = act;
    }

    /**
     * Constructor por Defecto
     */
    public DenunciaWs(){
    }

    /**
     * Metodo asincrono para trabajar el WS de modo Background
     * @param params Denuncia en modo paramtro
     * @return resultado del WS
     */
    protected Boolean doInBackground(Denuncia... params) {
        boolean result = false;
        BDControler BDLocal = new BDControler(actividad.getApplicationContext(),1);
        SoapObject resultsRequestSOAP;
        Denuncia denuncia = params[0];
        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        //Prueba imagen
        // final String METHOD_NAME = "CopiarImagen";
       //  request.addProperty("imgBytes", Base64.encode(denuncia.getImagen()));

                  final String METHOD_NAME = "CrearDenuncia";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
       DenunciaXml mensaje = new DenunciaXml(denuncia);
       request.addProperty("denuncia", mensaje);
        //  request.addProperty("imgBytes", Base64.encode(denuncia.getImagen()));


        Log.i("WS_DENUNCIA", "ya anadi las propiedades");
        Log.i("WS_DENUNCIA", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        //Prueba imagen
        envelope.addMapping(SOAP_ACTION, "denuncia", new DenunciaXml().getClass());

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            Log.i("WS_DENUNCIA", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            Log.i("WS_DENUNCIA", "esperando");
            SoapObject resSoap =(SoapObject)envelope.getResponse();
            Log.i("WS_DENUNCIA", "RECIBI ALGO");

            Object obj = envelope.bodyIn;
            if (obj==null)
            {
                Log.i("WS_DENUNCIA", "nulo");

            }
            else {
                resultsRequestSOAP = (SoapObject) envelope.bodyIn;

                Log.i("WS_DENUNCIA", "trajo algo");


                lista = resultsRequestSOAP.getPropertyCount();
                Log.i("WS_DENUNCIA", "total de objetos:" + lista);

                for (int i = 0; i < lista; i++) {
                    Log.i("WS_DENUNCIA", "objeto:" + i);

                    SoapObject ic = (SoapObject) resultsRequestSOAP.getProperty(i);

                    Log.i("WS_DENUNCIA id", ic.getProperty("idGenerico").toString());
                    idDenuncia= Integer.parseInt(ic.getProperty("idGenerico").toString());

                    BDLocal.actualizaDenunciaInsercion(denuncia,idDenuncia);
                    Log.i("WS_DENUNCIA fin act", ic.getProperty("idGenerico").toString());
                    Log.i("WS_DENUNCIA error", ic.getProperty("mensaje").toString());
                    //BDLocal.actualizarControlDenuncia(idDenuncia);

                    result = true;
                }
            }
            Log.i("WS_DENUNCIA", "exito");
        }
        catch (org.xmlpull.v1.XmlPullParserException ex2)
        {
            Log.i(" wsEXCEPTION: " , ex2.getMessage().toString());
            result = false;
        }
        catch (SoapFault e) {

            Log.i("wsSOAPFAULT", "aca fallo");//(String) e.printStackTrace());
            result = false;
        }
        catch (IOException e) {
            Log.i("wsSOAPFAULT", "acax fallo");//(String) e.printStackTrace());

            //System.out.println("IOEXCEPTION====");
            //e.printStackTrace();
            result = false;
        }

        catch (java.lang.NullPointerException ex1)
        {
           // Log.i("ws error:", ex1.getMessage().toString().substring(1,15));//e.getMessage().toString());
            result = false;
        }
        catch (Exception e)
        {
          //  Log.i("ws error:",e.getCause().toString());//e.getMessage().toString());
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
            idDenuncia=0;

        }
    }



    public Integer idDenuncia()
    {
        return     idDenuncia;
    }


}