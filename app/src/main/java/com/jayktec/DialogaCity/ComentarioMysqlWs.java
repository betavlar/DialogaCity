
/**
 * Created by Usuario on 25-08-2015.
 */
package com.jayktec.DialogaCity;

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
public class ComentarioMysqlWs extends AsyncTask<String,Integer,Comentario[]> {
    private     Comentario[] listaComentario;
    public     Boolean Trabajando =true ;


    public Boolean getTrabajando() {
        return Trabajando;
    }

    public Comentario[] getListaComentario() {
        return this.listaComentario;
    }

    protected Comentario[] doInBackground(String... params) {

        SoapObject resultsRequestSOAP;

        final String NAMESPACE = "http://AppMobile/";
        final String URL="http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile?wsdl";
        final String METHOD_NAME = "ConsultarListaComentarios";

        final String SOAP_ACTION = "http://www.jayktec.com.ve:8080/AplicacionWebServices/UsuarioMobile";

        //Log.i("ws",URL);
        int lista=0;
        String param = params[0];

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


        request.addProperty("idDenuncia", param);
        //Log.i("ls com ws idDe:",":"+param);

        //Log.v("ws com", request.toString());


        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);


        HttpTransportSE transporte = new HttpTransportSE(URL);


        try
        {
            //Log.i("WS_COMENT", "enviando");
            transporte.call(SOAP_ACTION, envelope);

            //Log.i("WS_COMENT", "esperando");

            Vector<SoapObject> result = (Vector<SoapObject>) envelope.getResponse();
            //Log.i("WS_COMENT", "RECIBI ALGO");

            //Log.i("WS_COMENT", "trajo algo");


            //lo siguiente es para recorrer, leer y guardar la respuesta en una lista
            //lista = resSoap.getPropertyCount();
            lista= result.size();
            //Log.i("ws", "total de objetos:" + lista);
            this.listaComentario= new Comentario[lista];

            for (int i = 0; i < lista; i++) {
              //  Log.i("ws", "objeto:" + i);
                SoapObject ic = (SoapObject) result.get(i);
                Comentario registro= new Comentario();
                registro.set_comentario(ic.getProperty("comentario").toString());
                registro.set_fecha(ic.getProperty("fechaCreacion").toString());
                registro.setId_denuncia(Integer.parseInt(ic.getProperty("idDenuncia").toString()));
                registro.setId_denuncia(Integer.parseInt(ic.getProperty("id").toString()));
                registro.setId_wow(Integer.parseInt(ic.getProperty("idWowzer").toString()));
                registro.setIdUsuario(Integer.parseInt(ic.getProperty("idUsuario").toString()));
                registro.setLoginUsuario(ic.getProperty("loginUsuario").toString());
                registro.setUsuarioWeb(Boolean.valueOf(ic.getProperty("usuarioWeb").toString()));

                this.listaComentario[i]=registro;
            }
            this.Trabajando=false;

           // Log.i("ws", "exito");
        }

    catch(java.lang.ClassCastException e) {

        try {
                       // Log.i("WS_COMENT", "enviando");
                        transporte.call(SOAP_ACTION, envelope);

                       // Log.i("WS_COMENT", "esperando");

                        SoapObject ic = (SoapObject) envelope.getResponse();
                        //Log.i("WS_COMENT", "RECIBI ALGO");

                        //Log.i("WS_COMENT", "trajo algo");


                        //lo siguiente es para recorrer, leer y guardar la respuesta en una lista
                        //lista = resSoap.getPropertyCount();
                        this.listaComentario= new Comentario[1];

                            Comentario registro= new Comentario();
                            registro.set_comentario(ic.getProperty("comentario").toString());
                            registro.setId_denuncia(Integer.parseInt(ic.getProperty("idDenuncia").toString()));
                            registro.setId_denuncia(Integer.parseInt(ic.getProperty("id").toString()));
                            registro.setId_wow(Integer.parseInt(ic.getProperty("idWowzer").toString()));
                            registro.setIdUsuario(Integer.parseInt(ic.getProperty("idUsuario").toString()));
                            registro.setLoginUsuario(ic.getProperty("loginUsuario").toString());
                            registro.setUsuarioWeb(Boolean.valueOf(ic.getProperty("usuarioWeb").toString()));
                            // registro.set_fecha();
                            this.listaComentario[0]=registro;

                        this.Trabajando=false;

                        Log.i("ws", "exito");
            }
                            catch( Exception ex1)
                        {
                            Log.i("ws error 1", ex1.getMessage().toString());
                            Trabajando=false;
                        }
    }



        catch (org.xmlpull.v1.XmlPullParserException ex2)
        {
            Trabajando=false;

            Log.i("wsEXCEPTION: " , ex2.getMessage().toString());
        }
        catch (SoapFault e) {
            Trabajando=false;

            Log.i("wsSOAPFAULT====","fallo");//)//e.printStackTrace());
        }
        catch (IOException e) {
            Trabajando=false;

            Log.i("wsIOEXCEPTION====","fallo");
           // e.printStackTrace();
        }
        catch (Exception e)

        {
                //  Log.i("ws error",e.getMessage().toString());
                //  todo aca tengo un problema no se como manejar que simplemente no tenga
                //  ningún comentario da un error y como realmente no es un error no tiene un error que mostrar
                //  y explota .. debo investigar mas mientras  apagado el log de errores....

            Trabajando=false;
       }
        Trabajando=false;
        return this.listaComentario ;
    }


    protected void onPostExecute(Comentario[] comentarios)
    {
        this.Trabajando = false;
    }






}
