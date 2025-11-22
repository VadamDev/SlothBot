package net.vadamdev.slothbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.vadamdev.dbk.DBKApplication;
import net.vadamdev.dbk.application.JDABot;
import net.vadamdev.dbk.application.annotations.AppConfig;
import net.vadamdev.dbk.application.annotations.Bot;
import net.vadamdev.dbk.config.loader.ConfigurationLoader;
import net.vadamdev.slothbot.channelcreator.LockeableCreatedChannel;
import net.vadamdev.slothbot.channelcreator.SimpleChannelCreator;
import net.vadamdev.slothbot.channelcreator.system.ChannelCreatorManager;
import net.vadamdev.slothbot.commands.ActivityCommand;
import net.vadamdev.slothbot.commands.MusicCommand;
import net.vadamdev.slothbot.commands.RoleReactionCommand;
import net.vadamdev.slothbot.configs.MainConfig;
import net.vadamdev.slothbot.listeners.EventListener;
import net.vadamdev.slothbot.music.GuildMusicManager;
import net.vadamdev.slothbot.rolereaction.RoleOption;
import net.vadamdev.slothbot.rolereaction.RoleReaction;
import net.vadamdev.slothbot.rolereaction.RoleReactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author VadamDev
 * @since 21/05/2025
 */
public class SlothBot extends JDABot {
    private final Logger logger;

    private final GuildLinkService guildLinkService;
    private final MainConfig mainConfig;

    private RoleReactionManager roleReactionManager;
    private ChannelCreatorManager channelCreatorManager;
    private GuildMusicManager musicManager;

    SlothBot() {
        super(() -> JDABuilder.createDefault(APP_CONFIG.TOKEN)
                .setActivity(APP_CONFIG.formatActivity()));

        this.logger = LoggerFactory.getLogger(SlothBot.class);

        this.guildLinkService = new GuildLinkService(APP_CONFIG);

        this.mainConfig = new MainConfig();
    }

    @Override
    protected void onStart() throws Exception {
        final Guild guild = guildLinkService.init(jda).waitCompleteLink();
        logger.info("Linked to guild: " + guild.getName() + " (" + guild.getId() + ") !");

        //Load main configuration
        ConfigurationLoader.loadConfiguration(mainConfig);

        //Load features
        roleReactionManager = new RoleReactionManager(); registerRoleReactions();
        channelCreatorManager = new ChannelCreatorManager(jda); registerChannelCreators();
        musicManager = new GuildMusicManager(jda);

        registerListeners(
                new EventListener(roleReactionManager)
        );

        registerCommands(
                new ActivityCommand(APP_CONFIG),
                new RoleReactionCommand(roleReactionManager),
                new MusicCommand(mainConfig, musicManager)
        );
    }

    @Override
    protected void onStop() {}

    private void registerRoleReactions() {
        roleReactionManager.addRoleReaction(new RoleReaction(
                "unlock",
                "\uD83D\uDD13 | Accès au Discord",
                null,
                RoleReaction.SelectType.SINGLE,
                new RoleOption(mainConfig.MEMBER_ROLE, Emoji.fromUnicode("\uD83E\uDDF1")),
                new RoleOption(mainConfig.FRIEND_ROLE, Emoji.fromUnicode("\uD83C\uDF79"))
        ));
    }

    private void registerChannelCreators() {
        channelCreatorManager.registerChannelCreators(new SimpleChannelCreator(
                mainConfig.VOICE_CREATOR,
                (channel, owner) -> new LockeableCreatedChannel(channel.getId(), owner.getId()),
                owner -> "\uD83E\uDDF1┃Vocal #%index%",
                owner -> owner.getGuild().getCategoryById(mainConfig.VOICE_CREATOR_CATEGORY)
        ));
    }

    public MainConfig getConfig() {
        return mainConfig;
    }

    public GuildLinkService getGuildLinkService() {
        return guildLinkService;
    }

    public ChannelCreatorManager getChannelCreatorManager() {
        return channelCreatorManager;
    }

    public GuildMusicManager getMusicManager() {
        return musicManager;
    }

    /*
       Main
     */

    @AppConfig
    private static final ApplicationConfig APP_CONFIG = new ApplicationConfig();

    @Bot
    private static final SlothBot INSTANCE = new SlothBot();
    public static SlothBot get() { return INSTANCE; }
    public static Logger getLogger() { return INSTANCE.logger; }

    private static DBKApplication dbkApplication;
    public static void stop() { dbkApplication.stop(); }
    public static ScheduledExecutorService getScheduledExecutorMonoThread() { return dbkApplication.getScheduledExecutorMonoThread(); }

    public static void main(String[] args) {
        dbkApplication = DBKApplication.of(SlothBot.class, INSTANCE.logger);
        dbkApplication.start();
    }
}
