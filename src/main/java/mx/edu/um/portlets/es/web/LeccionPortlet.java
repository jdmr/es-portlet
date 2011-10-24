package mx.edu.um.portlets.es.web;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.*;
import javax.sql.DataSource;
import mx.edu.um.portlets.es.utils.EstadisticasUtil;
import mx.edu.um.portlets.es.utils.TagsUtil;
import mx.edu.um.portlets.es.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class LeccionPortlet {

    private static final Logger log = LoggerFactory.getLogger(LeccionPortlet.class);
    @Autowired
    private DataSource bibliaDS;

    public LeccionPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de lecciones");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model model) {
        log.debug("Viendo la leccion");
        TimeZone tz = null;
        DateTimeZone zone = null;
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        model.addAttribute("currentURL", themeDisplay.getURLCurrent());
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
                log.info("No encontre el atributo hoy");
                hoy = new DateTime(zone);
                request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
                DateTimeFormatter fmt3 = DateTimeFormat.forPattern("dd/MM/yyyy");
                request.getPortletSession().setAttribute("hoyString", fmt3.print(hoy));
            }
            
            String[] tags = TagsUtil.getTagsConDia(new String[4], hoy);

            long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(JournalArticle.class.getName())) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(asset.getClassPK());
                    AssetEntryServiceUtil.incrementViewCounter(asset.getClassName(), ja.getResourcePrimKey());
                    String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                    model.addAttribute("leccion", ja);
                    model.addAttribute("contenido", contenido);
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE dd/MM/yyyy");
                    DateTimeFormatter fmt2 = fmt.withLocale(themeDisplay.getLocale());
                    StringBuilder sb = new StringBuilder(fmt2.print(hoy));
                    String fecha = StringUtil.upperCaseFirstLetter(sb.toString());
                    model.addAttribute("fecha", fecha);
                    model.addAttribute("assetEntry", asset);
                    int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(JournalArticle.class.getName()), ja.getPrimaryKey(), WorkflowConstants.STATUS_APPROVED);
                    if (discussionMessagesCount > 0) {
                        model.addAttribute("discussionMessages", true);
                    }

                    break;
                }
            }


        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }



        return "leccion/ver";
    }

    @RequestMapping(params = "action=navega")
    public void navega(ActionRequest request, ActionResponse response,
            @ModelAttribute("resultado") EstadisticasUtil resultado, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam(required = false) Integer dias) {
        log.debug("Navegando");
        TimeZone tz = null;
        DateTimeZone zone = null;
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        try {
            tz = themeDisplay.getTimeZone();
            zone = DateTimeZone.forID(tz.getID());
        } catch (IllegalArgumentException e) {
            zone = DateTimeZone.forID(ZonaHorariaUtil.getConvertedId(tz.getID()));
        }
        DateTime hoy = (DateTime) request.getPortletSession().getAttribute("hoy", PortletSession.APPLICATION_SCOPE);
        if (hoy == null) {
            hoy = new DateTime(zone);
        }

        DateTime inicio = new DateTime(hoy.getYear(), 3, 26, 0, 0, 0, 0, hoy.getZone());

        log.debug("Dias: {}", dias);
        if (dias != null && dias < 0) {
            hoy = hoy.minusDays(dias * (-1));
        } else if (dias != null && dias > 0) {
            hoy = hoy.plusDays(dias);
        }
        if (hoy.isBefore(inicio)) {
            hoy = hoy.withDayOfMonth(26);
        }
        request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        request.getPortletSession().setAttribute("hoyString", fmt.print(hoy), PortletSession.APPLICATION_SCOPE);

        sessionStatus.setComplete();

    }

    @RequestMapping(params = "action=discusion")
    public void discusion(ActionRequest request, ActionResponse response,
            @ModelAttribute("resultado") EstadisticasUtil resultado, BindingResult result,
            Model model, SessionStatus sessionStatus,
            @RequestParam("entradaId") Long entradaId,
            @RequestParam("assetId") Long assetId) {
        log.debug("Ver discusion");
        log.debug("EntradaId: " + entradaId);

        try {
            String cmd = ParamUtil.getString(request, Constants.CMD);
            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                updateMessage(request);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteMessage(request);
            }
        } catch (Exception e) {
            log.error("Error al intentar actualizar el mensaje", e);
        }

        response.setRenderParameter("action", "completo");
        response.setRenderParameter("entradaId", entradaId.toString());
        response.setRenderParameter("assetId", assetId.toString());
    }

    protected void deleteMessage(ActionRequest actionRequest) throws Exception {
        long groupId = PortalUtil.getScopeGroupId(actionRequest);

        String className = ParamUtil.getString(actionRequest, "className");
        long classPK = ParamUtil.getLong(actionRequest, "classPK");

        String permissionClassName = ParamUtil.getString(
                actionRequest, "permissionClassName");

        long permissionClassPK = ParamUtil.getLong(
                actionRequest, "permissionClassPK");

        long messageId = ParamUtil.getLong(actionRequest, "messageId");


        MBMessageServiceUtil.deleteDiscussionMessage(
                groupId, className, classPK, permissionClassName, permissionClassPK,
                messageId);
    }

    protected MBMessage updateMessage(ActionRequest actionRequest)
            throws Exception {

        String className = ParamUtil.getString(actionRequest, "className");
        long classPK = ParamUtil.getLong(actionRequest, "classPK");
        String permissionClassName = ParamUtil.getString(
                actionRequest, "permissionClassName");
        long permissionClassPK = ParamUtil.getLong(
                actionRequest, "permissionClassPK");

        long messageId = ParamUtil.getLong(actionRequest, "messageId");

        long threadId = ParamUtil.getLong(actionRequest, "threadId");
        long parentMessageId = ParamUtil.getLong(
                actionRequest, "parentMessageId");
        String subject = ParamUtil.getString(actionRequest, "subject");
        String body = ParamUtil.getString(actionRequest, "body");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                MBMessage.class.getName(), actionRequest);

        MBMessage message = null;

        if (messageId <= 0) {

            // Add message

            message = MBMessageServiceUtil.addDiscussionMessage(
                    serviceContext.getScopeGroupId(), className, classPK,
                    permissionClassName, permissionClassPK, threadId,
                    parentMessageId, subject, body, serviceContext);
        } else {

            // Update message

            message = MBMessageServiceUtil.updateDiscussionMessage(
                    className, classPK, permissionClassName, permissionClassPK,
                    messageId, subject, body, serviceContext);
        }

        return message;
    }

    @ResourceMapping(value = "buscaVersiculo")
    public void buscaVersiculo(@RequestParam(value = "_biblia_WAR_esportlet_libro", required = false) Integer libro, @RequestParam(value = "_biblia_WAR_esportlet_capitulo", required = false) Integer capitulo, @RequestParam(value = "_biblia_WAR_esportlet_versiculo", required = false) Integer versiculo, @RequestParam(value = "_biblia_WAR_esportlet_vid", required = false) Integer vid, @RequestParam(value = "_biblia_WAR_esportlet_version", required = false) Integer version, ResourceRequest request, ResourceResponse response) {
        log.debug("Buscando el versiculo con ajax");
        if (vid != null) {
            request.getPortletSession().setAttribute("vid", vid, PortletSession.APPLICATION_SCOPE);

            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = bibliaDS.getConnection();
                StringBuilder sb = new StringBuilder();
                sb.append("select v.id, v.versiculo, l.nombre as libro, v.texto, v.libro_id, v.capitulo from ");
                if (version != null) {
                    sb.append(version).append(" v ");
                } else {
                    sb.append("rv2000 v ");
                }
                sb.append(", libros l ");
                sb.append(" where v.libro_id = l.id ");
                sb.append(" and v.id between ? and ? ");
                sb.append(" order by v.id");

                ps = conn.prepareStatement(sb.toString());
                ps.setLong(1, vid);
                ps.setLong(2, vid + 5);
                rs = ps.executeQuery();
                StringBuilder resultado = new StringBuilder();
                while (rs.next()) {
                    if (libro == null
                            || libro != rs.getInt("libro_id")
                            || capitulo != rs.getInt("capitulo")) {
                        libro = rs.getInt("libro_id");
                        capitulo = rs.getInt("capitulo");
                        resultado.append("<h2>");
                        resultado.append(rs.getString("libro"));
                        resultado.append(" ");
                        resultado.append(rs.getInt("capitulo"));
                        resultado.append(" : ");
                        resultado.append(rs.getInt("versiculo"));
                        resultado.append("</h2>");
                    }
                    resultado.append("<p>");
                    resultado.append(rs.getInt("versiculo"));
                    resultado.append(" ");
                    resultado.append(rs.getString("texto"));
                    resultado.append("</p>");
                }

                PrintWriter writer = response.getWriter();
                writer.println(resultado.toString());

            } catch (Exception e) {
                log.error("No se pudo conectar a la base de datos", e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        log.error("No se pudo cerrar la conexion", ex);
                    }
                }
            }

        } else if (libro != null) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                conn = bibliaDS.getConnection();
                StringBuilder sb = new StringBuilder();
                sb.append("select v.id from ");
                if (version != null) {
                    sb.append(version).append(" v ");
                } else {
                    sb.append("rv2000 v ");
                }
                sb.append(", libros l ");
                sb.append(" where v.libro_id = l.id ");
                sb.append(" and v.libro_id = ? ");
                sb.append(" and v.capitulo = ? ");
                sb.append(" and v.versiculo >= ? ");
                sb.append(" order by v.id limit 1");

                if (capitulo == null) {
                    capitulo = 1;
                    versiculo = 1;
                } else if (versiculo == null) {
                    versiculo = 1;
                }

                ps = conn.prepareStatement(sb.toString());
                ps.setLong(1, libro);
                ps.setLong(2, capitulo);
                ps.setLong(3, versiculo);
                rs = ps.executeQuery();
                if (rs.next()) {
                    vid = rs.getInt("id");
                    request.getPortletSession().setAttribute("vid", vid, PortletSession.APPLICATION_SCOPE);
                }

                sb = new StringBuilder();
                sb.append("select v.id, v.versiculo, l.nombre as libro, v.texto, v.libro_id, v.capitulo from ");
                if (version != null) {
                    sb.append(version).append(" v ");
                } else {
                    sb.append("rv2000 v ");
                }
                sb.append(", libros l ");
                sb.append(" where v.libro_id = l.id ");
                sb.append(" and v.id between ? and ? ");
                sb.append(" order by v.id");

                ps = conn.prepareStatement(sb.toString());
                ps.setLong(1, vid);
                ps.setLong(2, vid + 5);
                rs = ps.executeQuery();

                StringBuilder resultado = new StringBuilder();
                boolean primeraVuelta = true;
                while (rs.next()) {
                    if (libro != rs.getInt("libro_id")
                            || capitulo != rs.getInt("capitulo")
                            || primeraVuelta) {
                        if (primeraVuelta) {
                            resultado.append("<form name='versiculoForm'><input type='hidden' name='vid' id='vid' value='").append(vid).append("'/></form>");
                        }
                        libro = rs.getInt("libro_id");
                        capitulo = rs.getInt("capitulo");
                        resultado.append("<h2>");
                        resultado.append(rs.getString("libro"));
                        resultado.append(" ");
                        resultado.append(capitulo);
                        resultado.append(" : ");
                        resultado.append(rs.getInt("versiculo"));
                        resultado.append("</h2>");
                        primeraVuelta = false;
                    }
                    resultado.append("<p>");
                    resultado.append(rs.getInt("versiculo"));
                    resultado.append(" ");
                    resultado.append(rs.getString("texto"));
                    resultado.append("</p>");
                }

                PrintWriter writer = response.getWriter();
                writer.println(resultado.toString());

            } catch (Exception e) {
                log.error("No se pudo conectar a la base de datos", e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        log.error("No se pudo cerrar la conexion", ex);
                    }
                }
            }
        }

    }
}
