package gamezone.ui;

import gamezone.model.DigitalVideoGame;
import gamezone.model.PhysicalVideoGame;
import gamezone.service.VideoGameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class AddGamePanel extends VBox {

    private final VideoGameService service;
    private final Stage stage;

    private TextField titleField, priceField, stockField, genreField;
    private ComboBox<String> platformBox, typeBox;
    private TextField sizeGBField, downloadPlatformField;
    private TextField conditionField, distributorField;
    private VBox extraFields;

    public AddGamePanel(VideoGameService service, Stage stage) {
        this.service = service;
        this.stage = stage;
        buildUI();
    }

    private void buildUI() {
        setSpacing(0);
        setPadding(new Insets(30, 40, 30, 40));
        setStyle("-fx-background-color: #0f0f1a;");

        Label title = new Label("Agregar Videojuego");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#b06aff"));
        title.setPadding(new Insets(0, 0, 20, 0));

        VBox form = new VBox(14);
        form.setMaxWidth(600);
        form.setPadding(new Insets(24));
        form.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12;");

        Label typeLabel = new Label("Tipo de Videojuego");
        styleLabel(typeLabel);
        typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Digital", "Físico");
        typeBox.setValue("Digital");
        styleCombo(typeBox);
        typeBox.setOnAction(e -> updateExtraFields());

        titleField = styledField("Título del juego");
        priceField = styledField("Precio (ej: 59900)");
        stockField = styledField("Stock disponible");
        genreField = styledField("Género (Acción, RPG, etc.)");

        platformBox = new ComboBox<>();
        platformBox.getItems().addAll("PC", "PlayStation 5", "PlayStation 4", "Xbox Series X",
                "Xbox One", "Nintendo Switch", "Mobile", "Cross-Platform");
        platformBox.setValue("PC");
        styleCombo(platformBox);
        Label platLabel = new Label("Plataforma");
        styleLabel(platLabel);

        extraFields = new VBox(14);
        buildDigitalFields();

        Button saveBtn = new Button("Guardar Videojuego");
        saveBtn.setStyle("-fx-background-color: #6c2dc7; -fx-text-fill: white; " +
                         "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                         "-fx-padding: 10 24 10 24; -fx-cursor: hand;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #8a3ff0; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24 10 24; -fx-cursor: hand;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #6c2dc7; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24 10 24; -fx-cursor: hand;"));
        saveBtn.setOnAction(e -> handleSave());

        form.getChildren().addAll(
            row(typeLabel, typeBox),
            labeled("Título", titleField),
            labeled("Precio ($)", priceField),
            labeled("Stock", stockField),
            labeled("Género", genreField),
            row(platLabel, platformBox),
            extraFields,
            saveBtn
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(title, scroll);
    }

    private void buildDigitalFields() {
        extraFields.getChildren().clear();
        sizeGBField = styledField("Tamaño en GB (ej: 45.5)");
        downloadPlatformField = styledField("Plataforma de descarga (Steam, Epic, etc.)");
        extraFields.getChildren().addAll(
            labeled("Tamaño (GB)", sizeGBField),
            labeled("Plataforma de descarga", downloadPlatformField)
        );
    }

    private void buildPhysicalFields() {
        extraFields.getChildren().clear();
        conditionField = styledField("nuevo / usado");
        distributorField = styledField("Nombre del distribuidor");
        extraFields.getChildren().addAll(
            labeled("Condición", conditionField),
            labeled("Distribuidor", distributorField)
        );
    }

    private void updateExtraFields() {
        if ("Digital".equals(typeBox.getValue())) buildDigitalFields();
        else buildPhysicalFields();
    }

    private void handleSave() {
        try {
            String t = titleField.getText().trim();
            double p = Double.parseDouble(priceField.getText().trim());
            int s = Integer.parseInt(stockField.getText().trim());
            String g = genreField.getText().trim();
            String pl = platformBox.getValue();

            if ("Digital".equals(typeBox.getValue())) {
                double size = Double.parseDouble(sizeGBField.getText().trim());
                String dlp = downloadPlatformField.getText().trim();
                service.addVideoGame(new DigitalVideoGame(t, p, pl, s, g, size, dlp));
            } else {
                String cond = conditionField.getText().trim();
                String dist = distributorField.getText().trim();
                service.addVideoGame(new PhysicalVideoGame(t, p, pl, s, g, cond, dist));
            }

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Videojuego agregado correctamente al catálogo.");
            clearFields();

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor ingresa valores numéricos válidos en los campos de precio, stock y tamaño.");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.WARNING, "Error de validación", ex.getMessage());
        }
    }

    private void clearFields() {
        titleField.clear(); priceField.clear(); stockField.clear();
        genreField.clear();
        if (sizeGBField != null) sizeGBField.clear();
        if (downloadPlatformField != null) downloadPlatformField.clear();
        if (conditionField != null) conditionField.clear();
        if (distributorField != null) distributorField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        alert.getDialogPane().setStyle("-fx-background-color: #1a1a2e;");
        alert.getDialogPane().lookup(".content.label")
             .setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");
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
        cb.setPrefWidth(200);
    }

    private void styleLabel(Label l) {
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#888888"));
    }

    private HBox row(Label label, Control ctrl) {
        HBox hb = new HBox(10, label, ctrl);
        hb.setAlignment(Pos.CENTER_LEFT);
        return hb;
    }

    private VBox labeled(String labelText, TextField field) {
        Label l = new Label(labelText);
        styleLabel(l);
        field.setMaxWidth(Double.MAX_VALUE);
        VBox vb = new VBox(4, l, field);
        return vb;
    }
}
