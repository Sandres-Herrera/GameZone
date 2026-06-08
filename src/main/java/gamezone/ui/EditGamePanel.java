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

public class EditGamePanel extends VBox {

    private final VideoGameService service;
    private final VideoGame original;
    private final MainApp mainApp;

    public EditGamePanel(VideoGameService service, VideoGame original, MainApp mainApp) {
        this.service = service;
        this.original = original;
        this.mainApp = mainApp;
        buildUI();
    }

    private void buildUI() {
        setSpacing(0);
        setPadding(new Insets(30, 40, 30, 40));
        setStyle("-fx-background-color: #0f0f1a;");

        Label heading = new Label("Editar: " + original.getTitle());
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setTextFill(Color.web("#b06aff"));
        heading.setPadding(new Insets(0, 0, 20, 0));

        VBox form = new VBox(14);
        form.setMaxWidth(600);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12;");

        TextField priceField = styledField("Precio");
        priceField.setText(String.valueOf(original.getPrice()));

        TextField stockField = styledField("Stock");
        stockField.setText(String.valueOf(original.getStock()));

        TextField genreField = styledField("Género");
        genreField.setText(original.getGenre());

        ComboBox<String> platformBox = new ComboBox<>();
        platformBox.getItems().addAll("PC", "PlayStation 5", "PlayStation 4", "Xbox Series X",
                "Xbox One", "Nintendo Switch", "Mobile", "Cross-Platform");
        platformBox.setValue(original.getPlatform());
        styleCombo(platformBox);

        form.getChildren().addAll(
            labeled("Precio ($)", priceField),
            labeled("Stock", stockField),
            labeled("Género", genreField),
            labeledCombo("Plataforma", platformBox)
        );

        TextField extra1 = null, extra2 = null;
        if (original instanceof DigitalVideoGame dg) {
            extra1 = styledField("Tamaño GB"); extra1.setText(String.valueOf(dg.getSizeGB()));
            extra2 = styledField("Plataforma descarga"); extra2.setText(dg.getDownloadPlatform());
            form.getChildren().addAll(labeled("Tamaño (GB)", extra1), labeled("Plataforma de descarga", extra2));
        } else if (original instanceof PhysicalVideoGame pg) {
            extra1 = styledField("Condición"); extra1.setText(pg.getCondition());
            extra2 = styledField("Distribuidor"); extra2.setText(pg.getDistributor());
            form.getChildren().addAll(labeled("Condición", extra1), labeled("Distribuidor", extra2));
        }

        final TextField fExtra1 = extra1, fExtra2 = extra2;

        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button saveBtn = new Button("Guardar cambios");
        saveBtn.setStyle("-fx-background-color: #6c2dc7; -fx-text-fill: white; " +
                         "-fx-font-size: 13px; -fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            try {
                double p = Double.parseDouble(priceField.getText().trim());
                int s = Integer.parseInt(stockField.getText().trim());
                String g = genreField.getText().trim();
                String pl = platformBox.getValue();

                VideoGame updated;
                if (original instanceof DigitalVideoGame) {
                    double size = Double.parseDouble(fExtra1.getText().trim());
                    String dlp = fExtra2.getText().trim();
                    updated = new DigitalVideoGame(original.getTitle(), p, pl, s, g, size, dlp);
                } else {
                    String cond = fExtra1.getText().trim();
                    String dist = fExtra2.getText().trim();
                    updated = new PhysicalVideoGame(original.getTitle(), p, pl, s, g, cond, dist);
                }

                service.updateVideoGame(original.getTitle(), updated);
                showAlert(Alert.AlertType.INFORMATION, "Actualizado", "Videojuego actualizado correctamente.");
                mainApp.showPanel(new ListGamesPanel(service, null, mainApp));
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Valores numéricos inválidos.");
            }
        });

        Button cancelBtn = new Button("Cancelar");
        cancelBtn.setStyle("-fx-background-color: #333; -fx-text-fill: #aaa; " +
                           "-fx-background-radius: 8; -fx-padding: 10 20 10 20; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> mainApp.showPanel(new ListGamesPanel(service, null, mainApp)));

        buttons.getChildren().addAll(saveBtn, cancelBtn);
        form.getChildren().add(buttons);

        getChildren().addAll(heading, form);
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #0f0f1a; -fx-text-fill: #cccccc; " +
                    "-fx-prompt-text-fill: #555555; -fx-border-color: #2a2a4e; " +
                    "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        return tf;
    }

    private void styleCombo(ComboBox<?> cb) {
        cb.setStyle("-fx-background-color: #0f0f1a; -fx-text-fill: #cccccc; " +
                    "-fx-border-color: #2a2a4e; -fx-border-radius: 6; -fx-background-radius: 6;");
        cb.setMaxWidth(Double.MAX_VALUE);
    }

    private VBox labeled(String labelText, TextField field) {
        Label l = new Label(labelText);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#888888"));
        field.setMaxWidth(Double.MAX_VALUE);
        return new VBox(4, l, field);
    }

    private VBox labeledCombo(String labelText, ComboBox<?> cb) {
        Label l = new Label(labelText);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#888888"));
        return new VBox(4, l, cb);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
