package net.vadamdev.slothbot.configs;

import net.vadamdev.dbk.framework.config.Configuration;
import net.vadamdev.dbk.framework.config.annotations.ConfigRange;
import net.vadamdev.dbk.framework.config.annotations.ConfigValue;

/**
 * @author VadamDev
 * @since 21/05/2025
 */
public class MainConfig extends Configuration {
    /*
       Roles
     */

    @ConfigValue(path = "roles.member")
    public String MEMBER_ROLE = "ID_HERE";

    @ConfigValue(path = "roles.friend")
    public String FRIEND_ROLE = "ID_HERE";

    /*
       Music
     */

    @ConfigValue(
            path = "music.maxIdleTime",
            comment = """
                    Define the maximum number of seconds that the bot will stay "idling" in a vc before disconnect
                    The bot is considered as "idling" if:
                    1. The vc is empty
                    2. The bot is not playing music"""
    )
    public int MAX_IDLE_TIME = 60;

    @ConfigRange(min = 1, max = 100)
    @ConfigValue(path = "music.defaultVolume")
    public int DEFAULT_VOLUME = 25;

    @ConfigRange(min = 1, max = 100)
    @ConfigValue(path = "music.maxVolume")
    public int MAX_VOLUME = 75;

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
}
