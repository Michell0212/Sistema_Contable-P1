package com.sistema.inventario.presentacion;

import com.sistema.inventario.modelo.TipoMovimiento;
import com.sistema.inventario.negocio.NegocioTipoMovimiento;
import java.math.BigDecimal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PantallaTipoMovimiento {

    private NegocioTipoMovimiento negocio = new NegocioTipoMovimiento();

    private TextField txtId = new TextField();
    private TextField txtNombre = new TextField();
    private ComboBox<String> cmbTipo = new ComboBox<>();
    private Label lblMensaje = new Label();

    private TableView<TipoMovimiento> tabla = new TableView<>();
    private ObservableList<TipoMovimiento> datos = FXCollections.observableArrayList();

    public void mostrar(Stage stage) {
        stage.setTitle("Inventario | Tipos de Movimiento");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));

        txtId.setEditable(false);
        txtId.setStyle("-fx-background-color: #f0f0f0;");
        
        // Configurar ComboBox
        cmbTipo.getItems().addAll("Ingreso (I)", "Egreso (E)");
        cmbTipo.setPrefWidth(200);

        form.add(new Label("ID:"), 0, 0); form.add(txtId, 1, 0);
        form.add(new Label("Nombre:"), 0, 1); form.add(txtNombre, 1, 1);
        form.add(new Label("Tipo:"), 0, 2); form.add(cmbTipo, 1, 2);

        // ── Botones CRUD ──
        Button btnNuevo = new Button("Nuevo");
        Button btnGuardar = new Button("Guardar");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnLimpiar = new Button("Limpiar");

        HBox botones = new HBox(10, btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnLimpiar);
        botones.setPadding(new Insets(10));
        botones.setAlignment(Pos.CENTER);

        // ── Tabla ──
        TableColumn<TipoMovimiento, BigDecimal> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idTipoMovimiento"));
        colId.setPrefWidth(80);

        TableColumn<TipoMovimiento, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(250);

        TableColumn<TipoMovimiento, Character> colTipo = new TableColumn<>("Tipo (I/E)");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setPrefWidth(120);

        tabla.getColumns().addAll(colId, colNombre, colTipo);
        tabla.setItems(datos);
        tabla.setPrefHeight(250);

        lblMensaje.setStyle("-fx-font-weight: bold;");

        VBox layout = new VBox(10, form, botones, lblMensaje, tabla);
        layout.setPadding(new Insets(10));

        // ── Eventos CRUD ──
        btnNuevo.setOnAction(e -> {
            txtId.setText(String.valueOf(negocio.obtenerSiguienteId()));
            txtNombre.clear();
            cmbTipo.getSelectionModel().clearSelection();
            mostrarMensaje("Modo Nuevo: Listo para ingresar datos.", "blue");
        });

        btnGuardar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                mostrarMensaje("Presione 'Nuevo' para generar un ID.", "red");
                return;
            }
            try {
                TipoMovimiento tm = new TipoMovimiento();
                tm.setIdTipoMovimiento(new BigDecimal(txtId.getText()));
                tm.setNombre(txtNombre.getText());
                tm.setTipo(obtenerCaracterDesdeCombo());

                negocio.guardar(tm);
                mostrarMensaje("Tipo de movimiento guardado correctamente.", "blue");
                cargarTabla();
                limpiar();
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage(), "red");
            }
        });

        btnActualizar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                mostrarMensaje("Seleccione un registro de la tabla primero.", "red");
                return;
            }
            try {
                TipoMovimiento tm = new TipoMovimiento();
                tm.setIdTipoMovimiento(new BigDecimal(txtId.getText()));
                tm.setNombre(txtNombre.getText());
                tm.setTipo(obtenerCaracterDesdeCombo());

                negocio.actualizar(tm);
                mostrarMensaje("Tipo de movimiento actualizado correctamente.", "blue");
                cargarTabla();
                limpiar();
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage(), "red");
            }
        });

        btnEliminar.setOnAction(e -> {
            if (txtId.getText().isEmpty()) {
                mostrarMensaje("Seleccione un registro de la tabla primero.", "red");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar este tipo de movimiento?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    try {
                        BigDecimal idAEliminar = new BigDecimal(txtId.getText());
                        negocio.eliminar(idAEliminar);
                        mostrarMensaje("Registro eliminado correctamente.", "blue");
                        cargarTabla();
                        limpiar();
                    } catch (Exception ex) {
                        mostrarMensaje(ex.getMessage(), "red");
                    }
                }
            });
        });

        btnLimpiar.setOnAction(e -> limpiar());

        // Seleccionar fila en tabla
        tabla.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> {
                    if (newVal != null) {
                        txtId.setText(String.valueOf(newVal.getIdTipoMovimiento()));
                        txtNombre.setText(newVal.getNombre());
                        
                        if (newVal.getTipo() != null && newVal.getTipo() == 'I') {
                            cmbTipo.getSelectionModel().select("Ingreso (I)");
                        } else if (newVal.getTipo() != null && newVal.getTipo() == 'E') {
                            cmbTipo.getSelectionModel().select("Egreso (E)");
                        }
                        lblMensaje.setText("");
                    }
                });

        cargarTabla();

        Scene scene = new Scene(layout, 500, 450);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarTabla() {
        datos.clear();
        var lista = negocio.obtenerTodos();
        if (lista != null) {
            datos.addAll(lista);
        }
    }

    private void limpiar() {
        txtId.clear();
        txtNombre.clear();
        cmbTipo.getSelectionModel().clearSelection();
        tabla.getSelectionModel().clearSelection();
    }
    
    private void mostrarMensaje(String mensaje, String color) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }
    
    // Método auxiliar para transformar lo seleccionado en el ComboBox a 'I' o 'E'
    private Character obtenerCaracterDesdeCombo() {
        String seleccion = cmbTipo.getValue();
        if (seleccion != null) {
            if (seleccion.equals("Ingreso (I)")) return 'I';
            if (seleccion.equals("Egreso (E)")) return 'E';
        }
        return null; // El negocio lanzará excepción si es null
    }
}