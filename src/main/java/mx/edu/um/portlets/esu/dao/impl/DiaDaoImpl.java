/*
 * The MIT License
 *
 * Copyright 2012 Universidad de Montemorelos A. C.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mx.edu.um.portlets.esu.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.sql.DataSource;
import mx.edu.um.portlets.esu.dao.DiaDao;
import mx.edu.um.portlets.esu.model.Dia;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Repository("diaDao")
public class DiaDaoImpl extends JdbcDaoSupport implements DiaDao {

    private static final Logger log = LoggerFactory.getLogger(DiaDaoImpl.class);

    @Autowired
    public DiaDaoImpl(DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    @Override
    public Dia obtiene(DateTime hoy) {
        log.debug("Obtiene dia {} : {}", new Object[]{hoy, hoy.toLocalDate().toDate()});
        Dia dia;
        try {
            dia = (Dia) getJdbcTemplate().queryForObject("select id, version, fecha, leccion, leccionasset, versiculo, versiculoasset, video, "
                    + "dialoga1, dialoga2, dialoga3, dialoga4, comunica1, comunica2, comunica3, comunica4, "
                    + "podcastsemanal, podcastdiario, cerrado from esu_dias where cerrado = true and fecha = ?", new Date[]{hoy.toLocalDate().toDate()}, new DiaMapper());
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            log.debug("NO LO ENCONTRE");
            dia = null;
        }
//        Query query = currentSession().createQuery("select d from Dia d where cerrado is true and d.fecha = :fecha");
//        query.setDate("fecha", new java.sql.Date(hoy.toDate().getTime()));
//        Dia dia = (Dia) query.uniqueResult();
        return dia;
    }

    @Override
    public Dia guarda(Dia dia) {
        log.debug("Guardando dia {}", dia);

        try {
            getJdbcTemplate().update("insert into esu_dias(version, fecha, leccion, leccionasset, versiculo, versiculoasset, video, "
                    + "dialoga1, dialoga2, dialoga3, dialoga4, comunica1, comunica2, comunica3, comunica4, "
                    + "podcastsemanal, podcastdiario, cerrado) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    1,
                    dia.getFecha(),
                    dia.getLeccion(),
                    dia.getLeccionAsset(),
                    dia.getVersiculo(),
                    dia.getVersiculoAsset(),
                    dia.getVideo(),
                    dia.getDialoga1(),
                    dia.getDialoga2(),
                    dia.getDialoga3(),
                    dia.getDialoga4(),
                    dia.getComunica1(),
                    dia.getComunica2(),
                    dia.getComunica3(),
                    dia.getComunica4(),
                    dia.getPodcastSemanal(),
                    dia.getPodcastDiario(),
                    dia.getCerrado());
        } catch (Exception e) {
            log.warn("Ya existe actualizando", e);
            log.debug("Buscando con fecha {}", dia.getFecha());
            try {
                Dia otro = (Dia) getJdbcTemplate().queryForObject("select id, version, fecha, leccion, leccionasset, versiculo, versiculoasset, video, "
                        + "dialoga1, dialoga2, dialoga3, dialoga4, comunica1, comunica2, comunica3, comunica4, "
                        + "podcastsemanal, podcastdiario, cerrado from esu_dias where fecha = ?", new Date[]{dia.getFecha()}, new DiaMapper());


                log.debug("Encontre dia para actualizar {} : {}", otro, dia);

                getJdbcTemplate().update("update esu_dias set version = ?, leccion = ?, leccionasset = ?, versiculo = ?, versiculoasset = ?, video = ?, "
                        + "dialoga1 = ?, dialoga2 = ?, dialoga3 = ?, dialoga4 = ?, comunica1 = ?, comunica2 = ?, comunica3 = ?, comunica4 = ?, "
                        + "podcastsemanal = ?, podcastdiario = ?, cerrado = ? where id = ?",
                        otro.getVersion() + 1,
                        dia.getLeccion(),
                        dia.getLeccionAsset(),
                        dia.getVersiculo(),
                        dia.getVersiculoAsset(),
                        dia.getVideo(),
                        dia.getDialoga1(),
                        dia.getDialoga2(),
                        dia.getDialoga3(),
                        dia.getDialoga4(),
                        dia.getComunica1(),
                        dia.getComunica2(),
                        dia.getComunica3(),
                        dia.getComunica4(),
                        dia.getPodcastSemanal(),
                        dia.getPodcastDiario(),
                        dia.getCerrado(),
                        otro.getId());
                
                log.debug("Se ha actualizado dia {}", otro);
                
            } catch (Exception ex) {
                log.warn("No pude actualizarlo tampoco", ex);
            }

        }

//        currentSession().saveOrUpdate(dia);
        return dia;
    }
}

class DiaMapper implements RowMapper<Dia> {

    @Override
    public Dia mapRow(ResultSet rs, int i) throws SQLException {
        Dia dia = new Dia();
        dia.setId(rs.getLong("id"));
        dia.setVersion(rs.getInt("version"));
        dia.setFecha(rs.getDate("fecha"));
        dia.setLeccion(rs.getLong("leccion"));
        dia.setLeccionAsset(rs.getLong("leccionAsset"));
        dia.setVersiculo(rs.getLong("versiculo"));
        dia.setVersiculoAsset(rs.getLong("versiculoAsset"));
        dia.setVideo(rs.getLong("video"));
        dia.setDialoga1(rs.getLong("dialoga1"));
        dia.setDialoga2(rs.getLong("dialoga2"));
        dia.setDialoga3(rs.getLong("dialoga3"));
        dia.setDialoga4(rs.getLong("dialoga4"));
        dia.setComunica1(rs.getLong("comunica1"));
        dia.setComunica2(rs.getLong("comunica2"));
        dia.setComunica3(rs.getLong("comunica3"));
        dia.setComunica4(rs.getLong("comunica4"));
        dia.setPodcastSemanal(rs.getLong("podcastsemanal"));
        dia.setPodcastDiario(rs.getLong("podcastdiario"));
        return dia;
    }
}
