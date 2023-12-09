package com.beadando.beadandoapi.service;

import com.beadando.beadandoapi.config.FileManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileManagerService {
    @Autowired
    FileManagerConfig fileManagerConfig;

    public void initRoot() {
        try {
            Files.createDirectories(fileManagerConfig.getRootpath());
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public Path getUserPathOrCreate(String userId) {
        try {
            Path path = Paths.get(fileManagerConfig.getRootpath() + "/" + userId);
            Files.createDirectories(path);
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public void save(MultipartFile file, String userId) {
        Path path = getUserPathOrCreate(userId);
        try {
            Files.copy(file.getInputStream(), path.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
    }

    public Resource load(String filename, String userId) {
        Path userPath = getUserPathOrCreate(userId);
        try {
            Path file = userPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public String getMimeType(String filename, String userId)
    {
        Path path = Paths.get(getUserPathOrCreate(userId) + "/" + filename);
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String filename, String userId) {
        Path userPath = getUserPathOrCreate(userId);
        String asd = userPath.toAbsolutePath().toString();
        return new File(userPath.toAbsolutePath() + "/" + filename).delete();
    }

    public Stream<Path> loadAll(String userId) {
        Path userPath = getUserPathOrCreate(userId);
        try {
            return Files.walk(userPath, 1).filter(path -> !path.equals(userPath)).map(userPath::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
