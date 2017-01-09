package com.jayktec.DialogaCity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by Yisheng on 20-Sep-15.
 */
public class RegistroWs  extends AsyncTask<Registro,Void, Boolean> {

    private int idRegistro;
    private Activity actividad;
    private boolean result = false;
    private boolean trabajando= true;

    public boolean isResult() {
        return result;
    }

    public RegistroWs(Activity actividad) {
        this.actividad = actividad;
    }

    public boolean isTrabajando() {
        return trabajando;
    }

    public RegistroWs() {
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    @Override
    protected Boolean doInBackground(Registro... params) {

        SoapObject resultsRequestSOAP;
        Registro registro = params[0];
        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "CrearWowzer";
        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";
        Log.i("ws", URL);
        int lista=0;
        trabajando=true;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        RegistroXml mensaje = new RegistroXml(registro);
        request.addProperty("wowzer", mensaje);


        Log.i("ws", "ya anadi las propiedades");
        Log.i("ws", request.toString());
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        envelope.addMapping(SOAP_ACTION, "wowzer", new RegistroXml().getClass());

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

                    Log.i("ws wowzer id:", " "+Integer.parseInt(ic.getProperty("idGenerico").toString()));

                    BDControler bdlocal = new BDControler(actividad,1);

                    idRegistro= Integer.parseInt(ic.getProperty("idGenerico").toString());
                    Log.i("ws","ya cree la bd ");

                    if (idRegistro !=0){
                        Log.i("ws","entre a intentar ");
                        if(bdlocal.registrarUsuarioLogeado(idRegistro,registro.getEmail().toString(),registro.getSexo().toString(),1) == "OK"){
                            Log.i("ws", "voy a llamar al mapa");
                            Intent miIntent = new Intent(actividad,com.jayktec.DialogaCity.ActividadMapaCityWowzer.class);
                            actividad.startActivity(miIntent);

                        }
                        else{

                            Toast.makeText(actividad, "Problemas para Guardar los", Toast.LENGTH_LONG).show();
                             Log.i("ws", ic.getProperty("mensaje").toString());
                        }
                        result = true;
                    }
                    else
                    {
                        result = false;
                        Toast.makeText(actividad, "Problemas para Guardar el Registro", Toast.LENGTH_LONG).show();
                    }

                    result = true;
                }
            }
            Log.i("ws", "exito");
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
            //BDControler bdlocal=new BDControler(ActividadMapaCityWowzer.class,0);
            //bdlocal.actualizaDenunciaInsercion(denuncia.getIdDenuncia(),idDenuncia);

        }
        else
        {
            idRegistro=0;
            //    txtResultado.setText("Error!");
        }
    }

}
