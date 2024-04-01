package ru.netology;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

import static ru.netology.Main.settingsCreate;

class MainTest {

    @Test
    public void createSettingsTest() {
        File settings = new File("settings.txt");
        Logger logger = new Logger();

        int port = settingsCreate(settings, logger);

        Assertions.assertEquals(port, 8988);
    }

    @Test
    public void createSettingsTestFalse() {
        File settings = new File("settings.txt");
        Logger logger = new Logger();

        int port = settingsCreate(settings, logger);

        Assertions.assertNotEquals(port, 8888);
    }
}