package gamezone.ui;

import gamezone.model.DigitalVideoGame;
import gamezone.model.PhysicalVideoGame;
import gamezone.model.VideoGame;
import gamezone.service.VideoGameService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;

public class ListGamesPanel extends VBox {

    private final VideoGameService service;
    private final Stage stage;
    private final MainApp mainApp;
    private TableView<VideoGame> table;

    public ListGamesPanel(VideoGameService service, Stage stage, MainApp mainApp) {
        this.service = service;
        this.stage = stage;
        this.mainApp = mainApp;
        buildUI();
    }

    private void buildUI() {
        setSpacing(14);
        setPadding(new Insets(30, 40, 20, 40));
        setStyle("-fx-background-color: #0f0f1a;");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Catálogo de Videojuegos");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#b06aff"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = actionBtn("Actualizar");
        refreshBtn.setOnAction(e -> loadTable());

        header.getChildren().addAll(title, spacer, refreshBtn);

        table = new TableView<>();
        table.setStyle("-fx-background-color: #1a1a2e; -fx-table-cell-border-color: #2a2a4e;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<VideoGame, String> colTitle = col("Título", 200);
        colTitle.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));

        TableColumn<VideoGame, String> colType = col("Tipo", 80);
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue() instanceof DigitalVideoGame ? "Digital" : "Físico"));

        TableColumn<VideoGame, String> colPlatform = col("Plataforma", 130);
        colPlatform.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPlatform()));

        TableColumn<VideoGame, String> colGenre = col("Género", 100);
        colGenre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGenre()));

        TableColumn<VideoGame, String> colPrice = col("Precio Final", 110);
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.format("$%,.0f", c.getValue().calculateFinalPrice())));

        TableColumn<VideoGame, String> colStock = col("Stock", 70);
        colStock.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getStock())));

        TableColumn<VideoGame, String> colExtra = col("Info adicional", 180);
        colExtra.setCellValueFactory(c -> {
            VideoGame g = c.getValue();
            if (g instanceof DigitalVideoGame dg)
                return new javafx.beans.property.SimpleStringProperty(dg.getSizeGB() + "GB | " + dg.getDownloadPlatform());
            else if (g instanceof PhysicalVideoGame pg)
                return new javafx.beans.property.SimpleStringProperty(pg.getCondition() + " | " + pg.getDistributor());
            return new javafx.beans.property.SimpleStringProperty("");
        });

        table.getColumns().addAll(colTitle, colType, colPlatform, colGenre, colPrice, colStock, colExtra);
        styleTable();

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button editBtn = actionBtn("Editar seleccionado");
        editBtn.setOnAction(e -> handleEdit());

        Button deleteBtn = actionBtn("Eliminar seleccionado");
        deleteBtn.setStyle("-fx-background-color: #8b1a1a; -fx-text-fill: white; " +
                          "-fx-font-size: 13px; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDelete());

        actions.getChildren().addAll(editBtn, deleteBtn);

        loadTable();
        getChildren().addAll(header, table, actions);
    }

    private void loadTable() {
        List<VideoGame> games = service.getAllVideoGames();
        table.setItems(FXCollections.observableArrayList(games));
        table.refresh();
    }

    private void handleDelete() {
        VideoGame selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Selecciona un videojuego de la tabla primero.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar '" + selected.getTitle() + "'?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                service.deleteVideoGame(selected.getTitle());
                loadTable();
                showAlert(Alert.AlertType.INFORMATION, "Eliminado", "Videojuego eliminado correctamente.");
            }
        });
    }

    private void handleEdit() {
        VideoGame selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Sin selección", "Selecciona un videojuego de la tabla primero.");
            return;
        }
        mainApp.showPanel(new EditGamePanel(service, selected, mainApp));
    }

    private TableColumn<VideoGame, String> col(String name, int prefW) {
        TableColumn<VideoGame, String> c = new TableColumn<>(name);
        c.setPrefWidth(prefW);
        c.setStyle("-fx-text-fill: black;");
        return c;
    }

    private void styleTable() {
        table.setStyle("-fx-background-color: #1a1a2e; -fx-table-cell-border-color: #2a2a4e; " +
                       "-fx-text-fill: black;");
    }

    private Button actionBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #6c2dc7; -fx-text-fill: white; " +
                     "-fx-font-size: 13px; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-cursor: hand;");
        return btn;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
