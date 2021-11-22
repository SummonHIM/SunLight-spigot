package su.nexmedia.sunlight.modules.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.modules.chat.config.ChatConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatUtils {

    private static final Map<String, String> LAST_MESSAGE = new HashMap<>();
    private static final Map<String, String> LAST_COMMAND = new HashMap<>();

    private static final Map<String, Map<String, Long>> NEXT_MESSAGE_IN = new HashMap<>();
    private static final Map<String, Long>              NEXT_COMMAND_IN = new HashMap<>();

    public static void clear(@NotNull Player player) {
        String key = player.getName();
        LAST_MESSAGE.remove(key);
        LAST_COMMAND.remove(key);
        NEXT_MESSAGE_IN.getOrDefault(key, Collections.emptyMap()).values().removeIf(date -> date < System.currentTimeMillis());
        NEXT_COMMAND_IN.values().removeIf(date -> date < System.currentTimeMillis());
    }


    public static void setLastMessage(@NotNull Player player, @NotNull String message) {
        LAST_MESSAGE.put(player.getName(), StringUT.colorOff(message));
    }

    @Nullable
    public static String getLastMessage(@NotNull Player player) {
        return LAST_MESSAGE.get(player.getName());
    }

    public static void setLastCommand(@NotNull Player player, @NotNull String command) {
        LAST_COMMAND.put(player.getName(), StringUT.colorOff(command));
    }

    @Nullable
    public static String getLastCommand(@NotNull Player player) {
        return LAST_COMMAND.get(player.getName());
    }


    public static long getNextCommandTime(@NotNull Player player) {
        return NEXT_COMMAND_IN.getOrDefault(player.getName(), 0L);
    }

    public static void setNextCommandTime(@NotNull Player player) {
        if (ChatConfig.ANTI_SPAM_COMMAND_COOLDOWN <= 0) return;

        NEXT_COMMAND_IN.put(player.getName(), (long) (System.currentTimeMillis() + (ChatConfig.ANTI_SPAM_COMMAND_COOLDOWN * 1000D)));
    }

    public static boolean isNextCommandAvailable(@NotNull Player player) {
        return System.currentTimeMillis() > getNextCommandTime(player);
    }


    public static void setNextMessageTime(@NotNull Player player, @NotNull ChatChannel channel) {
        if (channel.getMessageCooldown() <= 0) return;

        NEXT_MESSAGE_IN.computeIfAbsent(player.getName(), k -> new HashMap<>())
            .put(channel.getId(), System.currentTimeMillis() + channel.getMessageCooldown() * 1000L);
    }

    public static long getNextMessageTime(@NotNull Player player, @NotNull ChatChannel channel) {
        return NEXT_MESSAGE_IN.getOrDefault(player.getName(), Collections.emptyMap())
            .getOrDefault(channel.getId(), 0L);
    }

    public static boolean isNextMessageAvailable(@NotNull Player player, @NotNull ChatChannel channel) {
        return System.currentTimeMillis() > getNextMessageTime(player, channel);
    }


    @NotNull
    public static String doAntiCaps(@NotNull String msgReal) {
        if (!ChatConfig.ANTI_CAPS_ENABLED) return msgReal;

        String msgRaw = StringUT.colorOff(msgReal);

        // Ignore player names in case if they are fully caps
        for (String name : PlayerUT.getPlayerNames()) {
            msgRaw = msgRaw.replace(name, "");
        }
        for (String ignored : ChatConfig.ANTI_CAPS_IGNORED_WORDS) {
            msgRaw = msgRaw.replace(ignored, "");
        }

        // Then check if message has enought length to check
        if (msgRaw.length() < ChatConfig.ANTI_CAPS_MESSAGE_LENGTH_MIN) return msgReal;

        double uppers = 0;
        double length = 0;
        for (char char2 : msgRaw.toCharArray()) {
            if (!Character.isLetter(char2) || Character.isWhitespace(char2)) continue;
            if (Character.isUpperCase(char2)) uppers++;
            length++;
        }
        double percent = uppers / length * 100D;
        return percent >= ChatConfig.ANTI_CAPS_UPPER_LETTER_PERCENT_MIN ? msgReal.toLowerCase() : msgReal;
    }


    public static boolean checkSpamSimilarMessage(@NotNull Player player, @NotNull String msgRaw) {
        return checkSpamSimilar(player, msgRaw, getLastMessage(player));
    }

    public static boolean checkSpamSimilarCommand(@NotNull Player player, @NotNull String msgRaw) {
        return checkSpamSimilar(player, msgRaw, getLastCommand(player));
    }

    private static boolean checkSpamSimilar(@NotNull Player player, @NotNull String msgRaw, @Nullable String msgLast) {
        if (!ChatConfig.ANTI_SPAM_ENABLED) return true;
        if (msgLast == null || msgLast.isEmpty()) return true;

        double similarity = similarity(msgRaw, msgLast);
        return !(similarity >= ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_PERCENT / 100D);
    }


    private static double similarity(@NotNull String s1, @NotNull String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    private static int editDistance(@NotNull String s1, @NotNull String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
