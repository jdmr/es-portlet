package mx.edu.um.portlets.esu.web;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.bookmarks.model.BookmarksEntry;
import com.liferay.portlet.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.esu.dao.DiaDao;
import mx.edu.um.portlets.esu.model.Dia;
import mx.edu.um.portlets.esu.utils.TagsUtil;
import mx.edu.um.portlets.esu.utils.TemaUtil;
import mx.edu.um.portlets.esu.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private DiaDao diaDao;

    public InicioPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de inicio");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model model) {
        log.debug("Iniciando armado de inicio");
        Date date = new Date();
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

            DateTime now = new DateTime(zone);
            DateTime hoy = (DateTime) request.getPortletSession().getAttribute("hoy", PortletSession.APPLICATION_SCOPE);
            if (hoy == null) {
                hoy = new DateTime(zone);
                log.debug("Subiendo atributo hoy({}) a la sesion", hoy);
                request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
            }

            Dia dia = diaDao.obtiene(hoy);
            if (dia == null) {
                log.debug("No encontre el dia de {}", hoy);
                dia = new Dia(hoy.toLocalDate().toDate());
                // Busca el contenido del dia
                String[] tags = TagsUtil.getTagsConDia(new String[4], hoy);
                String[] etiquetas = ArrayUtil.clone(tags);
                log.debug("Buscando contenido del dia");

                long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                log.debug("Buscando la leccion del dia {}", hoy);
                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(JournalArticle.class.getName())) {
                        dia.setLeccion(asset.getEntryId());
                        dia.setLeccionAsset(asset.getEntryId());
                        model.addAttribute("tituloLeccion", asset.getTitle().toUpperCase());
                        model.addAttribute("contenidoLeccion", asset.getDescription());
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE dd/MM/yyyy");
                        DateTimeFormatter fmt2 = fmt.withLocale(themeDisplay.getLocale());
                        StringBuilder sb = new StringBuilder(fmt2.print(hoy));
                        String fecha = StringUtil.upperCaseFirstLetter(sb.toString());
                        model.addAttribute("fecha", fecha);
                    }
                }

                tags[3] = "versiculo";
                log.debug("Buscando el versiculo");
                assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(JournalArticle.class.getName())) {
                        dia.setVersiculo(asset.getClassPK());
                        dia.setVersiculoAsset(asset.getEntryId());
                        JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(asset.getClassPK());
                        String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);

                        model.addAttribute("versiculo", contenido);
                        break;
                    }
                }

                tags[3] = "video";
                log.debug("Buscando el video");
                assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                boolean encontreVideo = false;
                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(BookmarksEntry.class.getName())) {
                        dia.setVideo(asset.getClassPK());
                        BookmarksEntry be = BookmarksEntryLocalServiceUtil.getBookmarksEntry(asset.getClassPK());
                        model.addAttribute("videoLeccion", be.getUrl());
                        encontreVideo = true;
                        break;
                    }
                }

                log.debug("encontreVideo? {}", encontreVideo);
                if (!encontreVideo) {
                    log.debug("Asignando");
                    model.addAttribute("imagenLeccion", "/image/image_gallery?uuid=6724e2d7-01da-45f6-8d86-d28b3429cb98&groupId=15711&t=1334800965228");
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
                int cont = 1;
                List<TemaUtil> temasDialoga = new ArrayList<TemaUtil>();
                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(JournalArticle.class.getName())) {
                        switch(cont++) {
                            case 1:
                                dia.setDialoga1(asset.getEntryId());
                                break;
                            case 2:
                                dia.setDialoga2(asset.getEntryId());
                                break;
                            case 3:
                                dia.setDialoga3(asset.getEntryId());
                                break;
                            case 4:
                                dia.setDialoga4(asset.getEntryId());
                                break;
                        }
                        StringBuilder url = new StringBuilder();
                        url.append("/dialoga?p_p_id=dialoga_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_dialoga_WAR_esportlet_assetId=");
                        url.append(asset.getPrimaryKey());
                        url.append("&_dialoga_WAR_esportlet_entradaId=");
                        url.append(asset.getClassPK());
                        url.append("&_dialoga_WAR_esportlet_action=completo");
                        User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                        temasDialoga.add(new TemaUtil(asset.getPrimaryKey(), asset.getClassPK(), asset.getTitle().toUpperCase(), autor.getFullName(), StringUtil.shorten(asset.getDescription(), 300), url.toString()));
                    }
                }
                model.addAttribute("temasDialoga", temasDialoga);

                // Buscando los temas de comunica de la semana
                tags[3] = "comunica";
                assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                log.debug("Buscando los temas de comunica");
                cont = 1;
                List<TemaUtil> temasComunica = new ArrayList<TemaUtil>();
                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(JournalArticle.class.getName())) {
                        switch(cont++) {
                            case 1:
                                dia.setComunica1(asset.getEntryId());
                                break;
                            case 2:
                                dia.setComunica2(asset.getEntryId());
                                break;
                            case 3:
                                dia.setComunica3(asset.getEntryId());
                                break;
                            case 4:
                                dia.setComunica4(asset.getEntryId());
                                break;
                        }
                        StringBuilder url = new StringBuilder();
                        url.append("/comunica?p_p_id=comunica_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_comunica_WAR_esportlet_assetId=");
                        url.append(asset.getPrimaryKey());
                        url.append("&_comunica_WAR_esportlet_entradaId=");
                        url.append(asset.getClassPK());
                        url.append("&_comunica_WAR_esportlet_action=completo");
                        User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                        temasComunica.add(new TemaUtil(asset.getPrimaryKey(), asset.getClassPK(), asset.getTitle().toUpperCase(), autor.getFullName(), StringUtil.shorten(asset.getDescription(), 300), url.toString()));
                    }
                }
                model.addAttribute("temasComunica", temasComunica);

                // Buscando podcast semanal
                log.debug("Buscando podcast semanal");
                //tags = TagsUtil.getTagsConDia(new String[5], hoy);
                tags[3] = "podcast";
                String[] x = ArrayUtil.append(tags, "semanal");
                tags = x;
                assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                results = AssetEntryServiceUtil.getEntries(assetEntryQuery);
                for (AssetEntry asset : results) {
                    if (asset.getClassName().equals(DLFileEntry.class.getName())) {
                        dia.setPodcastSemanal(asset.getClassPK());
                        DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(asset.getClassPK());
                        model.addAttribute("podcastSemanalURL",
                                "/documents/"
                                + themeDisplay.getScopeGroupId()
                                + StringPool.SLASH
                                + fileEntry.getUuid());
                    }
                }

                // Buscando podcast diario
                log.debug("Buscando podcast diario");
                etiquetas[4] = "podcast";
                tags = etiquetas;
                for (String s : tags) {
                    log.debug("Tags: {}", s);
                }
                assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                assetEntryQuery.setAllTagIds(assetTagIds);

                results = AssetEntryServiceUtil.getEntries(assetEntryQuery);
                for (AssetEntry asset : results) {
                    log.debug("TIPO: {}", asset.getClassName());
                    if (asset.getClassName().equals(DLFileEntry.class.getName())) {
                        dia.setPodcastDiario(asset.getClassPK());
                        log.debug("Lo encontre!!!");
                        DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(asset.getClassPK());
                        model.addAttribute("podcastDiarioURL",
                                "/documents/"
                                + themeDisplay.getScopeGroupId()
                                + StringPool.SLASH
                                + fileEntry.getUuid());
                    }
                }

                if (hoy.isBefore(now)) {
                    dia.setCerrado(true);
                }
                
                diaDao.guarda(dia);
            } else {
                log.debug("Buscando la leccion del dia {}", hoy);
                AssetEntry asset = AssetEntryServiceUtil.getEntry(dia.getLeccion());
                model.addAttribute("tituloLeccion", asset.getTitle().toUpperCase());
                model.addAttribute("contenidoLeccion", asset.getDescription());
                DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE dd/MM/yyyy");
                DateTimeFormatter fmt2 = fmt.withLocale(themeDisplay.getLocale());
                StringBuilder sb = new StringBuilder(fmt2.print(hoy));
                String fecha = StringUtil.upperCaseFirstLetter(sb.toString());
                model.addAttribute("fecha", fecha);
                
                log.debug("Buscando el versiculo");
                JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(dia.getVersiculo());
                AssetEntryServiceUtil.incrementViewCounter(asset.getClassName(), ja.getResourcePrimKey());
                String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                model.addAttribute("versiculo", contenido);
                
                log.debug("Buscando el video");
                boolean encontreVideo = false;
                if (dia.getVideo() != null && dia.getVideo() > 0) {
                    BookmarksEntry be = BookmarksEntryLocalServiceUtil.getBookmarksEntry(dia.getVideo());
                    model.addAttribute("videoLeccion", be.getUrl());
                    encontreVideo = true;
                }

                if (!encontreVideo) {
                    model.addAttribute("imagenLeccion", "/image/image_gallery?uuid=b80bc3fa-fb91-4a05-8c45-4827dbebe935&groupId=15711&t=1334874376458");
                }
                
                log.debug("Buscando los temas de dialoga");
                List<TemaUtil> temasDialoga = new ArrayList<TemaUtil>();
                for(int cont = 1;cont<=4;cont++) {
                    switch(cont) {
                        case 1:
                            if (dia.getDialoga1() != null && dia.getDialoga1() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getDialoga1());
                            } else {
                                asset = null;
                            }
                            break;
                        case 2:
                            if (dia.getDialoga2() != null && dia.getDialoga2() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getDialoga2());
                            } else {
                                asset = null;
                            }
                            break;
                        case 3:
                            if (dia.getDialoga3() != null && dia.getDialoga3() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getDialoga3());
                            } else {
                                asset = null;
                            }
                            break;
                        case 4:
                            if (dia.getDialoga4() != null && dia.getDialoga4() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getDialoga4());
                            } else {
                                asset = null;
                            }
                            break;
                    }
                    if (asset != null) {
                        StringBuilder url = new StringBuilder();
                        url.append("/dialoga?p_p_id=dialoga_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_dialoga_WAR_esportlet_assetId=");
                        url.append(asset.getPrimaryKey());
                        url.append("&_dialoga_WAR_esportlet_entradaId=");
                        url.append(asset.getClassPK());
                        url.append("&_dialoga_WAR_esportlet_action=completo");
                        User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                        temasDialoga.add(new TemaUtil(asset.getPrimaryKey(), asset.getClassPK(), asset.getTitle().toUpperCase(), autor.getFullName(), StringUtil.shorten(asset.getDescription(), 300), url.toString()));
                    }
                }
                model.addAttribute("temasDialoga", temasDialoga);
                
                log.debug("Buscando los temas de comunica");
                List<TemaUtil> temasComunica = new ArrayList<TemaUtil>();
                for(int cont = 1;cont<=4;cont++) {
                    switch(cont) {
                        case 1:
                            if (dia.getComunica1() != null && dia.getComunica1() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getComunica1());
                            } else {
                                asset = null;
                            }
                            break;
                        case 2:
                            if (dia.getComunica2() != null && dia.getComunica2() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getComunica2());
                            } else {
                                asset = null;
                            }
                            break;
                        case 3:
                            if (dia.getComunica3() != null && dia.getComunica3() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getComunica3());
                            } else {
                                asset = null;
                            }
                            break;
                        case 4:
                            if (dia.getComunica4() != null && dia.getComunica4() > 0) {
                                asset = AssetEntryServiceUtil.getEntry(dia.getComunica4());
                            } else {
                                asset = null;
                            }
                            break;
                    }
                    if (asset != null) {
                        StringBuilder url = new StringBuilder();
                        url.append("/comunica?p_p_id=comunica_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_comunica_WAR_esportlet_assetId=");
                        url.append(asset.getPrimaryKey());
                        url.append("&_comunica_WAR_esportlet_entradaId=");
                        url.append(asset.getClassPK());
                        url.append("&_comunica_WAR_esportlet_action=completo");
                        User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                        temasComunica.add(new TemaUtil(asset.getPrimaryKey(), asset.getClassPK(), asset.getTitle().toUpperCase(), autor.getFullName(), StringUtil.shorten(asset.getDescription(), 300), url.toString()));
                    }
                }
                model.addAttribute("temasComunica", temasComunica);
                
                // Buscando podcast semanal
                log.debug("Buscando podcast semanal");
                if (dia.getPodcastSemanal() != null && dia.getPodcastSemanal() > 0) {
                    DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(dia.getPodcastSemanal());
                    model.addAttribute("podcastSemanalURL",
                            "/documents/"
                            + themeDisplay.getScopeGroupId()
                            + StringPool.SLASH
                            + fileEntry.getUuid());
                }
                
                log.debug("Buscando podcast diario");
                if (dia.getPodcastDiario() != null && dia.getPodcastDiario() > 0) {
                    DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(asset.getClassPK());
                    model.addAttribute("podcastDiarioURL",
                            "/documents/"
                            + themeDisplay.getScopeGroupId()
                            + StringPool.SLASH
                            + fileEntry.getUuid());
                }
                
            }

        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }
        float segundos = (new Date().getTime() - date.getTime()) / 1000;
        log.debug("Se armo pagina en {}", segundos);

        return "inicio/ver";
    }
}
