package br.com.heraoliveira.hcommerce.repository;

import br.com.heraoliveira.hcommerce.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

abstract class AbstractJsonRepository<T> {

    private final Path filePath;
    private final TypeReference<List<T>> listTypeReference;
    private final String loadErrorMessage;
    private final String saveErrorMessage;
    private final List<T> entities;

    protected AbstractJsonRepository(
            Path filePath,
            TypeReference<List<T>> listTypeReference,
            String loadErrorMessage,
            String saveErrorMessage
    ) {
        this.filePath = Objects.requireNonNull(filePath, "Repository file path cannot be null.");
        this.listTypeReference = listTypeReference;
        this.loadErrorMessage = loadErrorMessage;
        this.saveErrorMessage = saveErrorMessage;
        this.entities = loadFromFile();
    }

    public long nextId() {
        return entities.stream()
                .mapToLong(this::getEntityId)
                .max()
                .orElse(0L) + 1;
    }

    public void save(T entity) {
        validateEntity(entity);

        int existingIndex = findIndexById(getEntityId(entity));

        if (existingIndex >= 0) {
            entities.set(existingIndex, entity);
        } else {
            entities.add(entity);
        }

        writeToFile();
    }

    public List<T> findAll() {
        return List.copyOf(entities);
    }

    protected Optional<T> findByIdInternal(long id) {
        return entities.stream()
                .filter(entity -> getEntityId(entity) == id)
                .findFirst();
    }

    protected boolean removeByIdInternal(long id) {
        int existingIndex = findIndexById(id);

        if (existingIndex < 0) {
            return false;
        }

        entities.remove(existingIndex);
        writeToFile();
        return true;
    }

    protected abstract void validateEntity(T entity);

    protected abstract long getEntityId(T entity);

    private int findIndexById(long id) {
        for (int i = 0; i < entities.size(); i++) {
            if (getEntityId(entities.get(i)) == id) {
                return i;
            }
        }
        return -1;
    }

    private List<T> loadFromFile() {
        try {
            createStorageIfNeeded();

            String content = Files.readString(filePath).strip();

            if (content.isEmpty()) {
                Files.writeString(filePath, "[]");
                return new ArrayList<>();
            }

            List<T> loadedEntities = JsonUtil.MAPPER.readValue(
                    filePath.toFile(),
                    listTypeReference
            );

            return new ArrayList<>(loadedEntities);
        } catch (IOException e) {
            throw new IllegalStateException(loadErrorMessage, e);
        }
    }

    private void writeToFile() {
        try {
            createStorageIfNeeded();
            JsonUtil.MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), entities);
        } catch (IOException e) {
            throw new IllegalStateException(saveErrorMessage, e);
        }
    }

    private void createStorageIfNeeded() throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (Files.notExists(filePath)) {
            Files.writeString(filePath, "[]");
        }
    }
}
