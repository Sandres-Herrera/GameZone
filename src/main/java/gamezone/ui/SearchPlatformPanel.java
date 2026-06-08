package gamezone.ui;

import gamezone.model.DigitalVideoGame;
import gamezone.model.VideoGame;
import gamezone.service.VideoGameService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.util.List;

public class SearchPlatformPanel extends VBox {

    private final VideoGameService service;
    private TableView<VideoGame> table;

    public SearchPlatformPanel(VideoGameService service) {
        this.service = service;
        buildUI();
    }

    private void buildUI() {
        setSpacing(16);
        setPadding(new Insets(30, 40, 30, 40));
        setStyle("-fx-background-color: #0f0f1a;");

        Label heading = new Label("🎮  Buscar por Plataforma");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web("#b06aff"));

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> platformBox = new ComboBox<>();
        platformBox.getItems().addAll("PC", "PlayStation 5", "PlayStation 4", "Xbox Series X",
                "Xbox One", "Nintendo Switch", "Mobile", "Cross-Platform");
        platformBox.setPromptText("Selecciona plataforma...");
        platformBox.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #cccccc; " +
                             "-fx-border-color: #2a2a4e; -fx-border-radius: 8; -fx-background-radius: 8;");
        platformBox.setPrefWidth(220);

        Button btn = new Button("Buscar");
        btn.setStyle("-fx-background-color: #6c2dc7; -fx-text-fill: white; " +
                     "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        btn.setOnAction(e -> {
            if (platformBox.getValue() != null) search(platformBox.getValue());
        });

        searchRow.getChildren().addAll(platformBox, btn);

        table = new TableView<>();
        table.setStyle("-fx-background-color: #1a1a2e;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<VideoGame, String> colTitle = col("Título", 200);
        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));

        TableColumn<VideoGame, String> colType = col("Tipo", 80);
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue() instanceof DigitalVideoGame ? "Digital" : "Físico"));

        TableColumn<VideoGame, String> colGenre = col("Género", 120);
        colGenre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGenre()));

        TableColumn<VideoGame, String> colPrice = col("Precio Final", 120);
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%,.0f", c.getValue().calculateFinalPrice())));

        TableColumn<VideoGame, String> colStock = col("Stock", 80);
        colStock.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getStock())));

        table.getColumns().addAll(colTitle, colType, colGenre, colPrice, colStock);

        Label placeholder = new Label("Selecciona una plataforma para ver los juegos disponibles.");
        placeholder.setTextFill(Color.web("#555555"));
        table.setPlaceholder(placeholder);

        getChildren().addAll(heading, searchRow, table);
    }

    private void search(String platform) {
        List<VideoGame> results = service.findByPlatform(platform);
        table.setItems(FXCollections.observableArrayList(results));
        if (results.isEmpty()) {
            Label empty = new Label("No hay juegos para la plataforma: " + platform);
            empty.setTextFill(Color.web("#888888"));
            table.setPlaceholder(empty);
        }
    }

    private TableColumn<VideoGame, String> col(String name, int w) {
        TableColumn<VideoGame, String> c = new TableColumn<>(name);
        c.setPrefWidth(w);
        return c;
    }
}
