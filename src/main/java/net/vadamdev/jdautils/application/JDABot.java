package net.vadamdev.jdautils.application;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.vadamdev.jdautils.JDAUtils;
import net.vadamdev.jdautils.commands.Command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a JDA bot
 *
 * @author VadamDev
 * @since 30/03/2023
 */
public class JDABot {
    private JDABuilder jdaBuilder;
    private final String commandPrefix;

    protected JDA jda;
    private JDAUtils jdaUtils;

    private String avatarUrl, appName;

    public JDABot(String token, @Nullable String commandPrefix) {
        this.jdaBuilder = createJDABuilder(token);
        this.commandPrefix = commandPrefix;
    }

    public JDABot(String token) {
        this(token, null);
    }

    void setup() throws InterruptedException {
        jda = jdaBuilder.build();
        jdaBuilder = null;

        jda.awaitReady();

        jdaUtils = new JDAUtils(jda, commandPrefix);

        final var selfUser = jda.getSelfUser();
        avatarUrl = selfUser.getAvatarUrl();
        appName = selfUser.getName();

        onEnable();

        jdaUtils.finishCommandRegistry();
    }

    @Nonnull
    protected JDABuilder createJDABuilder(String token) {
        return JDABuilder.createDefault(token);
    }

    public void onEnable() {}
    public void onDisable() {}

    /*
       JDA Listeners
     */
    protected void registerListeners(Object... listeners) {
        jda.addEventListener(listeners);
    }

    /*
       JDAUtils Commands
     */
    protected void registerCommands(Command... commands) {
        for(Command command : commands)
            jdaUtils.registerCommand(command);
    }

    /*
       Getters
     */

    public JDA getJDA() {
        return jda;
    }

    public String getAvatarURL() {
        return avatarUrl;
    }

    public String getAppName() {
        return appName;
    }
}
