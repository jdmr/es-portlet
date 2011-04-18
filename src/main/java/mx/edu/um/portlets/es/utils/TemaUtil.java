/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.edu.um.portlets.es.utils;

/**
 *
 * @author jdmr
 */
public class TemaUtil {
    private String titulo;
    private String contenido;
    
    public TemaUtil() {}
    
    public TemaUtil(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
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
     * @return the contenido
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * @param contenido the contenido to set
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
}
