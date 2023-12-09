package com.beadando.beadandoapi.model;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class FileInfo {
    private String name;

    public FileInfo(String name) {
        this.name = name;
    }

}
