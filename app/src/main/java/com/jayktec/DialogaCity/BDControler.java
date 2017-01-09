package com.jayktec.DialogaCity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Alexander on 29-Aug-15.
 * Metodo Utilizado para el manejo de la Base de datos local
 */
public class BDControler extends SQLiteOpenHelper {
    //TABLA DENUNCIA
    private static final String NOMBRE_BD = "WowserBD.db";
    private static final String TABLA_DENUNCIAS = "tbl_denuncias";
    private static final String COLUMNA_DENUNCIA_ID = "id_denuncia";
    private static final String COLUMNA_DENUNCIA_ID_WOWZER = "id_usuario_denuncia";
    private static final String COLUMNA_DENUNCIA_WOWZER = "usuario_denuncia";
    private static final String COLUMNA_DENUNCIA_FECHA = "fecha_denuncia";
    private static final String COLUMNA_DENUNCIA_LATITUD = "latitud";
    private static final String COLUMNA_DENUNCIA_LONGITUD = "longitud";
    private static final String COLUMNA_DENUNCIA_TIPO = "tipo_denuncia";
    private static final String COLUMNA_DENUNCIA_ESTADO = "estado";
    private static final String COLUMNA_DENUNCIA_COMENTARIO = "comentario";
    private static final String COLUMNA_DENUNCIA_IMAGEN = "foto_denuncia";
    private static final String COLUMNA_DENUNCIA_RATING = "rating_denuncia";
    private static final String COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION = "fecha_ult_act";
    private static final String COLUMNA_DENUNCIA_VERSION = "version";
    private static final String COLUMNA_DENUNCIA_ID_DEN_WOW = "id_den_wow";

    //TABLA LOGIN
    private static final String TABLA_LOGIN = "tbl_login";
    private static final String COLUMNA_LOGIN_USUARIO = "lg_usuario";
    private static final String COLUMNA_LOGIN_ID_USUARIO = "lg_id_usuario";
    private static final String COLUMNA_LOGIN_ESTATUS = "lg_estatus_user";
    private static final String COLUMNA_LOGIN_SEXO = "lg_sexo_user";
    private static final String COLUMNA_LOGIN_NIVEL = "lg_nivel_user";

    //TABLA COMENTARIOS
    private static final String TABLA_COMENTARIOS = "tbl_comentarios";
    private static final String COLUMNA_COMENTARIO_ID_DENUNCIA = "id_denuncia";
    private static final String COLUMNA_COMENTARIO_ID_WOWZER = "id_wowzer";
    private static final String COLUMNA_COMENTARIO_ID_COMENTADOR = "id_wowzer_otro";
    private static final String COLUMNA_COMENTARIO_COMENTARIO = "comentario";
    private static final String COLUMNA_COMENTARIO_FECHA = "fecha_comentario";

    //TABLA CONTROL
    private static final String TABLA_CONTROL = "tbl_control";
    private static final String COLUMNA_CTRL_NOMBRE = "ctrl_nombre";
    private static final String COLUMNA_VALOR_INT = "ctrl_int";
    private static final String COLUMNA_VALOR_FLOAT = "ctrl_float";
    private static final String COLUMNA_VALOR_STRING = "ctrl_string";
    private static final String COLUMNA_VALOR_FECHA = "ctrl_fecha";

    // 28/09/2015 yle
    // TABLA NOTIFICACION
    private static final String TABLA_NOTIFICACION = "tbl_notificacion";
    private static final String COLUMNA_NTF_DENUNCIA = "ntf_id_denuncia";
    private static final String COLUMNA_NTF_PROCESADA = "ntf_procesada";
    private static final String COLUMNA_NTF_MENSAJE = "ntf_mensaje";

    public BDControler(Context miContexto, int versionBD) {
        super(miContexto, NOMBRE_BD, null, versionBD);
    }

