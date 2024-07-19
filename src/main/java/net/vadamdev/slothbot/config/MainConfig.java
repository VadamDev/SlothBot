package net.vadamdev.slothbot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.vadamdev.jdautils.configuration.ConfigValue;
import net.vadamdev.jdautils.configuration.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * @author VadamDev
 * @since 17/07/2024
 */
public class MainConfig extends Configuration {
    /*
       Activity
     */

    @ConfigValue(path = "activity.activityType")
    public String ACTIVITY_TYPE = Activity.ActivityType.CUSTOM_STATUS.name();

    @ConfigValue(path = "activity.activity")
    public String ACTIVITY = "artemis.vadamdev.net";

    /*
       Roles
     */

    @ConfigValue(path = "roles.member")
    public String MEMBER_ROLE = "ID_HERE";

    @ConfigValue(path = "roles.friend")
    public String FRIEND_ROLE = "ID_HERE";

    /*
       Channel Creator
     */

    @ConfigValue(path = "channelCreator.voiceCreator")
    public String VOICE_CREATOR = "ID_HERE";

    @ConfigValue(path = "channelCreator.voiceCreatorCategory")
    public String VOICE_CREATOR_CATEGORY = "ID_HERE";

    public MainConfig() {
        super("./config.yml");
    }

    @Nullable
    public Activity formatActivity() {
        if(ACTIVITY_TYPE == null || ACTIVITY == null)
            return null;

        return Activity.of(Activity.ActivityType.valueOf(ACTIVITY_TYPE), ACTIVITY);
    }

    public void updateActivity(@Nonnull JDA jda, @Nullable Activity.ActivityType activityType, @Nullable String activity) throws IOException {
        setValue("ACTIVITY_TYPE", activityType != null ? activityType.name() : null);
        setValue("ACTIVITY", activity);
        save();

        jda.getPresence().setActivity(formatActivity());
    }
}
