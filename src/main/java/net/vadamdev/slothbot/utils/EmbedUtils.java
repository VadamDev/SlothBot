package net.vadamdev.slothbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.vadamdev.slothbot.SlothBot;

import java.awt.*;

/**
 * @author VadamDev
 * @since 21/05/2025
 */
public final class EmbedUtils {
    private EmbedUtils() {}

    public static final int DEFAULT_COLOR = new Color(83, 185, 80).getRGB();
    public static final int SUCCESS_COLOR = new Color(0, 255, 0).getRGB();
    public static final int ERROR_COLOR = new Color(255, 0, 0).getRGB();

    public static EmbedBuilder defaultEmbed() {
        return new EmbedBuilder()
                .setColor(DEFAULT_COLOR)
                .setFooter(SlothBot.get().getAppName(), SlothBot.get().getAvatarURL());
    }

    public static EmbedBuilder defaultEmbed(Color color) {
        return new EmbedBuilder()
                .setColor(color)
                .setFooter(SlothBot.get().getAppName(), SlothBot.get().getAvatarURL());
    }

    public static EmbedBuilder defaultEmbed(int color) {
        return new EmbedBuilder()
                .setColor(color)
                .setFooter(SlothBot.get().getAppName(), SlothBot.get().getAvatarURL());
    }

    public static EmbedBuilder defaultSuccess(String message) {
        return defaultEmbed(SUCCESS_COLOR)
                .setDescription(message);
    }

    public static EmbedBuilder defaultError(String message) {
        return defaultEmbed(ERROR_COLOR)
                .setDescription(message);
    }
}
