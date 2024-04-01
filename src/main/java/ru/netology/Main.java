package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final int STANDART_PORT = 8988;
    private static final Map<String, PrintWriter> clientList = new HashMap<>();

    public static void main(String[] args) {
        Logger logger = new Logger();
        File settings = new File("settings.txt");

        int serverPort = settingsCreate(settings, logger);
        if (serverPort != 0) {
            logger.log(String.format("Сервер запущен: Порт %d", serverPort), LoggerEnum.MESSAGE, false);

            new Thread(() -> {
                Socket clientSocket;
                while (true) {
                    try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                        clientSocket = serverSocket.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        String name = in.readLine();
                        clientList.put(name, out);

                        logger.log(String.format("Соединение с %d принято", clientSocket.getPort()), LoggerEnum.MESSAGE, true);

                        new Thread(() -> {
                            boolean isRunning = true;
                            while (isRunning) {
                                try {
                                    final String clientName = in.readLine();
                                    final String message = in.readLine();

                                    for (String keyName : clientList.keySet()) {
                                        if (!(keyName.equals(clientName))) {
                                            clientList.get(keyName).println(message);
                                        }
                                    }
                                    logger.log(message, LoggerEnum.MESSAGE, true);
                                } catch (IOException e) {
                                    logger.log("Не удалось получить сообщение: Возможно, пользователь отключился от сервера", LoggerEnum.WARN, true);
                                    isRunning = false;
                                }
                            }
                        }).start();
                    } catch (IOException e) {
                        logger.log("Не удалось подключиться к серверу", LoggerEnum.ERROR, true);
                    }
                }
            }).start();
        } else {
            logger.log("Ошибка при запуске сервера: Порт введён не верно", LoggerEnum.ERROR, true);
        }
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