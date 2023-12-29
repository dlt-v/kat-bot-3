package com.katbot.commands.Zabawa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class ZabawaUrlLoader {
    private static final Logger logger = LoggerFactory.getLogger(ZabawaUrlLoader.class);
    public static List<String> zabawaArray = null;

    public ZabawaUrlLoader() {
        zabawaArray = loadZabawaUrls();
    }

    private List<String> loadZabawaUrls() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/zabawaUrls.json")) {
            logger.info("Loaded zabawa URLs.");
            return mapper.readValue(inputStream,
                    TypeFactory.defaultInstance()
                            .constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load zabawa URLs", e);
        }
    }
    public String getRandomMemeUrl() {
        Random random = new Random();
        return zabawaArray.get(random.nextInt(zabawaArray.size()));
    }
}
