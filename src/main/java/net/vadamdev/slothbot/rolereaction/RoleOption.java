package net.vadamdev.slothbot.rolereaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Optional;

/**
 * @author VadamDev
 * @since 18/07/2024
 */
public record RoleOption(String roleId, Emoji icon) {
    public Button toButton(String roleReactionId) {
        return Button.secondary("SlothBot-RR-" + roleReactionId + "-" + roleId, icon);
    }

    public Optional<Role> toRole(Guild guild) {
        return Optional.ofNullable(guild.getRoleById(roleId));
    }
}
