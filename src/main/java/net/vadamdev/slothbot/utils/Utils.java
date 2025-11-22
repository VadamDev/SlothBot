package net.vadamdev.slothbot.utils;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.dbk.interactive.api.components.InteractiveComponent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /*
       Misc
     */

    public static boolean isURL(String str) {
        try {
            new URL(str); //Deprecated since Java 20. TOO BAD!
            return true;
        }catch (MalformedURLException ignored) {
            return false;
        }
    }

    public static String formatMsToHMS(long ms) {
        final StringBuilder result = new StringBuilder();

        final long hours = (ms / 3600000) % 24;
        if(hours > 0)
            result.append(hours + "h ");

        final long minutes = (ms / 60000) % 60;
        if(minutes > 0)
            result.append(minutes + "m ");

        final long seconds = (ms / 1000) % 60;
        if(seconds > 0)
            result.append(seconds + "s");

        return result.toString();
    }

    public static EmojiUnion formatDigitToDiscordEmoji(int digit) {
        //You cannot format theses unicodes inside Intellij, discord doesn't like it. WTF?
        final String unicode = switch(digit) {
            case 0 -> "0\uFE0F⃣";
            case 1 -> "1\uFE0F⃣";
            case 2 -> "2\uFE0F⃣";
            case 3 -> "3\uFE0F⃣";
            case 4 -> "4\uFE0F⃣";
            case 5 -> "5\uFE0F⃣";
            case 6 -> "6\uFE0F⃣";
            case 7 -> "7\uFE0F⃣";
            case 8 -> "8\uFE0F⃣";
            case 9 -> "9\uFE0F⃣";
            case 10 -> "\uD83D\uDD1F";
            default -> "❔";
        };

        return Emoji.fromFormatted(unicode);
    }

    /*
       Youtube Thumbnail
     */

    //Thanks to https://gist.github.com/jvanderwee/b30fdb496acff43aef8e
    private static final Pattern youtubeRegex = Pattern.compile("^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/");
    private static final Pattern[] videoIdRegex = {
            Pattern.compile("\\?vi?=([^&]*)"),
            Pattern.compile("watch\\?.*v=([^&]*)"),
            Pattern.compile("(?:embed|vi?)/([^/?]*)"),
            Pattern.compile("^([A-Za-z0-9\\-]*)")
    };

    @Nullable
    public static String retrieveVideoThumbnail(String url) {
        final Matcher urlMatcher = youtubeRegex.matcher(url);
        final String youTubeLinkWithoutProtocolAndDomain = urlMatcher.find() ? url.replace(urlMatcher.group(), "") : url;

        for(Pattern pattern : videoIdRegex) {
            final Matcher matcher = pattern.matcher(youTubeLinkWithoutProtocolAndDomain);
            if(!matcher.find())
                continue;

            return "https://img.youtube.com/vi/" + matcher.group(1) + "/0.jpg";
        }

        return null;
    }
}
