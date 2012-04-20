package mx.edu.um.portlets.esu.utils;

import java.io.Serializable;

/**
 *
 * @author jdmr
 */
public class Perfil implements Serializable {

    private String nombre;
    private String titulo;
    private String twitter;
    private String facebook;
    private String resena;
    private String imagen;
    private String correo;
    private String urlPerfil;

    public Perfil() {
    }

    public Perfil(String nombre, String titulo, String twitter, String facebook, String resena, String imagen, String correo, String urlPerfil) {
        this.nombre = nombre;
        this.titulo = titulo;
        this.twitter = twitter;
        this.facebook = facebook;
        this.resena = resena;
        this.imagen = imagen;
        this.correo = correo;
        this.urlPerfil = urlPerfil;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the twitter
     */
    public String getTwitter() {
        return twitter;
    }

    /**
     * @param twitter the twitter to set
     */
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    /**
     * @return the facebook
     */
    public String getFacebook() {
        return facebook;
    }

    /**
     * @param facebook the facebook to set
     */
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    /**
     * @return the resena
     */
    public String getResena() {
        return resena;
    }

    /**
     * @param resena the resena to set
     */
    public void setResena(String resena) {
        this.resena = resena;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }
}
