package com.jayktec.DialogaCity;

/**
 * Created by Usuario on 06-08-2015.
 */

/**
 * Created by Usuario on 10-07-2015.
 */
public class Comentario {
    private Integer id_comentario;
    private Integer idwow;
    private Integer idUsuario;
    private String  comentario;
    private String fecha;
    private Integer id_denuncia;
    private  String loginUsuario;
    private  Boolean usuarioWeb;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setId_comentario(Integer idComentario)
    {
        id_comentario=idComentario;
    }
    public Integer getId_comentario( )
    {
        return id_comentario;
    }


    public void setId_denuncia(Integer idDenuncia)
    {
        id_denuncia=idDenuncia;
    }

    public Integer getId_denuncia( )
    {
        return id_denuncia;
    }


    public void setId_wow(Integer idWow)
    {
        idwow=idWow;
    }

    public Integer getId_Wow( )
    {
        return idwow;
    }

    public void setUsuarioWeb(Boolean UsuarioWeb)
    {
        usuarioWeb=UsuarioWeb;
    }

    public Boolean getUsuarioWeb( )
    {
        return usuarioWeb;
    }

    public void setLoginUsuario(String LoginUsuario)
    {
        loginUsuario=LoginUsuario;
    }

    public String getLoginUsuario( )
    {
        return loginUsuario;
    }

    public void set_comentario(String Comentario)
    {
        comentario=Comentario;
    }

    public String get_comentario( )
    {
        return comentario;
    }

    public void set_fecha(String Fecha)
    {
        fecha=Fecha;
    }

    public String get_fecha( )
    {
        return fecha;
    }

}
