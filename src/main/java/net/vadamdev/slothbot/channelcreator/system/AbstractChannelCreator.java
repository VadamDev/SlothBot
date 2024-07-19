package net.vadamdev.slothbot.channelcreator.system;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.vadamdev.slothbot.Main;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * @author VadamDev
 * @since 27/08/2023
 */
public abstract class AbstractChannelCreator<T extends CreatedChannel> {
    private final Supplier<String> creatorId;
    private Constructor<T> constructor;

    public AbstractChannelCreator(Supplier<String> creatorId, Class<T> clazz) {
        this.creatorId = creatorId;

        try {
            this.constructor = clazz.getConstructor(String.class, String.class);
        }catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    void createChannel(Guild guild, Member owner) {
        final var creatorId = this.creatorId.get();

        final var categoryId = getCategoryId(owner);
        final var channelName = getChannelName(owner)
                .replace("%index%", (Main.slothBot.getChannelCreatorManager().getActiveChannelAmount(creatorId) + 1) + "");

        computeChannelAction(guild.createVoiceChannel(channelName, categoryId != null ? guild.getCategoryById(categoryId) : null)).queue(channel -> {
            guild.moveVoiceMember(owner, channel).queue();

            try {
                final var createdChannel = constructor.newInstance(channel.getId(), owner.getId());
                createdChannel.onChannelCreation(channel, owner);
                Main.slothBot.getChannelCreatorManager().registerCreatedChannel(creatorId, createdChannel);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Nonnull
    protected abstract String getChannelName(Member owner);

    @Nullable
    protected abstract String getCategoryId(Member owner);

    @Nonnull
    protected ChannelAction<VoiceChannel> computeChannelAction(ChannelAction<VoiceChannel> voiceAction) {
        return voiceAction;
    }

    Supplier<String> getCreatorId() {
        return creatorId;
    }
}
