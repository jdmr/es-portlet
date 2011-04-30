package mx.edu.um.portlets.es.utils;

/**
 *
 * @author jdmr
 */
public class TemaUtil {
    private Long assetId;
    private Long entradaId;
    private String titulo;
    private String autor;
    private String contenido;
    private String url;
    
    public TemaUtil() {}
    
    public TemaUtil(Long assetId, Long entradaId, String titulo, String autor, String contenido, String url) {
        this.assetId = assetId;
        this.entradaId = entradaId;
        this.titulo = titulo;
        this.autor = autor;
        this.contenido = contenido;
        this.url = url;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getEntradaId() {
        return entradaId;
    }

    public void setEntradaId(Long entradaId) {
        this.entradaId = entradaId;
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

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
