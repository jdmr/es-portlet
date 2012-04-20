package mx.edu.um.portlets.esu.utils;

import com.liferay.portlet.blogs.model.BlogsStatsUser;
import java.io.Serializable;

/**
 *
 * @author jdmr
 */
public class EstadisticasUtil implements Serializable {

	private static final long serialVersionUID = -862868344419727561L;
	private BlogsStatsUser statsUser;
    private String url;
    private String fecha;

    public EstadisticasUtil() {
    }

    public EstadisticasUtil(BlogsStatsUser statsUser, String url, String fecha) {
        this.statsUser = statsUser;
        this.url = url;
        this.fecha = fecha;
    }

    public BlogsStatsUser getStatsUser() {
        return statsUser;
    }

    public void setStatsUser(BlogsStatsUser statsUser) {
        this.statsUser = statsUser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
