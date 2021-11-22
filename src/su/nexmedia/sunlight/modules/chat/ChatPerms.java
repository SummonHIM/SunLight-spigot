package su.nexmedia.sunlight.modules.chat;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.chat.commands.channel.ChatChannelCommand;

public class ChatPerms {

    private static final String PREFIX = SunPerms.PREFIX + "chat.";

    public static final String COLOR = PREFIX + "color";
    public static final String SPY   = PREFIX + "spy";

    public static final String CMD_CHANNEL     = PREFIX + "cmd.channel.";
    public static final String CMD_CHATCHANNEL = PREFIX + "cmd." + ChatChannelCommand.NAME;

    public static final String CHANNEL_HEAR  = PREFIX + "channel.hear.";
    public static final String CHANNEL_SPEAK = PREFIX + "channel.speak.";

    public static final String BYPASS_CHANNEL_DISTANCE = PREFIX + "bypass.channel.distance";
    public static final String BYPASS_COOLDOWN_MESSAGE = PREFIX + "bypass.cooldown.message";
    public static final String BYPASS_COOLDOWN_COMMAND = PREFIX + "bypass.cooldown.command";
    public static final String BYPASS_ANTICAPS         = PREFIX + "bypass.anticaps";
    public static final String BYPASS_ANTISPAM         = PREFIX + "bypass.antispam";
    public static final String BYPASS_RULES            = PREFIX + "bypass.rules";
}
