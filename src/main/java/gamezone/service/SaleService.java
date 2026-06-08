package gamezone.service;

import gamezone.model.Sale;
import gamezone.model.VideoGame;
import gamezone.repository.SaleRepository;
import gamezone.repository.VideoGameRepository;

import java.util.List;
import java.util.UUID;

public class SaleService {

    private final VideoGameRepository gameRepository;
    private final SaleRepository saleRepository;

    public SaleService(VideoGameRepository gameRepository, SaleRepository saleRepository) {
        this.gameRepository = gameRepository;
        this.saleRepository = saleRepository;
    }

    public Sale sellVideoGame(String title, int quantity) {
        VideoGame game = gameRepository.findByTitle(title);
        if (game == null) {
            throw new IllegalArgumentException("El videojuego '" + title + "' no existe en el catálogo.");
        }
        if (game.getStock() < quantity) {
            throw new IllegalStateException("Stock insuficiente. Disponible: " + game.getStock() + ", solicitado: " + quantity);
        }

        double unitPrice = game.calculateFinalPrice();
        double total = game.sell(quantity);

        gameRepository.update(title, game);

        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Sale sale = new Sale(id, game, quantity, unitPrice);
        saleRepository.save(sale);
        return sale;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
}
