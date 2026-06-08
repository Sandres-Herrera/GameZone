package gamezone.service;

import gamezone.model.VideoGame;
import gamezone.repository.VideoGameRepository;

import java.util.List;

public class VideoGameService {

    private final VideoGameRepository repository;

    public VideoGameService(VideoGameRepository repository) {
        this.repository = repository;
    }

    public void addVideoGame(VideoGame game) {
        if (game.getTitle() == null || game.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede ser nulo o vacío.");
        }
        if (game.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (game.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        repository.save(game);
    }

    public List<VideoGame> getAllVideoGames() {
        return repository.findAll();
    }

    public VideoGame findByTitle(String title) {
        return repository.findByTitle(title);
    }

    public List<VideoGame> findByPlatform(String platform) {
        return repository.findByPlatform(platform);
    }

    public boolean updateVideoGame(String title, VideoGame updated) {
        return repository.update(title, updated);
    }

    public boolean deleteVideoGame(String title) {
        return repository.delete(title);
    }
}
