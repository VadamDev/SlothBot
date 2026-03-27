package net.vadamdev.slothbot.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.utils.Checks;
import net.vadamdev.dbk.components.SmartComponents;
import net.vadamdev.dbk.components.api.registry.MessageRegistry;
import net.vadamdev.dbk.components.entities.button.SmartButton;
import net.vadamdev.dbk.menu.ActionComponentMenu;
import net.vadamdev.slothbot.SlothBot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author VadamDev
 * @since 02/04/2025
 */
public class ConfirmationRequest {
    protected final ActionComponentMenu menu;

    protected ConfirmationRequest(ActionComponentMenu menu) {
        this.menu = menu;
    }

    public void send(IReplyCallback callback) {
        menu.display(callback, false).queue();
    }

    /*
       Builder
     */

    public static Builder builder() {
        return new Builder().timeout(15, TimeUnit.MINUTES);
    }

    public static class Builder {
        private final ActionComponentMenu.Builder menuBuilder;

        private final List<Permission> requiredPermissions;
        private final List<String> authorizedUserIds;

        @Nullable private Consumer<ButtonInteractionEvent> onConfirm, onDeny;

        @Nullable private ButtonStyle confirmButtonStyle, denyButtonStyle;
        @Nullable private String confirmButtonLabel, denyButtonLabel;

        protected Builder() {
            this.menuBuilder = ActionComponentMenu.builder();

            this.requiredPermissions = new ArrayList<>();
            this.authorizedUserIds = new ArrayList<>();
        }

        public Builder requirePermissions(Permission permission, Permission... morePermissions) {
            requiredPermissions.add(permission);

            if(morePermissions.length > 0)
                requiredPermissions.addAll(List.of(morePermissions));

            return this;
        }

        public Builder onlyAuthorizedUsers(User user, User... moreUsers) {
            authorizedUserIds.add(user.getId());

            for(User anotherUser : moreUsers)
                authorizedUserIds.add(anotherUser.getId());

            return this;
        }

        public Builder whenConfirmed(ButtonStyle style, String label, Consumer<ButtonInteractionEvent> action) {
            this.confirmButtonStyle = style;
            this.confirmButtonLabel = label;

            this.onConfirm = action;

            return this;
        }

        public Builder whenDenied(ButtonStyle style, String label, Consumer<ButtonInteractionEvent> action) {
            this.denyButtonStyle = style;
            this.denyButtonLabel = label;

            this.onDeny = action;

            return this;
        }

        public Builder addEmbed(MessageEmbed embed, MessageEmbed... moreEmbeds) {
            menuBuilder.addEmbed(embed, moreEmbeds);
            return this;
        }

        public Builder timeout(long timeout, TimeUnit unit) {
            menuBuilder.timeout(timeout, unit, SlothBot.getScheduledExecutorMonoThread());
            return this;
        }

        protected Predicate<Member> createMemberPredicate() {
            if(requiredPermissions.isEmpty() && authorizedUserIds.isEmpty())
                return member -> true;

            if(!requiredPermissions.isEmpty() && !authorizedUserIds.isEmpty())
                return member -> authorizedUserIds.contains(member.getId()) && member.hasPermission(requiredPermissions);

            if(!requiredPermissions.isEmpty())
                return member -> member.hasPermission(requiredPermissions);

            return member -> authorizedUserIds.contains(member.getId());
        }

        public ConfirmationRequest build() {
            Checks.check(onConfirm != null && confirmButtonStyle != null && confirmButtonLabel != null, "The confirm button must be set!");

            final Predicate<Member> memberPredicate = createMemberPredicate();

            final MessageRegistry<Button> confirm = SmartButton.builder(confirmButtonStyle)
                    .label(confirmButtonLabel)
                    .action((event, invalidatable) -> {
                        if(!memberPredicate.test(event.getMember()))
                            return;

                        SmartComponents.findComponentManager(event.getJDA())
                                .ifPresent(manager -> manager.invalidateMessageAttachedComponents(event.getMessageId()));

                        onConfirm.accept(event);
                    })
                    .build();

            MessageRegistry<Button> deny = null;
            if(denyButtonStyle != null && denyButtonLabel != null && onDeny != null) {
                deny = SmartButton.builder(denyButtonStyle)
                        .label(denyButtonLabel)
                        .action((event, invalidatable) -> {
                            if(!memberPredicate.test(event.getMember()))
                                return;

                            SmartComponents.findComponentManager(event.getJDA())
                                    .ifPresent(manager -> manager.invalidateMessageAttachedComponents(event.getMessageId()));

                            onDeny.accept(event);
                        })
                        .build();
            }

            if(deny != null)
                menuBuilder.addActionRow(confirm, deny);
            else
                menuBuilder.addActionRow(confirm);

            return new ConfirmationRequest(menuBuilder.build());
        }
    }
}
