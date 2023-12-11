package com.beadando.beadandoapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class FileInfoDTO {
    public String filename;
    public Timestamp date_added;

    public FileInfoDTO(String filename, Timestamp date_added) {
        this.filename = filename;
        this.date_added = date_added;
    }
}
