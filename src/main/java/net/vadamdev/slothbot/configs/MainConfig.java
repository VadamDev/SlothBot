package net.vadamdev.slothbot.configs;

import net.vadamdev.dbk.framework.config.Configuration;
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
