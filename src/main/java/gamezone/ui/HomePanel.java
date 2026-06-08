package gamezone.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class HomePanel extends VBox {

    public HomePanel() {
        setAlignment(Pos.CENTER);
        setSpacing(18);
        setPadding(new Insets(60));
        setStyle("-fx-background-color: #0f0f1a;");

        Label icon = new Label("🎮");
        icon.setFont(Font.font("Arial", 72));

        Label title = new Label("Bienvenido a GameZone");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#b06aff"));

        Label sub = new Label("Sistema de Gestión de Videojuegos Digitales");
        sub.setFont(Font.font("Arial", 16));
        sub.setTextFill(Color.web("#888888"));

        Separator sep = new Separator();
        sep.setMaxWidth(400);
        sep.setStyle("-fx-background-color: #6c2dc7;");

        Label hint = new Label("Usa el menú lateral para navegar por las opciones del sistema.");
        hint.setFont(Font.font("Arial", 13));
        hint.setTextFill(Color.web("#666666"));
        hint.setWrapText(true);
        hint.setMaxWidth(500);
        hint.setTextAlignment(TextAlignment.CENTER);

        HBox cards = new HBox(16);
        cards.setAlignment(Pos.CENTER);
        cards.setPadding(new Insets(20, 0, 0, 0));
        cards.getChildren().addAll(
            card("➕", "Agregar", "Registra nuevos videojuegos"),
            card("📋", "Listar", "Ve todo el catálogo"),
            card("💰", "Vender", "Realiza ventas"),
            card("📊", "Estadísticas", "Consulta ventas")
        );

        getChildren().addAll(icon, title, sub, sep, hint, cards);
    }

    private VBox card(String emoji, String title, String desc) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(150);
        card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 12; " +
                      "-fx-border-color: #2a2a4e; -fx-border-radius: 12; -fx-border-width: 1;");

        Label e = new Label(emoji);
        e.setFont(Font.font("Arial", 28));

        Label t = new Label(title);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        t.setTextFill(Color.web("#b06aff"));

        Label d = new Label(desc);
        d.setFont(Font.font("Arial", 11));
        d.setTextFill(Color.web("#888888"));
        d.setWrapText(true);
        d.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(e, t, d);
        return card;
    }
}
