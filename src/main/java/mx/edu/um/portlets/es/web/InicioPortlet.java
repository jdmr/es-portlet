package mx.edu.um.portlets.es.web;

import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.imagegallery.model.IGImage;
import com.liferay.portlet.imagegallery.service.IGImageLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.es.utils.TemaUtil;
import mx.edu.um.portlets.es.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Weeks;
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
        log.debug("Se ha creado una nueva instancia del portlet de inicio");
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
            String[] tags = getTags(hoy);

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
            tags[3] = "fotos";
            assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            log.debug("Buscando las fotos");
            int fotos = 0;
            for (AssetEntry asset : results) {
                log.debug("ASSET: " + asset.getClassName());
                if (asset.getClassName().equals(IGImage.class.getName())) {
                    IGImage image = IGImageLocalServiceUtil.getImage(asset.getClassPK());
                    String url = themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getLargeImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getLargeImageId());
                    log.debug("URL: {}", url);
                    model.addAttribute("imagen" + (fotos++), url);
                }
            }
            model.addAttribute("cantidadFotos", fotos);

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
                    temasDialoga.add(new TemaUtil(asset.getPrimaryKey(),asset.getClassPK(),asset.getTitle().toUpperCase(), StringUtil.shorten(asset.getDescription(), 150), url.toString()));
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
                    url.append("/comunica?p_p_id=comunica_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_dialoga_WAR_esportlet_assetId=");
                    url.append(asset.getPrimaryKey());
                    url.append("&_comunica_WAR_esportlet_entradaId=");
                    url.append(asset.getClassPK());
                    url.append("&_comunica_WAR_esportlet_action=completo");
                    temasComunica.add(new TemaUtil(asset.getPrimaryKey(),asset.getClassPK(),asset.getTitle().toUpperCase(), StringUtil.shorten(asset.getDescription(), 150), url.toString()));
                }
            }
            model.addAttribute("temasComunica", temasComunica);

        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "inicio/ver";
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
        switch (hoy.getDayOfWeek()) {
            case 1:
                tags[3] = "lunes";
                break;
            case 2:
                tags[3] = "martes";
                break;
            case 3:
                tags[3] = "miercoles";
                break;
            case 4:
                tags[3] = "jueves";
                break;
            case 5:
                tags[3] = "viernes";
                break;
            case 6:
                tags[3] = "sabado";
                break;
            case 7:
                tags[3] = "domingo";
                break;
        }
        log.debug("TAGS: {} {} {} {}", tags);

        return tags;
    }
}
