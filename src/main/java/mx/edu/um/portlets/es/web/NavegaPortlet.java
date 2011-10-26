package mx.edu.um.portlets.es.web;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import java.util.List;
import javax.portlet.*;
import mx.edu.um.portlets.es.utils.EstadisticasUtil;
import mx.edu.um.portlets.es.utils.TagsUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class NavegaPortlet {

    private static final Logger log = LoggerFactory.getLogger(NavegaPortlet.class);

    public NavegaPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de navegacion");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model model) throws PortalException, SystemException {
        log.debug("Navegacion");
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        long scopeGroupId = themeDisplay.getScopeGroupId();
        DateTime hoy = (DateTime) request.getPortletSession().getAttribute("hoy", PortletSession.APPLICATION_SCOPE);
        String[] tags = TagsUtil.getTagsConDia(new String[5], hoy);
        tags[4] = "podcast";
        long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

        AssetEntryQuery assetEntryQuery = new AssetEntryQuery();
        assetEntryQuery.setAllTagIds(assetTagIds);

        List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);
        for (AssetEntry asset : results) {
            log.debug("TIPO: {}", asset.getClassName());
            if (asset.getClassName().equals(DLFileEntry.class.getName())) {
                log.debug("Lo encontre!!!");
                DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(asset.getClassPK());
                model.addAttribute("audioLeccion", true);
                model.addAttribute("podcastDiarioURL",
                        "/documents/"
                        + themeDisplay.getScopeGroupId()
                        + StringPool.SLASH
                        + fileEntry.getUuid());
            }
        }

        return "navega/ver";
    }

    @RequestMapping(params = "action=cambiaFecha")
    public void cambiaFecha(ActionRequest request, ActionResponse response,
            @RequestParam String fechaNavegaTxt,
            @ModelAttribute EstadisticasUtil estadisticasUtil,
            BindingResult result,
            Model model, SessionStatus sessionStatus) {
        log.debug("Asignando fecha {}", fechaNavegaTxt);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime hoy = fmt.parseDateTime(fechaNavegaTxt);
        request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
        request.getPortletSession().setAttribute("hoyString", fechaNavegaTxt, PortletSession.APPLICATION_SCOPE);
    }
}
