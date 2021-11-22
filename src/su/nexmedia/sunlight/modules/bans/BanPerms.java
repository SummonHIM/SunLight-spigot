package su.nexmedia.sunlight.modules.bans;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.bans.command.KickCommand;
import su.nexmedia.sunlight.modules.bans.command.ban.BanCommand;
import su.nexmedia.sunlight.modules.bans.command.ban.BanipCommand;
import su.nexmedia.sunlight.modules.bans.command.ban.UnbanCommand;
import su.nexmedia.sunlight.modules.bans.command.history.BanHistoryCommand;
import su.nexmedia.sunlight.modules.bans.command.history.MuteHistoryCommand;
import su.nexmedia.sunlight.modules.bans.command.history.WarnHistoryCommand;
import su.nexmedia.sunlight.modules.bans.command.list.BanListCommand;
import su.nexmedia.sunlight.modules.bans.command.list.MuteListCommand;
import su.nexmedia.sunlight.modules.bans.command.list.WarnListCommand;
import su.nexmedia.sunlight.modules.bans.command.mute.MuteCommand;
import su.nexmedia.sunlight.modules.bans.command.mute.UnmuteCommand;
import su.nexmedia.sunlight.modules.bans.command.warn.UnwarnCommand;
import su.nexmedia.sunlight.modules.bans.command.warn.WarnCommand;

public class BanPerms {

    private static final String PREFIX = SunPerms.PREFIX + "bans.";

    public static final String CMD_BAN                = PREFIX + "cmd." + BanCommand.NAME;
    public static final String CMD_BANIP              = PREFIX + "cmd." + BanipCommand.NAME;
    public static final String CMD_KICK               = PREFIX + "cmd." + KickCommand.NAME;
    public static final String CMD_MUTE               = PREFIX + "cmd." + MuteCommand.NAME;
    public static final String CMD_UNBAN              = PREFIX + "cmd." + UnbanCommand.NAME;
    public static final String CMD_UNMUTE             = PREFIX + "cmd." + UnmuteCommand.NAME;
    public static final String CMD_UNWARN             = PREFIX + "cmd." + UnwarnCommand.NAME;
    public static final String CMD_WARN               = PREFIX + "cmd." + WarnCommand.NAME;
    public static final String CMD_MUTEHISTORY        = PREFIX + "cmd." + MuteHistoryCommand.NAME;
    public static final String CMD_MUTEHISTORY_OTHERS = PREFIX + "cmd." + MuteHistoryCommand.NAME + ".others";
    public static final String CMD_BANHISTORY         = PREFIX + "cmd." + BanHistoryCommand.NAME;
    public static final String CMD_BANHISTORY_OTHERS  = PREFIX + "cmd." + BanHistoryCommand.NAME + ".others";
    public static final String CMD_WARNHISTORY        = PREFIX + "cmd." + WarnHistoryCommand.NAME;
    public static final String CMD_WARNHISTORY_OTHERS = PREFIX + "cmd." + WarnHistoryCommand.NAME + ".others";
    public static final String CMD_BANLIST            = PREFIX + "cmd." + BanListCommand.NAME;
    public static final String CMD_MUTELIST           = PREFIX + "cmd." + MuteListCommand.NAME;
    public static final String CMD_WARNLIST           = PREFIX + "cmd." + WarnListCommand.NAME;
    public static final String HISTORY_REMOVE         = PREFIX + "history.remove";
    public static final String HISTORY_EXPIRE         = PREFIX + "history.expire";
}
