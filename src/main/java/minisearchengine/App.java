package minisearchengine;

import static spark.Spark.*;
import java.nio.file.Paths;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        port(getPort());
        staticFiles.location("/public"); // serves /public/index.html from resources

        // ---- build the engine from the bundled test.xml ----
        String xmlPath = Paths.get(
                Objects.requireNonNull(App.class.getResource("/test.xml")).toURI()
        ).toString();

        SearchEngine engine = new SearchEngine(xmlPath);

        // crawl from the root page in your XML (siteA exists in test.xml)
        for (String start : List.of(
                "www.steamcommunity.com", "www.ubisoft.com", "www.netflix.com",
                "www.spotify.com", "www.crunchyroll.com", "www.marvel.com",
                "www.dc.com", "www.youtube.com", "www.imdb.com",
                "www.epicgames.com", "www.espn.com"
        )) {
            try { engine.crawlAndIndex(start); } catch (Exception ignore) {}
        }
        engine.assignPageRanks(1e-3);





        // compute page ranks once; pick a small epsilon (tolerance)


        // ---- search endpoint ----
        get("/search", (req, res) -> {
            String q = Optional.ofNullable(req.queryParams("q")).orElse("").trim().toLowerCase();
            List<String> results = q.isEmpty() ? List.of() : engine.getResults(q);
            res.type("application/json");
            return toJson(results);
        });
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
