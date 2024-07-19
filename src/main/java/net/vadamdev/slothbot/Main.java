package net.vadamdev.slothbot;

import net.vadamdev.jdautils.application.JDAApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VadamDev
 * @since 17/07/2024
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(SlothBot.class);
    public static final SlothBot slothBot = new SlothBot();

    public static void main(String[] args) {
        final JDAApplication<SlothBot> application = new JDAApplication<>(slothBot, logger);
        application.start();
    }
}
