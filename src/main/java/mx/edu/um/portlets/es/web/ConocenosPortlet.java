package mx.edu.um.portlets.escuelasabatica;

import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.service.UserGroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;
import java.util.ArrayList;
import java.util.List;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.escuelasabatica.util.Perfil;
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
public class ConocenosPortlet {

    private static final Logger log = LoggerFactory.getLogger(ConocenosPortlet.class);

    public ConocenosPortlet() {
        log.debug("Se ha creado una nueva instancia del portlet de conocenos");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response, Model model) {
        log.debug("Mostrando conocenos");
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        try {
            UserGroup group = UserGroupLocalServiceUtil.getUserGroup(themeDisplay.getCompanyId(), "Editores");
            List<User> usuarios = UserLocalServiceUtil.getUserGroupUsers(group.getUserGroupId());
            List<Perfil> perfiles = new ArrayList<Perfil>();
            for(User usuario : usuarios) {
                String acercaDe = HtmlUtil.escape(ExpandoValueLocalServiceUtil.getData(usuario.getCompanyId(), User.class.getName(), "SN", "aboutMe", usuario.getUserId(), StringPool.BLANK)); 
                String imagen = themeDisplay.getPathImage() + "/user_portrait?img_id=" + usuario.getPortraitId() + "&t=" + ImageServletTokenUtil.getToken(usuario.getPortraitId());
                String profileUrl = "/web/" + usuario.getScreenName();

                perfiles.add(
                        new Perfil(
                        usuario.getFullName()
                        ,usuario.getContact().getJobTitle()
                        ,usuario.getContact().getTwitterSn()
                        ,usuario.getContact().getFacebookSn()
                        ,acercaDe
                        ,imagen
                        ,usuario.getEmailAddress()
                        ,profileUrl
                        ));
            }
            request.setAttribute("perfiles", perfiles);
        } catch (Exception e) {
            log.error("No se pudo obtener la lista de miembros de editores",e);
        }
        return "conocenos/ver";
    }
}
