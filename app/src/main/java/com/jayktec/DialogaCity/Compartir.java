
/**
 * Created by Usuario on 29-09-2015.
 */
package com.jayktec.DialogaCity;

        import android.app.Activity;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.content.pm.ResolveInfo;
        import android.net.Uri;
        import android.widget.Toast;

        import java.util.List;

/**
 * Created by Yisheng on 05-Sep-15.
 */
public class Compartir {
    private String[] aplicaciones;
    private int app;
    private Denuncia denuncia;
    private Activity actividad;
    private AccionWs accionCompartir;


    public Compartir(Activity actividad, Denuncia denuncia) {
       // setAplicaciones();
        this.denuncia = denuncia;
        this.actividad=actividad;
        wsAccion(denuncia.getIdWowzer(),denuncia.getIdDenWow());
    }

    public Compartir(){
        setAplicaciones();
    }


    public void wsAccion (int wowzer, int denuncia)
    {
        accionCompartir= new AccionWs();
        Integer[] lista = {wowzer,denuncia,3};
        accionCompartir.execute(lista);
    }

    public void setAplicaciones(){
        aplicaciones[0]="com.facebook.katana";
        aplicaciones[1]="com.twitter.android";
        aplicaciones[2]="com.whatsapp";


    }
    public void compartirSocialMedia(String laApp){
        switch (laApp){
            case "FaceBook":
                app = 0;
                break;
            case "twitter":
                app = 1;
                break;
        }

        Intent medioSocial = new Intent();
        medioSocial.setAction(Intent.ACTION_SEND);
        medioSocial.setType("plain/text");
        medioSocial.putExtra(Intent.EXTRA_TEXT,denuncia.getComentarios());

    }

    public void compartirTwitter(){
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        //todo mensaje a compartir
        String mensaje ="Alerta agregada via @DialogaCity "
                + denuncia.getComentarios()
                + " http://www.jayktec.com.ve/consultarAlertas.jsp?miLatitud="
                + denuncia.getLatitud() + "&miLongitud="+ denuncia.getLongitud() +"" ;

        tweetIntent.putExtra(Intent.EXTRA_SUBJECT,"denuncia de DialogaCity");
        tweetIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        tweetIntent.putExtra(Intent.EXTRA_STREAM,denuncia.getImagen());
        tweetIntent.setType("image/jpeg");

        PackageManager packManager = actividad.getPackageManager();
       // packManager.GET_SHARED_LIBRARY_FILES
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for(ResolveInfo resolveInfo: resolvedInfoList){
            if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                tweetIntent.setClassName(

                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name );
                resolved = true;
                break;
            }
        }
        if(resolved)
        {
            actividad.startActivity(tweetIntent);
        }
            else{
                Intent i = new Intent();
                i.putExtra(Intent.EXTRA_TEXT, denuncia.getComentarios());
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://twitter.com/intent/tweet?text=message&via=profileName"));
                actividad.startActivity(i);
                Toast.makeText(actividad, "Twitter app isn't found", Toast.LENGTH_LONG).show();
            }

    }

}