    @Override
    public void onCreate(SQLiteDatabase miBD) {
        miBD.execSQL("create table " + TABLA_DENUNCIAS + " " +
                "(" + COLUMNA_DENUNCIA_ID + " integer PRIMARY KEY AUTOINCREMENT," +
                COLUMNA_DENUNCIA_ID_WOWZER + " integer," +
                COLUMNA_DENUNCIA_WOWZER + " text," +
                COLUMNA_DENUNCIA_FECHA + " text," +
                COLUMNA_DENUNCIA_LATITUD + " real," +
                COLUMNA_DENUNCIA_LONGITUD + " real," +
                COLUMNA_DENUNCIA_TIPO + " integer," +
                COLUMNA_DENUNCIA_ESTADO + " text," +
                COLUMNA_DENUNCIA_COMENTARIO + " text," +
                COLUMNA_DENUNCIA_IMAGEN + " blob," +
                COLUMNA_DENUNCIA_RATING + " real," +
                COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION + " text," +
                COLUMNA_DENUNCIA_VERSION + " integer," +
                COLUMNA_DENUNCIA_ID_DEN_WOW + " integer)");

        miBD.execSQL("create index idx_den1 on " + TABLA_DENUNCIAS + " (" + COLUMNA_DENUNCIA_ID + "," + COLUMNA_DENUNCIA_WOWZER + "," +
                COLUMNA_DENUNCIA_FECHA + ")");

        miBD.execSQL("create table " + TABLA_LOGIN + " " +
                "(" + COLUMNA_LOGIN_USUARIO + " text," +
                COLUMNA_LOGIN_ID_USUARIO + " integer," +
                COLUMNA_LOGIN_ESTATUS + " text," +
                COLUMNA_LOGIN_SEXO + " text," +
                COLUMNA_LOGIN_NIVEL + " int)");

        miBD.execSQL("create table " + TABLA_COMENTARIOS + " " +
                "(" + COLUMNA_COMENTARIO_ID_DENUNCIA + " integer," +
                COLUMNA_COMENTARIO_ID_WOWZER + " text," +
                COLUMNA_COMENTARIO_ID_COMENTADOR + " text," +
                COLUMNA_COMENTARIO_FECHA + " text," +
                COLUMNA_COMENTARIO_COMENTARIO + " text)");


        miBD.execSQL("create index idx_coment1 on " +
                TABLA_COMENTARIOS + " (" +
                COLUMNA_COMENTARIO_ID_DENUNCIA + "," +
                COLUMNA_COMENTARIO_ID_WOWZER + "," +
                COLUMNA_COMENTARIO_FECHA + ")");

        miBD.execSQL("create table " + TABLA_CONTROL + " " +
                "(" + COLUMNA_CTRL_NOMBRE + " text," +
                COLUMNA_VALOR_INT + " integer," +
                COLUMNA_VALOR_FLOAT + " real," +
                COLUMNA_VALOR_FECHA + " text," +
                COLUMNA_VALOR_STRING + " text)");

        // 28/09/2015 yle
        miBD.execSQL("create table " + TABLA_NOTIFICACION + " " +
                "(" + COLUMNA_NTF_DENUNCIA + " integer," +
                COLUMNA_NTF_PROCESADA + " integer," +
                COLUMNA_NTF_MENSAJE + " text)");


        miBD.execSQL("INSERT INTO  " + TABLA_CONTROL + "(" + COLUMNA_CTRL_NOMBRE + "," + COLUMNA_VALOR_INT + ")" + " VALUES ( 'ID_DENUNCIA',0  )");
        miBD.execSQL("INSERT INTO  " + TABLA_CONTROL + "(" + COLUMNA_CTRL_NOMBRE + "," + COLUMNA_VALOR_INT + ")" + " VALUES ( 'PRIMERAVEZ',1  )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase miBD, int viejaVersion, int nuevaVersion) {
        miBD.execSQL("DROP TABLE IF EXISTS " + TABLA_DENUNCIAS);
        miBD.execSQL("DROP TABLE IF EXISTS " + TABLA_LOGIN);
        miBD.execSQL("DROP TABLE IF EXISTS " + TABLA_COMENTARIOS);
        miBD.execSQL("DROP TABLE IF EXISTS " + TABLA_CONTROL);


        onCreate(miBD);
    }


    /**
     * Revisa si es  primera vez que ingresa a la aplicación con ese usuario
     *
     * @return
     */
    public Boolean primeraVez() {
        Boolean resp = false;
        int primera_vez = 0;
        SQLiteDatabase miBD = this.getReadableDatabase();
        Cursor resultado = miBD.rawQuery("select * from " + TABLA_CONTROL + " where " + COLUMNA_CTRL_NOMBRE + "='PRIMERAVEZ'", null);
      //  Log.i("desvirgado?","entrando");
        resultado.moveToFirst();
        if (!resultado.isAfterLast()) {
            primera_vez = resultado.getInt(resultado.getColumnIndex(COLUMNA_VALOR_INT));

        }
        if (primera_vez == 1) {
            resp = true;
        }
        resultado.close();
        miBD.close();
        //Log.i("desvirgado?", resp.toString());

        return resp;
    }


    /**
     * Para ingresar una denuncia en la Base de datos
     *
     * @param unaDenuncia Objeto del tipo Denuncia
     * @return el ID de la denuncia guardada
     */
    public Long insertarDenuncia(Denuncia unaDenuncia) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        Long idDenuncia;
        ContentValues valoresDenuncia = new ContentValues();
        valoresDenuncia.put(COLUMNA_DENUNCIA_WOWZER, unaDenuncia.getWowzer());
        valoresDenuncia.put(COLUMNA_DENUNCIA_ID_WOWZER, unaDenuncia.getIdWowzer());
        valoresDenuncia.put(COLUMNA_DENUNCIA_ID_DEN_WOW, unaDenuncia.getIdDenWow());
        valoresDenuncia.put(COLUMNA_DENUNCIA_FECHA, unaDenuncia.getFechaDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_LATITUD, unaDenuncia.getLatitud());
        valoresDenuncia.put(COLUMNA_DENUNCIA_LONGITUD, unaDenuncia.getLongitud());
        valoresDenuncia.put(COLUMNA_DENUNCIA_TIPO, unaDenuncia.getTipoDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_ESTADO, unaDenuncia.getEstadoDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_COMENTARIO, unaDenuncia.getComentarios());
        valoresDenuncia.put(COLUMNA_DENUNCIA_IMAGEN, unaDenuncia.getImagen());
        valoresDenuncia.put(COLUMNA_DENUNCIA_RATING, unaDenuncia.getRating());
        valoresDenuncia.put(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION, unaDenuncia.getFecha_ult_actualizacion());
        valoresDenuncia.put(COLUMNA_DENUNCIA_VERSION, unaDenuncia.getVersion());
        idDenuncia = miBD.insert(TABLA_DENUNCIAS, null, valoresDenuncia);
        miBD.close();
        return idDenuncia;
    }


