package net.vadamdev.jdautils.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.vadamdev.jdautils.commands.data.ICommandData;
import net.vadamdev.jdautils.commands.data.SlashCmdData;
import net.vadamdev.jdautils.commands.data.TextCmdData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 17/10/2022
 */
public final class CommandHandler {
    public static boolean LOGGING = true;
    public static Consumer<Message> SEND_NO_PERMISSION_MESSAGE = message -> message.reply("You don't have enough permission.").queue();
    public static Consumer<IReplyCallback> SEND_NO_DM_MESSAGE = callback -> callback.reply("❌ Slash commands only work in guilds!").setEphemeral(true).queue();

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    private final List<Command> commands;
    private final String commandPrefix;

    public CommandHandler(String commandPrefix, JDA jda) {
        this.commands = new ArrayList<>();
        this.commandPrefix = commandPrefix;

        if(commandPrefix != null && !jda.getGatewayIntents().contains(GatewayIntent.MESSAGE_CONTENT))
            LOGGER.warn("MESSAGE_CONTENT intent is currently not enabled, legacy commands are disabled!");
    }

    /*
       Handle Events
     */

    public void handleMessageReceive(@Nonnull MessageReceivedEvent event) {
        if(commandPrefix == null)
            return;

        final var messageContent = event.getMessage().getContentRaw();

        if(messageContent.startsWith(commandPrefix)) {
            final var args = messageContent.split(" ");
            final var commandName = args[0].replace(commandPrefix, "");

            commands.stream()
                    .filter(command -> command.check(commandName))
                    .findFirst().ifPresent(command -> {
                        if(command instanceof ISlashCommand slashCommand && slashCommand.isSlashOnly())
                            return;

                        final var member = event.getMember();
                        if(command.getPermission() != null && !member.hasPermission(command.getPermission())) {
                            SEND_NO_PERMISSION_MESSAGE.accept(event.getMessage());
                            return;
                        }

                        final var commandData = new TextCmdData(event, args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));

                        logCommandExecution(member, commandData, commandName);
                        command.execute(member, commandData);
                    });
        }
    }

    public void handleSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        commands.stream()
                .filter(ISlashCommand.class::isInstance)
                .filter(command -> command.check(event.getName()))
                .findFirst().ifPresent(command -> {
                    if(event.getGuild() == null) {
                        SEND_NO_DM_MESSAGE.accept(event);
                        return;
                    }

                    final var commandData = new SlashCmdData(event);
                    final var member = event.getMember();

                    logCommandExecution(member, commandData, event.getFullCommandName());
                    command.execute(member, commandData);
                });
    }

    /*
       Register
     */

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public void registerSlashCommands(JDA jda) {
        jda.updateCommands().addCommands(
                commands.stream()
                        .filter(ISlashCommand.class::isInstance)
                        .map(command -> {
                            final CommandData commandData = ((ISlashCommand) command).createSlashCommand();

                            if(command.getPermission() != null)
                                commandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getPermission()));
                            else
                                commandData.setDefaultPermissions(DefaultMemberPermissions.ENABLED);

                            return commandData;
                        }).toList()
        ).queue();
    }

    /*
       Utils
     */

    private void logCommandExecution(Member sender, ICommandData commandData, String commandName) {
        if(!LOGGING)
            return;

        final var formattedCommand = new StringBuilder(commandName);

        if(commandData.getType().equals(ICommandData.Type.TEXT)) {
            for(String arg : commandData.castOrNull(TextCmdData.class).args())
                formattedCommand.append(" " + arg);
        }else if(commandData.getType().equals(ICommandData.Type.SLASH)) {
            final var event = commandData.castOrNull(SlashCmdData.class).event();
            event.getOptions().forEach(optionMapping -> formattedCommand.append(" (" + optionMapping.getName() + ": " + formatOptionMapping(optionMapping) + ")"));
        }

        LOGGER.info(sender.getUser().getName() + " in " + sender.getGuild().getId() + " issued command: " + formattedCommand);
    }

    private String formatOptionMapping(OptionMapping optionMapping) {
        return switch(optionMapping.getType()) {
            case STRING -> optionMapping.getAsString() + " (string)";
            case INTEGER -> optionMapping.getAsInt() + " (integer)";
            case BOOLEAN -> optionMapping.getAsBoolean() + " (boolean)";
            case USER -> optionMapping.getAsUser().getEffectiveName() + " (user)";
            case CHANNEL -> optionMapping.getAsChannel().getName() + " (channel)";
            case ROLE -> optionMapping.getAsRole().getName() + " (role)";
            case MENTIONABLE -> optionMapping.getAsMentionable().getAsMention() + " (mention)";
            case ATTACHMENT -> {
                final Message.Attachment attachment = optionMapping.getAsAttachment();
                yield attachment.getFileName() + "." + attachment.getFileExtension() + " (attachment)";
            }
            default -> "UNKNOWN_OPTION";
        };
    }
}
