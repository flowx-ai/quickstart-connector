package ai.flowx.quickstart.connector.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonResourceReader {
    private final String MOCKS_RELATIVE_PATH = "src/test/it/resources/mock";
    private final ObjectMapper objectMapper;

    public JsonResourceReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readObjectFromFile(String relativeLocation, Class<T> clazz) throws IOException {
        return this.readObjectFromFile(Paths.get(relativeLocation), clazz);
    }

    public <T> T readObjectFromFile(Path path, Class<T> clazz) throws IOException {
        String json = new String(Files.readAllBytes(path));
        T obj = objectMapper.readValue(json, clazz);

        return obj;
    }

    public <T> List<T> readObjectsFromDir(String relativeDirLocation, Class<T> clazz) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(relativeDirLocation))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try {
                            return this.readObjectFromFile(path, clazz);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    public <T> T readObjectFromMocks(String relativeToMocksLocation, Class<T> clazz) throws IOException {
        String path = (relativeToMocksLocation.startsWith("/")) ? MOCKS_RELATIVE_PATH + relativeToMocksLocation : MOCKS_RELATIVE_PATH + "/" + relativeToMocksLocation;

        return readObjectFromFile(path, clazz);
    }

    public <T> List<T> readObjectsFromMocksDir(String relativeToMocksDirLocation, Class<T> clazz) throws IOException {
        String path = (relativeToMocksDirLocation.startsWith("/")) ? MOCKS_RELATIVE_PATH + relativeToMocksDirLocation : MOCKS_RELATIVE_PATH + "/" + relativeToMocksDirLocation;

        return readObjectsFromDir(path, clazz);
    }
}
