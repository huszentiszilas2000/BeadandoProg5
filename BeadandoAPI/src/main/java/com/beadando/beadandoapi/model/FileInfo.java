package com.beadando.beadandoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Entity
@Setter
@Table(name = "file_info")
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userid;
    private String filename;
    private Timestamp date_added;



    public FileInfo(String userid, String filename, Timestamp date_added) {
        this.userid = userid;
        this.filename = filename;
        this.date_added = date_added;
    }

    public FileInfo() {

    }
}
