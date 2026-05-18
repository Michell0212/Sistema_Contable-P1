package com.sistema.cxc.presentacion;

import com.sistema.cxc.modelo.FormaPago;
import com.sistema.cxc.negocio.NegocioFormaPago;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PantallaFormaPago {

    private final NegocioFormaPago negocio = new NegocioFormaPago();

    private TextField txtCodigo;
    private TextField txtNombre;
    private TableView<FormaPago> tablaFormasPago;

    public void mostrar(Stage stage) {
        stage.setTitle("Mantenimiento de Formas de Pago - CxC");


        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Código:"), 0, 0);
        txtCodigo = new TextField();
        txtCodigo.setPromptText("Autogenerado al insertar"); // Guía visual para el usuario
        grid.add(txtCodigo, 1, 0);

        grid.add(new Label("Nombre de la Forma de Pago:"), 0, 1);
        txtNombre = new TextField();
        grid.add(txtNombre, 1, 1);


        HBox panelBotones = new HBox(10);
        panelBotones.setPadding(new Insets(10, 0, 10, 0));

        Button btnGuardar = new Button("Guardar/Insertar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiar = new Button("Limpiar");

        panelBotones.getChildren().addAll(btnGuardar, btnModificar, btnEliminar, btnBuscar, btnLimpiar);


        tablaFormasPago = new TableView<>();

        TableColumn<FormaPago, Integer> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<FormaPago, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        tablaFormasPago.getColumns().addAll(colCodigo, colNombre);
        colNombre.setPrefWidth(200);
        tablaFormasPago.setPrefHeight(200);


        btnGuardar.setOnAction(e -> ejecutarInsertar());
        btnModificar.setOnAction(e -> ejecutarModificar());
        btnEliminar.setOnAction(e -> ejecutarEliminar());
        btnBuscar.setOnAction(e -> ejecutarBuscar());
        btnLimpiar.setOnAction(e -> limpiarCampos());


        tablaFormasPago.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                txtCodigo.setText(String.valueOf(newSelection.getCodigo()));
                txtNombre.setText(newSelection.getNombre());
                txtCodigo.setEditable(false);
            }
        });


        refrescarTabla();


        VBox layoutPrincipal = new VBox(10);
        layoutPrincipal.setPadding(new Insets(10));
        layoutPrincipal.getChildren().addAll(grid, panelBotones, tablaFormasPago);

        Scene scene = new Scene(layoutPrincipal, 450, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void ejecutarInsertar() {
        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta("Error", "El Nombre es obligatorio.", Alert.AlertType.ERROR);
            return;
        }

        FormaPago fp = new FormaPago();

        fp.setNombre(txtNombre.getText());


        boolean exito = negocio.insertar(fp);
        if (exito) {
            mostrarAlerta("Éxito", "Forma de pago registrada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            refrescarTabla();
        } else {
            mostrarAlerta("Error", "No se pudo registrar la forma de pago.", Alert.AlertType.ERROR);
        }
    }

    private void ejecutarModificar() {
        if (txtCodigo.getText().isEmpty()) {
            mostrarAlerta("Advertencia", "Seleccione o escriba un código a modificar.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FormaPago fp = new FormaPago();

            fp.setCodigo(Integer.parseInt(txtCodigo.getText()));
            fp.setNombre(txtNombre.getText());

            negocio.modificar(fp);
            mostrarAlerta("Éxito", "Forma de pago modificada con éxito.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            refrescarTabla();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El código debe ser un valor numérico.", Alert.AlertType.ERROR);
        }
    }

    private void ejecutarEliminar() {
        if (txtCodigo.getText().isEmpty()) {
            mostrarAlerta("Advertencia", "Seleccione o escriba un código a eliminar.", Alert.AlertType.WARNING);
            return;
        }
        try {

            int codigo = Integer.parseInt(txtCodigo.getText());
            negocio.eliminar(codigo);

            mostrarAlerta("Éxito", "Forma de pago eliminada.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            refrescarTabla();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El código debe ser un valor numérico.", Alert.AlertType.ERROR);
        }
    }

    private void ejecutarBuscar() {
        if (txtCodigo.getText().isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese un código numérico para buscar.", Alert.AlertType.WARNING);
            return;
        }
        try {

            int codigo = Integer.parseInt(txtCodigo.getText());
            FormaPago fp = negocio.buscar(codigo);

            if (fp != null) {
                txtNombre.setText(fp.getNombre());
                tablaFormasPago.getSelectionModel().select(fp);
            } else {
                mostrarAlerta("No Encontrado", "No existe una forma de pago con ese código.", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El código debe ser un valor numérico.", Alert.AlertType.ERROR);
        }
    }

    private void refrescarTabla() {
        var lista = negocio.listarTodos();
        if (lista != null) {
            tablaFormasPago.setItems(FXCollections.observableArrayList(lista));
        }
    }

    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtCodigo.setEditable(true);
        tablaFormasPago.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}