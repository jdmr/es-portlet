package mx.edu.um.portlets.es.web;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.journal.model.JournalArticle;
import java.text.NumberFormat;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.es.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class ResumenComunicaPortlet {

    private static final Logger log = LoggerFactory.getLogger(ResumenComunicaPortlet.class);

    public ResumenComunicaPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de resumen de comunica");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model model) {
        log.debug("Mostrando el resumen");
        TimeZone tz = null;
        DateTimeZone zone = null;
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        try {
            tz = themeDisplay.getTimeZone();
            zone = DateTimeZone.forID(tz.getID());
        } catch (IllegalArgumentException e) {
            zone = DateTimeZone.forID(ZonaHorariaUtil.getConvertedId(tz.getID()));
        }
        try {
            long scopeGroupId = themeDisplay.getScopeGroupId();

            AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

            DateTime hoy = (DateTime) request.getPortletSession().getAttribute("hoy", PortletSession.APPLICATION_SCOPE);
            if (hoy == null) {
                hoy = new DateTime(zone);
                log.debug("Subiendo atributo hoy({}) a la sesion", hoy);
                request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
            }

            // Buscando los temas de comunica de la semana
            String[] tags = getTags(hoy);
            tags[3] = "comunica";

            long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            log.debug("Buscando los temas de comunica");
            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(JournalArticle.class.getName())) {
                    User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                    model.addAttribute("autorTema", autor.getFullName());
                    model.addAttribute("tituloTema",asset.getTitle().toUpperCase());
                    model.addAttribute("contenidoTema",asset.getDescription());
                    model.addAttribute("assetId",asset.getPrimaryKey());
                    model.addAttribute("entradaId",asset.getClassPK());
                    StringBuilder url = new StringBuilder();
                    url.append("/comunica?p_p_id=comunica_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_comunica_WAR_esportlet_assetId=");
                    url.append(asset.getPrimaryKey());
                    url.append("&_comunica_WAR_esportlet_entradaId=");
                    url.append(asset.getClassPK());
                    url.append("&_comunica_WAR_esportlet_action=completo");
                    model.addAttribute("verTema", url.toString());
                    break;
                }
            }

        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "resumenComunica/ver";
    }

    private String[] getTags(DateTime hoy) {
        String[] tags = new String[4];
        DateTime inicio = new DateTime(hoy.getYear(), 3, 26, 0, 0, 0, 0, hoy.getZone());
        log.debug("HOY: {}", hoy);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        tags[0] = new Integer(hoy.getYear()).toString();
        tags[1] = "t2";
        Weeks weeks = Weeks.weeksBetween(inicio, hoy);
        tags[2] = "l" + nf.format(weeks.getWeeks() + 1);
        log.debug("TAGS: {} {} {} {}", tags);

        return tags;
    }
}
