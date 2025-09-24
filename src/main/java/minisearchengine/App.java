package minisearchengine;

import static spark.Spark.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class App {
    public static void main(String[] args) throws Exception {
        port(getPort());
        staticFiles.location("/public"); // serves /public/index.html

        // simple health check (optional but handy on Render)
        get("/health", (req, res) -> "OK");

        // Load test.xml from classpath as a stream and copy to a temp file
        try (var is = Objects.requireNonNull(App.class.getResourceAsStream("/test.xml"))) {
            var tmp = Files.createTempFile("dataset", ".xml");
            Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);

            SearchEngine engine = new SearchEngine(tmp.toString());

            // crawl the real roots in your dataset
            for (String start : List.of(
                    "www.steamcommunity.com","www.ubisoft.com","www.netflix.com",
                    "www.spotify.com","www.crunchyroll.com","www.marvel.com",
                    "www.dc.com","www.youtube.com","www.imdb.com",
                    "www.epicgames.com","www.espn.com"
            )) {
                try { engine.crawlAndIndex(start); } catch (Exception ignore) {}
            }
            engine.assignPageRanks(1e-3);

            // search endpoint
            get("/search", (req, res) -> {
                String q = Optional.ofNullable(req.queryParams("q")).orElse("").trim().toLowerCase();
                List<String> results = q.isEmpty() ? List.of() : engine.getResults(q);
                res.type("application/json");
                return toJson(results);
            });
        }
    }

    private static int getPort() {
        String p = System.getenv("PORT");
        return p != null ? Integer.parseInt(p) : 8080;
    }

    private static String toJson(List<String> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append('"').append(items.get(i).replace("\"", "\\\"")).append('"');
        }
        return sb.append(']').toString();
    }
}
