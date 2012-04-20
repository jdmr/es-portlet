package mx.edu.um.portlets.esu.web;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.esu.dao.DiaDao;
import mx.edu.um.portlets.esu.model.Dia;
import mx.edu.um.portlets.esu.utils.TagsUtil;
import mx.edu.um.portlets.esu.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class VersiculoPortlet {

    private static final Logger log = LoggerFactory.getLogger(VersiculoPortlet.class);
    @Autowired
    private DiaDao diaDao;

    public VersiculoPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de versiculos");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, @RequestParam(required = false) Integer dias, Model model) {
        log.debug("Viendo el versiculo");
        TimeZone tz = null;
        DateTimeZone zone;
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

            Dia dia = diaDao.obtiene(hoy);
            if (dia == null) {
                log.debug("No encontre el dia");
                // Busca el contenido del dia
                String[] tags = TagsUtil.getTags(new String[4], hoy);
                tags[3] = "versiculo";

                long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                log.debug("Buscando el versiculo de la semana {}", hoy);
                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(JournalArticle.class.getName())) {
                        JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(asset.getClassPK());
                        String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", ""+themeDisplay.getLocale(), themeDisplay);
                        model.addAttribute("contenido",contenido);
                    }
                }
            } else {
                JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(dia.getVersiculo());
                String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", ""+themeDisplay.getLocale(), themeDisplay);
                model.addAttribute("contenido",contenido);
            }

        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "versiculo/ver";
    }

}
