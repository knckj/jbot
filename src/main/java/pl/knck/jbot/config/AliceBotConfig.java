package pl.knck.jbot.config;

import org.springframework.context.annotation.Configuration;

import org.alicebot.ab.Bot;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import org.alicebot.ab.MagicBooleans;

@Configuration
public class AliceBotConfig {

    @Bean
    public String resourcesPath() {
        return Paths.get("src/main/resources").toAbsolutePath().toString();
    }

    @Bean
    public void initializeMagicBooleans() {
        MagicBooleans.trace_mode = false; // Disable AIML tracing for better performance
    }

    @Bean
    public Map<String, Bot> bots(String resourcesPath) throws IOException {
        Path botsDir = Paths.get(resourcesPath, "bots");
        try (Stream<Path> stream = Files.list(botsDir)) {
            return stream.filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            botName -> new Bot(botName, resourcesPath),
                            (map1, map2) -> map1,
                            LinkedHashMap::new
                    ));
        }
    }
}
