package com.sistema.inventario.presentacion;

import com.sistema.inventario.negocio.NegocioReporteInv;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class PantallaReporte {

    private NegocioReporteInv negocioReportes = new NegocioReporteInv();

    private DatePicker dpInicio = new DatePicker(LocalDate.now().minusMonths(1));
    private DatePicker dpFin = new DatePicker(LocalDate.now());

    private TableView<SaldosFila> tablaSaldos = new TableView<>();
    private TableView<ObservableList<String>> tablaMatriz = new TableView<>();

    public void mostrar(Stage stage) {
        stage.setTitle("Modulo de Reportes e Impresion");

        // Cabecera de Filtros
        HBox panelFiltros = new HBox(15);
        panelFiltros.setAlignment(Pos.CENTER_LEFT);
        panelFiltros.setPadding(new Insets(15));
        panelFiltros.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0;");

        Button btnGenerar = new Button("Generar Reportes");
        btnGenerar.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button btnImprimir = new Button("Imprimir Pestana Actual");
        btnImprimir.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        panelFiltros.getChildren().addAll(
                new Label("Fecha Inicio:"), dpInicio,
                new Label("Fecha Fin:"), dpFin,
                btnGenerar, btnImprimir
        );

        // Panel de Pestañas (Tabs)
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Pestaña 1: Saldos por Artículo
        Tab tabSaldos = new Tab("Reporte 1: Saldos por Articulo");
        configurarTablaSaldos();
        VBox boxSaldos = new VBox(10, new Label("Saldos calculados (Ingresos - Egresos) en el rango seleccionado:"), tablaSaldos);
        boxSaldos.setPadding(new Insets(15));
        VBox.setVgrow(tablaSaldos, Priority.ALWAYS);
        tabSaldos.setContent(boxSaldos);

        // Pestaña 2: Matriz Cruzada
        Tab tabMatriz = new Tab("Reporte 2: Matriz Cruzada");
        VBox boxMatriz = new VBox(10, new Label("Detalle por tipo de movimiento (Filas = Articulos, Columnas = Movimientos):"), tablaMatriz);
        boxMatriz.setPadding(new Insets(15));
        VBox.setVgrow(tablaMatriz, Priority.ALWAYS);
        tabMatriz.setContent(boxMatriz);

        tabPane.getTabs().addAll(tabSaldos, tabMatriz);

        // Layout Principal
        VBox layout = new VBox(panelFiltros, tabPane);
        layout.setStyle("-fx-background-color: white;");

        // Configuración de Eventos
        btnGenerar.setOnAction(e -> generarReportes());

        btnImprimir.setOnAction(e -> {
            TableView<?> tablaAImprimir = tabPane.getSelectionModel().getSelectedItem() == tabSaldos ? tablaSaldos : tablaMatriz;
            imprimirNodo(tablaAImprimir, stage);
        });

        // Carga inicial automática de datos
        generarReportes();

        Scene scene = new Scene(layout, 850, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void configurarTablaSaldos() {
        TableColumn<SaldosFila, String> colArt = new TableColumn<>("Articulo");
        colArt.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().articulo));
        colArt.setPrefWidth(300);

        TableColumn<SaldosFila, String> colCant = new TableColumn<>("Stock Existente");
        colCant.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().cantidad)));
        colCant.setPrefWidth(150);

        tablaSaldos.getColumns().clear();
        tablaSaldos.getColumns().addAll(colArt, colCant);
    }

    private void generarReportes() {
        Date inicio = Date.from(dpInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(dpFin.getValue().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());

        List<Object[]> datosRaw = negocioReportes.obtenerDatosReporte(inicio, fin);

        if (datosRaw == null) return;

        Map<String, Long> saldosTotales = new HashMap<>(); 
        Set<String> tiposMovimientos = new TreeSet<>();    
        Map<String, Map<String, Long>> matriz = new HashMap<>(); 

        for (Object[] fila : datosRaw) {
            String articulo = String.valueOf(fila[0]);
            String movNombre = String.valueOf(fila[1]);
            String movTipo = String.valueOf(fila[2]); 
            long cantidad = ((Number) fila[3]).longValue();

            saldosTotales.putIfAbsent(articulo, 0L);
            
            if ("I".equalsIgnoreCase(movTipo)) {
                saldosTotales.put(articulo, saldosTotales.get(articulo) + cantidad);
            } else {
                saldosTotales.put(articulo, saldosTotales.get(articulo) - cantidad);
            }

            tiposMovimientos.add(movNombre);
            matriz.putIfAbsent(articulo, new HashMap<>());
            matriz.get(articulo).put(movNombre, matriz.get(articulo).getOrDefault(movNombre, 0L) + cantidad);
        }

        // Población del Reporte de Saldos
        ObservableList<SaldosFila> datosSaldos = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : saldosTotales.entrySet()) {
            datosSaldos.add(new SaldosFila(entry.getKey(), entry.getValue()));
        }
        tablaSaldos.setItems(datosSaldos);

        // Población del Reporte de Matriz Cruzada
        tablaMatriz.getColumns().clear();
        
        TableColumn<ObservableList<String>, String> colArtMatriz = new TableColumn<>("Articulo");
        colArtMatriz.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        colArtMatriz.setPrefWidth(200);
        tablaMatriz.getColumns().add(colArtMatriz);

        List<String> columnasMov = new ArrayList<>(tiposMovimientos);
        for (int i = 0; i < columnasMov.size(); i++) {
            final int index = i + 1; 
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnasMov.get(i));
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));
            col.setPrefWidth(120);
            tablaMatriz.getColumns().add(col);
        }

        ObservableList<ObservableList<String>> datosMatriz = FXCollections.observableArrayList();
        for (Map.Entry<String, Map<String, Long>> entryArticulo : matriz.entrySet()) {
            ObservableList<String> filaMatriz = FXCollections.observableArrayList();
            filaMatriz.add(entryArticulo.getKey()); 
            
            for (String mov : columnasMov) {
                Long cant = entryArticulo.getValue().getOrDefault(mov, 0L);
                filaMatriz.add(String.valueOf(cant));
            }
            datosMatriz.add(filaMatriz);
        }
        tablaMatriz.setItems(datosMatriz);
    }

    private void imprimirNodo(TableView<?> tabla, Stage owner) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean showDialog = job.showPrintDialog(owner);
            if (showDialog) {
                tabla.setStyle("-fx-border-color: transparent;");
                boolean printed = job.printPage(tabla);
                if (printed) {
                    job.endJob();
                }
                tabla.setStyle(""); 
            }
        }
    }

    public static class SaldosFila {
        String articulo;
        Long cantidad;

        public SaldosFila(String articulo, Long cantidad) {
            this.articulo = articulo;
            this.cantidad = cantidad;
        }
    }
}