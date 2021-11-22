package su.nexmedia.sunlight.modules.chat.config;

import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.modules.chat.ChatManager;

import java.util.Set;

public class ChatConfig {

    public static EventPriority CHAT_EVENT_PRIORITY;

    public static boolean     ANTI_CAPS_ENABLED;
    public static int         ANTI_CAPS_MESSAGE_LENGTH_MIN;
    public static int         ANTI_CAPS_UPPER_LETTER_PERCENT_MIN;
    public static Set<String> ANTI_CAPS_AFFECTED_COMMANDS;
    public static Set<String> ANTI_CAPS_IGNORED_WORDS;

    public static boolean     ANTI_SPAM_ENABLED;
    public static double      ANTI_SPAM_BLOCK_SIMILAR_PERCENT;
    public static double      ANTI_SPAM_COMMAND_COOLDOWN;
    public static Set<String> ANTI_SPAM_COMMAND_WHITELIST;

    public static boolean ITEM_SHOW_ENABLED;
    public static String  ITEM_SHOW_PLACEHOLDER;
    public static String  ITEM_SHOW_FORMAT;

    public static void load(@NotNull ChatManager chatManager) {
        JYML cfg = chatManager.getConfig();

        CHAT_EVENT_PRIORITY = cfg.getEnum("Chat_Event_Priority", EventPriority.class, EventPriority.HIGH);

        String path = "Anti_Caps.";
        if (ANTI_CAPS_ENABLED = cfg.getBoolean(path + "Enabled")) {
            ANTI_CAPS_MESSAGE_LENGTH_MIN = cfg.getInt(path + "Message_Length_Min", 3);
            ANTI_CAPS_UPPER_LETTER_PERCENT_MIN = cfg.getInt(path + "Upper_Letters_Percent_Min", 80);
            ANTI_CAPS_AFFECTED_COMMANDS = cfg.getStringSet(path + "Affected_Commands");
            ANTI_CAPS_IGNORED_WORDS = cfg.getStringSet(path + "Ignored_Words");
        }

        path = "Anti_Spam.";
        if (ANTI_SPAM_ENABLED = cfg.getBoolean(path + "Enabled")) {
            if (cfg.getBoolean(path + "Block_Similar_Messages.Enabled")) {
                ANTI_SPAM_BLOCK_SIMILAR_PERCENT = cfg.getDouble(path + "Block_Similar_Messages.Percentage", 90);
            }
            ANTI_SPAM_COMMAND_COOLDOWN = cfg.getDouble(path + "Command_Cooldown");
            ANTI_SPAM_COMMAND_WHITELIST = cfg.getStringSet(path + "Command_Whitelist");
        }

        path = "Item_Show.";
        if (ITEM_SHOW_ENABLED = cfg.getBoolean(path + "Enabled")) {
            ITEM_SHOW_PLACEHOLDER = cfg.getString(path + "Placeholder");
            ITEM_SHOW_FORMAT = StringUT.color(cfg.getString(path + "Format", "%item%"));
        }
    }

}
