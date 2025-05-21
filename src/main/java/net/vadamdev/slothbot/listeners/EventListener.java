package net.vadamdev.slothbot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.slothbot.rolereaction.RoleReactionManager;

/**
 * @author VadamDev
 * @since 21/05/2025
 */
public class EventListener extends ListenerAdapter {
    private final RoleReactionManager roleReactionManager;

    public EventListener(RoleReactionManager roleReactionManager) {
        this.roleReactionManager = roleReactionManager;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        roleReactionManager.handleButtonInteraction(event);
    }
}
