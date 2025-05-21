package net.vadamdev.slothbot.utils;

import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.dbk.framework.interactive.api.components.InteractiveComponent;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 21/05/2025
 */
public final class Utils {
    private Utils() {}

    /*
        Confirmation Request
     */

    private static final TimeUnit CONFIRMATION_TIMEOUT_UNIT = TimeUnit.MINUTES;
    private static final long CONFIRMATION_TIMEOUT_DELAY = 5;

    public static ConfirmationRequest createDefaultConfirmationRequest(Consumer<GenericComponentInteractionCreateEvent> onConfirm) {
        final long timeoutTime = (System.currentTimeMillis() / 1000) + CONFIRMATION_TIMEOUT_UNIT.toSeconds(CONFIRMATION_TIMEOUT_DELAY);

        return ConfirmationRequest.builder()
                .timeout(CONFIRMATION_TIMEOUT_DELAY, CONFIRMATION_TIMEOUT_UNIT)
                .whenConfirmed(Button.success(InteractiveComponent.generateComponentUID(), "Confirmer"), onConfirm)
                .addEmbed(EmbedUtils.defaultEmbed(Color.WHITE)
                        .setTitle("Êtes vous sur(e) de faire cela ?")
                        .setDescription(String.format(
                                """
                                Veuillez confirmer votre choix en cliquant sur le bouton ci-dessous.
                                
                                -# Si vous ne confirmez pas, ce message expirera <t:%d:R>.
                                """,
                                timeoutTime
                        )).build()
                ).build();
    }
}
