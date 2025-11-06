package de.leycm.jacc.config;

import de.leycm.flux.handler.Handler;
import de.leycm.flux.handler.HandlerList;
import de.leycm.flux.handler.HandlerPriority;
import de.leycm.jacc.event.LogEvent;
import de.leycm.vault.Config;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class ConfigHandlerList implements HandlerList {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM:dd:yyyy").withZone(ZoneId.systemDefault());

    private final Config config;

    /** Caches für die Patterns */
    private final Map<String, List<CompiledFilter>> consoleFilters = new HashMap<>();
    private final Map<String, List<CompiledFilter>> monitorFilters = new HashMap<>();

    public ConfigHandlerList(Config config) {
        this.config = config;
        loadFilters();
    }

    /** Lädt die Filter einmal und kompiliert die Patterns */
    private void loadFilters() {
        loadFilter("console.formatting.filter.filters", "console.formatting.filter.type", consoleFilters);
        loadFilter("console.monitor.filter.filters", "console.monitor.filter.type", monitorFilters);
    }

    @SuppressWarnings("unchecked")
    private void loadFilter(String filterPath, String typePath, Map<String, List<CompiledFilter>> cache) {
        List<?> filters = config.getOr(filterPath, List.class, List.of());
        String filterType = config.getOr(typePath, String.class, "blacklist");

        List<CompiledFilter> compiled = new ArrayList<>();
        for (Object obj : filters) {
            if (!(obj instanceof Map<?, ?> map)) continue;

            Object scopeObj = map.get("scope");
            Object regexObj = map.get("regex");
            if (scopeObj == null || regexObj == null) continue;

            compiled.add(new CompiledFilter(scopeObj.toString(), Pattern.compile(regexObj.toString())));
        }
        cache.put(filterType.toLowerCase(), compiled);
    }

    @Handler(priority = HandlerPriority.EARLY)
    public void onConsoleFormat(LogEvent e) {
        String filterType = config.getOr("console.formatting.filter.type", String.class, "blacklist");
        if (applyCompiledFilters(e, consoleFilters.getOrDefault(filterType.toLowerCase(), List.of()), filterType)) {
            String format = config.getOr("console.formatting.format", String.class, "[%timestamp%](%profile.prefix%): %message%");
            String line = format
                    .replace("%timestamp%", TIME_FORMATTER.format(e.timestamp()))
                    .replace("%daystamp%", DATE_FORMATTER.format(e.timestamp()))
                    .replace("%message%", e.getRecord().getMessage())
                    .replace("%profile.prefix%", e.getRecord().getProfile().simpleName())
                    .replace("%profile.class%", e.getRecord().getProfile().id())
                    .replace("%type.type%", e.getRecord().getType() != null ? e.getRecord().getType().type() : "null")
                    .replace("%type.desc%", e.getRecord().getType() != null ? e.getRecord().getType().desc() : "null");

            System.out.println(line);
        }
    }

    @Handler(priority = HandlerPriority.EARLY)
    public void onConsoleMonitor(LogEvent e) {
        String filterType = config.getOr("console.monitor.filter.type", String.class, "whitelist");
        if (applyCompiledFilters(e, monitorFilters.getOrDefault(filterType.toLowerCase(), List.of()), filterType)) {
            @SuppressWarnings("unchecked")
            List<String> webhooks = config.getOr("console.monitor.discord.webhooks", List.class, List.of());
            for (String webhook : webhooks) {
                sendToDiscord(webhook, e.getRecord().getFormattedMessage());
            }
        }
    }

    private boolean applyCompiledFilters(LogEvent e, @NonNull List<CompiledFilter> compiledFilters, String filterType) {
        if (compiledFilters.isEmpty()) return true;

        boolean matched = false;
        for (CompiledFilter cf : compiledFilters) {
            String target = switch (cf.scope) {
                case "message" -> e.getRecord().getMessage();
                case "profile.prefix" -> e.getRecord().getProfile().simpleName();
                case "type.type" -> e.getRecord().getType() != null ? e.getRecord().getType().type() : "";
                default -> "";
            };
            if (cf.pattern.matcher(target).find()) {
                matched = true;
                break;
            }
        }

        return switch (filterType.toLowerCase()) {
            case "whitelist" -> matched;
            case "blacklist" -> !matched;
            default -> true;
        };
    }

    private static class CompiledFilter {
        final String scope;
        final Pattern pattern;

        CompiledFilter(String scope, Pattern pattern) {
            this.scope = scope;
            this.pattern = pattern;
        }
    }

    public static void sendToDiscord(String webhookUrl, String message) {
        try {
            HttpURLConnection connection = getHttpURLConnection(webhookUrl, message);

            int responseCode = connection.getResponseCode();
            if (responseCode == 204) {
                System.out.println("Message sent successfully!");
            } else {
                System.out.println("Failed to send message. Response code: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static @NonNull HttpURLConnection getHttpURLConnection(String webhookUrl, @NonNull String message) throws IOException {
        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonPayload = "{\"content\": \"" + message.replace("\"", "\\\"") + "\"}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }

}
