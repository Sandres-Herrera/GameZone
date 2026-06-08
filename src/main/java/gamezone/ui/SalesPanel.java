package gamezone.ui;

import gamezone.model.Sale;
import gamezone.service.SaleService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesPanel extends VBox {

    private final SaleService saleService;
    private TableView<Sale> table;
    private Label totalLabel;

    public SalesPanel(SaleService saleService) {
        this.saleService = saleService;
        buildUI();
    }

    private void buildUI() {
        setSpacing(14);
        setPadding(new Insets(30, 40, 20, 40));
        setStyle("-fx-background-color: #0f0f1a;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label heading = new Label("Historial de Ventas");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web("#b06aff"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("Actualizar");
        refreshBtn.setStyle("-fx-background-color: #6c2dc7; -fx-text-fill: white; " +
                            "-fx-font-size: 12px; -fx-background-radius: 6; -fx-padding: 6 14 6 14; -fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadTable());

        header.getChildren().addAll(heading, spacer, refreshBtn);

        table = new TableView<>();
        table.setStyle("-fx-background-color: #1a1a2e;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        TableColumn<Sale, String> colId = col("ID", 90);
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));

        TableColumn<Sale, String> colGame = col("Videojuego", 200);
        colGame.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getVideoGame().getTitle()));

        TableColumn<Sale, String> colQty = col("Cantidad", 80);
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getQuantity())));

        TableColumn<Sale, String> colUnit = col("Precio Unitario", 130);
        colUnit.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%,.0f", c.getValue().getUnitPrice())));

        TableColumn<Sale, String> colTotal = col("Total", 130);
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%,.0f", c.getValue().getTotal())));

        TableColumn<Sale, String> colDate = col("Fecha", 150);
        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getSaleDate().format(fmt)));

        table.getColumns().addAll(colId, colGame, colQty, colUnit, colTotal, colDate);

        HBox summary = new HBox(20);
        summary.setAlignment(Pos.CENTER_LEFT);
        summary.setPadding(new Insets(12, 16, 12, 16));
        summary.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 8;");

        totalLabel = new Label("Total recaudado: $0");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        totalLabel.setTextFill(Color.web("#44cc77"));

        summary.getChildren().add(totalLabel);

        loadTable();
        getChildren().addAll(header, table, summary);
    }

    private void loadTable() {
        List<Sale> sales = saleService.getAllSales();
        table.setItems(FXCollections.observableArrayList(sales));

        double total = sales.stream().mapToDouble(Sale::getTotal).sum();
        totalLabel.setText(String.format("Total recaudado: $%,.0f  |  Ventas realizadas: %d", total, sales.size()));
    }

    private TableColumn<Sale, String> col(String name, int w) {
        TableColumn<Sale, String> c = new TableColumn<>(name);
        c.setPrefWidth(w);
        return c;
    }
}
