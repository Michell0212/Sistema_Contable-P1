package com.sistema.facturacion.negocio;

import com.sistema.facturacion.modelo.FacturaCabecera;
import com.sistema.facturacion.modelo.FacturaDetalle;
import javax.persistence.*;
import java.util.List;

public class NegocioFactura {

    private static final String PU = "SistemaContablePU";

    public int insertar(FacturaCabecera factura) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            tx  = em.getTransaction();
            tx.begin();
            em.persist(factura);
            for (FacturaDetalle det : factura.getDetalles()) {
                det.setFactura(factura);
                em.persist(det);
            }
            tx.commit();
            return 1;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return -1;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public int eliminar(Integer idFactura) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            tx  = em.getTransaction();
            tx.begin();
            FacturaCabecera f = em.find(FacturaCabecera.class, idFactura);
            if (f != null) {
                // Eliminar detalles primero
                List<FacturaDetalle> detalles = f.getDetalles();
                if (detalles != null) {
                    for (FacturaDetalle det : detalles) {
                        em.remove(det);
                    }
                }
                em.remove(f);
            }
            tx.commit();
            return 1;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return -1;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public int eliminarDetalle(Integer idDetalle) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            tx  = em.getTransaction();
            tx.begin();
            FacturaDetalle det = em.find(FacturaDetalle.class, idDetalle);
            if (det != null) em.remove(det);
            tx.commit();
            return 1;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return -1;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public int modificarDetalle(FacturaDetalle detalle) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            tx  = em.getTransaction();
            tx.begin();
            FacturaDetalle det = em.find(FacturaDetalle.class, detalle.getIdFacturaDet());
            if (det != null) {
                det.setCantidad(detalle.getCantidad());
                det.setPrecio(detalle.getPrecio());
                em.persist(det);
            }
            tx.commit();
            return 1;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            return -1;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public FacturaCabecera buscar(Integer idFactura) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            return em.find(FacturaCabecera.class, idFactura);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public List<FacturaCabecera> listarTodos() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            return em.createQuery(
                    "SELECT f FROM FacturaCabecera f ORDER BY f.fecha DESC",
                    FacturaCabecera.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public List<Object[]> reporteVentasPorCiudad() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            return em.createQuery(
                            "SELECT c.nombre, SUM(f.valorTotal) " +
                                    "FROM FacturaCabecera f JOIN f.ciudad c " +
                                    "GROUP BY c.nombre ORDER BY SUM(f.valorTotal) DESC")
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public Integer obtenerSiguienteId() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            Number max = (Number) em.createQuery(
                            "SELECT COALESCE(MAX(f.idFactura), 0) FROM FacturaCabecera f")
                    .getSingleResult();
            return max.intValue() + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }

    public Integer obtenerSiguienteIdDetalle() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = Persistence.createEntityManagerFactory(PU);
            em  = emf.createEntityManager();
            Number max = (Number) em.createQuery(
                            "SELECT COALESCE(MAX(d.idFacturaDet), 0) FROM FacturaDetalle d")
                    .getSingleResult();
            return max.intValue() + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            if (em  != null) em.close();
            if (emf != null) emf.close();
        }
    }
}