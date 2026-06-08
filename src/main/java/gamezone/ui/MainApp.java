package gamezone.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import gamezone.repository.SaleRepository;
import gamezone.repository.VideoGameRepository;
import gamezone.service.SaleService;
import gamezone.service.VideoGameService;

public class MainApp extends Application {

    private VideoGameRepository gameRepo;
    private SaleRepository saleRepo;
    private VideoGameService gameService;
    private SaleService saleService;

    private Stage primaryStage;
    private BorderPane root;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        gameRepo = new VideoGameRepository();
        saleRepo = new SaleRepository(gameRepo);
        gameService = new VideoGameService(gameRepo);
        saleService = new SaleService(gameRepo, saleRepo);

        root = new BorderPane();
        root.setStyle("-fx-background-color: #0f0f1a;");

        root.setTop(buildHeader());
        root.setLeft(buildSidebar());
        showPanel(new HomePanel());

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("GameZone - Sistema de Gestión");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #6c2dc7; -fx-border-width: 0 0 2 0;");
        header.setPadding(new Insets(12, 24, 12, 24));
        header.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label("🎮 GameZone");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        logo.setTextFill(Color.web("#b06aff"));

        Label sub = new Label("  |  Sistema de Gestión de Videojuegos");
        sub.setFont(Font.font("Arial", 13));
        sub.setTextFill(Color.web("#888888"));

        header.getChildren().addAll(logo, sub);
        return header;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(210);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setStyle("-fx-background-color: #16213e;");

        Label menuLabel = new Label("MENÚ PRINCIPAL");
        menuLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        menuLabel.setTextFill(Color.web("#666666"));
        menuLabel.setPadding(new Insets(0, 0, 10, 8));

        sidebar.getChildren().add(menuLabel);
        sidebar.getChildren().add(menuBtn("🏠  Inicio", () -> showPanel(new HomePanel())));
        sidebar.getChildren().add(menuBtn("➕  Agregar Videojuego", () -> showPanel(new AddGamePanel(gameService, primaryStage))));
        sidebar.getChildren().add(menuBtn("📋  Listar Videojuegos", () -> showPanel(new ListGamesPanel(gameService, primaryStage, this))));
        sidebar.getChildren().add(menuBtn("🔍  Buscar por Título", () -> showPanel(new SearchTitlePanel(gameService))));
        sidebar.getChildren().add(menuBtn("🎮  Buscar por Plataforma", () -> showPanel(new SearchPlatformPanel(gameService))));
        sidebar.getChildren().add(menuBtn("💰  Realizar Venta", () -> showPanel(new SellPanel(saleService, gameService, primaryStage))));
        sidebar.getChildren().add(menuBtn("📊  Ver Ventas", () -> showPanel(new SalesPanel(saleService))));

        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        sidebar.getChildren().add(menuBtn("Salir", () -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Salir");
            alert.setHeaderText("¿Deseas salir de GameZone?");
            alert.showAndWait().ifPresent(r -> { if (r == ButtonType.OK) primaryStage.close(); });
        }));

        return sidebar;
    }

    private Button menuBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10, 14, 10, 14));
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cccccc; " +
                     "-fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 6;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #2a1f5e; -fx-text-fill: #b06aff; " +
                "-fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #cccccc; " +
                "-fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 6;"));
        btn.setOnAction(e -> action.run());
        return btn;
    }

    public void showPanel(javafx.scene.Node panel) {
        root.setCenter(panel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
