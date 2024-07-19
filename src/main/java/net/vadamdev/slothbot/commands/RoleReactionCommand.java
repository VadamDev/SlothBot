package net.vadamdev.slothbot.commands;

import net.dv8tion.jda.api.Permission;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author VadamDev
 * @since 19/07/2024
 */
public class RoleReactionCommand extends Command implements ISlashCommand {
    public RoleReactionCommand() {
        super("rolereaction");
        setPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(@Nonnull Member sender, @NotNull ICommandData commandData) {
        final var event = commandData.castOrNull(SlashCmdData.class).event();

        final var optRoleReaction = Main.slothBot.getRoleReactionManager().getRoleReaction(event.getOption("id", null, OptionMapping::getAsString));
        if(optRoleReaction.isEmpty()) {
            event.replyEmbeds(new SlothEmbed()
                    .setTitle("RoleReaction - Erreur")
                    .setDescription("Aucun rolereaction ne porte cette id !")
                    .setColor(SlothEmbed.ERROR_COLOR)
                    .build()).setEphemeral(true).queue();

            return;
        }

        optRoleReaction.get().sendMessage(event.getChannel().asTextChannel());

        event.replyEmbeds(new SlothEmbed()
                .setTitle("RoleReaction - Succès")
                .setDescription("Le rolereaction a été créé")
                .setColor(SlothEmbed.NEUTRAL_COLOR)
                .build()).setEphemeral(true).queue();
    }

    @NotNull
    @Override
    public SlashCommandData createSlashCommand() {
        return Commands.slash(name, "Créé un role reaction dans le salon ou la commande est envoyé")
                .addOptions(
                        new OptionData(OptionType.STRING, "id", "ID du Rolereaction")
                                .setRequired(true)
                );
    }
}
