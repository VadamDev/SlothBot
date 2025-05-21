package net.vadamdev.slothbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.vadamdev.dbk.framework.config.Configuration;
import net.vadamdev.dbk.framework.config.annotations.ConfigValue;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author VadamDev
 * @since 21/05/2025
 */
public class ApplicationConfig extends Configuration {
    @ConfigValue(path = "client.token", comment = "The bot token, provided by the Discord Developer Portal")
    String TOKEN = "Secret Token Here";

    @ConfigValue(path = "client.guildId", comment = "The guild ID associated with the bot")
    public String GUILD_ID = "";

    /*
       Activity
     */

    @ConfigValue(path = "activity.activityType")
    public String ACTIVITY_TYPE = Activity.ActivityType.PLAYING.name();

    @ConfigValue(path = "activity.activity")
    public String ACTIVITY = "artemis.vadamdev.net";

    ApplicationConfig() {
        super("./application.yml");
    }

    @Nullable
    public Activity formatActivity() {
        if(ACTIVITY_TYPE == null || ACTIVITY == null)
            return null;

        return Activity.of(Activity.ActivityType.valueOf(ACTIVITY_TYPE), ACTIVITY);
    }

    public void updateActivity(JDA jda, @Nullable Activity.ActivityType activityType, @Nullable String activity) throws IOException {
        setValue("ACTIVITY_TYPE", activityType != null ? activityType.name() : null);
        setValue("ACTIVITY", activity);
        save();

        jda.getPresence().setActivity(formatActivity());
    }
}
