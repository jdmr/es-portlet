package mx.edu.um.portlets.es.web;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.es.utils.TagsUtil;
import mx.edu.um.portlets.es.utils.TemaUtil;
import mx.edu.um.portlets.es.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
public class InicioPortlet {

    private static final Logger log = LoggerFactory.getLogger(InicioPortlet.class);

    public InicioPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de inicio");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model model) {
        log.debug("Mostrando el inicio");
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


            // Busca el contenido del dia
            String[] tags = TagsUtil.getTagsConDia(new String[4], hoy);

            long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            log.debug("Buscando la leccion del dia {}", hoy);
            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(JournalArticle.class.getName())) {
                    model.addAttribute("tituloLeccion", asset.getTitle().toUpperCase());
                    model.addAttribute("contenidoLeccion", asset.getDescription());
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE dd/MM/yyyy");
                    DateTimeFormatter fmt2 = fmt.withLocale(themeDisplay.getLocale());
                    StringBuilder sb = new StringBuilder(fmt2.print(hoy));
                    String fecha = StringUtil.upperCaseFirstLetter(sb.toString());
                    model.addAttribute("fecha", fecha);
                }
            }
            
            log.debug("Buscando el versiculo");
            tags[3] = "versiculo";
            assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(JournalArticle.class.getName())) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(asset.getClassPK());
                    AssetEntryServiceUtil.incrementViewCounter(asset.getClassName(), ja.getResourcePrimKey());
                    String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                    
                    model.addAttribute("versiculo", contenido);
                    break;
                }
            }

            // Tags para buscar las fotos de la semana
//            log.debug("Buscando las fotos");
//            tags[3] = "fotos";
//            assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);
//
//            assetEntryQuery.setAllTagIds(assetTagIds);
//
//            results = AssetEntryServiceUtil.getEntries(assetEntryQuery);
//
//            int fotos = 0;
//            for (AssetEntry asset : results) {
//                log.debug("ASSET: " + asset.getClassName());
//                if (asset.getClassName().equals(IGImage.class.getName())) {
//                    IGImage image = IGImageLocalServiceUtil.getImage(asset.getClassPK());
//                    String url = themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getLargeImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getLargeImageId());
//                    log.debug("URL: {}", url);
//                    model.addAttribute("imagen" + (fotos++), url);
//                }
//            }
//            model.addAttribute("cantidadFotos", fotos);

            // Buscando los temas de dialoga de la semana
            tags[3] = "dialoga";
            assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            log.debug("Buscando los temas de dialoga");
            List<TemaUtil> temasDialoga = new ArrayList<TemaUtil>();
            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(JournalArticle.class.getName())) {
                    StringBuilder url = new StringBuilder();
                    url.append("/dialoga?p_p_id=dialoga_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_dialoga_WAR_esportlet_assetId=");
                    url.append(asset.getPrimaryKey());
                    url.append("&_dialoga_WAR_esportlet_entradaId=");
                    url.append(asset.getClassPK());
                    url.append("&_dialoga_WAR_esportlet_action=completo");
                    User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                    temasDialoga.add(new TemaUtil(asset.getPrimaryKey(),asset.getClassPK(),asset.getTitle().toUpperCase(), autor.getFullName(), StringUtil.shorten(asset.getDescription(), 150), url.toString()));
                }
            }
            model.addAttribute("temasDialoga", temasDialoga);

            // Buscando los temas de comunica de la semana
            tags[3] = "comunica";
            assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            log.debug("Buscando los temas de comunica");
            List<TemaUtil> temasComunica = new ArrayList<TemaUtil>();
            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(JournalArticle.class.getName())) {
                    StringBuilder url = new StringBuilder();
                    url.append("/comunica?p_p_id=comunica_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_comunica_WAR_esportlet_assetId=");
                    url.append(asset.getPrimaryKey());
                    url.append("&_comunica_WAR_esportlet_entradaId=");
                    url.append(asset.getClassPK());
                    url.append("&_comunica_WAR_esportlet_action=completo");
                    User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                    temasComunica.add(new TemaUtil(asset.getPrimaryKey(),asset.getClassPK(),asset.getTitle().toUpperCase(), autor.getFullName(), StringUtil.shorten(asset.getDescription(), 150), url.toString()));
                }
            }
            model.addAttribute("temasComunica", temasComunica);

        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "inicio/ver";
    }

}
