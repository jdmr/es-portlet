package mx.edu.um.portlets.esu.utils;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TemaUtil other = (TemaUtil) obj;
        if (this.assetId != other.assetId && (this.assetId == null || !this.assetId.equals(other.assetId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.assetId != null ? this.assetId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "TemaUtil{" + "assetId=" + assetId + ", entradaId=" + entradaId + ", titulo=" + titulo + ", autor=" + autor + ", contenido=" + contenido + ", url=" + url + '}';
    }
    
}
