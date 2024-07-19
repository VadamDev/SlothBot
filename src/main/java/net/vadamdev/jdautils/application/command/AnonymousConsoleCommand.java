package net.vadamdev.jdautils.application.command;

import net.vadamdev.jdautils.application.JDABot;

/**
 * Represents a {@link ConsoleCommand} action
 *
 * @author VadamDev
 * @since 01/06/2024
 */
@FunctionalInterface
public interface AnonymousConsoleCommand<T extends JDABot> {
    void execute(T jdaBot);
}
