package net.vadamdev.jdautils.application.command;

import net.vadamdev.jdautils.application.JDABot;

/**
 * @author VadamDev
 * @since 01/06/2024
 */
public abstract class ConsoleCommand<T extends JDABot> implements AnonymousConsoleCommand<T> {
    private final String name;

    public ConsoleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
