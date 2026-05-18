/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sistema.inventario.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "COMPROBANTE_DETALLE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComprobanteDetalle.findAll", query = "SELECT c FROM ComprobanteDetalle c"),
    @NamedQuery(name = "ComprobanteDetalle.findByIdComprobanteDet", query = "SELECT c FROM ComprobanteDetalle c WHERE c.idComprobanteDet = :idComprobanteDet"),
    @NamedQuery(name = "ComprobanteDetalle.findByCantidad", query = "SELECT c FROM ComprobanteDetalle c WHERE c.cantidad = :cantidad"),
    @NamedQuery(name = "ComprobanteDetalle.findByPrecio", query = "SELECT c FROM ComprobanteDetalle c WHERE c.precio = :precio")})
public class ComprobanteDetalle implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "ID_COMPROBANTE_DET")
    private BigDecimal idComprobanteDet;
    @Basic(optional = false)
    @Column(name = "CANTIDAD")
    private BigInteger cantidad;
    @Basic(optional = false)
    @Column(name = "PRECIO")
    private BigDecimal precio;
    @JoinColumn(name = "ID_ARTICULO", referencedColumnName = "ID_ARTICULO")
    @ManyToOne(optional = false)
    private Articulo idArticulo;
    @JoinColumn(name = "ID_COMPROBANTE", referencedColumnName = "ID_COMPROBANTE")
    @ManyToOne(optional = false)
    private ComprobanteCabecera idComprobante;

    public ComprobanteDetalle() {
    }

    public ComprobanteDetalle(BigDecimal idComprobanteDet) {
        this.idComprobanteDet = idComprobanteDet;
    }

    public ComprobanteDetalle(BigDecimal idComprobanteDet, BigInteger cantidad, BigDecimal precio) {
        this.idComprobanteDet = idComprobanteDet;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public BigDecimal getIdComprobanteDet() {
        return idComprobanteDet;
    }

    public void setIdComprobanteDet(BigDecimal idComprobanteDet) {
        this.idComprobanteDet = idComprobanteDet;
    }

    public BigInteger getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigInteger cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Articulo getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Articulo idArticulo) {
        this.idArticulo = idArticulo;
    }

    public ComprobanteCabecera getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(ComprobanteCabecera idComprobante) {
        this.idComprobante = idComprobante;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idComprobanteDet != null ? idComprobanteDet.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComprobanteDetalle)) {
            return false;
        }
        ComprobanteDetalle other = (ComprobanteDetalle) object;
        if ((this.idComprobanteDet == null && other.idComprobanteDet != null) || (this.idComprobanteDet != null && !this.idComprobanteDet.equals(other.idComprobanteDet))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sistema.inventario.modelo.ComprobanteDetalle[ idComprobanteDet=" + idComprobanteDet + " ]";
    }

    public void setIdComprobanteDetalle(BigDecimal valueOf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
