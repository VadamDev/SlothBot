package net.vadamdev.jdautils.commands.data;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.annotation.Nonnull;

/**
 * Represents the data of a Slash Command
 *
 * @author VadamDev
 * @since 01/06/2024
 */
public record SlashCmdData(SlashCommandInteractionEvent event) implements ICommandData {
    @Nonnull
    @Override
    public Type getType() {
        return Type.SLASH;
    }
}
