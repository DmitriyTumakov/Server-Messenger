package ru.netology;

import ru.netology.Logger.FileLogger;
import ru.netology.Logger.Logger;
import ru.netology.Logger.LoggerEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    private static final int STANDART_PORT = 8988;

    public static void main(String[] args) {
        Logger logger = new FileLogger();
        File settings = new File("settings.txt");

        int serverPort = settingsCreate(settings, logger);

        Server server = new Server(logger, serverPort);
        server.start();
    }

    protected static int settingsCreate(File settings, Logger logger) {
        if (!settings.exists()) {
            try (FileOutputStream fos = new FileOutputStream("settings.txt")) {
                if (settings.createNewFile()) {
                    logger.log("Файл настроек успешно создан", LoggerEnum.MESSAGE, true);
                }
                fos.write(("PORT = " + STANDART_PORT).getBytes());
            } catch (IOException e) {
                logger.log("Ошибка при создании файла конфигурации: Файл уже существует", LoggerEnum.WARN, true);
            }
            return STANDART_PORT;
        } else {
            StringBuilder settingsBuilder = new StringBuilder();
            try (FileInputStream fis = new FileInputStream("settings.txt")) {
                int i;
                while ((i = fis.read()) != -1) {
                    settingsBuilder.append((char) i);
                }
                String[] settingsArray = settingsBuilder.toString().split("\n");
                for (String setting : settingsArray) {
                    String[] value = setting.split(" = ");
                    if (!setting.contains("//")) {
                        if (setting.contains("PORT")) {
                            if (!(value[1].length() > 5 || value[1].length() < 4)) {
                                return Integer.parseInt(value[1]);
                            } else {
                                logger.log("Ошибка при чтении настроек: Порт введён не верно", LoggerEnum.ERROR, true);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.log("Ошибка при чтении настроек: Не удалось получить доступ к файлу", LoggerEnum.ERROR, true);
            }
        }
        return 0;
    }
}
