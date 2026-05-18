package com.sistema.inventario.negocio;

import com.sistema.inventario.modelo.ComprobanteCabecera;
import com.sistema.inventario.modelo.ComprobanteDetalle;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class NegocioComprobante {

    // Misma unidad de persistencia
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaContablePU");

    /**
     * Guarda la Cabecera y el Detalle en una sola transacción atómica.
     */
    public void registrarTransaccion(ComprobanteCabecera cabecera, List<ComprobanteDetalle> detalles) throws Exception {
        // 1. Validaciones estrictas de negocio
        validarTransaccion(cabecera, detalles);

        // 2. Asignar fecha actual si viene vacía
        if (cabecera.getFecha() == null) {
            cabecera.setFecha(new Date());
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // 3. Enlazar bidireccionalmente los objetos (Vital para Hibernate)
            for (ComprobanteDetalle detalle : detalles) {
                detalle.setIdComprobante(cabecera); // El hijo (detalle) conoce a su padre
            }
            cabecera.setComprobanteDetalleCollection(detalles); // El padre conoce a sus hijos

            // 4. Guardar. Gracias a CascadeType.ALL, persistir la cabecera guardará los detalles.
            em.merge(cabecera);

            // 5. Confirmar transacción
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Deshacer todo si hay un fallo
            }
            throw new Exception("Error al guardar la transacción: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene el historial de comprobantes generados.
     */
    public List<ComprobanteCabecera> obtenerHistorialComprobantes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ComprobanteCabecera> query = em.createNamedQuery("ComprobanteCabecera.findAll", ComprobanteCabecera.class);
            List<ComprobanteCabecera> lista = query.getResultList();
            
            // Recorremos la lista y "tocamos" los detalles para obligar 
            // a Hibernate a traerlos ANTES de cerrar la conexión (em.close)
            for (ComprobanteCabecera cab : lista) {
                if (cab.getComprobanteDetalleCollection() != null) {
                    cab.getComprobanteDetalleCollection().size(); 
                }
            }
            
            return lista;
        } finally {
            em.close(); // Ahora sí podemos cerrar la conexión en paz
        }
    }

    /**
     * Centraliza las reglas de negocio para asegurar la integridad de los datos.
     */
    private void validarTransaccion(ComprobanteCabecera cabecera, List<ComprobanteDetalle> detalles) throws Exception {
        if (cabecera == null) {
            throw new Exception("Los datos del comprobante están vacíos.");
        }
        if (cabecera.getNumeroComprobante() == null || cabecera.getNumeroComprobante().trim().isEmpty()) {
            throw new Exception("El número de comprobante es obligatorio.");
        }
        if (cabecera.getIdTipoMovimiento() == null) {
            throw new Exception("Debe seleccionar un tipo de movimiento (Ingreso/Egreso).");
        }
        
        // Regla: No se puede guardar un comprobante sin artículos
        if (detalles == null || detalles.isEmpty()) {
            throw new Exception("El comprobante debe tener al menos un artículo en el detalle.");
        }

        // Reglas a nivel de cada fila del detalle
        for (ComprobanteDetalle det : detalles) {
            if (det.getIdArticulo() == null) {
                throw new Exception("Una de las filas no tiene un artículo seleccionado.");
            }
            
            // Validar que la cantidad sea mayor a 0 (Usando BigInteger)
            if (det.getCantidad() == null || det.getCantidad().compareTo(BigInteger.ZERO) <= 0) {
                throw new Exception("Las cantidades de los artículos deben ser mayores a cero.");
            }
            
            // Validar que el precio no sea negativo (Usando BigDecimal)
            if (det.getPrecio() == null || det.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("El precio en el detalle no puede ser negativo.");
            }
        }
    }
    
    /**
     * Obtiene el siguiente ID disponible para la Cabecera.
     */
    public BigDecimal obtenerSiguienteId() {
        EntityManager em = emf.createEntityManager();
        try {
            Number max = (Number) em.createQuery(
                            "SELECT COALESCE(MAX(c.idComprobante), 0) FROM ComprobanteCabecera c")
                    .getSingleResult();
            return new BigDecimal(max.longValue() + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ONE;
        } finally {
            em.close();
        }
    }

    /**
     * Obtiene el siguiente ID disponible para el Detalle (uso temporal en memoria).
     */
    public BigDecimal obtenerSiguienteIdDetalle() {
        EntityManager em = emf.createEntityManager();
        try {
            Number max = (Number) em.createQuery(
                            "SELECT COALESCE(MAX(d.idComprobanteDet), 0) FROM ComprobanteDetalle d")
                    .getSingleResult();
            return new BigDecimal(max.longValue() + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ONE;
        } finally {
            em.close();
        }
    }
    
    /**
     * Modifica un comprobante existente y sus detalles.
     */
    public void modificarTransaccion(ComprobanteCabecera cabeceraUI, List<ComprobanteDetalle> detallesActualizados) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Traer el original directo de la base de datos (Entidad "Manejada" por Hibernate)
            ComprobanteCabecera cabeceraBD = em.find(ComprobanteCabecera.class, cabeceraUI.getIdComprobante());
            if (cabeceraBD == null) {
                throw new Exception("El comprobante no existe.");
            }

            // 2. Actualizar los datos básicos de la cabecera
            cabeceraBD.setNumeroComprobante(cabeceraUI.getNumeroComprobante());
            cabeceraBD.setFecha(cabeceraUI.getFecha());
            cabeceraBD.setIdTipoMovimiento(cabeceraUI.getIdTipoMovimiento());

            // 3. 🔥 EL TRUCO PARA EL ORPHAN REMOVAL 🔥
            // Vaciamos la lista que tiene Hibernate en memoria (esto detecta cuáles vas a eliminar)
            cabeceraBD.getComprobanteDetalleCollection().clear();

            // 4. Llenamos la lista nuevamente con los que quedaron en tu pantalla
            for (ComprobanteDetalle det : detallesActualizados) {
                // IMPORTANTÍSIMO: Le decimos al detalle quién es su padre
                det.setIdComprobante(cabeceraBD); 
                cabeceraBD.getComprobanteDetalleCollection().add(det);
            }

            // 5. Guardamos los cambios
            em.merge(cabeceraBD);
            em.getTransaction().commit();

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Error al modificar en BD: " + ex.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Elimina un comprobante y sus detalles por ID.
     */
    public void eliminarTransaccion(BigDecimal idComprobante) throws Exception {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ComprobanteCabecera cabecera = em.find(ComprobanteCabecera.class, idComprobante);
            if (cabecera != null) {
                em.remove(cabecera); // Gracias al CascadeType.ALL, esto también borra los detalles
            } else {
                throw new Exception("El comprobante no existe.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new Exception("Error al eliminar: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Busca un comprobante específico por su ID.
     */
    public ComprobanteCabecera buscarPorId(BigDecimal idComprobante) {
        EntityManager em = emf.createEntityManager();
        try {
            ComprobanteCabecera cabecera = em.find(ComprobanteCabecera.class, idComprobante);
            // "Tocamos" la colección de detalles para forzar a Hibernate a traerlos (Lazy Loading)
            if (cabecera != null && cabecera.getComprobanteDetalleCollection() != null) {
                cabecera.getComprobanteDetalleCollection().size(); 
            }
            return cabecera;
        } finally {
            em.close();
        }
    }
}