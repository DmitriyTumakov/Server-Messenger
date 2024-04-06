package ru.netology;

import ru.netology.Logger.Logger;
import ru.netology.Logger.LoggerEnum;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final Map<String, PrintWriter> clientList = new HashMap<>();
    private static boolean isRunning = true;
    private final Logger logger;
    private final int serverPort;

    public Server(Logger logger, int serverPort) {
        this.logger = logger;
        this.serverPort = serverPort;
    }

    public void start() {
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
                            while (isRunning) {
                                sendMessage(in, logger);
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

    private static void sendMessage(BufferedReader in, Logger logger) {
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
}