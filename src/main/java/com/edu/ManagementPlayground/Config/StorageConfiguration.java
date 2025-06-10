package com.edu.ManagementPlayground.Config;

import com.edu.ManagementPlayground.Enum.StorageContext;
import com.edu.ManagementPlayground.Exception.StorageException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
    public class StorageConfiguration {
    @Value("${storage.upload-default}")
    private Path basePath;

    @Value("${storage.upload-notafiscal}")
    private Path notaFiscalPath;
    @Value("${storage.upload-boleto}")
    private Path boletoPath;
    @Value("${storage.upload-comprovante}")
    private Path comprovantePath;

    @PostConstruct
    public void initContextPaths() {
        StorageContext.DEFAULT.configurePath(basePath);
        StorageContext.NOTAFISCAL.configurePath(notaFiscalPath);
        StorageContext.BOLETO.configurePath(boletoPath);
        StorageContext.COMPROVANTEPAGAMENTO.configurePath(comprovantePath);
        for (StorageContext context : StorageContext.values()) {
            try {
                Files.createDirectories(context.getFolder());
            } catch (IOException e) {
                throw new StorageException("Could not create directory for context: " + context.name(), e);
            }
        }
    }
}