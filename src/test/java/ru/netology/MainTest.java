package ru.netology;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.Logger.Logger;
import ru.netology.Logger.TextLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static ru.netology.Main.settingsCreate;

class MainTest {
    public static ServerSocket serverSocketMock;
    public static Socket clientSocketMock;

    @BeforeAll
    public static void setup() {
        serverSocketMock = Mockito.mock(ServerSocket.class);
        try {
            Mockito.when(serverSocketMock.accept()).thenReturn(clientSocketMock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clientSocketMock = Mockito.mock(Socket.class);
        try {
            PipedOutputStream output = new PipedOutputStream();
            Mockito.when(clientSocketMock.getOutputStream()).thenReturn(output);
            PipedInputStream input = new PipedInputStream(output);
            Mockito.when(clientSocketMock.getInputStream()).thenReturn(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createSettingsTest() {
        File settings = new File("settings.txt");
        Logger logger = new TextLogger();

        int port = settingsCreate(settings, logger);

        Assertions.assertEquals(port, 8988);
    }

    @Test
    public void createSettingsTestFalse() {
        File settings = new File("settings.txt");
        Logger logger = new TextLogger();

        int port = settingsCreate(settings, logger);

        Assertions.assertNotEquals(port, 8888);
    }

    @Test
    public void sendMessageTest() throws IOException {
        PrintWriter out = new PrintWriter(clientSocketMock.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocketMock.getInputStream()));
        Map<String, PrintWriter> clientList = new HashMap<>();
        clientList.put("Dmitriy", out);
        clientList.put("Kolya", out);

        Thread sendMessageTest = new Thread(() -> {
            out.println("Dmitriy");
            out.println("Hello");
        });
        sendMessageTest.start();
        in.readLine();
        String result = in.readLine();

        Assertions.assertEquals("Hello", result);
    }
}