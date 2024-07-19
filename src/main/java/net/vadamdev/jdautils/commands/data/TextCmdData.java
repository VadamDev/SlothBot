package net.vadamdev.jdautils.commands.data;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;

/**
 * Represents the data of a legacy command
 *
 * @author VadamDev
 * @since 01/06/2024
 */
public record TextCmdData(MessageReceivedEvent event, String[] args) implements ICommandData {
    @Nonnull
    @Override
    public Type getType() {
        return Type.TEXT;
    }
}
