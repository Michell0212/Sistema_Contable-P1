package com.sistema.facturacion.presentacion;

import com.sistema.facturacion.negocio.NegocioFactura;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ReporteVentasCiudad {

    private NegocioFactura negocio = new NegocioFactura();

    public void mostrar(Stage stage) {
        stage.setTitle("Reporte | Ventas por Ciudad");

        // ── Título ──
        Label lblTitulo = new Label("REPORTE DE VENTAS POR CIUDAD");
        lblTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1F4E79;");

        Label lblSubtitulo = new Label("Total facturado agrupado por ciudad de entrega");
        lblSubtitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");

        // ── Tabla ──
        TableView<Object[]> tabla = new TableView<>();
        ObservableList<Object[]> datos = FXCollections.observableArrayList();

        TableColumn<Object[], String> colCiudad = new TableColumn<>("Ciudad");
        colCiudad.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(data.getValue()[0])));
        colCiudad.setPrefWidth(300);

        TableColumn<Object[], String> colTotal = new TableColumn<>("Total Vendido ($)");
        colTotal.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f", data.getValue()[1])));
        colTotal.setPrefWidth(200);

        tabla.getColumns().addAll(colCiudad, colTotal);
        tabla.setItems(datos);
        tabla.setPrefHeight(350);

        // ── Total general ──
        Label lblTotalGeneral = new Label("Total General: $0.00");
        lblTotalGeneral.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #27AE60;");

        // ── Botones ──
        Button btnActualizar = new Button("🔄 Actualizar");
        Button btnCerrar     = new Button("Cerrar");
        btnCerrar.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");

        HBox botones = new HBox(10, btnActualizar, btnCerrar);
        botones.setAlignment(Pos.CENTER_RIGHT);
        botones.setPadding(new Insets(10));

        // ── Layout ──
        VBox layout = new VBox(12,
                lblTitulo, lblSubtitulo,
                new Separator(),
                tabla,
                lblTotalGeneral,
                botones
        );
        layout.setPadding(new Insets(20));

        // ── Cargar datos ──
        Runnable cargar = () -> {
            datos.clear();
            var lista = negocio.reporteVentasPorCiudad();
            if (lista != null && !lista.isEmpty()) {
                datos.addAll(lista);
                double totalGeneral = lista.stream()
                        .mapToDouble(row -> ((Number) row[1]).doubleValue())
                        .sum();
                lblTotalGeneral.setText(
                        String.format("Total General: $%.2f", totalGeneral));
            } else {
                lblTotalGeneral.setText("Total General: $0.00");
            }
        };

        cargar.run();

        btnActualizar.setOnAction(e -> cargar.run());
        btnCerrar.setOnAction(e -> stage.close());

        Scene scene = new Scene(layout, 580, 500);
        stage.setScene(scene);
        stage.show();
    }
}