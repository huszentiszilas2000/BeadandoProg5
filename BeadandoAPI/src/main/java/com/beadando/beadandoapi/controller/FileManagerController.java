package com.beadando.beadandoapi.controller;

import com.beadando.beadandoapi.dto.FileInfoDTO;
import com.beadando.beadandoapi.jwt.CustomJwt;
import com.beadando.beadandoapi.model.FileInfo;
import com.beadando.beadandoapi.model.ResponseMessage;
import com.beadando.beadandoapi.service.FileManagerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/file")
public class FileManagerController {

    FileManagerService managerService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
            managerService.save(file, jwt.getName());
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @DeleteMapping ("/delete/{filename:.+}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String filename) {
        String message = "";
        try {
            var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
            if(managerService.delete(filename, jwt.getName())) {
                message = "Deleted the file successfully: " + filename;
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }
            else{
                message = "Could not delete the file: " + filename + ". Error: Undefined";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
            }
        } catch (Exception e) {
            message = "Could not delete the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/myfiles")
    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    public ResponseEntity<List<FileInfoDTO>> getListFiles() {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.status(HttpStatus.OK).body(managerService.loadAll(jwt.getName()));
    }

    @GetMapping("/download/{filename:.+}")
    @PreAuthorize("hasAnyAuthority('ROLE_MEMBER', 'ROLE_ADMIN')")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
        Resource file = managerService.load(filename,jwt.getName());
        String mimeType = managerService.getMimeType(filename, jwt.getName());
        if( mimeType == null ||mimeType.isEmpty())
        {
            mimeType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
