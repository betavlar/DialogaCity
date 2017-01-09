
/**
 * Created by Usuario on 25-08-2015.
 */
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
        import java.util.Vector;

/**
 * Created by Usuario on 25-08-2015.
 */
public class DenunciaMysqlWs   extends AsyncTask<Integer,Integer,Boolean> {

    private Boolean resultado= false;
    private Integer idDenuncia=0;
    private Integer wowzer=0;
    public Activity laActividad;


    public DenunciaMysqlWs(Activity act){
        this.laActividad = act;
    }

    public DenunciaMysqlWs(){

    }



    protected Boolean doInBackground(Integer... params) {

        SoapObject resultsRequestSOAP;
        idDenuncia=params[0];
        wowzer=params[1];
        BDControler BDTelefono = new BDControler(laActividad.getApplicationContext(),1);
        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "ConsultarListaDenuncias";

        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";

        //Log.i("ws",URL);
        //Log.i("ws",METHOD_NAME);

        int lista=0;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


        request.addProperty("idWowzer", wowzer);
        request.addProperty("contDenuncia", idDenuncia);

        //Log.i("ws contDenuncia", "" + idDenuncia);

        //Log.i("ws sin", "ya anadi las propiedades");
        //Log.v("ws sin", request.toString());
        SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);


        try
        {
           // Log.i("ws sin", "enviando");
            transporte.call(SOAP_ACTION, envelope);

           // Log.i("ws sin", "esperando");

            //  SoapObject resSoap =(SoapObject)envelope.getResponse();
            Vector<SoapObject> result = (Vector<SoapObject>) envelope.getResponse();
            //Log.i("ws sin", "RECIBI ALGO");

            //Log.i("ws sin", "trajo algo");


            //lo siguiente es para recorrer, leer y guardar la respuesta en una lista
            //lista = resSoap.getPropertyCount();
            lista= result.size();
            //Log.i("ws sin", "total de objetos:" + lista);


            for (int i = 0; i < lista; i++) {
             //   Log.i("ws sin", "objeto:" + i);
                SoapObject ic = (SoapObject) result.get(i);
                Denuncia registro= new Denuncia();
                registro.setComentarios(ic.getProperty("descripcion").toString());
                registro.setIdDenWow(Integer.parseInt(ic.getProperty("id").toString()));
                registro.setFechaDenuncia(ic.getProperty("fecha").toString());
                registro.setTipoDenuncia(Integer.parseInt(ic.getProperty("tipo").toString()));
                registro.setEstadoDenuncia(ic.getProperty("estado").toString());
                registro.setLongitud(Double.parseDouble(ic.getProperty("longitud").toString()));
               // Log.i("ws lat", (ic.getProperty("latitud").toString()));
                registro.setLatitud(Double.parseDouble(ic.getProperty("latitud").toString()));
               // Log.i("ws lon", (ic.getProperty("longitud").toString()));

                registro.setRating(Double.parseDouble(ic.getProperty("valoracion").toString()));
                registro.setWowzer(ic.getProperty("loginUsuario").toString());
                registro.setIdWowzer(Integer.parseInt(ic.getProperty("wowzer").toString()));
                registro.setVersion(Integer.parseInt(ic.getProperty("version").toString()));
                registro.setUsuarioWeb(Boolean.getBoolean(ic.getProperty("usuarioWeb").toString()));

                //si la denuncia no existe en la bd la inserta
                if (!BDTelefono.verificarDenuncia(Integer.parseInt(ic.getProperty("id").toString()))) {
                    BDTelefono.insertarDenuncia(registro);
                }
                    if (i == lista -1){
                    BDTelefono.actualizarControlDenuncia(registro.getIdDenWow());
                }
            }
            resultado = true;
        if (wowzer==0)
        {
            BDTelefono.PrimeraVez(0);
        }

           // Log.i("ws", "exito");

        }

        catch (org.xmlpull.v1.XmlPullParserException ex2)
        {
            Log.i(" wsEXCEPTION: " , ex2.getMessage().toString());
            return resultado ;

        }
        catch (SoapFault e) {

            Log.i("wsSOAPFAULT", "aca fallo");//(String) e.printStackTrace());
            return resultado ;

        }
        catch (IOException e) {
            Log.i("wsSOAPFAULT", "acax fallo");//(String) e.printStackTrace());

            //System.out.println("IOEXCEPTION====");
            //e.printStackTrace();
            return resultado ;

        }
        catch (Exception e)
        {

            return resultado ;

            //   Log.i("ws EXPLOTO",e.getMessage());

        }
        return resultado ;


    }


    protected void onPostExecute(boolean result) {

        if (result)
        {

        }
        else
        {
           Log.i("ws Denuncia","no trajo registros");
        }

    }






}
