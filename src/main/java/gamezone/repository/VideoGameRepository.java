package gamezone.repository;

import gamezone.model.DigitalVideoGame;
import gamezone.model.PhysicalVideoGame;
import gamezone.model.VideoGame;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class VideoGameRepository {

    private static final String FILE_PATH = "data/videogames.json";

    public VideoGameRepository() {
        try {
            Files.createDirectories(Paths.get("data"));
            File f = new File(FILE_PATH);
            if (!f.exists()) {
                f.createNewFile();
                writeJson("[]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(VideoGame game) {
        List<VideoGame> all = findAll();
        for (VideoGame g : all) {
            if (g.getTitle().equalsIgnoreCase(game.getTitle())) {
                throw new IllegalArgumentException("El videojuego ya existe en el catálogo");
            }
        }
        all.add(game);
        persistAll(all);
    }

    public List<VideoGame> findAll() {
        String json = readJson();
        return parseJson(json);
    }

    public VideoGame findByTitle(String title) {
        for (VideoGame g : findAll()) {
            if (g.getTitle().equalsIgnoreCase(title)) return g;
        }
        return null;
    }

    public List<VideoGame> findByPlatform(String platform) {
        List<VideoGame> result = new ArrayList<>();
        for (VideoGame g : findAll()) {
            if (g.getPlatform().equalsIgnoreCase(platform)) result.add(g);
        }
        return result;
    }

    public boolean update(String title, VideoGame updated) {
        List<VideoGame> all = findAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getTitle().equalsIgnoreCase(title)) {
                all.set(i, updated);
                persistAll(all);
                return true;
            }
        }
        return false;
    }

    public boolean delete(String title) {
        List<VideoGame> all = findAll();
        boolean removed = all.removeIf(g -> g.getTitle().equalsIgnoreCase(title));
        if (removed) persistAll(all);
        return removed;
    }

    private String readJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(FILE_PATH));
            String content = new String(bytes).trim();
            return content.isEmpty() ? "[]" : content;
        } catch (IOException e) {
            return "[]";
        }
    }

    private void writeJson(String json) {
        try (FileWriter fw = new FileWriter(FILE_PATH)) {
            fw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void persistAll(List<VideoGame> games) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < games.size(); i++) {
            sb.append(toJson(games.get(i)));
            if (i < games.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeJson(sb.toString());
    }

    private String toJson(VideoGame g) {
        if (g instanceof DigitalVideoGame dg) {
            return "  {\"type\":\"digital\",\"title\":\"" + esc(dg.getTitle()) + "\",\"price\":" + dg.getPrice() +
                   ",\"platform\":\"" + esc(dg.getPlatform()) + "\",\"stock\":" + dg.getStock() +
                   ",\"genre\":\"" + esc(dg.getGenre()) + "\",\"sizeGB\":" + dg.getSizeGB() +
                   ",\"downloadPlatform\":\"" + esc(dg.getDownloadPlatform()) + "\"}";
        } else if (g instanceof PhysicalVideoGame pg) {
            return "  {\"type\":\"physical\",\"title\":\"" + esc(pg.getTitle()) + "\",\"price\":" + pg.getPrice() +
                   ",\"platform\":\"" + esc(pg.getPlatform()) + "\",\"stock\":" + pg.getStock() +
                   ",\"genre\":\"" + esc(pg.getGenre()) + "\",\"condition\":\"" + esc(pg.getCondition()) +
                   "\",\"distributor\":\"" + esc(pg.getDistributor()) + "\"}";
        }
        return "{}";
    }

    private List<VideoGame> parseJson(String json) {
        List<VideoGame> result = new ArrayList<>();
        json = json.trim();
        if (json.equals("[]") || json.isEmpty()) return result;

        json = json.substring(1, json.lastIndexOf(']')).trim();
        if (json.isEmpty()) return result;

        List<String> objects = splitObjects(json);
        for (String obj : objects) {
            obj = obj.trim();
            if (obj.isEmpty()) continue;
            String type = extractField(obj, "type");
            String title = extractField(obj, "title");
            double price = Double.parseDouble(extractField(obj, "price"));
            String platform = extractField(obj, "platform");
            int stock = Integer.parseInt(extractField(obj, "stock"));
            String genre = extractField(obj, "genre");

            if ("digital".equals(type)) {
                double sizeGB = Double.parseDouble(extractField(obj, "sizeGB"));
                String dlp = extractField(obj, "downloadPlatform");
                result.add(new DigitalVideoGame(title, price, platform, stock, genre, sizeGB, dlp));
            } else if ("physical".equals(type)) {
                String condition = extractField(obj, "condition");
                String distributor = extractField(obj, "distributor");
                result.add(new PhysicalVideoGame(title, price, platform, stock, genre, condition, distributor));
            }
        }
        return result;
    }

    private List<String> splitObjects(String json) {
        List<String> objs = new ArrayList<>();
        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    objs.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objs;
    }

    private String extractField(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int from = idx + search.length();
        char first = json.charAt(from);
        if (first == '"') {
            int end = json.indexOf('"', from + 1);
            return json.substring(from + 1, end);
        } else {
            int end = from;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(from, end).trim();
        }
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
