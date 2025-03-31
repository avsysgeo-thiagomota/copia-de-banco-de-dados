package com.vms.util;

import java.io.IOException;
import java.util.logging.*;

public class LogConfig {
    public static void configurar() {
        Logger rootLogger = Logger.getLogger("");
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);

        try {
            // Cria diretório logs/ se não existir
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("logs"));

            // Define o arquivo de log
            Handler fileHandler = new FileHandler("logs/migracao.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            // Limpa handlers anteriores e adiciona novos
            rootLogger.setUseParentHandlers(false);
            rootLogger.addHandler(consoleHandler);
            rootLogger.addHandler(fileHandler);

        } catch (IOException e) {
            System.err.println("Falha ao configurar log: " + e.getMessage());
        }
    }
}