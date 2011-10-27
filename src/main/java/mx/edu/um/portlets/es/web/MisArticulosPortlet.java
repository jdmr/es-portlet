package mx.edu.um.portlets.es.web;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ClassName;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.es.utils.TemaUtil;
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
public class MisArticulosPortlet {

    private static final Logger log = LoggerFactory.getLogger(MisArticulosPortlet.class);

    public MisArticulosPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de mis articulos");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model modelo) throws PortalException, SystemException {
        log.debug("Mostrando mis articulos");

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        Group group = GroupLocalServiceUtil.getGroup(themeDisplay.getScopeGroupId());
        log.debug("UserId: {}", group.getClassPK());
        DynamicQuery query = DynamicQueryFactoryUtil.forClass(
                JournalArticle.class, 
                "articleParent", 
                PortalClassLoaderUtil.getClassLoader()).add(PropertyFactoryUtil.forName("userId").eq(group.getClassPK())).addOrder(OrderFactoryUtil.desc("resourcePrimKey")).addOrder(OrderFactoryUtil.desc("version"));

        List<JournalArticle> journalArticles = JournalArticleLocalServiceUtil.dynamicQuery(query);
        Set<TemaUtil> articulos = new LinkedHashSet<TemaUtil>();
        for (JournalArticle article : journalArticles) {
        }
        
        ClassName nombre = ClassNameLocalServiceUtil.
            getClassName("com.liferay.portlet.journal.model.JournalArticle");
        long classNameIdJournal = nombre.getClassNameId();
        DynamicQuery query2 = DynamicQueryFactoryUtil.forClass(AssetEntry.class, PortalClassLoaderUtil.getClassLoader())
                .add(PropertyFactoryUtil.forName("userId").eq(group.getClassPK()))
                .add(PropertyFactoryUtil.forName("classNameId").eq(classNameIdJournal))
                .addOrder(OrderFactoryUtil.desc("entryId"));
        List<AssetEntry> entries = AssetEntryLocalServiceUtil.dynamicQuery(query2);
        for(AssetEntry entry : entries) {
            StringBuilder url = new StringBuilder();
            for(String tag : entry.getTagNames()) {
                if (tag.equals("dialoga")) {
                    url.append("/dialoga?p_p_id=dialoga_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=1&_dialoga_WAR_esportlet_assetId=");
                    url.append(entry.getPrimaryKey());
                    url.append("&_dialoga_WAR_esportlet_entradaId=");
                    url.append(entry.getClassPK());
                    url.append("&_dialoga_WAR_esportlet_action=completo");
                    break;
                } else if (tag.equals("comunica")) {
                    url.append("/comunica?p_p_id=comunica_WAR_esportlet&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&p_p_col_id=column-1&p_p_col_count=2&_comunica_WAR_esportlet_assetId=");
                    url.append(entry.getPrimaryKey());
                    url.append("&_comunica_WAR_esportlet_entradaId=");
                    url.append(entry.getClassPK());
                    url.append("&_comunica_WAR_esportlet_action=completo");
                    break;
                }
            }
            JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(entry.getClassPK());

            TemaUtil tema = new TemaUtil(entry.getPrimaryKey(),entry.getClassPK(), entry.getTitle(), null, ja.getDescription(), url.toString());
            articulos.add(tema);
        }
        modelo.addAttribute("articulos", articulos);
        
        return "misArticulos/ver";
    }
}