    /**
     * Para actualizar una denuncia en la Base de datos
     *
     * @param unaDenuncia Objeto del tipo Denuncia
     * @param Tipo        1 si actualiza con id_denuncia 2 si actualiza con id_den_wow
     * @return devuelve verdadero si actualiza una y solo una denuncia
     */
    public Boolean actualizaDenuncia(Denuncia unaDenuncia, Integer Tipo) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        Boolean result = false;
        int contador = 0;
        ContentValues valoresDenuncia = new ContentValues();
        valoresDenuncia.put(COLUMNA_DENUNCIA_ID_DEN_WOW, unaDenuncia.getIdDenWow());
        valoresDenuncia.put(COLUMNA_DENUNCIA_ID, unaDenuncia.getIdDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_ID_WOWZER, unaDenuncia.getIdWowzer());
        valoresDenuncia.put(COLUMNA_DENUNCIA_WOWZER, unaDenuncia.getWowzer());
        valoresDenuncia.put(COLUMNA_DENUNCIA_FECHA, unaDenuncia.getFechaDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_LATITUD, unaDenuncia.getLatitud());
        valoresDenuncia.put(COLUMNA_DENUNCIA_LONGITUD, unaDenuncia.getLongitud());
        valoresDenuncia.put(COLUMNA_DENUNCIA_TIPO, unaDenuncia.getTipoDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_ESTADO, unaDenuncia.getEstadoDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_COMENTARIO, unaDenuncia.getComentarios());
        valoresDenuncia.put(COLUMNA_DENUNCIA_IMAGEN, unaDenuncia.getImagen());
        valoresDenuncia.put(COLUMNA_DENUNCIA_RATING, unaDenuncia.getRating());
        valoresDenuncia.put(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION, unaDenuncia.getFecha_ult_actualizacion());
        valoresDenuncia.put(COLUMNA_DENUNCIA_VERSION, unaDenuncia.getVersion());
        if (Tipo == 1) {
            contador = miBD.update(TABLA_DENUNCIAS, valoresDenuncia, COLUMNA_DENUNCIA_ID + "=" + unaDenuncia.getIdDenuncia().toString(), null);
        } else if (Tipo == 2) {
            contador = miBD.update(TABLA_DENUNCIAS, valoresDenuncia, COLUMNA_DENUNCIA_ID_DEN_WOW + "=" + unaDenuncia.getIdDenWow().toString(), null);
        }
        miBD.close();
        if (contador == 1) result = true;
        return result;
    }

    /**
     * Para actualizar una denuncia en la Base de datos pero no actualiza las imagenes
     *
     * @param unaDenuncia Objeto del tipo Denuncia
     * @param Tipo        1 si actualiza con id_denuncia 2 si actualiza con id_den_wow
     * @return devuelve verdadero si actualiza una y solo una denuncia
     */
    public Boolean actualizaDenunciaSinImagen(Denuncia unaDenuncia, Integer Tipo) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        Boolean result = false;
        int contador = 0;
        ContentValues valoresDenuncia = new ContentValues();
        //valoresDenuncia.put(COLUMNA_DENUNCIA_ID_DEN_WOW, unaDenuncia.getIdDenWow());
        //valoresDenuncia.put(COLUMNA_DENUNCIA_ID, unaDenuncia.getIdDenuncia());
        //valoresDenuncia.put(COLUMNA_DENUNCIA_ID_WOWZER, unaDenuncia.getIdWowzer());
        valoresDenuncia.put(COLUMNA_DENUNCIA_WOWZER, unaDenuncia.getWowzer());
        valoresDenuncia.put(COLUMNA_DENUNCIA_FECHA, unaDenuncia.getFechaDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_LATITUD, unaDenuncia.getLatitud());
        valoresDenuncia.put(COLUMNA_DENUNCIA_LONGITUD, unaDenuncia.getLongitud());
        valoresDenuncia.put(COLUMNA_DENUNCIA_TIPO, unaDenuncia.getTipoDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_ESTADO, unaDenuncia.getEstadoDenuncia());
        valoresDenuncia.put(COLUMNA_DENUNCIA_COMENTARIO, unaDenuncia.getComentarios());
        valoresDenuncia.put(COLUMNA_DENUNCIA_RATING, unaDenuncia.getRating());
        valoresDenuncia.put(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION, unaDenuncia.getFecha_ult_actualizacion());
        valoresDenuncia.put(COLUMNA_DENUNCIA_VERSION, unaDenuncia.getVersion());
        if (Tipo == 1) {
            contador = miBD.update(TABLA_DENUNCIAS, valoresDenuncia, COLUMNA_DENUNCIA_ID + "=" + unaDenuncia.getIdDenuncia().toString(), null);
        } else if (Tipo == 2) {
            contador = miBD.update(TABLA_DENUNCIAS, valoresDenuncia, COLUMNA_DENUNCIA_ID_DEN_WOW + "=" + unaDenuncia.getIdDenWow().toString(), null);
        }
        miBD.close();
        if (contador == 1) result = true;
        return result;
    }


    /**
     * Obtener una denuncia dado el ID Wowzer
     *
     * @param idDenWow
     * @return
     */
    public Denuncia obtenerLaDenunciaWOW(int idDenWow) {
        Denuncia unaDenuncia = new Denuncia();
        SQLiteDatabase miMD = this.getReadableDatabase();
        //28/09/2015 YLE
        //Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_ID + "=" + id_denuncia +
        //      " and " + COLUMNA_DENUNCIA_WOWZER + "='" + usrWowzer + "'", null);

        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_ID_DEN_WOW + "=" + idDenWow, null);

        resultado.moveToFirst();
        if (!resultado.isAfterLast()) {
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return unaDenuncia;
    }


    public boolean verificarDenuncia(int idDenWow) {
        boolean result = false;
        Denuncia unaDenuncia = new Denuncia();
        SQLiteDatabase miMD = this.getReadableDatabase();

        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where "
                + COLUMNA_DENUNCIA_ID_DEN_WOW + "=" + idDenWow + "", null);
        resultado.moveToFirst();

        if (resultado.getCount() > 0) {
            result = true;
        }

        return result;

    }

    /**
     * Obtener una denuncia dada el ID y el usuario
     *
     * @param id_denuncia ID de la BD en el marcador
     * @return
     */
    public Denuncia obtenerLaDenuncia(int id_denuncia) {
        Denuncia unaDenuncia = new Denuncia();
        SQLiteDatabase miMD = this.getReadableDatabase();

        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_ID + "=" + id_denuncia, null);

        resultado.moveToFirst();
        if (!resultado.isAfterLast()) {
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return unaDenuncia;
    }


    /**
     * Obtener una denuncia dada el ID y el usuario
     *
     * @param id_denuncia ID de la BD en el marcador
     * @param usrWowzer   usuario Wowzer
     * @return
     */
    public Denuncia obtenerLaDenuncia(int id_denuncia, String usrWowzer) {
        Denuncia unaDenuncia = new Denuncia();
        SQLiteDatabase miMD = this.getReadableDatabase();

        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_ID + "=" + id_denuncia +
                " and " + COLUMNA_DENUNCIA_WOWZER + "='" + usrWowzer + "'", null);

        resultado.moveToFirst();
        if (!resultado.isAfterLast()) {
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return unaDenuncia;
    }

    /**
     * Obtener una denuncia dada el ID y el usuario
     *
     * @param id_denuncia ID de la BD en el marcador
     * @param idWowzer    id usuario Wowzer
     * @return
     */
    public Denuncia obtenerLaDenuncia(int id_denuncia, int idWowzer) {
        Denuncia unaDenuncia = new Denuncia();
        SQLiteDatabase miMD = this.getReadableDatabase();

        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_ID + "=" + id_denuncia +
                " and " + COLUMNA_DENUNCIA_ID_WOWZER + "='" + idWowzer + "'", null);

        resultado.moveToFirst();
        if (!resultado.isAfterLast()) {
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return unaDenuncia;
    }


    /**
     * Devuelve todas las denuncias registradas en la Base de datos
     *
     * @return arreglo de con todas las denuncias y sus datos basicos
     */
    public ArrayList<Denuncia> obtenerTodasLasDenuncias(int wowzer) {
        SQLiteDatabase miMD = this.getReadableDatabase();
        ArrayList<Denuncia> arregloDenuncias = new ArrayList<>();
        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_ID_WOWZER + "=" + wowzer, null);
      //  Log.i("BD 1:", "cant YO:" + resultado.getCount());
        resultado.moveToFirst();
        while (!resultado.isAfterLast()) {
            Denuncia unaDenuncia = new Denuncia();
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));
            arregloDenuncias.add(unaDenuncia);
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return arregloDenuncias;
    }

    public ArrayList<Denuncia> obtenerTodasLasDenunciasOtros(int wowzer) {
        SQLiteDatabase miMD = this.getReadableDatabase();
        ArrayList<Denuncia> arregloDenuncias = new ArrayList<>();
        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_ID_WOWZER + "<>" + wowzer, null);
      //  Log.i("BD 1:", "cant otros:" + resultado.getCount());
        resultado.moveToFirst();
        while (!resultado.isAfterLast()) {
            Denuncia unaDenuncia = new Denuncia();
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));
            arregloDenuncias.add(unaDenuncia);
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return arregloDenuncias;
    }

    public ArrayList<Denuncia> obtenerTodasLasDenuncias() {
        SQLiteDatabase miMD = this.getReadableDatabase();
        ArrayList<Denuncia> arregloDenuncias = new ArrayList<>();
        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS, null);
        resultado.moveToFirst();
        while (!resultado.isAfterLast()) {
            Denuncia unaDenuncia = new Denuncia();
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));

            arregloDenuncias.add(unaDenuncia);
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return arregloDenuncias;
    }

    /**
     * Devuelve todas las denuncias registradas en la Base de datos local y no sincronizadas en Mysql
     *
     * @return arreglo de con todas las denuncias y sus datos basicos
     */
    public ArrayList<Denuncia> obtenerTodasLasDenunciasVersion0() {
        SQLiteDatabase miMD = this.getReadableDatabase();
        ArrayList<Denuncia> arregloDenuncias = new ArrayList<>();
        Cursor resultado = miMD.rawQuery("select * from " + TABLA_DENUNCIAS + " where " + COLUMNA_DENUNCIA_VERSION + "=0", null);
        resultado.moveToFirst();
        while (!resultado.isAfterLast()) {
            Denuncia unaDenuncia = new Denuncia();
            unaDenuncia.setVersion(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_VERSION)));
            unaDenuncia.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_DEN_WOW)));
            unaDenuncia.setIdDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID)));
            unaDenuncia.setIdWowzer(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_ID_WOWZER)));
            unaDenuncia.setWowzer(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_WOWZER)));
            unaDenuncia.setFechaDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA)));
            unaDenuncia.setLatitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LATITUD)));
            unaDenuncia.setLongitud(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_LONGITUD)));
            unaDenuncia.setTipoDenuncia(resultado.getInt(resultado.getColumnIndex(COLUMNA_DENUNCIA_TIPO)));
            unaDenuncia.setEstadoDenuncia(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_ESTADO)));
            unaDenuncia.setComentarios(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_COMENTARIO)));
            unaDenuncia.setImagen(resultado.getBlob(resultado.getColumnIndex(COLUMNA_DENUNCIA_IMAGEN)));
            unaDenuncia.setRating(resultado.getDouble(resultado.getColumnIndex(COLUMNA_DENUNCIA_RATING)));
            unaDenuncia.setFecha_ult_actualizacion(resultado.getString(resultado.getColumnIndex(COLUMNA_DENUNCIA_FECHA_ULT_ACTUALIZACION)));
            arregloDenuncias.add(unaDenuncia);
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return arregloDenuncias;
    }

    /**
     * Metodo que actualiza la Denuncia luego de insertar en la bd centrar
     *
     * @param laDenuncia objeto tipoDenuncia
     * @param elIDWOW    ID de la Denuncia en la BD Central
     * @return Registros Actualizados (Debe ser 1)
     */
    public int actualizaDenunciaInsercion(Denuncia laDenuncia, Integer elIDWOW) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        int valorUpdate;
        if (elIDWOW != 0) {
            datos.put(COLUMNA_DENUNCIA_ID_DEN_WOW, elIDWOW);
            datos.put(COLUMNA_DENUNCIA_VERSION, 1);
            valorUpdate = miBD.update(TABLA_DENUNCIAS, datos, COLUMNA_DENUNCIA_ID + "=" + laDenuncia.getIdDenuncia(), null);
        } else {
            valorUpdate = -1;
        }
    //    Log.i("BD UPDATE WS:", "Valor:" + valorUpdate + " WS:" + elIDWOW.toString());
        return valorUpdate;
    }

    /**
     * Metodo que acutaliza la valoracion de una Denuncia/Alerta
     *
     * @param elID     ID de la Denuncia a Actualizar
     * @param elWowzer Alias del Wowzer que creo la Denuncia
     * @param elRating Valor de la Valoracion de la denuncia
     * @return Registros Actualizados (Debe ser 1)
     */
    public int actualizarValoracion(String elID, String elWowzer, Double elRating) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put(COLUMNA_DENUNCIA_RATING, elRating);
        return miBD.update(TABLA_DENUNCIAS, datos, COLUMNA_DENUNCIA_ID + "=" + elID + " and " + COLUMNA_DENUNCIA_WOWZER + " ='" + elWowzer + "'", null);
    }

    /**
     * Metodo para registrar los datos localmente del usuario para efectuar el ingreso automatico sin buscar en internet
     *
     * @param usr   Datos del usuario
     * @param alias Alias del usuario/wowzer
     * @param sexo  sexo del wowzer
     * @param nivel nivel del wowzer
     * @return El estatus del registro en la base de datos del telefono
     */
    public String registrarUsuarioLogeado(int usr, String alias, String sexo, int nivel) {
        String status = null;
        SQLiteDatabase miBD = this.getWritableDatabase();
        ContentValues valoresLogin = new ContentValues();
        valoresLogin.put(COLUMNA_LOGIN_USUARIO, alias);
        valoresLogin.put(COLUMNA_LOGIN_ID_USUARIO, usr);
        valoresLogin.put(COLUMNA_LOGIN_ESTATUS, "ACTIVO");
        valoresLogin.put(COLUMNA_LOGIN_SEXO, sexo);
        valoresLogin.put(COLUMNA_LOGIN_NIVEL, nivel);
        if (miBD.insert(TABLA_LOGIN, null, valoresLogin) > 0) {
            status = "OK";
        } else {
            status = "ERROR";
        }
        miBD.close();
      //  Log.i("registra login", " ");
        return status;
    }

    /**
     * Devuelve el usuario ya registrado en el telefono para no cargar el LOGIN
     *
     * @return Alias del usuario/wowzer
     */
    public String devolverAliasUsrLogueado() {
        SQLiteDatabase miBD = this.getReadableDatabase();
        String idLogged = null;
        Cursor resultado = miBD.rawQuery("select * from " + TABLA_LOGIN + " where lg_estatus_user='ACTIVO'", null);
        if (resultado.getCount() > 0) {
            resultado.moveToFirst();
            if (!resultado.isAfterLast()) {
                idLogged = resultado.getString(resultado.getColumnIndex(COLUMNA_LOGIN_USUARIO));
            }
        } else {
            idLogged = "X";
        }
        resultado.close();
        miBD.close();
        return idLogged;
    }

    /**
     * Devuelve el usuario ya registrado en el telefono para no cargar el LOGIN
     *
     * @return devuelve datos del logueado, usuario, sexo , nivel
     */
    public String[] devolverDatosLogueado() {
        SQLiteDatabase miBD = this.getReadableDatabase();
        String[] Logged = {};
        Cursor resultado = miBD.rawQuery("select * from " + TABLA_LOGIN + " where lg_estatus_user='ACTIVO'", null);
        if (resultado.getCount() > 0) {
            resultado.moveToFirst();
            if (!resultado.isAfterLast()) {
                String[] tmpLogged = {resultado.getString(resultado.getColumnIndex(COLUMNA_LOGIN_USUARIO)),
                        resultado.getString(resultado.getColumnIndex(COLUMNA_LOGIN_SEXO)),
                        String.valueOf(resultado.getInt(resultado.getColumnIndex(COLUMNA_LOGIN_NIVEL)))};


                Logged = tmpLogged;
            }
        } else {
            String[] tmp1Logged = {
                    "X", "", "0"};

            Logged = tmp1Logged;
        }
        resultado.close();
        miBD.close();
        return Logged;
    }


    /**
     * Devuelve el Id del usuario ya registrado en el telefono
     *
     * @return ID del usuario/wowzer
     */
    public int devolverIDUsrLogueado() {
        SQLiteDatabase miBD = this.getReadableDatabase();
        int idLogged = 0;
        Cursor resultado = miBD.rawQuery("select * from " + TABLA_LOGIN + " where lg_estatus_user='ACTIVO'", null);
        if (resultado.getCount() > 0) {
            resultado.moveToFirst();
            if (!resultado.isAfterLast()) {
                idLogged = resultado.getInt(resultado.getColumnIndex(COLUMNA_LOGIN_ID_USUARIO));
            }
        } else {
            idLogged = 0;
        }
        resultado.close();
        miBD.close();
        return idLogged;
    }

    /**
     * Para limpiar el login, solo para las pruebas, eliminar al terminar
     */
    public void limpiarConexion() {
        SQLiteDatabase miBD = this.getWritableDatabase();
        Cursor resultado = miBD.rawQuery("delete from " + TABLA_LOGIN, null);
        if (resultado.getCount() > 0) {
        }
        miBD.close();
    }


    /**
     * Para limpiar el login logueado y poder volver ingresar con otra sesion.
     */
    public void limpiarIDUsrLogueadoConexion() {
        SQLiteDatabase miBD = this.getWritableDatabase();
        Cursor resultado = miBD.rawQuery("delete from " + TABLA_LOGIN + " where lg_estatus_user='ACTIVO'", null);
        if (resultado.getCount() > 0) {
        }
        miBD.close();
        PrimeraVez(1);
    }


    /**
     * Devuelve el id de la mayor denuncia obtenida en la bd Mysql o central
     *
     * @return Mayor id de denuncia que se encuentra almacenada en el  telefono
     */
    public Integer devolverIdUltimaDenuncia() {
        SQLiteDatabase miBD = this.getReadableDatabase();
        Integer id = 0;
        Cursor resultado = miBD.rawQuery("select * from " + TABLA_CONTROL + " where " + COLUMNA_CTRL_NOMBRE + "='ID_DENUNCIA'", null);
        if (resultado.getCount() > 0) {
            resultado.moveToFirst();
            if (!resultado.isAfterLast()) {
                id = resultado.getInt(resultado.getColumnIndex(COLUMNA_VALOR_INT));
            }
        } else {
            id = 0;
        }
        resultado.close();
        miBD.close();
        return id;
    }

    /**
     * Desvirga o devuelve la virginidad
     * @param virgen  si es la primera vez se pone 1 sino es 0
     * @return
     */
    public int PrimeraVez(int virgen) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put(COLUMNA_VALOR_INT, virgen);
        String idDenuncia = "'PRIMERAVEZ'";
        int resultado = miBD.update(TABLA_CONTROL, datos, COLUMNA_CTRL_NOMBRE + "=" + idDenuncia, null);
        miBD.close();
        return resultado;

    }

    /**
     * @param IdDenuncia
     * @return
     */
    public int actualizarControlDenuncia(Integer IdDenuncia) {
            SQLiteDatabase miBD = this.getWritableDatabase();
            ContentValues datos = new ContentValues();
            datos.put(COLUMNA_VALOR_INT, IdDenuncia);
            String idDenuncia = "'ID_DENUNCIA'";
            int resultado = miBD.update(TABLA_CONTROL, datos, COLUMNA_CTRL_NOMBRE + "=" + idDenuncia, null);
        miBD.close();
        return resultado;

    }

    /**
     * se elimina de la bd del celular la denuncia
     * @param IdDenuncia id de mysql de la denuncia
     * @return
     */
    public int eliminarDenuncia(Integer IdDenuncia)
    {
        SQLiteDatabase miBD = this.getWritableDatabase();
        String idDenuncia = "'ID_DENUNCIA'";
        int resultado = miBD.delete(TABLA_DENUNCIAS, COLUMNA_DENUNCIA_ID_DEN_WOW + "=?", new String[]{IdDenuncia.toString()});
        miBD.close();
        return resultado;

    }

    public int eliminarDenunciaIDInterno(Integer denuncia){
        int resultado;
        SQLiteDatabase miBD = this.getWritableDatabase();
        resultado = miBD.delete(TABLA_DENUNCIAS, COLUMNA_DENUNCIA_ID + "=" + denuncia.toString(), null);
        miBD.close();
        return resultado;

    }


    /**
     * inserta las notificaciones a ser mostradas en el servicio de notificaciones, siermpre inserta 0 porque no ha sido procesado
     * @param IdDenWow
     * @return
     */
    public Boolean insertarNotificacion(Integer IdDenWow, String Mensaje) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        long result=0;

        ContentValues valoresVerificacion = new ContentValues();
        Cursor resultado = miBD.rawQuery("select * from " + TABLA_NOTIFICACION + " where " + COLUMNA_NTF_PROCESADA + "=0 and " + COLUMNA_NTF_DENUNCIA + "=" +IdDenWow , null);

        if (resultado.getCount()==0)
        {
            ContentValues valoresNotificacion = new ContentValues();
            valoresNotificacion.put(COLUMNA_NTF_DENUNCIA, IdDenWow);
            valoresNotificacion.put(COLUMNA_NTF_PROCESADA, 0);
            valoresNotificacion.put(COLUMNA_NTF_MENSAJE, Mensaje);
            result = miBD.insert(TABLA_NOTIFICACION, null, valoresNotificacion);
        }
            miBD.close();
        return (result==1);
    }


    /**
     * obtiene todas las notificaciones
     * @return
     */
    public ArrayList<Notificacion> obtenerNotificaciones() {
        SQLiteDatabase miMD = this.getReadableDatabase();
        ArrayList<Notificacion> arregloNotificacion = new ArrayList<Notificacion>();
        Cursor resultado = miMD.rawQuery("select * from " + TABLA_NOTIFICACION + " where " + COLUMNA_NTF_PROCESADA + "=0", null);
        resultado.moveToFirst();
        while (!resultado.isAfterLast()) {
            Notificacion registro= new Notificacion();
            registro.setIdDenWow(resultado.getInt(resultado.getColumnIndex(COLUMNA_NTF_DENUNCIA)));
            registro.setProcesada(resultado.getInt(resultado.getColumnIndex(COLUMNA_NTF_DENUNCIA)));
            registro.setMensaje(resultado.getString(resultado.getColumnIndex(COLUMNA_NTF_MENSAJE)));
            arregloNotificacion.add(registro);
            resultado.moveToNext();
        }
        resultado.close();
        miMD.close();
        return arregloNotificacion;
    }

    /**
     * borra de la tabla de notificaciones las notificaciones ya mostradas o procesadas
     * @return devuelve la cantidad de notificaciones depurdas
     */
 public  int depurarNotificaciones()
    {
         SQLiteDatabase miBD = this.getWritableDatabase();

        int result= miBD.delete(TABLA_NOTIFICACION, COLUMNA_NTF_PROCESADA + "=?", new String[]{"1"});
        miBD.close();
        return result;
     }

    /**proceso para actualizar la notificacion procesada
     * @param IdDenuncia
     * @return
     */
    public int actualizarNotificacion(Integer IdDenuncia) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put(COLUMNA_NTF_PROCESADA, 1);
        int result= miBD.update(TABLA_NOTIFICACION, datos, COLUMNA_NTF_DENUNCIA + "=?", new String[]{IdDenuncia.toString()});
        miBD.close();
        return result;

    }

    /**
     * actualiza la imagen en la bd de una denuncia
     * @param IdDenWow
     * @param Imagen
     * @return
     */
    public int guardarImagen(Integer IdDenWow,byte[] Imagen) {
        SQLiteDatabase miBD = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put(COLUMNA_DENUNCIA_IMAGEN,  Imagen);
        int result= miBD.update(TABLA_DENUNCIAS, datos, COLUMNA_DENUNCIA_ID_DEN_WOW + "=?", new String[]{IdDenWow.toString()});
        miBD.close();
        return result;

    }


}
