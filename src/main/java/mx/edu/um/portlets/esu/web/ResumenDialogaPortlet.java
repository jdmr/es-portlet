package mx.edu.um.portlets.esu.web;

import java.util.List;
import java.util.TimeZone;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import mx.edu.um.portlets.esu.utils.ZonaHorariaUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.journal.model.JournalArticle;
import mx.edu.um.portlets.esu.dao.DiaDao;
import mx.edu.um.portlets.esu.model.Dia;
import mx.edu.um.portlets.esu.utils.TagsUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class ResumenDialogaPortlet {

    private static final Logger log = LoggerFactory.getLogger(ResumenDialogaPortlet.class);
    @Autowired
    private DiaDao diaDao;

    public ResumenDialogaPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de resumen de dialoga");
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

            Dia dia = diaDao.obtiene(hoy);
            if (dia == null) {
                log.debug("No encontre el dia");
                // Buscando los temas de dialoga de la semana
                String[] tags = TagsUtil.getTags(new String[4], hoy);
                tags[3] = "dialoga";

                long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                log.debug("Buscando los temas de dialoga");
                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(JournalArticle.class.getName())) {
                        User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                        model.addAttribute("autorTema", autor.getFullName());
                        model.addAttribute("tituloTema", asset.getTitle().toUpperCase());
                        model.addAttribute("contenidoTema", asset.getDescription());
                        StringBuilder url = new StringBuilder();
                        url.append("/dialoga?p_p_id=dialoga_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_dialoga_WAR_esportlet_assetId=");
                        url.append(asset.getPrimaryKey());
                        url.append("&_dialoga_WAR_esportlet_entradaId=");
                        url.append(asset.getClassPK());
                        url.append("&_dialoga_WAR_esportlet_action=completo");
                        model.addAttribute("verTema", url.toString());
                        break;
                    }
                }
            } else {
                AssetEntry asset = AssetEntryServiceUtil.getEntry(dia.getDialoga1());
                User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                model.addAttribute("autorTema", autor.getFullName());
                model.addAttribute("tituloTema", asset.getTitle().toUpperCase());
                model.addAttribute("contenidoTema", asset.getDescription());
                StringBuilder url = new StringBuilder();
                url.append("/dialoga?p_p_id=dialoga_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_dialoga_WAR_esportlet_assetId=");
                url.append(asset.getPrimaryKey());
                url.append("&_dialoga_WAR_esportlet_entradaId=");
                url.append(asset.getClassPK());
                url.append("&_dialoga_WAR_esportlet_action=completo");
                model.addAttribute("verTema", url.toString());
            }

        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "resumenDialoga/ver";
    }

}
