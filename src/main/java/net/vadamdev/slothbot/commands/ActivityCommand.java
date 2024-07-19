package net.vadamdev.slothbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vadamdev.jdautils.commands.Command;
import net.vadamdev.jdautils.commands.ISlashCommand;
import net.vadamdev.jdautils.commands.data.ICommandData;
import net.vadamdev.jdautils.commands.data.SlashCmdData;
import net.vadamdev.slothbot.Main;
import net.vadamdev.slothbot.utils.SlothEmbed;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VadamDev
 * @since 09/06/2023
 */
public class ActivityCommand extends Command implements ISlashCommand {
    public ActivityCommand() {
        super("activity");
        setPermission(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(@Nonnull Member sender, @Nonnull ICommandData commandData) {
        final var event = commandData.castOrNull(SlashCmdData.class).event();

        try {
            final var type = event.getOption("type", OptionMapping::getAsString).toUpperCase();
            if(type.equals("NONE"))
                Main.slothBot.mainConfig.updateActivity(event.getJDA(), null, null);
            else {
                Main.slothBot.mainConfig.updateActivity(
                        event.getJDA(),
                        Activity.ActivityType.valueOf(type),
                        event.getOption("activity").getAsString()
                );
            }

            event.replyEmbeds(new SlothEmbed()
                    .setTitle("Paresseux - Activité")
                    .setDescription("L'activité du bot a été mis à jour.")
                    .setColor(SlothEmbed.NEUTRAL_COLOR).build()).queue();
        } catch (IOException e) {
            event.replyEmbeds(new SlothEmbed()
                    .setTitle("Paresseux - Activité")
                    .setDescription("Une erreur est survenue.")
                    .setColor(SlothEmbed.ERROR_COLOR).build()).queue();

            e.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public SlashCommandData createSlashCommand() {
        return Commands.slash(name, "Commande permettant de changer l'activité du bot")
                .addOptions(
                        new OptionData(OptionType.STRING, "type", "Type d'activité")
                                .addChoice("Aucune", "NONE")
                                .addChoice("Joue à", "PLAYING")
                                .addChoice("Ecoute", "LISTENING")
                                .addChoice("Regarde", "WATCHING")
                                .addChoice("Participant à", "COMPETING")
                                .addChoice("Custom", "CUSTOM_STATUS")
                                .setRequired(true),
                        new OptionData(OptionType.STRING, "activity", "Activité")
                                .setRequired(true)
                );
    }
}
