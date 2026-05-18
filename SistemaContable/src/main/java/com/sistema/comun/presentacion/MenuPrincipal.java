package com.sistema.comun.presentacion;

import com.sistema.comun.modelo.Usuario;
import com.sistema.facturacion.presentacion.PantallaCliente;
import com.sistema.facturacion.presentacion.PantallaCiudad;
import com.sistema.facturacion.presentacion.PantallaFactura;
import com.sistema.facturacion.presentacion.ReporteVentasCiudad;

import com.sistema.inventario.presentacion.PantallaTipoMovimiento;
import com.sistema.inventario.presentacion.PantallaArticulo;
import com.sistema.inventario.presentacion.PantallaComprobante;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuPrincipal {

    public void mostrar(Stage stage, Usuario usuarioActual) {
        stage.setTitle("Sistema Contable | Menú Principal");

        Label lblTitulo = new Label("SISTEMA CONTABLE");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F4E79;");

        Label lblSubtitulo = new Label("Módulo de Facturación");
        lblSubtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #2E75B6;");

        Label lblUsuario = new Label("Usuario: " + usuarioActual.getUsername()
                + " | Rol: " + usuarioActual.getRol());
        lblUsuario.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

        String estiloBtn = "-fx-pref-width: 250px; -fx-pref-height: 35px; -fx-font-size: 13px;";
        String estiloBtnDeshabilitado = estiloBtn + "-fx-opacity: 0.5;";

        // ── Facturación ──
        Label lblFacturacion = new Label("── Facturación ──");
        lblFacturacion.setStyle("-fx-font-weight: bold; -fx-text-fill: #404040;");

        Button btnClientes   = new Button("Clientes");
        Button btnCiudades   = new Button("Ciudades de Entrega");
        Button btnFacturas   = new Button("Facturas");

        btnClientes.setStyle(estiloBtn);
        btnCiudades.setStyle(estiloBtn);
        btnFacturas.setStyle(estiloBtn);

        // ── Reportes ──
        Label lblReportes = new Label("── Reportes ──");
        lblReportes.setStyle("-fx-font-weight: bold; -fx-text-fill: #404040;");

        Button btnReporte1 = new Button("Ventas por Ciudad");
        Button btnReporte2 = new Button("Facturación por Cliente");

        btnReporte1.setStyle(estiloBtn);
        btnReporte2.setStyle(estiloBtn);

        // ── Inventarios ──
        Label lblInventarios = new Label("── Inventarios ──");
        lblInventarios.setStyle("-fx-font-weight: bold; -fx-text-fill: #404040;");

        Button btnArticulos    = new Button("Artículos");
        Button btnMovimientos  = new Button("Movimientos");
        Button btnComprobantes = new Button("Comprobantes"); // <-- CORREGIDO EL TEXTO AQUÍ

        btnArticulos.setStyle(estiloBtn);
        btnMovimientos.setStyle(estiloBtn);
        btnComprobantes.setStyle(estiloBtn);

        // ── CxC ──
        Label lblCxc = new Label("── Cuentas por Cobrar ──");
        lblCxc.setStyle("-fx-font-weight: bold; -fx-text-fill: #404040;");

        Button btnRecibos = new Button("Recibos CxC");
        Button btnPagos   = new Button("Pagos");

        btnRecibos.setStyle(estiloBtnDeshabilitado);
        btnPagos.setStyle(estiloBtnDeshabilitado);
        btnRecibos.setDisable(true);
        btnPagos.setDisable(true);

        // ── Administración ──
        Label lblAdmin = new Label("── Administración ──");
        lblAdmin.setStyle("-fx-font-weight: bold; -fx-text-fill: #404040;");

        Button btnUsuarios = new Button("Usuarios");
        btnUsuarios.setStyle(estiloBtn);

        if (!"ADMIN".equals(usuarioActual.getRol())) {
            btnUsuarios.setDisable(true);
            btnUsuarios.setStyle(estiloBtnDeshabilitado);
        }

        // ── Salir ──
        Button btnSalir = new Button("Salir");
        btnSalir.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;"
                + "-fx-pref-width: 250px; -fx-pref-height: 35px;");

        // ── Layout ──
        VBox layout = new VBox(10,
                lblTitulo, lblSubtitulo, lblUsuario,
                new Separator(),
                lblFacturacion,
                btnClientes, btnCiudades, btnFacturas,
                new Separator(),
                lblReportes,
                btnReporte1, btnReporte2,
                new Separator(),
                lblInventarios,
                btnArticulos, btnMovimientos, btnComprobantes,
                new Separator(),
                lblCxc,
                btnRecibos, btnPagos,
                new Separator(),
                lblAdmin,
                btnUsuarios,
                new Separator(),
                btnSalir
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // ── Eventos ──
        btnArticulos.setOnAction(e -> new PantallaArticulo().mostrar(new Stage()));
        btnComprobantes.setOnAction(e -> new PantallaComprobante().mostrar(new Stage()));
        btnMovimientos.setOnAction(e -> new PantallaTipoMovimiento().mostrar(new Stage()));
        btnClientes.setOnAction(e -> new PantallaCliente().mostrar(new Stage()));
        btnCiudades.setOnAction(e -> new PantallaCiudad().mostrar(new Stage()));
        btnFacturas.setOnAction(e -> new PantallaFactura().mostrar(new Stage()));
        btnReporte1.setOnAction(e -> new ReporteVentasCiudad().mostrar(new Stage()));
        btnReporte2.setOnAction(e -> { /* siguiente paso - Reporte 2 */ });
        btnUsuarios.setOnAction(e -> new PantallaUsuarios().mostrar(new Stage()));
        btnSalir.setOnAction(e -> stage.close());

        // Aumenté un poco el alto de la ventana para que el nuevo botón encaje bien
        Scene scene = new Scene(layout, 350, 820); 
        stage.setScene(scene);
        stage.show();
    }
}