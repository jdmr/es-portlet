package mx.edu.um.portlets.esu.web;

import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.AssetTag;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;
import java.util.List;
import java.util.TimeZone;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.esu.utils.EstadisticasUtil;
import mx.edu.um.portlets.esu.utils.TagsUtil;
import mx.edu.um.portlets.esu.utils.TemaUtil;
import mx.edu.um.portlets.esu.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
public class DialogaPortlet {

    private static final Logger log = LoggerFactory.getLogger(DialogaPortlet.class);

    public DialogaPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de dialoga");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model model) {
        log.debug("Mostrando los articulos de dialoga");
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

            // Buscando los temas de dialoga de la semana
            String[] tags = TagsUtil.getTags(new String[4], hoy);
            tags[3] = "dialoga";

            long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

            assetEntryQuery.setAllTagIds(assetTagIds);

            List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            log.debug("Buscando los temas de dialoga");
            int cont = 1;
            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(JournalArticle.class.getName())) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(asset.getClassPK());
                    String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                    contenido = HtmlUtil.stripHtml(contenido);
                    contenido = StringUtil.shorten(contenido, 700);
                    User autor = UserLocalServiceUtil.getUser(asset.getUserId());
                    model.addAttribute("tema" + (cont++), new TemaUtil(asset.getPrimaryKey(),asset.getClassPK(), asset.getTitle().toUpperCase(), autor.getFullName(), contenido, null));
                }
            }
            
        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "dialoga/ver";
    }

    @RequestMapping(params = "action=completo")
    public String completo(RenderRequest request, RenderResponse response, @RequestParam Long entradaId, @RequestParam Long assetId, Model model) {
        log.debug("Ver completo");
        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            JournalArticle entrada = JournalArticleLocalServiceUtil.getLatestArticle(entradaId);
            String contenido = JournalArticleLocalServiceUtil.getArticleContent(entrada.getGroupId(), entrada.getArticleId(), "view", ""+themeDisplay.getLocale(), themeDisplay);
            AssetEntry assetEntry = AssetEntryServiceUtil.getEntry(assetId);
            AssetEntryServiceUtil.incrementViewCounter(assetEntry.getClassName(), entradaId);
            assetEntry.setViewCount(assetEntry.getViewCount()+1);
            User autor = UserLocalServiceUtil.getUser(assetEntry.getUserId());
            model.addAttribute("entrada", entrada);
            model.addAttribute("assetEntry", assetEntry);
            model.addAttribute("contenido",contenido);
            model.addAttribute("autor",autor.getFullName());
            model.addAttribute("currentURL", themeDisplay.getURLCurrent());
            int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(BlogsEntry.class.getName()), entrada.getPrimaryKey(), WorkflowConstants.STATUS_APPROVED);
            if (discussionMessagesCount > 0) {
                model.addAttribute("discussionMessages", true);
            }

            List<AssetTag> tags = assetEntry.getTags();
            AssetEntryQuery assetEntryQuery = new AssetEntryQuery();
            long[] assetTagIds = new long[tags.size()];
            int i = 0;
            for (AssetTag tag : tags) {
                assetTagIds[i++] = tag.getTagId();
            }
            assetEntryQuery.setAllTagIds(assetTagIds);
            List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

            for (AssetEntry asset : results) {
                if (asset.getClassName().equals(MBMessage.class.getName())) {
                    model.addAttribute("messageUrl", "/foros/-/message_boards/view_message/" + asset.getClassPK());
                    break;
                }
            }
        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }
        return "dialoga/completo";
    }

    @RequestMapping(params = "action=discusion")
    public void discusion(ActionRequest request, ActionResponse response,
            @RequestParam Long entradaId,
            @RequestParam Long assetId, 
            @ModelAttribute EstadisticasUtil estadisticasUtil,
            BindingResult result,
            Model model, SessionStatus sessionStatus) {
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

}
