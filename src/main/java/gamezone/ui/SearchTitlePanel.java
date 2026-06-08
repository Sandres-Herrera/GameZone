package gamezone.ui;

import gamezone.model.DigitalVideoGame;
import gamezone.model.PhysicalVideoGame;
import gamezone.model.VideoGame;
import gamezone.service.VideoGameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class SearchTitlePanel extends VBox {

    private final VideoGameService service;
    private VBox resultBox;

    public SearchTitlePanel(VideoGameService service) {
        this.service = service;
        buildUI();
    }

    private void buildUI() {
        setSpacing(20);
        setPadding(new Insets(30, 40, 30, 40));
        setStyle("-fx-background-color: #0f0f1a;");

        Label heading = new Label("Buscar por Título");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web("#b06aff"));

        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        searchRow.setMaxWidth(600);

        TextField searchField = new TextField();
        searchField.setPromptText("Escribe el título del videojuego...");
        searchField.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #cccccc; " +
                             "-fx-prompt-text-fill: #555555; -fx-border-color: #2a2a4e; " +
                             "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-font-size: 13px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button btn = new Button("Buscar");
        btn.setStyle("-fx-background-color: #6c2dc7; -fx-text-fill: white; " +
                     "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        btn.setOnAction(e -> search(searchField.getText().trim()));
        searchField.setOnAction(e -> search(searchField.getText().trim()));

        searchRow.getChildren().addAll(searchField, btn);

        resultBox = new VBox(12);
        resultBox.setMaxWidth(600);

        getChildren().addAll(heading, searchRow, resultBox);
    }

    private void search(String query) {
        resultBox.getChildren().clear();
        if (query.isEmpty()) {
            showMsg("Escribe un título para buscar.", "#888888");
            return;
        }
        VideoGame game = service.findByTitle(query);
        if (game == null) {
            showMsg("No se encontró ningún videojuego con ese título.", "#cc4444");
        } else {
            resultBox.getChildren().add(buildCard(game));
        }
    }

    private VBox buildCard(VideoGame g) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12; " +
                      "-fx-border-color: #6c2dc7; -fx-border-radius: 12; -fx-border-width: 1;");

        String type = g instanceof DigitalVideoGame ? "Digital" : "Físico";

        addRow(card, "Título:", g.getTitle());
        addRow(card, "Tipo:", type);
        addRow(card, "Plataforma:", g.getPlatform());
        addRow(card, "Género:", g.getGenre());
        addRow(card, "Precio base:", String.format("$%,.0f", g.getPrice()));
        addRow(card, "Precio final:", String.format("$%,.0f", g.calculateFinalPrice()));
        addRow(card, "Stock:", String.valueOf(g.getStock()));

        if (g instanceof DigitalVideoGame dg) {
            addRow(card, "Tamaño:", dg.getSizeGB() + " GB");
            addRow(card, "Descarga en:", dg.getDownloadPlatform());
        } else if (g instanceof PhysicalVideoGame pg) {
            addRow(card, "Condición:", pg.getCondition());
            addRow(card, "Distribuidor:", pg.getDistributor());
        }

        return card;
    }

    private void addRow(VBox card, String label, String value) {
        HBox row = new HBox(8);
        Label l = new Label(label);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#888888"));
        l.setPrefWidth(120);
        Label v = new Label(value);
        v.setFont(Font.font("Arial", 13));
        v.setTextFill(Color.web("#cccccc"));
        row.getChildren().addAll(l, v);
        card.getChildren().add(row);
    }

    private void showMsg(String msg, String color) {
        Label l = new Label(msg);
        l.setFont(Font.font("Arial", 14));
        l.setTextFill(Color.web(color));
        resultBox.getChildren().add(l);
    }
}
