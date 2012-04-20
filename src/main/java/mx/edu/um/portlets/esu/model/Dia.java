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
package mx.edu.um.portlets.esu.model;

import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Entity
@Table(name = "esu_dias")
public class Dia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @Temporal(TemporalType.DATE)
    @Column(unique = true)
    private Date fecha;
    private Long leccion;
    private Long leccionAsset;
    private Long versiculo;
    private Long versiculoAsset;
    private Long video;
    private Long dialoga1;
    private Long dialoga2;
    private Long dialoga3;
    private Long dialoga4;
    private Long comunica1;
    private Long comunica2;
    private Long comunica3;
    private Long comunica4;
    private Long podcastSemanal;
    private Long podcastDiario;
    private Boolean cerrado = false;

    public Dia() {
    }

    public Dia(Date fecha) {
        this.fecha = fecha;
    }

    public Dia(Date fecha, Long leccion, Long dialoga1, Long dialoga2, Long dialoga3, Long dialoga4, Long comunica1, Long comunica2, Long comunica3, Long comunica4) {
        this.fecha = fecha;
        this.leccion = leccion;
        this.dialoga1 = dialoga1;
        this.dialoga2 = dialoga2;
        this.dialoga3 = dialoga3;
        this.dialoga4 = dialoga4;
        this.comunica1 = comunica1;
        this.comunica2 = comunica2;
        this.comunica3 = comunica3;
        this.comunica4 = comunica4;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the leccion
     */
    public Long getLeccion() {
        return leccion;
    }

    /**
     * @param leccion the leccion to set
     */
    public void setLeccion(Long leccion) {
        this.leccion = leccion;
    }

    /**
     * @return the leccionAsset
     */
    public Long getLeccionAsset() {
        return leccionAsset;
    }

    /**
     * @param leccionAsset the leccionAsset to set
     */
    public void setLeccionAsset(Long leccionAsset) {
        this.leccionAsset = leccionAsset;
    }

    /**
     * @return the versiculo
     */
    public Long getVersiculo() {
        return versiculo;
    }

    /**
     * @param versiculo the versiculo to set
     */
    public void setVersiculo(Long versiculo) {
        this.versiculo = versiculo;
    }

    /**
     * @return the versiculoAsset
     */
    public Long getVersiculoAsset() {
        return versiculoAsset;
    }

    /**
     * @param versiculoAsset the versiculoAsset to set
     */
    public void setVersiculoAsset(Long versiculoAsset) {
        this.versiculoAsset = versiculoAsset;
    }

    /**
     * @return the video
     */
    public Long getVideo() {
        return video;
    }

    /**
     * @param video the video to set
     */
    public void setVideo(Long video) {
        this.video = video;
    }

    /**
     * @return the dialoga1
     */
    public Long getDialoga1() {
        return dialoga1;
    }

    /**
     * @param dialoga1 the dialoga1 to set
     */
    public void setDialoga1(Long dialoga1) {
        this.dialoga1 = dialoga1;
    }

    /**
     * @return the dialoga2
     */
    public Long getDialoga2() {
        return dialoga2;
    }

    /**
     * @param dialoga2 the dialoga2 to set
     */
    public void setDialoga2(Long dialoga2) {
        this.dialoga2 = dialoga2;
    }

    /**
     * @return the dialoga3
     */
    public Long getDialoga3() {
        return dialoga3;
    }

    /**
     * @param dialoga3 the dialoga3 to set
     */
    public void setDialoga3(Long dialoga3) {
        this.dialoga3 = dialoga3;
    }

    /**
     * @return the dialoga4
     */
    public Long getDialoga4() {
        return dialoga4;
    }

    /**
     * @param dialoga4 the dialoga4 to set
     */
    public void setDialoga4(Long dialoga4) {
        this.dialoga4 = dialoga4;
    }

    /**
     * @return the comunica1
     */
    public Long getComunica1() {
        return comunica1;
    }

    /**
     * @param comunica1 the comunica1 to set
     */
    public void setComunica1(Long comunica1) {
        this.comunica1 = comunica1;
    }

    /**
     * @return the comunica2
     */
    public Long getComunica2() {
        return comunica2;
    }

    /**
     * @param comunica2 the comunica2 to set
     */
    public void setComunica2(Long comunica2) {
        this.comunica2 = comunica2;
    }

    /**
     * @return the comunica3
     */
    public Long getComunica3() {
        return comunica3;
    }

    /**
     * @param comunica3 the comunica3 to set
     */
    public void setComunica3(Long comunica3) {
        this.comunica3 = comunica3;
    }

    /**
     * @return the comunica4
     */
    public Long getComunica4() {
        return comunica4;
    }

    /**
     * @param comunica4 the comunica4 to set
     */
    public void setComunica4(Long comunica4) {
        this.comunica4 = comunica4;
    }

    /**
     * @return the podcastSemanal
     */
    public Long getPodcastSemanal() {
        return podcastSemanal;
    }

    /**
     * @param podcastSemanal the podcastSemanal to set
     */
    public void setPodcastSemanal(Long podcastSemanal) {
        this.podcastSemanal = podcastSemanal;
    }

    /**
     * @return the podcastDiario
     */
    public Long getPodcastDiario() {
        return podcastDiario;
    }

    /**
     * @param podcastDiario the podcastDiario to set
     */
    public void setPodcastDiario(Long podcastDiario) {
        this.podcastDiario = podcastDiario;
    }

    /**
     * @return the cerrado
     */
    public Boolean getCerrado() {
        return cerrado;
    }

    /**
     * @param cerrado the cerrado to set
     */
    public void setCerrado(Boolean cerrado) {
        this.cerrado = cerrado;
    }

    @Override
    public String toString() {
        return "Dia{" + "id=" + id + ", fecha=" + fecha + ", cerrado=" + cerrado + '}';
    }
    
}
