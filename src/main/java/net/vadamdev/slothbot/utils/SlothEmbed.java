package net.vadamdev.slothbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.vadamdev.slothbot.Main;

import java.awt.*;

/**
 * @author VadamDev
 * @since 17/07/2024
 */
public class SlothEmbed extends EmbedBuilder {
    public static final Color NEUTRAL_COLOR = Color.WHITE;
    public static final Color ERROR_COLOR = Color.RED;

    public SlothEmbed() {
        setFooter("Paresseux", Main.slothBot.getAvatarURL());
    }
}
