package net.vadamdev.slothbot.channelcreator;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.slothbot.Main;
import net.vadamdev.slothbot.channelcreator.system.CreatedChannel;
import net.vadamdev.slothbot.utils.SlothEmbed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VadamDev
 * @since 29/08/2023
 */
public class LockeableCreatedChannel extends CreatedChannel {
    protected static MessageEmbed NOT_OWNER_MESSAGE = new SlothEmbed()
            .setTitle("Salon Personnalisé")
            .setDescription("Vous pouvez interagir avec ces boutons seulement si vous êtes le propriétaire de ce salon !")
            .setColor(SlothEmbed.ERROR_COLOR).build();

    private String configMessageId;
    private boolean locked;

    public LockeableCreatedChannel(String channelId, String ownerId) {
        super(channelId, ownerId);
    }

    /*
       Events
     */

    @Override
    protected void onChannelCreation(VoiceChannel voiceChannel, Member owner) {
        updateOrCreateConfigMessage(voiceChannel, owner);
    }

    @Override
    protected void handleButtonInteractionEvent(@Nonnull ButtonInteractionEvent event) {
        final var member = event.getMember();

        switch(event.getComponentId()) {
            case "JafarBot-LockeableChannel-Lock":
                if(!isOwner(member.getId(), event))
                    return;

                event.deferEdit().queue();
                setLocked(event.getGuild(), !locked);

                break;
            case "JafarBot-LockeableChannel-Delete":
                if(!isOwner(member.getId(), event))
                    return;

                event.replyEmbeds(new SlothEmbed()
                        .setTitle("Salon de " + member.getEffectiveName())
                        .setDescription(
                                """
                                Êtes-vous sur(e) de vouloir supprimer ce salon ?
                                *Cela déconnectera toutes les personnes présentent à l'intérieur !*
                                """
                        ).setColor(SlothEmbed.NEUTRAL_COLOR).build()).setActionRow(
                                Button.danger("JafarBot-LockeableChannel-ConfirmDelete", "Confirmer")
                        ).setEphemeral(true).queue();

                break;
            case "JafarBot-LockeableChannel-ConfirmDelete":
                if(!isOwner(member.getId(), event))
                    return;

                event.deferEdit().queue();
                Main.slothBot.getChannelCreatorManager().deleteCreatedChannel(event.getGuild(), channelId);

                break;
            default:
                break;
        }
    }

    /*
       Utility
     */

    protected void setLocked(Guild guild, boolean locked) {
        final var voiceChannel = guild.getVoiceChannelById(channelId);
        final var owner = guild.getMemberById(ownerId);

        if(voiceChannel == null || owner == null)
            return;

        if(locked && !this.locked) {
            final var memberSize = voiceChannel.getMembers().size();
            voiceChannel.getManager().setUserLimit(Math.max(2, memberSize)).complete();
        }else if(!locked && this.locked)
            voiceChannel.getManager().setUserLimit(0).complete();

        this.locked = locked;

        updateOrCreateConfigMessage(voiceChannel, owner);
    }

    public boolean isOwner(String memberId, @Nullable IReplyCallback replyCallback) {
        final var isOwner = memberId.equals(ownerId);

        if(!isOwner && replyCallback != null)
            replyCallback.replyEmbeds(NOT_OWNER_MESSAGE).setEphemeral(true).queue();

        return isOwner;
    }

    /*
       Config Message
     */

    protected void updateOrCreateConfigMessage(VoiceChannel voiceChannel, Member owner) {
        if(configMessageId != null) {
            voiceChannel.retrieveMessageById(configMessageId).queue(message ->
                    message.editMessageEmbeds(createConfigEmbed(owner))
                            .setActionRow(getComponents())
                            .queue()
            );
        }else {
            voiceChannel.sendMessageEmbeds(createConfigEmbed(owner))
                    .setActionRow(getComponents())
                    .queue(message -> configMessageId = message.getId());
        }
    }

    @Nonnull
    protected MessageEmbed createConfigEmbed(Member owner) {
        return new SlothEmbed()
                .setTitle("Salon de " + owner.getEffectiveName())
                .setDescription(String.format(
                        """
                        **Informations:**
                        > Status: %s
                        
                        **Boutons**
                        > %s
                        > \uD83D\uDDD1 *: Fermer le salon*
                        """,
                        locked ? "\uD83D\uDD12" : "\uD83D\uDD13",
                        (!locked ? "\uD83D\uDD12" : "\uD83D\uDD13") + " *: " + (locked ? "Déverrouiller" : "Verrouiller") + " le salon*")
                ).setColor(SlothEmbed.NEUTRAL_COLOR).build();
    }

    @Nonnull
    protected ItemComponent[] getComponents() {
        return new ItemComponent[] {
                Button.secondary("JafarBot-LockeableChannel-Lock", Emoji.fromUnicode(locked ? "\uD83D\uDD13" : "\uD83D\uDD12")),
                Button.secondary("JafarBot-LockeableChannel-Delete", Emoji.fromUnicode("\uD83D\uDDD1️"))
        };
    }

    /*
       Getters
     */

    public boolean isLocked() {
        return locked;
    }
}
