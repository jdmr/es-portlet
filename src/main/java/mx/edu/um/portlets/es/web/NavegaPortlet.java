package mx.edu.um.portlets.es.web;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import mx.edu.um.portlets.es.utils.EstadisticasUtil;

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
    public String ver(RenderRequest request, RenderResponse response, Model model) {
        log.debug("Navegacion");

        return "navega/ver";
    }
    
    @RequestMapping(params = "action=cambiaFecha")
    public void cambiaFecha(ActionRequest request, ActionResponse response,
            @RequestParam String fechaNavegaTxt,
            @ModelAttribute EstadisticasUtil estadisticasUtil,
            BindingResult result,
            Model model, SessionStatus sessionStatus) {
    	log.debug("Asignando fecha {}",fechaNavegaTxt);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTime hoy = fmt.parseDateTime(fechaNavegaTxt);
        request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
        request.getPortletSession().setAttribute("hoyString", fechaNavegaTxt, PortletSession.APPLICATION_SCOPE);
    }

}
