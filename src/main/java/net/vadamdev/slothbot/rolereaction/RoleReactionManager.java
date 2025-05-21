package net.vadamdev.slothbot.rolereaction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author VadamDev
 * @since 18/07/2024
 */
public class RoleReactionManager {
    private final Map<String, RoleReaction> roleReactions;

    public RoleReactionManager() {
        this.roleReactions = new HashMap<>();
    }

    public void handleButtonInteraction(ButtonInteractionEvent event) {
        final var componentId = event.getComponentId();
        if(!componentId.startsWith("SlothBot-RR-"))
            return;

        final var split = componentId.split("-");
        if(split.length != 4)
            return;

        if(!roleReactions.containsKey(split[2]))
            return;

        roleReactions.get(split[2]).handleButtonInteraction(event, split[3]);
    }

    public void addRoleReaction(RoleReaction roleReaction) {
        roleReactions.put(roleReaction.getId(), roleReaction);
    }

    public void addRoleReactions(RoleReaction... roleReactions) {
        for(RoleReaction roleReaction : roleReactions)
            addRoleReaction(roleReaction);
    }

    public Optional<RoleReaction> getRoleReaction(String id) {
        return Optional.ofNullable(roleReactions.get(id));
    }
}
