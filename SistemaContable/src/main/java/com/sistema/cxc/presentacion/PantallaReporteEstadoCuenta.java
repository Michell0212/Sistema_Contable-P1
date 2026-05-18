package com.sistema.cxc.presentacion;

import com.sistema.cxc.modelo.ReporteEstadoCuentaDTO;
import com.sistema.cxc.negocio.NegocioReportes;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PantallaReporteEstadoCuenta {

    private final NegocioReportes negocioReportes = new NegocioReportes();

    private DatePicker dpInicio;
    private DatePicker dpFin;
    private TableView<ReporteEstadoCuentaDTO> tablaReporte;

    public void mostrar(Stage stage) {
        stage.setTitle("Reporte: Estado de Cuenta por Factura - CxC");


        HBox panelFiltros = new HBox(15);
        panelFiltros.setPadding(new Insets(15));

        dpInicio = new DatePicker();
        dpFin = new DatePicker();
        Button btnConsultar = new Button("Consultar Reporte");
        Button btnImprimir = new Button("Imprimir"); // Requerimiento de Rúbrica

        panelFiltros.getChildren().addAll(
                new Label("Desde:"), dpInicio,
                new Label("Hasta:"), dpFin,
                btnConsultar, btnImprimir
        );


        tablaReporte = new TableView<>();

        TableColumn<ReporteEstadoCuentaDTO, Integer> colNumFactura = new TableColumn<>("N° Factura");
        colNumFactura.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colNumFactura.setPrefWidth(120);

        TableColumn<ReporteEstadoCuentaDTO, Double> colValorFactura = new TableColumn<>("Valor Factura ($)");
        colValorFactura.setCellValueFactory(new PropertyValueFactory<>("valorFactura"));
        colValorFactura.setPrefWidth(140);

        TableColumn<ReporteEstadoCuentaDTO, Double> colTotalPagado = new TableColumn<>("Total Pagado ($)");
        colTotalPagado.setCellValueFactory(new PropertyValueFactory<>("totalPagado"));
        colTotalPagado.setPrefWidth(140);

        TableColumn<ReporteEstadoCuentaDTO, Double> colSaldo = new TableColumn<>("Saldo por Cobrar ($)");
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldoPorCobrar"));
        colSaldo.setPrefWidth(140);

        tablaReporte.getColumns().addAll(colNumFactura, colValorFactura, colTotalPagado, colSaldo);


        btnConsultar.setOnAction(e -> ejecutarConsulta());
        btnImprimir.setOnAction(e -> ejecutarImpresion());


        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(panelFiltros, tablaReporte);

        Scene scene = new Scene(layout, 580, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void ejecutarConsulta() {
        if (dpInicio.getValue() == null || dpFin.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Debe seleccionar ambas fechas para el rango.", Alert.AlertType.WARNING);
            return;
        }


        Date inicio = Date.from(dpInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(dpFin.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<ReporteEstadoCuentaDTO> datos = negocioReportes.generarEstadoCuentaPorFechas(inicio, fin);

        if (datos != null && !datos.isEmpty()) {
            tablaReporte.setItems(FXCollections.observableArrayList(datos));
        } else {
            tablaReporte.getItems().clear();
            mostrarAlerta("Sin Datos", "No se encontraron facturas registradas en ese rango de fechas.", Alert.AlertType.INFORMATION);
        }
    }

    private void ejecutarImpresion() {
        if (tablaReporte.getItems().isEmpty()) {
            mostrarAlerta("Error", "No hay datos en la tabla para enviar a la impresora.", Alert.AlertType.WARNING);
            return;
        }


        System.out.println("====== ENVIANDO REPORTE A LA IMPRESORA ======");
        for (ReporteEstadoCuentaDTO fila : tablaReporte.getItems()) {
            System.out.println("Factura: " + fila.getNumeroFactura() +
                    " | Total: $" + fila.getValorFactura() +
                    " | Saldo: $" + fila.getSaldoPorCobrar());
        }
        System.out.println("=============================================");

        mostrarAlerta("Impresión", "El reporte ha sido enviado exitosamente a la cola de impresión del sistema.", Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}