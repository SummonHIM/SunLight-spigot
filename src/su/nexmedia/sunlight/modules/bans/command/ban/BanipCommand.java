package su.nexmedia.sunlight.modules.bans.command.ban;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.command.api.AbstractPunishCommand;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class BanipCommand extends AbstractPunishCommand {

    public static final String NAME = "banip";

    public BanipCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_BANIP, PunishmentType.BAN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Banip_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Banip_Desc.getMsg();
    }

    @Override
    @NotNull
    protected String fineUserName(@NotNull CommandSender sender, @NotNull String userName) {
        SunUser user = plugin.getUserManager().getOrLoadUser(userName, false);
        if (user != null) {
            userName = user.getIp();
        }
        return super.fineUserName(sender, userName);
    }

    @Override
    protected boolean checkUserName(@NotNull CommandSender sender, @NotNull String userName) {
        if (!RegexUT.isIpAddress(userName)) {
            module.getLang().Error_NotIP.replace("%ip%", userName).send(sender);
            return false;
        }
        return true;
    }
}
