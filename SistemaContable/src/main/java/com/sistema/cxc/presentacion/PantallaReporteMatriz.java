package com.sistema.cxc.presentacion;

import com.sistema.cxc.modelo.FormaPago;
import com.sistema.cxc.modelo.ReporteMatrizDTO;
import com.sistema.cxc.negocio.NegocioFormaPago;
import com.sistema.cxc.negocio.NegocioReportes;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class PantallaReporteMatriz {

    private final NegocioReportes negocioReportes = new NegocioReportes();
    private final NegocioFormaPago negocioFormaPago = new NegocioFormaPago();

    private DatePicker dpInicio;
    private DatePicker dpFin;
    private TableView<ReporteMatrizDTO> tablaMatriz;

    public void mostrar(Stage stage) {
        stage.setTitle("Matriz Cruzada: Recaudación por Cobrador");


        HBox panelFiltros = new HBox(10);
        panelFiltros.setPadding(new Insets(15));

        dpInicio = new DatePicker();
        dpFin = new DatePicker();
        Button btnConsultar = new Button("Generar Matriz");
        Button btnImprimir = new Button("Imprimir");

        panelFiltros.getChildren().addAll(new Label("Desde:"), dpInicio, new Label("Hasta:"), dpFin, btnConsultar, btnImprimir);


        tablaMatriz = new TableView<>();
        configurarColumnasDinamicas(); // Creamos las columnas según las formas de pago de la BD


        btnConsultar.setOnAction(e -> ejecutarConsulta());
        btnImprimir.setOnAction(e -> ejecutarImpresion());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(panelFiltros, tablaMatriz);

        Scene scene = new Scene(layout, 700, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void configurarColumnasDinamicas() {

        TableColumn<ReporteMatrizDTO, String> colCobrador = new TableColumn<>("Cobrador");
        colCobrador.setCellValueFactory(new PropertyValueFactory<>("nombreCobrador"));
        colCobrador.setPrefWidth(150);
        tablaMatriz.getColumns().add(colCobrador);


        List<FormaPago> formasPago = negocioFormaPago.listarTodos();
        if (formasPago != null) {
            for (FormaPago fp : formasPago) {
                TableColumn<ReporteMatrizDTO, Double> colDinamica = new TableColumn<>(fp.getNombre());


                colDinamica.setCellValueFactory(cellData -> {
                    Double valor = cellData.getValue().getValoresPorFormaPago().getOrDefault(fp.getNombre(), 0.0);
                    return new SimpleObjectProperty<>(valor);
                });

                colDinamica.setPrefWidth(100);
                tablaMatriz.getColumns().add(colDinamica);
            }
        }


        TableColumn<ReporteMatrizDTO, Double> colTotal = new TableColumn<>("Total Recaudado");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalRecaudado"));
        colTotal.setStyle("-fx-font-weight: bold;");
        tablaMatriz.getColumns().add(colTotal);
    }

    private void ejecutarConsulta() {
        if (dpInicio.getValue() == null || dpFin.getValue() == null) return;

        Date inicio = Date.from(dpInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fin = Date.from(dpFin.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<ReporteMatrizDTO> datos = negocioReportes.generarMatrizCruzada(inicio, fin);

        if (datos != null) {
            tablaMatriz.setItems(FXCollections.observableArrayList(datos));
        }
    }

    private void ejecutarImpresion() {
        System.out.println("====== MATRIZ CRUZADA ENVIADA A IMPRESIÓN ======");
        for (ReporteMatrizDTO fila : tablaMatriz.getItems()) {
            System.out.println(fila.getNombreCobrador() + " | Total: $" + fila.getTotalRecaudado());
        }
        System.out.println("================================================");
    }
}