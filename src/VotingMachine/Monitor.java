package VotingMachine;

import Display.ButtonData;
import Drivers.ScreenDriver;
import Drivers.Battery;
import Drivers.Printer;
import Drivers.SDCard;
import Drivers.LLSensor;
import Display.Template;

import java.io.IOException;

public class Monitor implements Runnable {

    private final LLSensor latchDriver;
    private final SDCard SD_Card1;
    private final SDCard SD_Card2;
    private final SDCard SD_Card3;
    private final ScreenDriver screenDriver;
    private final Battery battery;
    private final Printer printer;

    private boolean failure = false;

    Template[] templates = new Template[3];

    public Monitor(
            LLSensor latchDriver,
            SDCard SD_Card1,
            SDCard SD_Card2,
            SDCard SD_Card3,
            ScreenDriver screenDriver,
            Battery battery,
            Printer printer
    ) {
        this.latchDriver = latchDriver;
        this.SD_Card1 = SD_Card1;
        this.SD_Card2 = SD_Card2;
        this.SD_Card3 = SD_Card3;
        this.screenDriver = screenDriver;
        this.battery = battery;
        this.printer = printer;
        // BUG FIX: createTemplates() was defined but never called,
        // leaving the templates array empty and causing a NullPointerException
        // in presentFailureTemplate() when a failure occurred.
        createTemplates();
    }

    public boolean getFailure() {
        return this.failure;
    }

    public void presentFailureTemplate() {
        if (!this.failure) return;
        if (this.battery.failure()) {
            this.screenDriver.sendTemplate(templates[0]);
        } else {
            this.screenDriver.sendTemplate(templates[2]);
        }
    }

    @Override
    public void run() {
        do {
            this.failure = this.latchDriver.failure() ||
                    this.SD_Card1.failure() ||
                    this.SD_Card2.failure() ||
                    this.SD_Card3.failure() ||
                    this.battery.failure() ||
                    this.screenDriver.failure() ||
                    this.printer.failure();
        } while (!this.failure);
    }

    private void createTemplates() {
        Template powerError = new Template(
                "ERROR",
                "Power to machine was interrupted. \n Insert Supervisor Card and shutdown machine.",
                "Select \"Shutdown Machine\" and select \"Submit\"",
                new ButtonData(true, "Shutdown Machine"),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(true),
                new ButtonData(false)
        );

        Template tamperError = new Template(
                "ERROR",
                "Machine Tamper Sensor was triggered. \n Insert Supervisor Card and shutdown machine.",
                "Select \"Shutdown Machine\" and select \"Submit\"",
                new ButtonData(true, "Shutdown Machine"),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(true),
                new ButtonData(false)
        );

        Template generalError = new Template(
                "ERROR",
                "An error was detected. \n Insert Supervisor Card and shutdown machine.",
                "Select \"Shutdown Machine\" and select \"Submit\"",
                new ButtonData(true, "Shutdown Machine"),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(false),
                new ButtonData(true),
                new ButtonData(false)
        );

        templates[0] = powerError;
        templates[1] = tamperError;
        templates[2] = generalError;
    }
}
