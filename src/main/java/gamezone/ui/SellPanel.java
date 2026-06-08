package gamezone.ui;

import gamezone.model.Sale;
import gamezone.model.VideoGame;
import gamezone.service.SaleService;
import gamezone.service.VideoGameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;

public class SellPanel extends VBox {

    private final SaleService saleService;
    private final VideoGameService gameService;
    private final Stage stage;

    public SellPanel(SaleService saleService, VideoGameService gameService, Stage stage) {
        this.saleService = saleService;
        this.gameService = gameService;
        this.stage = stage;
        buildUI();
    }

    private void buildUI() {
        setSpacing(0);
        setPadding(new Insets(30, 40, 30, 40));
        setStyle("-fx-background-color: #0f0f1a;");

        Label heading = new Label("Realizar Venta");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web("#b06aff"));
        heading.setPadding(new Insets(0, 0, 20, 0));

        VBox form = new VBox(16);
        form.setMaxWidth(560);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12;");

        Label gameLabel = new Label("Selecciona el videojuego");
        styleLabel(gameLabel);

        List<VideoGame> games = gameService.getAllVideoGames();
        ComboBox<String> gameBox = new ComboBox<>();
        for (VideoGame g : games) gameBox.getItems().add(g.getTitle() + "  [Stock: " + g.getStock() + "]  $" + String.format("%,.0f", g.calculateFinalPrice()));
        gameBox.setPromptText("Selecciona un juego...");
        gameBox.setStyle("-fx-background-color: #0f0f1a; -fx-text-fill: #cccccc; " +
                         "-fx-border-color: #2a2a4e; -fx-border-radius: 6; -fx-background-radius: 6;");
        gameBox.setMaxWidth(Double.MAX_VALUE);

        Label previewLabel = new Label("");
        previewLabel.setFont(Font.font("Arial", 12));
        previewLabel.setTextFill(Color.web("#888888"));

        gameBox.setOnAction(e -> {
            int idx = gameBox.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < games.size()) {
                VideoGame g = games.get(idx);
                previewLabel.setText("Precio final: $" + String.format("%,.0f", g.calculateFinalPrice()) +
                        "  |  Stock disponible: " + g.getStock());
            }
        });

        Label qtyLabel = new Label("Cantidad");
        styleLabel(qtyLabel);
        TextField qtyField = styledField("Cantidad a vender");

        Label clientLabel = new Label("Nombre del cliente (opcional)");
        styleLabel(clientLabel);
        TextField clientField = styledField("Nombre del cliente");

        Button sellBtn = new Button("Confirmar Venta");
        sellBtn.setStyle("-fx-background-color: #1a7a3c; -fx-text-fill: white; " +
                         "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                         "-fx-padding: 10 24 10 24; -fx-cursor: hand;");
        sellBtn.setOnAction(e -> {
            int idx = gameBox.getSelectionModel().getSelectedIndex();
            if (idx < 0) {
                showAlert(Alert.AlertType.WARNING, "Sin selección", "Selecciona un videojuego primero.");
                return;
            }
            String title = games.get(idx).getTitle();
            try {
                int qty = Integer.parseInt(qtyField.getText().trim());
                if (qty <= 0) throw new NumberFormatException();

                Sale sale = saleService.sellVideoGame(title, qty);
                showReceipt(sale, clientField.getText().trim());
                qtyField.clear();
                clientField.clear();
                gameBox.getSelectionModel().clearSelection();
                previewLabel.setText("");
                refreshGameBox(gameBox, games);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Ingresa una cantidad válida mayor a 0.");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No se pudo realizar la venta");
                alert.setHeaderText("Error en la venta");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        form.getChildren().addAll(
            gameLabel, gameBox, previewLabel,
            qtyLabel, qtyField,
            clientLabel, clientField,
            sellBtn
        );

        getChildren().addAll(heading, form);
    }

    private void refreshGameBox(ComboBox<String> gameBox, List<VideoGame> games) {
        gameBox.getItems().clear();
        List<VideoGame> updated = gameService.getAllVideoGames();
        games.clear();
        games.addAll(updated);
        for (VideoGame g : games)
            gameBox.getItems().add(g.getTitle() + "  [Stock: " + g.getStock() + "]  $" + String.format("%,.0f", g.calculateFinalPrice()));
    }

    private void showReceipt(Sale sale, String client) {
        Alert receipt = new Alert(Alert.AlertType.INFORMATION);
        receipt.setTitle("Venta Exitosa");
        receipt.setHeaderText("¡Venta realizada correctamente!");

        String content = "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "RECIBO DE VENTA\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "ID:         " + sale.getId() + "\n" +
                "Juego:      " + sale.getVideoGame().getTitle() + "\n" +
                (client.isEmpty() ? "" : "Cliente:    " + client + "\n") +
                "Cantidad:   " + sale.getQuantity() + "\n" +
                "Precio u.:  $" + String.format("%,.0f", sale.getUnitPrice()) + "\n" +
                "TOTAL:      $" + String.format("%,.0f", sale.getTotal()) + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━";

        receipt.setContentText(content);
        receipt.showAndWait();
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #0f0f1a; -fx-text-fill: #cccccc; " +
                    "-fx-prompt-text-fill: #555555; -fx-border-color: #2a2a4e; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        return tf;
    }

    private void styleLabel(Label l) {
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#888888"));
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
