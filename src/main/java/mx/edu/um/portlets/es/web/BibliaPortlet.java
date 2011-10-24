package mx.edu.um.portlets.es.web;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class BibliaPortlet {

    private static final Logger log = LoggerFactory.getLogger(BibliaPortlet.class);
    
    @Autowired
    private DataSource bibliaDS;

    public BibliaPortlet() {
        log.info("Se ha creado una nueva instancia del portlet de biblia");
    }

    @RequestMapping
    public String ver(RenderRequest request, RenderResponse response
            , @RequestParam(required = false) Integer libro
            , @RequestParam(required = false) Integer capitulo 
            , @RequestParam(required = false) Integer versiculo
            , @RequestParam(required = false) Integer version
            , @RequestParam(required = false) Integer vid
            , Model model) {
        log.debug("Mostrando el versiculo");
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
                ps.setLong(2, vid+5);
                rs = ps.executeQuery();
                StringBuilder resultado = new StringBuilder();
                StringBuilder nombre = new StringBuilder();
                while (rs.next()) {
                    if(nombre.length() == 0) {
                        libro = rs.getInt("libro_id");
                        capitulo = rs.getInt("capitulo");
                        nombre.append(rs.getString("libro"));
                        nombre.append(" ");
                        nombre.append(rs.getInt("capitulo"));
                        nombre.append(" : ");
                        nombre.append(rs.getInt("versiculo"));
                    }
                    if (libro != rs.getInt("libro_id") ||
                            capitulo != rs.getInt("capitulo")) {
                        libro = rs.getInt("libro_id");
                        capitulo = rs.getInt("capitulo");
                        resultado.append("<h2>");
                        resultado.append(rs.getString("libro"));
                        resultado.append(" ");
                        resultado.append(capitulo);
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
                log.debug("NOMBRE {}",nombre.toString());
                model.addAttribute("ubicacion", nombre.toString());
                model.addAttribute("texto", resultado.toString());
                model.addAttribute("vid", vid);
            } catch (Exception e) {
                log.error("No se pudo conectar a la base de datos",e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        log.error("No se pudo cerrar la conexion",ex);
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
                log.debug("Ejecutando {}",sb.toString());
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
                ps.setLong(2, vid+5);
                log.debug("Ejecutando {}",sb.toString());
                rs = ps.executeQuery();
                
                StringBuilder resultado = new StringBuilder();
                StringBuilder nombre = new StringBuilder();
                while (rs.next()) {
                    if(nombre.length() == 0) {
                        vid = rs.getInt("id");
                        nombre.append(rs.getString("libro"));
                        nombre.append(" ");
                        nombre.append(capitulo);
                        nombre.append(" : ");
                        nombre.append(versiculo);
                    }
                    if (libro != rs.getInt("libro_id") ||
                            capitulo != rs.getInt("capitulo")) {
                        libro = rs.getInt("libro_id");
                        capitulo = rs.getInt("capitulo");
                        resultado.append("<h2>");
                        resultado.append(rs.getString("libro"));
                        resultado.append(" ");
                        resultado.append(capitulo);
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
                log.debug("NOMBRE {}",nombre.toString());
                model.addAttribute("ubicacion", nombre.toString());
                model.addAttribute("texto", resultado.toString());
                model.addAttribute("vid", vid);
            } catch (Exception e) {
                log.error("No se pudo conectar a la base de datos",e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        log.error("No se pudo cerrar la conexion",ex);
                    }
                }
            }
        }
        return "biblia/ver";
    }
    
    @ResourceMapping(value = "versiculoSiguiente")
    public void versiculoSiguiente(@RequestParam(required = false) Integer vid, ResourceRequest request, ResourceResponse response) {
        log.debug("Versiculo siguiente {}",vid);
        if (vid == null) {
            vid = (Integer) request.getPortletSession().getAttribute("vid", PortletSession.APPLICATION_SCOPE);
        }
        if (vid != null) {
            vid += 5;
            try {
                PrintWriter writer = response.getWriter();
                writer.println(armaContenido(vid));

                request.getPortletSession().setAttribute("vid", vid, PortletSession.APPLICATION_SCOPE);
            } catch (Exception e) {
                log.error("No se pudo conectar a la base de datos",e);
            }
        } else {
            log.warn("No se pudo encontrar el vid en la sesion");
            StringBuilder resultado = new StringBuilder();
            resultado.append("<p>");
            resultado.append("Por alguna razón no pudimos cargar el versículo, si el problema persiste, creemos que el problema tiene que ver con tu navegador, puedes intentar con uno más reciente, y realmente necesitamos tu ayuda, contáctanos mandando un correo a <a href='mailto:david.mendoza@um.edu.mx'>david.mendoza@um.edu.mx.</a>");
            resultado.append("</p>");
            try {
                PrintWriter writer = response.getWriter();
                writer.println(resultado.toString());
            } catch(Exception e) {
                log.error("No se pudo pintar el versiculo",e);
            }
        }
    }
    
    @ResourceMapping(value = "versiculoAnterior")
    public void versiculoAnterior(@RequestParam(required = false) Integer vid, ResourceRequest request, ResourceResponse response) {
        log.debug("Versiculo anterior {}",vid);
        if (vid == null) {
            vid = (Integer) request.getPortletSession().getAttribute("vid", PortletSession.APPLICATION_SCOPE);
        }
        if (vid != null) {
            vid -= 5;
            try {
                PrintWriter writer = response.getWriter();
                writer.println(armaContenido(vid));

                request.getPortletSession().setAttribute("vid", vid, PortletSession.APPLICATION_SCOPE);
            } catch (Exception e) {
                log.error("No se pudo conectar a la base de datos",e);
            }
        } else {
            log.warn("No se pudo encontrar el vid en la sesion");
            StringBuilder resultado = new StringBuilder();
            resultado.append("<p>");
            resultado.append("Por alguna razón no pudimos cargar el versículo, si el problema persiste, creemos que el problema tiene que ver con tu navegador, puedes intentar con uno más reciente, y realmente necesitamos tu ayuda, contáctanos mandando un correo a <a href='mailto:david.mendoza@um.edu.mx'>david.mendoza@um.edu.mx.</a>");
            resultado.append("</p>");
            try {
                PrintWriter writer = response.getWriter();
                writer.println(resultado.toString());
            } catch(Exception e) {
                log.error("No se pudo pintar el versiculo",e);
            }
        }
    }
    
    private String armaContenido(Integer vid) throws SQLException {
        Integer libro = 0;
        Integer capitulo = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = bibliaDS.getConnection();
            StringBuilder sb = new StringBuilder();
            sb.append("select v.id, v.versiculo, l.nombre as libro, v.texto, v.libro_id, v.capitulo from ");
            sb.append("rv2000 v ");
            sb.append(", libros l ");
            sb.append(" where v.libro_id = l.id ");
            sb.append(" and v.id between ? and ? ");
            sb.append(" order by v.id");

            ps = conn.prepareStatement(sb.toString());
            ps.setLong(1, vid);
            ps.setLong(2, vid+5);
            rs = ps.executeQuery();
            
            StringBuilder resultado = new StringBuilder();
            boolean primeraVuelta = true;
            while (rs.next()) {
                if (libro != rs.getInt("libro_id") ||
                        capitulo != rs.getInt("capitulo")
                        || primeraVuelta) {
                    if (primeraVuelta) {
                        resultado.append("<form name='versiculoForm'><input type='hidden' name='vid' id='vid' value='").append(vid).append("'/></form>");
                        primeraVuelta = false;
                    }
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
                resultado.append(rs.getLong("versiculo"));
                resultado.append(" ");
                resultado.append(rs.getString("texto"));
                resultado.append("</p>");
            }

            return resultado.toString();
            
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    log.error("No se pudo cerrar la conexion",ex);
                }
            }
        }
    }

}
