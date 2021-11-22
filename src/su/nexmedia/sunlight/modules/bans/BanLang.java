package su.nexmedia.sunlight.modules.bans;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.bans.punishment.Punishment;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class BanLang extends ILangTemplate {

    public BanLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

    @Override
    public void setup() {
        super.setup();
        this.setupEnum(PunishmentType.class);
    }

    public ILangMsg Kick_Kicked = new ILangMsg(this, """
        &c&nYou have been kicked from the server!
        &7
        &cReason: &e%punishment_reason%
        &cAdmin: &e%punishment_punisher%"""
    );
    public ILangMsg Kick_Banned_Perma = new ILangMsg(this, """
        &c&nYou have been banned on this server!
        &7
        &cReason: &e%punishment_reason% &7| &cAdmin: &e%punishment_punisher%
        &cBan Date: &e%punishment_date_created%
        &7
        &7Appeal at: &fwww.myserver.com"""
    );
    public ILangMsg Kick_Banned_Temp = new ILangMsg(this, """
        &c&nYou have been banned on this server!
        &7
        &cReason: &e%punishment_reason% &7| &cAdmin: &e%punishment_punisher%
        &cBan Date: &e%punishment_date_created%
        &7
        &cUnban In: &f%punishment_expires_in%
        &7
        &7Appeal at: &fwww.myserver.com"""
    );
    public ILangMsg Command_History_Mute_Desc  = new ILangMsg(this, "View player's mutes history.");
    public ILangMsg Command_History_Mute_Usage = new ILangMsg(this, "[player]");
    public ILangMsg Command_History_Ban_Desc   = new ILangMsg(this, "View player's bans history.");
    public ILangMsg Command_History_Ban_Usage  = new ILangMsg(this, "[player]");
    public ILangMsg Command_History_Warn_Desc  = new ILangMsg(this, "View player's warns history.");
    public ILangMsg Command_History_Warn_Usage = new ILangMsg(this, "[player]");

    public ILangMsg Command_List_Ban_Desc  = new ILangMsg(this, "List of all currently banned players and IPs.");
    public ILangMsg Command_List_Mute_Desc = new ILangMsg(this, "List of all currently muted players and IPs.");
    public ILangMsg Command_List_Warn_Desc = new ILangMsg(this, "List of all currently warned players.");

    public ILangMsg Command_Ban_Desc  = new ILangMsg(this, "Ban player.");
    public ILangMsg Command_Ban_Usage = new ILangMsg(this, "<player> <time> <reason>");

    public ILangMsg Command_Banip_Desc  = new ILangMsg(this, "Ban IP address.");
    public ILangMsg Command_Banip_Usage = new ILangMsg(this, "<player/ip> <time> <reason>");

    public ILangMsg Command_Mute_Desc  = new ILangMsg(this, "Mute player or players with IP.");
    public ILangMsg Command_Mute_Usage = new ILangMsg(this, "<player/ip> <time> <reason>");

    public ILangMsg Command_Kick_Desc  = new ILangMsg(this, "Kick player or players with IP.");
    public ILangMsg Command_Kick_Usage = new ILangMsg(this, "<player/ip> <reason>");

    public ILangMsg Command_Unmute_Desc  = new ILangMsg(this, "Unmute player or IP.");
    public ILangMsg Command_Unmute_Usage = new ILangMsg(this, "<player/ip>");

    public ILangMsg Command_Unban_Desc  = new ILangMsg(this, "Unban player or IP.");
    public ILangMsg Command_Unban_Usage = new ILangMsg(this, "<player/ip>");

    public ILangMsg Command_Warn_Desc  = new ILangMsg(this, "Warn player.");
    public ILangMsg Command_Warn_Usage = new ILangMsg(this, "<player> <time> <reason>");

    public ILangMsg Command_Unwarn_Desc  = new ILangMsg(this, "Remove the most recent warn of player.");
    public ILangMsg Command_Unwarn_Usage = new ILangMsg(this, "<player>");

    public ILangMsg Ban_User_Perma_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User &c%punishment_user% &7has been permanently banned: &c%punishment_reason%");
    public ILangMsg Ban_User_Perma_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7permanently banned &c%punishment_user%&7. Reason: &c%punishment_reason%");
    public ILangMsg Ban_User_Temp_Done       = new ILangMsg(this, "{message: ~prefix: false;}&7User &c%punishment_user% &7has been banned for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");
    public ILangMsg Ban_User_Temp_Broadcast  = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7banned &c%punishment_user% &7for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");

    public ILangMsg Ban_IP_Perma_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7IP &c%punishment_user% &7has been permanently banned: &c%punishment_reason%");
    public ILangMsg Ban_IP_Perma_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7permanently banned IP &c%punishment_user%&7. Reason: &c%punishment_reason%");
    public ILangMsg Ban_IP_Temp_Done       = new ILangMsg(this, "{message: ~prefix: false;}&7IP &c%punishment_user% &7has been banned for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");
    public ILangMsg Ban_IP_Temp_Broadcast  = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7banned IP &c%punishment_user% &7for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");

    public ILangMsg Unban_User_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User unbanned: &a%punishment_user%&7.");
    public ILangMsg Unban_User_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &a%admin% &7unbanned user: &a%punishment_user%&7.");
    public ILangMsg Unban_IP_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7IP unbanned: &a%punishment_user%&7.");
    public ILangMsg Unban_IP_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &a%admin% &7unbanned IP: &a%punishment_user%&7.");

    public ILangMsg Kick_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User &c%punishment_user% &7has been kicked. Reason: &c%punishment_reason%");
    public ILangMsg Kick_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7kicked &c%punishment_user%&7. Reason: &c%punishment_reason%");

    public ILangMsg Mute_User_Perma_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User &c%punishment_user% &7has been permanently muted: &c%punishment_reason%");
    public ILangMsg Mute_User_Perma_Notify    = new ILangMsg(this, """
        {message: ~prefix: false;}
        &8&m                                                       &7
        &c&l You have been permanently muted!
        &7
        &7       Reason: &c%punishment_reason%&7.
        &8&m                                                       &7""");
    public ILangMsg Mute_User_Perma_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7permanently muted &c%punishment_user%&7. Reason: &c%punishment_reason%");
    public ILangMsg Mute_User_Temp_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User &c%punishment_user% &7has been muted for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");
    public ILangMsg Mute_User_Temp_Notify    = new ILangMsg(this, """
        {message: ~prefix: false;}
        &8&m                                                       &7
        &c&l        You have been muted!
        &7
        &7       Reason: &c%punishment_reason%&7.
        &7         Expires in: &c%punishment_expires_in%
        &8&m                                                       &7""");
    public ILangMsg Mute_User_Temp_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7muted &c%punishment_user% &7for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");

    public ILangMsg Mute_IP_Perma_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7IP &c%punishment_user% &7has been permanently muted: &c%punishment_reason%");
    public ILangMsg Mute_IP_Perma_Notify    = new ILangMsg(this, """
        {message: ~prefix: false;}
        &8&m                                                       &7
        &c&l You have been permanently muted!
        &7
        &7       Reason: &c%punishment_reason%&7.
        &8&m                                                       &7""");
    public ILangMsg Mute_IP_Perma_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7permanently muted IP &c%punishment_user%&7. Reason: &c%punishment_reason%");
    public ILangMsg Mute_IP_Temp_Done       = new ILangMsg(this, "{message: ~prefix: false;}&7IP &c%punishment_user% &7has been muted for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");
    public ILangMsg Mute_IP_Temp_Notify     = new ILangMsg(this, """
        {message: ~prefix: false;}
        &8&m                                                       &7
        &c&l        You have been muted!
        &7
        &7       Reason: &c%punishment_reason%&7.
        &7         Expires in: &c%punishment_expires_in%
        &8&m                                                       &7""");
    public ILangMsg Mute_IP_Temp_Broadcast  = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7muted IP &c%punishment_user% &7for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%");

    public ILangMsg Unmute_User_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User unmuted: &a%punishment_user%&7.");
    public ILangMsg Unmute_User_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &a%admin% &7unmuted user: &a%punishment_user%&7.");
    public ILangMsg Unmute_IP_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7IP unmuted: &a%punishment_user%&7.");
    public ILangMsg Unmute_IP_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &a%admin% &7unmuted IP: &a%punishment_user%&7.");

    public ILangMsg Warn_User_Temp_Done       = new ILangMsg(this, "{message: ~prefix: false;}&7User &c%punishment_user% &7has been warned for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%&7.");
    public ILangMsg Warn_User_Temp_Notify     = new ILangMsg(this, """
        {message: ~prefix: false;}
        &8&m                                                       &7
        &c&l        You have been warned!
        &7
        &7       Reason: &c%punishment_reason%&7.
        &7         Expires in: &c%punishment_expires_in%
        &8&m                                                       &7""");
    public ILangMsg Warn_User_Temp_Broadcast  = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7warned &c%punishment_user%&7 for &c%punishment_expires_in%&7. Reason: &c%punishment_reason%&7.");
    public ILangMsg Warn_User_Perma_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User &c%punishment_user% &7has been permanently warned for &c%punishment_reason%&7.");
    public ILangMsg Warn_User_Perma_Notify    = new ILangMsg(this, """
        {message: ~prefix: false;}
        &8&m                                                       &7
        &c&l        You have been warned!
        &7
        &7       Reason: &c%punishment_reason%&7.
        &7         Expires in: &c%punishment_expires_in%
        &8&m                                                       &7""");
    public ILangMsg Warn_User_Perma_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &c%punishment_punisher% &7permanently warned &c%punishment_user%&7 for &c%punishment_reason%&7.");

    public ILangMsg Unwarn_User_Done      = new ILangMsg(this, "{message: ~prefix: false;}&7User unwarned: &a%punishment_user%&7.");
    public ILangMsg Unwarn_User_Broadcast = new ILangMsg(this, "{message: ~prefix: false;}&7Admin &a%punishment_punisher% &7unwarned user: &a%punishment_user%&7.");

    public ILangMsg Error_Immune    = new ILangMsg(this, "{message: ~prefix: false;}&c%user% &7can not be punished.");
    public ILangMsg Error_NotBanned = new ILangMsg(this, "{message: ~prefix: false;}&c%user% &7is not banned.");
    public ILangMsg Error_NotMuted  = new ILangMsg(this, "{message: ~prefix: false;}&c%user% &7is not muted.");
    public ILangMsg Error_NotWarned = new ILangMsg(this, "{message: ~prefix: false;}&c%user% &7does not have active warns.");
    public ILangMsg Error_NotIP     = new ILangMsg(this, "{message: ~prefix: false;}&c%ip% &7is not IP address!");
    public ILangMsg Other_Never = new ILangMsg(this, "Never");

    @NotNull
    public ILangMsg getForNotPunished(@NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> this.Error_NotBanned;
            case MUTE -> this.Error_NotMuted;
            case WARN -> this.Error_NotWarned;
        };
    }

    @NotNull
    public ILangMsg getForUser(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        if (punishment.isPermanent()) {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? this.Mute_IP_Perma_Notify : this.Mute_User_Perma_Notify;
                case BAN -> this.Kick_Banned_Perma;
                case WARN -> this.Warn_User_Perma_Notify;
            };
        }
        else {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? this.Mute_IP_Temp_Notify : this.Mute_User_Temp_Notify;
                case BAN -> this.Kick_Banned_Temp;
                case WARN -> this.Warn_User_Temp_Notify;
            };
        }
    }

    @NotNull
    public ILangMsg getForAdmin(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        if (punishment.isPermanent()) {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? this.Mute_IP_Perma_Done : this.Mute_User_Perma_Done;
                case BAN -> isIp ? this.Ban_IP_Perma_Done : this.Ban_User_Perma_Done;
                case WARN -> this.Warn_User_Perma_Done;
            };
        }
        else {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? this.Mute_IP_Temp_Done : this.Mute_User_Temp_Done;
                case BAN -> isIp ? this.Ban_IP_Temp_Done : this.Ban_User_Temp_Done;
                case WARN -> this.Warn_User_Temp_Done;
            };
        }
    }

    @NotNull
    public ILangMsg getForBroadcast(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        if (punishment.isPermanent()) {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? this.Mute_IP_Perma_Broadcast : this.Mute_User_Perma_Broadcast;
                case BAN -> isIp ? this.Ban_IP_Perma_Broadcast : this.Ban_User_Perma_Broadcast;
                case WARN -> this.Warn_User_Perma_Broadcast;
            };
        }
        else {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? this.Mute_IP_Temp_Broadcast : this.Mute_User_Temp_Broadcast;
                case BAN -> isIp ? this.Ban_IP_Temp_Broadcast : this.Ban_User_Temp_Broadcast;
                case WARN -> this.Warn_User_Temp_Broadcast;
            };
        }
    }

    @NotNull
    public ILangMsg getForgiveForAdmin(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        return switch (punishment.getType()) {
            case MUTE -> isIp ? this.Unmute_IP_Done : this.Unmute_User_Done;
            case BAN -> isIp ? this.Unban_IP_Done : this.Unban_User_Done;
            case WARN -> this.Unwarn_User_Done;
        };
    }

    @NotNull
    public ILangMsg getForgiveForBroadcast(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        return switch (punishment.getType()) {
            case MUTE -> isIp ? this.Unmute_IP_Broadcast : this.Unmute_User_Broadcast;
            case BAN -> isIp ? this.Unban_IP_Broadcast : this.Unban_User_Broadcast;
            case WARN -> this.Unwarn_User_Broadcast;
        };
    }
}
