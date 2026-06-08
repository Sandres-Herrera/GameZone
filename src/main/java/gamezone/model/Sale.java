package gamezone.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale {
    private String id;
    private VideoGame videoGame;
    private int quantity;
    private double unitPrice;
    private double total;
    private LocalDateTime saleDate;

    public Sale(String id, VideoGame videoGame, int quantity, double unitPrice) {
        this.id = id;
        this.videoGame = videoGame;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = unitPrice * quantity;
        this.saleDate = LocalDateTime.now();
    }

    public String getId() { return id; }
    public VideoGame getVideoGame() { return videoGame; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getTotal() { return total; }
    public LocalDateTime getSaleDate() { return saleDate; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Sale{id='" + id + "', game='" + videoGame.getTitle() +
               "', qty=" + quantity + ", unitPrice=" + unitPrice +
               ", total=" + total + ", date=" + saleDate.format(fmt) + "}";
    }
}
