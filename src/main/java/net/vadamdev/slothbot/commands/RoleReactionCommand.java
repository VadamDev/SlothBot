package net.vadamdev.slothbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vadamdev.slothbot.commands.api.GuildLinkedCommand;
import net.vadamdev.slothbot.rolereaction.RoleReaction;
import net.vadamdev.slothbot.rolereaction.RoleReactionManager;
import net.vadamdev.slothbot.utils.EmbedUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author VadamDev
 * @since 21/05/2025
 */
public class RoleReactionCommand extends GuildLinkedCommand {
    private final RoleReactionManager roleReactionManager;

    public RoleReactionCommand(RoleReactionManager roleReactionManager) {
        super("rolereaction", "Créé un role reaction dans le salon ou la commande est envoyé");
        setRequiredPermissions(Permission.ADMINISTRATOR);

        this.roleReactionManager = roleReactionManager;
    }

    @Override
    public void executeCommand(Member sender, SlashCommandInteractionEvent event) {
        final Optional<RoleReaction> optRoleReaction = roleReactionManager.getRoleReaction(event.getOption("id", null, OptionMapping::getAsString));
        if(optRoleReaction.isEmpty()) {
            event.replyEmbeds(EmbedUtils.defaultError("Aucun rolereaction ne porte cette id !")
                    .setTitle("RoleReaction - Erreur")
                    .build()).setEphemeral(true).queue();

            return;
        }

        optRoleReaction.get().sendMessage(event.getChannel().asTextChannel());

        event.replyEmbeds(EmbedUtils.defaultSuccess("Le rolereaction a été créé")
                .setTitle("RoleReaction - Succès")
                .build()).setEphemeral(true).queue();
    }

    @NotNull
    @Override
    public SlashCommandData createCommandData() {
        return super.createCommandData().addOptions(
                new OptionData(OptionType.STRING, "id", "ID du Rolereaction", true)
        );
    }
}
