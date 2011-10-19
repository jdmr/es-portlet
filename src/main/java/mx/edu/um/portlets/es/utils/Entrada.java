package mx.edu.um.portlets.escuelasabatica.util;

import java.io.Serializable;

/**
 *
 * @author jdmr
 */
public class Entrada implements Serializable {
    private Long id;
    private Long assetId;
    private String contenido;

    public Entrada() {}

    public Entrada(Long id, Long assetId, String contenido) {
        this.id = id;
        this.assetId = assetId;
        this.contenido = contenido;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
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
