package ru.netology.Logger;

import ru.netology.Logger.Logger;
import ru.netology.Logger.LoggerEnum;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;

public class FileLogger implements Logger {
    @Override
    public void log(String message, LoggerEnum messageType, boolean append) {
        File log = new File("info.log");
        if (log.exists()) {
            try (FileOutputStream fos = new FileOutputStream(log.getName(), append)) {
                fos.write(("[" + LocalDateTime.now() + "] " + messageType + " -> " + message + ".\n").getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                if (log.createNewFile()) {}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
