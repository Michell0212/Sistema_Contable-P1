/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sistema.inventario.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "COMPROBANTE_CABECERA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComprobanteCabecera.findAll", query = "SELECT c FROM ComprobanteCabecera c"),
    @NamedQuery(name = "ComprobanteCabecera.findByIdComprobante", query = "SELECT c FROM ComprobanteCabecera c WHERE c.idComprobante = :idComprobante"),
    @NamedQuery(name = "ComprobanteCabecera.findByNumeroComprobante", query = "SELECT c FROM ComprobanteCabecera c WHERE c.numeroComprobante = :numeroComprobante"),
    @NamedQuery(name = "ComprobanteCabecera.findByFecha", query = "SELECT c FROM ComprobanteCabecera c WHERE c.fecha = :fecha")})
public class ComprobanteCabecera implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "ID_COMPROBANTE")
    private BigDecimal idComprobante;
    @Basic(optional = false)
    @Column(name = "NUMERO_COMPROBANTE")
    private String numeroComprobante;
    @Basic(optional = false)
    @Column(name = "FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "ID_TIPO_MOVIMIENTO", referencedColumnName = "ID_TIPO_MOVIMIENTO")
    @ManyToOne(optional = false)
    private TipoMovimiento idTipoMovimiento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idComprobante", orphanRemoval = true)
    private Collection<ComprobanteDetalle> comprobanteDetalleCollection;

    public ComprobanteCabecera() {
    }

    public ComprobanteCabecera(BigDecimal idComprobante) {
        this.idComprobante = idComprobante;
    }

    public ComprobanteCabecera(BigDecimal idComprobante, String numeroComprobante, Date fecha) {
        this.idComprobante = idComprobante;
        this.numeroComprobante = numeroComprobante;
        this.fecha = fecha;
    }

    public BigDecimal getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(BigDecimal idComprobante) {
        this.idComprobante = idComprobante;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public TipoMovimiento getIdTipoMovimiento() {
        return idTipoMovimiento;
    }

    public void setIdTipoMovimiento(TipoMovimiento idTipoMovimiento) {
        this.idTipoMovimiento = idTipoMovimiento;
    }

    @XmlTransient
    public Collection<ComprobanteDetalle> getComprobanteDetalleCollection() {
        return comprobanteDetalleCollection;
    }

    public void setComprobanteDetalleCollection(Collection<ComprobanteDetalle> comprobanteDetalleCollection) {
        this.comprobanteDetalleCollection = comprobanteDetalleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idComprobante != null ? idComprobante.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComprobanteCabecera)) {
            return false;
        }
        ComprobanteCabecera other = (ComprobanteCabecera) object;
        if ((this.idComprobante == null && other.idComprobante != null) || (this.idComprobante != null && !this.idComprobante.equals(other.idComprobante))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sistema.inventario.modelo.ComprobanteCabecera[ idComprobante=" + idComprobante + " ]";
    }
    
}
