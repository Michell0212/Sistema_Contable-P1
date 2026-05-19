package com.sistema.cxc.presentacion;

import com.sistema.cxc.modelo.Cobrador;
import com.sistema.cxc.negocio.NegocioCobrador;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PantallaCobrador {

    private final NegocioCobrador negocio = new NegocioCobrador();

    private final NegocioCobrador negocioCobrador = new NegocioCobrador();

    private TextField txtCedula;
    private TextField txtNombre;
    private TextField txtDireccion;
    private TableView<Cobrador> tablaCobradores;

    public void mostrar(Stage stage) {
        stage.setTitle("Mantenimiento de Cobradores - CxC");


        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Cédula:"), 0, 0);
        txtCedula = new TextField();
        grid.add(txtCedula, 1, 0);

        grid.add(new Label("Nombre:"), 0, 1);
        txtNombre = new TextField();
        grid.add(txtNombre, 1, 1);

        grid.add(new Label("Dirección:"), 0, 2);
        txtDireccion = new TextField();
        grid.add(txtDireccion, 1, 2);


        HBox panelBotones = new HBox(10);
        panelBotones.setPadding(new Insets(10, 0, 10, 0));

        Button btnGuardar = new Button("Guardar/Insertar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiar = new Button("Limpiar");

        panelBotones.getChildren().addAll(btnGuardar, btnModificar, btnEliminar, btnBuscar, btnLimpiar);


        tablaCobradores = new TableView<>();

        TableColumn<Cobrador, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idCobrador"));

        TableColumn<Cobrador, String> colCedula = new TableColumn<>("Cédula");
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));

        TableColumn<Cobrador, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Cobrador, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        tablaCobradores.getColumns().addAll(colId, colCedula, colNombre, colDireccion);
        tablaCobradores.setPrefHeight(200);


        btnGuardar.setOnAction(e -> ejecutarInsertar());
        btnModificar.setOnAction(e -> ejecutarModificar());
        btnEliminar.setOnAction(e -> ejecutarEliminar());
        btnBuscar.setOnAction(e -> ejecutarBuscar());
        btnLimpiar.setOnAction(e -> limpiarCampos());


        tablaCobradores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtCedula.setText(newSelection.getCedula());
                txtNombre.setText(newSelection.getNombre());
                txtDireccion.setText(newSelection.getDireccion());
                txtCedula.setEditable(false); // No editar la PK activa
            }
        });


        refrescarTabla();


        VBox layoutPrincipal = new VBox(10);
        layoutPrincipal.setPadding(new Insets(10));
        layoutPrincipal.getChildren().addAll(grid, panelBotones, tablaCobradores);

        Scene scene = new Scene(layoutPrincipal, 500, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void ejecutarInsertar() {

        Cobrador nuevoCobrador = new Cobrador();


        nuevoCobrador.setCedula(txtCedula.getText());
        nuevoCobrador.setNombre(txtNombre.getText());
        nuevoCobrador.setDireccion(txtDireccion.getText());


        boolean exito = negocioCobrador.insertar(nuevoCobrador);

        if (exito) {
            mostrarAlerta("Éxito", "Cobrador registrado correctamente.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
            cargarDatosTabla();
        } else {
            mostrarAlerta("Error", "No se pudo registrar al cobrador en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    private void ejecutarModificar() {
        if (txtCedula.getText().isEmpty()) return;
        Cobrador c = new Cobrador();
        c.setCedula(txtCedula.getText());
        c.setNombre(txtNombre.getText());
        c.setDireccion(txtDireccion.getText());

        negocio.modificar(c);
        mostrarAlerta("Éxito", "Cobrador modificado con éxito.", Alert.AlertType.INFORMATION);
        limpiarCampos();
        refrescarTabla();
    }

    private void ejecutarEliminar() {
        if (txtCedula.getText().isEmpty()) return;
        negocio.eliminar(txtCedula.getText());
        mostrarAlerta("Éxito", "Cobrador eliminado.", Alert.AlertType.INFORMATION);
        limpiarCampos();
        refrescarTabla();
    }

    private void ejecutarBuscar() {
        if (txtCedula.getText().isEmpty()) return;
        Cobrador c = negocio.buscar(txtCedula.getText());
        if (c != null) {
            txtNombre.setText(c.getNombre());
            txtDireccion.setText(c.getDireccion());
            tablaCobradores.getSelectionModel().select(c);
        } else {
            mostrarAlerta("No Encontrado", "No existe un cobrador con esa cédula.", Alert.AlertType.WARNING);
        }
    }

    private void refrescarTabla() {
        var lista = negocio.listarTodos();
        if (lista != null) {
            tablaCobradores.setItems(FXCollections.observableArrayList(lista));
        }
    }

    private void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        txtDireccion.clear();
        txtCedula.setEditable(true);
        tablaCobradores.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarFormulario() {
        txtCedula.clear();
        txtNombre.clear();
        txtDireccion.clear();
    }

    private void cargarDatosTabla() {

        if (negocioCobrador.listarTodos() != null) {
            tablaCobradores.setItems(FXCollections.observableArrayList(negocioCobrador.listarTodos()));
        }
    }
}