package gamezone.repository;

import gamezone.model.Sale;
import gamezone.model.VideoGame;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {

    private static final String FILE_PATH = "data/sales.json";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final VideoGameRepository gameRepo;

    public SaleRepository(VideoGameRepository gameRepo) {
        this.gameRepo = gameRepo;
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

    public void save(Sale sale) {
        List<Sale> all = findAll();
        all.add(sale);
        persistAll(all);
    }

    public List<Sale> findAll() {
        String json = readJson();
        return parseJson(json);
    }

    private String readJson() {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(FILE_PATH));
            String c = new String(bytes).trim();
            return c.isEmpty() ? "[]" : c;
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

    private void persistAll(List<Sale> sales) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < sales.size(); i++) {
            sb.append(toJson(sales.get(i)));
            if (i < sales.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeJson(sb.toString());
    }

    private String toJson(Sale s) {
        return "  {\"id\":\"" + s.getId() + "\",\"gameTitle\":\"" + esc(s.getVideoGame().getTitle()) +
               "\",\"quantity\":" + s.getQuantity() + ",\"unitPrice\":" + s.getUnitPrice() +
               ",\"total\":" + s.getTotal() + ",\"saleDate\":\"" + s.getSaleDate().format(FMT) + "\"}";
    }

    private List<Sale> parseJson(String json) {
        List<Sale> result = new ArrayList<>();
        json = json.trim();
        if (json.equals("[]") || json.isEmpty()) return result;
        json = json.substring(1, json.lastIndexOf(']')).trim();
        if (json.isEmpty()) return result;

        List<String> objects = splitObjects(json);
        for (String obj : objects) {
            if (obj.trim().isEmpty()) continue;
            String id = extractField(obj, "id");
            String gameTitle = extractField(obj, "gameTitle");
            int quantity = Integer.parseInt(extractField(obj, "quantity"));
            double unitPrice = Double.parseDouble(extractField(obj, "unitPrice"));
            double total = Double.parseDouble(extractField(obj, "total"));
            String dateStr = extractField(obj, "saleDate");

            VideoGame game = gameRepo.findByTitle(gameTitle);
            if (game == null) continue;

            Sale sale = new Sale(id, game, quantity, unitPrice);
            result.add(sale);
        }
        return result;
    }

    private List<String> splitObjects(String json) {
        List<String> objs = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') { depth--; if (depth == 0 && start != -1) { objs.add(json.substring(start, i + 1)); start = -1; } }
        }
        return objs;
    }

    private String extractField(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int from = idx + search.length();
        char first = json.charAt(from);
        if (first == '"') { int end = json.indexOf('"', from + 1); return json.substring(from + 1, end); }
        else { int end = from; while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++; return json.substring(from, end).trim(); }
    }

    private String esc(String s) { return s == null ? "" : s.replace("\"", "\\\""); }
}
