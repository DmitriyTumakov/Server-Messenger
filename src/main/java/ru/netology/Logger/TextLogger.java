package ru.netology.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class TextLogger implements Logger {
    @Override
    public void log(String message, LoggerEnum messageType, boolean append) {
        System.out.println("[" + LocalDateTime.now() + "] " + messageType + " -> " + message + ".\n");
    }
}
