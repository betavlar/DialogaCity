package com.jayktec.DialogaCity;

/**
 * Created by Alexander on 29-Aug-15.
 * Clase que define el objeto Denuncia/Alerta
 */
public class Denuncia {
    private Integer idDenuncia;
    private Integer idWowzer;
    private String wowzer;
    private String fechaDenuncia;
    private Double latitud;
    private Double longitud;
    private Integer tipoDenuncia;
    private String estadoDenuncia;
    private String comentarios;
    private byte[] imagen;
    private Double rating;
    private String fecha_ult_actualizacion;
    private Boolean usuarioWeb;
    /* tipos de denuncias

SEMAFORO	1
CLOACA	2
CALLE	3
ARBOL	4
ALUMBRADO	5
BASURA	6
    */


    //yle
    private int version;
    private Integer idDenWow;

    public Boolean getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(Boolean usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public Integer getIdWowzer() {
        return idWowzer;
    }

    public void setIdWowzer(Integer IdWowzer) {
        this.idWowzer = IdWowzer;
    }

    public Integer getIdDenuncia() {
        return idDenuncia;
    }

    public void setIdDenuncia(Integer idDenuncia) {
        this.idDenuncia = idDenuncia;
    }

    public String getWowzer() {
        return wowzer;
    }

    public void setWowzer(String wowzer) {
        this.wowzer = wowzer;
    }

    public String getFechaDenuncia() {
        return fechaDenuncia;
    }

    public void setFechaDenuncia(String fechaDenuncia) {
        this.fechaDenuncia = fechaDenuncia;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public int getTipoDenuncia() {
        return tipoDenuncia;
    }

    public void setTipoDenuncia(int tipoDenuncia) {
        this.tipoDenuncia = tipoDenuncia;
    }

    public String getEstadoDenuncia() {
        return estadoDenuncia;
    }

    public void setEstadoDenuncia(String estadoDenuncia) {
        this.estadoDenuncia = estadoDenuncia;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getFecha_ult_actualizacion() {
        return fecha_ult_actualizacion;
    }

    public void setFecha_ult_actualizacion(String fecha_ult_actualizacion) {
        this.fecha_ult_actualizacion = fecha_ult_actualizacion;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Integer getIdDenWow() {
        return idDenWow;
    }

    public void setIdDenWow(Integer idDenWow) {
        this.idDenWow = idDenWow;
    }
}
