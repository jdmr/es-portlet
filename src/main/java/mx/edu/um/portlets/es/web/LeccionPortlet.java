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
import java.text.NumberFormat;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.es.utils.EstadisticasUtil;
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
public class LeccionPortlet {

    private static final Logger log = LoggerFactory.getLogger(LeccionPortlet.class);

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

            long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, getTags(hoy));

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
