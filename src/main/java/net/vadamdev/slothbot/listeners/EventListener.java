package net.vadamdev.slothbot.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.slothbot.Main;
import net.vadamdev.slothbot.SlothBot;
import net.vadamdev.slothbot.config.MainConfig;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * @author VadamDev
 * @since 02/03/2023
 */
public class EventListener extends ListenerAdapter {
    private final SlothBot slothBot;
    private final MainConfig mainConfig;

    public EventListener() {
        this.slothBot = Main.slothBot;
        this.mainConfig = slothBot.mainConfig;
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        /*final User user = event.getUser();
        if(user.isBot())
            return;

        final Guild guild = event.getGuild();

        //Add default roles
        guild.addRoleToMember(user, guild.getRoleById(mainConfig.MEMBER_ROLE)).queue();
        guild.addRoleToMember(user, guild.getRoleById(mainConfig.SEPARATOR_1_ROLE)).queue();
        guild.addRoleToMember(user, guild.getRoleById(mainConfig.SEPARATOR_2_ROLE)).queue();
        guild.addRoleToMember(user, guild.getRoleById(mainConfig.SEPARATOR_3_ROLE)).queue();

        //Send a welcome message
        guild.getTextChannelById(mainConfig.WELCOME_CHANNEL).sendMessageEmbeds(new JafarEmbed()
                .setTitle("Bienvenue " + user.getEffectiveName() + " !")
                .setDescription("Bienvenue **" + user.getAsMention() + "** sur le discord de **" + guild.getName() + "** !")
                .setThumbnail(user.getAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(JafarEmbed.NEUTRAL_COLOR).build()).queue();*/
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        slothBot.getChannelCreatorManager().handleButtonInteractionEvent(event);
        slothBot.getRoleReactionManager().handleButtonInteraction(event);
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        slothBot.getChannelCreatorManager().handleSelectInteractionEvent(event);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        slothBot.getChannelCreatorManager().handleModalInteractionEvent(event);
    }

    @Override
    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        slothBot.getChannelCreatorManager().handleVoiceUpdateEvent(event);
    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        if(!event.getChannelType().equals(ChannelType.VOICE))
            return;

        slothBot.getChannelCreatorManager().handleChannelDelete(event.getChannel().asVoiceChannel());
    }
}
