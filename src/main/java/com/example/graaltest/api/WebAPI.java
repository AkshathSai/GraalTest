package com.example.graaltest.api;

import com.example.graaltest.model.Music;
import com.example.graaltest.storage.Storage;
import com.example.graaltest.util.Stats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.nio.ByteBuffer;

//import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.example.graaltest.model.ByteBufferBackedInputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebAPI {

    /*@GetMapping("/")
    public String index() {
        return "Graal VM: Hello World!";
    }*/

    private final Storage storage;

    //@SuppressWarnings("java:S1215")
    @PostMapping("/music")
    String saveMovie(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) throws IOException {
        //by default this way of reading a MultipartFile is limited by int size. If case of very large files, tou may face "Required array length is too large" error
        storage.put(name, file);
        log.info(Stats.getMemoryInformation(storage.getTotalNoHeapMemoryUsage()));
        return "Video saved successfully.";
    }

    @GetMapping("/music/{name}")
    ResponseEntity<Resource> getMovieByName(@PathVariable String name) {
        return storage.pull(name)
                .map(Music::movieByteBuffer)
                .map(ByteBuffer::slice)
                .map(ByteBufferBackedInputStream::new)
                .map(InputStreamResource::new)
                .<ResponseEntity<Resource>>map(resource ->
                        ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(resource))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/music/titles")
    Set<String> getMovieNames(){
        return storage.getMovieNames();
    }

    @DeleteMapping("/music/{name}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String name) {
        boolean isDeleted = storage.delete(name);

        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
