package net.vadamdev.slothbot;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.vadamdev.jdautils.application.IReloadable;
import net.vadamdev.jdautils.application.JDABot;
import net.vadamdev.jdautils.configuration.ConfigurationLoader;
import net.vadamdev.slothbot.channelcreator.LockeableCreatedChannel;
import net.vadamdev.slothbot.channelcreator.SimpleChannelCreator;
import net.vadamdev.slothbot.channelcreator.system.ChannelCreatorManager;
import net.vadamdev.slothbot.commands.ActivityCommand;
import net.vadamdev.slothbot.commands.RoleReactionCommand;
import net.vadamdev.slothbot.config.MainConfig;
import net.vadamdev.slothbot.listeners.EventListener;
import net.vadamdev.slothbot.rolereaction.RoleOption;
import net.vadamdev.slothbot.rolereaction.RoleReaction;
import net.vadamdev.slothbot.rolereaction.RoleReactionManager;

import java.io.IOException;

/**
 * @author VadamDev
 * @since 17/07/2024
 */
public class SlothBot extends JDABot implements IReloadable {
    public final MainConfig mainConfig;

    private RoleReactionManager roleReactionManager;
    private ChannelCreatorManager channelCreatorManager;

    public SlothBot() {
        super(BotToken.RELEASE.getToken());

        this.mainConfig = new MainConfig();
    }

    @Override
    public void onEnable() {
        initFiles();

        jda.getPresence().setActivity(mainConfig.formatActivity());

        roleReactionManager = new RoleReactionManager();
        roleReactionManager.addRoleReaction(new RoleReaction(
                "unlock",
                "\uD83D\uDD13 | Accès au Discord",
                null,
                RoleReaction.SelectType.SINGLE,
                new RoleOption(mainConfig.MEMBER_ROLE, Emoji.fromUnicode("\uD83E\uDDF1")),
                new RoleOption(mainConfig.FRIEND_ROLE, Emoji.fromUnicode("\uD83C\uDF20"))
        ));

        channelCreatorManager = new ChannelCreatorManager();
        channelCreatorManager.registerChannelCreator(
                new SimpleChannelCreator<>(
                        () -> mainConfig.VOICE_CREATOR,
                        () -> mainConfig.VOICE_CREATOR_CATEGORY,
                        owner -> "\uD83E\uDDF1┃Vocal #%index%",
                        LockeableCreatedChannel.class
                )
        );

        registerListeners(
                new EventListener()
        );

        registerCommands(
                new RoleReactionCommand(),
                new ActivityCommand()
        );
    }

    @Override
    public void onReload() {
        try {
            ConfigurationLoader.loadConfiguration(mainConfig);
            jda.getPresence().setActivity(mainConfig.formatActivity());

            Main.logger.info("Configuration was reloaded successfully !");
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }

    private void initFiles() {
        try {
            ConfigurationLoader.loadConfiguration(mainConfig);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public RoleReactionManager getRoleReactionManager() {
        return roleReactionManager;
    }

    public ChannelCreatorManager getChannelCreatorManager() {
        return channelCreatorManager;
    }
}
