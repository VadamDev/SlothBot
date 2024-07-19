package net.vadamdev.slothbot.rolereaction;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.vadamdev.slothbot.utils.SlothEmbed;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author VadamDev
 * @since 18/07/2024
 */
public class RoleReaction {
    private final String id;

    protected final String title, description;
    protected final SelectType selectType;
    protected final RoleOption[] options;

    public RoleReaction(String id, String title, String description, SelectType selectType, RoleOption... options) {
        this.id = id;
        this.title = title;
        this.description = description != null ? description : "";
        this.selectType = selectType;
        this.options = options;
    }

    public RoleReaction(String id, String title, String description, RoleOption[] options) {
        this(id, title, description, SelectType.MULTIPLE, options);
    }

    public void sendMessage(TextChannel textChannel) {
        final var components = new ItemComponent[options.length];
        for (int i = 0; i < options.length; i++)
            components[i] = options[i].toButton(id);

        textChannel.sendMessageEmbeds(createEmbed(textChannel.getGuild())).setActionRow(components).queue();
    }

    protected MessageEmbed createEmbed(Guild guild) {
        final var description = new StringBuilder(this.description + switch(selectType) {
                    case SINGLE -> "\n\n__Veuillez choisir votre rôle:__\n";
                    case MULTIPLE -> "\n\n__Veuillez choisir votre/vos rôle(s) :__\n";
                });

        for(RoleOption option : options) {
            final var optRole = option.toRole(guild);
            if(optRole.isEmpty())
                continue;

            description.append("> " + option.icon().getName() + " : " + optRole.get().getAsMention() + "\n");
        }

        return new SlothEmbed()
                .setTitle(title)
                .setDescription(description)
                .setColor(SlothEmbed.NEUTRAL_COLOR)
                .build();
    }

    public void handleButtonInteraction(ButtonInteractionEvent event, String roleId) {
        RoleOption result = null;
        for(RoleOption option : options) {
            if(!option.roleId().equals(roleId))
                continue;

            result = option;
            break;
        }

        if(result == null)
            return;

        final var guild = event.getGuild();

        final var member = event.getMember();
        final var memberRoles = member.getRoles();

        final var optionalRole = result.toRole(event.getGuild());
        optionalRole.ifPresent(role -> {
            switch(selectType) {
                case MULTIPLE:
                    if(memberRoles.contains(role)) {
                        guild.removeRoleFromMember(member, role).queue(a -> event.replyEmbeds(new EmbedBuilder()
                                .setTitle("Role Reaction")
                                .setDescription("Le role " + role.getAsMention() + " vous a été retirer !")
                                .setColor(SlothEmbed.NEUTRAL_COLOR).build()
                        ).setEphemeral(true).queue());
                    }else {
                        guild.addRoleToMember(member, role).queue(a -> event.replyEmbeds(new EmbedBuilder()
                                .setTitle("Role Reaction")
                                .setDescription("Le role " + role.getAsMention() + " vous a été attribuer !")
                                .setColor(SlothEmbed.NEUTRAL_COLOR).build()
                        ).setEphemeral(true).queue());
                    }

                    break;
                case SINGLE:
                    final List<Role> otherRoles = Arrays.stream(options)
                            .map(option -> option.toRole(guild))
                            .filter(optRole -> optRole.isPresent() && !optRole.get().equals(role))
                            .map(Optional::get)
                            .toList();

                    for(Role otherRole : otherRoles) {
                        if(!memberRoles.contains(otherRole))
                            continue;

                        guild.removeRoleFromMember(member, otherRole).complete();
                    }

                    if(memberRoles.contains(role)) {
                        guild.removeRoleFromMember(member, role).queue(a -> event.replyEmbeds(new EmbedBuilder()
                                .setTitle("Role Reaction")
                                .setDescription("Le role " + role.getAsMention() + " vous a été retirer !")
                                .setColor(SlothEmbed.NEUTRAL_COLOR).build()
                        ).setEphemeral(true).queue());
                    }else {
                        guild.addRoleToMember(member, role).queue(a -> event.replyEmbeds(new EmbedBuilder()
                                .setTitle("Role Reaction")
                                .setDescription("Le role " + role.getAsMention() + " vous a été attribuer !")
                                .setColor(SlothEmbed.NEUTRAL_COLOR).build()
                        ).setEphemeral(true).queue());
                    }

                    break;
                default:
                    break;
            }
        });
    }

    public String getId() {
        return id;
    }

    public enum SelectType {
        MULTIPLE,
        SINGLE
    }
}
