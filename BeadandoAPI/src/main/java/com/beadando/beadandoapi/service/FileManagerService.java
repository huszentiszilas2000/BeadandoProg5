package com.beadando.beadandoapi.service;

import com.beadando.beadandoapi.config.FileManagerConfig;
import com.beadando.beadandoapi.db.FileInfoRepository;
import com.beadando.beadandoapi.dto.FileInfoDTO;
import com.beadando.beadandoapi.model.FileInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FileManagerService {
    @Autowired
    FileManagerConfig fileManagerConfig;

    private final FileInfoRepository fileInfoRepository;


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
            throw new RuntimeException("Could not initialize folder for user!");
        }
    }

    public void save(MultipartFile file, String userId) {
        Path path = getUserPathOrCreate(userId);
        try {
            Files.copy(file.getInputStream(), path.resolve(file.getOriginalFilename()));
            fileInfoRepository.save(new FileInfo(userId, file.getOriginalFilename(), Timestamp.from(Instant.now())));
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
                throw new RuntimeException("Can't read the file!");
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

    @Transactional
    public boolean delete(String filename, String userId) {
        Path userPath = getUserPathOrCreate(userId);
        this.fileInfoRepository.deleteByUseridAndFilename(userId, filename);
        return new File(userPath.toAbsolutePath() + "/" + filename).delete();
    }

    public List<FileInfoDTO> loadAll(String userId) {
        if( userId.isEmpty())
            return null;

        List<FileInfo> fileInfos = this.fileInfoRepository.findAllByUserid(userId);
        return fileInfos.stream()
                .map(entity -> new FileInfoDTO(entity.getFilename(), entity.getDate_added()))
                .collect(Collectors.toList());
    }
}
