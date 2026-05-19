package com.sistema.facturacion.presentacion;

import com.sistema.facturacion.modelo.Cliente;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;

public class PantallaReporteMatriz extends BorderPane {

    private TableView<Integer> tablaReporte; // La tabla maneja directamente los IDs de los Artículos

    public PantallaReporteMatriz(Map<Integer, Map<Integer, Double>> datosReporte, List<Cliente> todosLosClientes) {
        
        // --- 1. CONFIGURACIÓN DEL TÍTULO (Mejora visual) ---
        Label lblTitulo = new Label("Reporte de Ventas: Artículos vs Clientes");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblTitulo.setPadding(new Insets(10, 0, 15, 0));
        BorderPane.setAlignment(lblTitulo, Pos.CENTER); // Centramos el título

        // --- 2. CONFIGURACIÓN DE LA TABLA ---
        tablaReporte = new TableView<>();
        
        // Configurar las columnas pasándole la estructura del mapa
        initColumnas(todosLosClientes, datosReporte);
        
        // Las filas de la tabla serán simplemente los IDs de los artículos con ventas
        ObservableList<Integer> listaIdsArticulos = FXCollections.observableArrayList(datosReporte.keySet());
        tablaReporte.setItems(listaIdsArticulos);
        
        // --- 3. ENSAMBLAJE DEL BORDERPANE ---
        this.setTop(lblTitulo);
        this.setCenter(tablaReporte);
        this.setPadding(new Insets(15)); // Márgenes para que no se pegue a los bordes de la ventana
    }

    private void initColumnas(List<Cliente> listaClientes, Map<Integer, Map<Integer, Double>> datosReporte) {
        // --- 1. COLUMNAS FIJAS (DATOS DEL ARTÍCULO) ---
        TableColumn<Integer, Integer> colId = new TableColumn<>("ID Art.");
        colId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        colId.setStyle("-fx-alignment: CENTER;");
        colId.setPrefWidth(80);
        
        TableColumn<Integer, String> colNombre = new TableColumn<>("Artículo / Producto");
        colNombre.setCellValueFactory(cellData -> {
            Integer idArticulo = cellData.getValue();
            return new ReadOnlyObjectWrapper<>("Artículo " + idArticulo); // O buscar en tu catálogo
        });
        colNombre.setPrefWidth(200);
        
        tablaReporte.getColumns().addAll(colId, colNombre);

        // --- 2. COLUMNAS DINÁMICAS (CLIENTES Y SUS DÓLARES) ---
        for (Cliente cliente : listaClientes) {
            TableColumn<Integer, String> colCliente = new TableColumn<>(cliente.getNombre());
            
            colCliente.setCellValueFactory(cellData -> {
                Integer idArticulo = cellData.getValue();
                
                // Buscamos el mapa de clientes de este artículo
                Map<Integer, Double> clientesDelArticulo = datosReporte.get(idArticulo);
                
                Double totalDolares = 0.0;
                if (clientesDelArticulo != null) {
                    totalDolares = clientesDelArticulo.getOrDefault(cliente.getIdCliente(), 0.0);
                }
                
                // Formatear visualización a 2 decimales
                String textoCelda = (totalDolares > 0) ? String.format("$%.2f", totalDolares) : "-";
                return new ReadOnlyObjectWrapper<>(textoCelda);
            });
            
            // Alineamos los valores monetarios a la derecha para mejor lectura
            colCliente.setStyle("-fx-alignment: CENTER-RIGHT;");
            colCliente.setPrefWidth(130);
            
            tablaReporte.getColumns().add(colCliente);
        }
    }
}