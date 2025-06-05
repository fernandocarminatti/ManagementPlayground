package com.edu.ManagementPlayground.Enum;

import java.nio.file.Path;

public enum StorageContext {
    DEFAULT,
    NOTAFISCAL,
    BOLETO,
    COMPROVANTEPAGAMENTO;

    private Path folder;

    StorageContext(){
    }

    public void configurePath(Path path){
        this.folder = path;
    }

    public Path getFolder(){
        return folder;
    }
}