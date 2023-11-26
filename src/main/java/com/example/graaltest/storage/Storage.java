package com.example.graaltest.storage;

import com.example.graaltest.model.Music;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static com.example.graaltest.util.Stats.formatSize;
import static java.lang.String.format;

@Slf4j
@Component
public class Storage {

    private final Map<String, Music> movieLibrary = new HashMap<>();

    public void put(String name, MultipartFile file) throws IOException {
        var length = (int) file.getSize();
        var byteBuffer = ByteBuffer.allocateDirect(length);
        try (var channel = file.getResource().readableChannel()) {
            IOUtils.readFully(channel, byteBuffer);
        }
        byteBuffer.position(0);
        movieLibrary.put(name, new Music(byteBuffer, length));
        log.info(format("Added a new movie '%s' with size %s", name, formatSize(length)));
    }

    public Optional<Music> pull(String name) {
        return Optional.of(movieLibrary.get(name));
    }

    public Set<String> getMovieNames() {
        return movieLibrary.keySet();
    }

    public Long getTotalNoHeapMemoryUsage() {
        return movieLibrary.values().stream()
                .map(Music::size)
                .map(Long::valueOf)
                .reduce(0L, Long::sum);
    }

    public boolean delete(String name) {
        Music removedMovie = movieLibrary.remove(name);
        if (removedMovie != null) {
            log.info(format("Removed a movie '%s'", name));
            return true;
        } else {
            log.info(format("Failed to remove a movie '%s', not found", name));
            return false;
        }
    }

}