package net.vadamdev.jdautils.application;

import net.vadamdev.jdautils.application.command.AnonymousConsoleCommand;
import net.vadamdev.jdautils.application.command.ConsoleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Represents a JDA application. This class will handle the basics running of a {@link JDABot}
 *
 * @author VadamDev
 * @since 08/06/2023
 */
public final class JDAApplication<T extends JDABot> {
    private final T jdaBot;

    private final Logger logger;
    private final boolean isLoggerGenerated;

    private final Map<String, AnonymousConsoleCommand<T>> commands;

    public JDAApplication(T jdaBot, @Nullable Logger logger) {
        this.jdaBot = jdaBot;

        if(logger == null) {
            this.logger = LoggerFactory.getLogger(JDAApplication.class);
            this.isLoggerGenerated = true;
        }else {
            this.logger = logger;
            this.isLoggerGenerated = false;
        }

        this.commands = new HashMap<>();
        addDefaultCommands();

        final var javaVersion = Runtime.version();
        this.logger.info(String.format(
                """
                
                -------------------------------------------------------
                  JDA Utils by VadamDev (https://github.com/VadamDev)
                
                  Bot Class: %s
                
                  Environment:
                   - Java %s (%s)
                   - Available Memory: %d Mo
                -------------------------------------------------------
                
                """,

                jdaBot.getClass().getName(),
                javaVersion.version().get(0),
                javaVersion,
                Runtime.getRuntime().maxMemory() / 1048576
        ));
    }

    public void start() {
        logger.info("Starting " + jdaBot.getClass().getSimpleName() + "...");

        try {
            jdaBot.setup();
        }catch(Exception e) {
            logger.error("An error occurred while starting the bot:");
            e.printStackTrace();

            System.exit(-1);
            return;
        }

        final Scanner scanner = new Scanner(System.in);
        do {
            try {
                final var keyboardInput = scanner.next();

                final var command = commands.entrySet().stream()
                        .filter(entry -> entry.getKey().equals(keyboardInput))
                        .findFirst().map(Map.Entry::getValue).orElse(null);

                if(command != null)
                    command.execute(jdaBot);
                else
                    logger.info("Unknown command ! Type help to see a list of all available commands.");
            } catch(NoSuchElementException | IllegalStateException e) {
                e.printStackTrace();
            }
        }while(scanner.hasNext());
    }

    public void stop() {
        logger.info("Disabling JDA Application...");

        jdaBot.onDisable();
        System.exit(0);
    }

    public void registerCommand(ConsoleCommand<T> consoleCommand) {
        commands.put(consoleCommand.getName(), consoleCommand);
    }

    public void registerCommand(String name, AnonymousConsoleCommand<T> command) {
        commands.put(name, command);
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isLoggerGenerated() {
        return isLoggerGenerated;
    }

    private void addDefaultCommands() {
        //Help Command
        commands.put("help", jdaBot -> {
            final var message = new StringBuilder("\nAvailable commands:");
            commands.forEach((k, v) -> message.append("\n- " + k));

            logger.info(message.toString());
        });

        //Stop Command
        commands.put("stop", jdaBot -> stop());

        //Reload Command
        if(jdaBot instanceof IReloadable reloadable) {
            commands.put("reload", jdaBot -> {
                logger.info("Trying to reload the app...");

                try {
                    reloadable.onReload();
                }catch (Exception e) {
                    logger.error("An error occurred while trying to reload the app:");
                    e.printStackTrace();
                    return;
                }

                logger.info("Reload completed !");
            });
        }
    }
}
