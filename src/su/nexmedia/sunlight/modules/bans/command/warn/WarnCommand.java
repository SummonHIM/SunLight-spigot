package su.nexmedia.sunlight.modules.bans.command.warn;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.command.api.AbstractPunishCommand;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class WarnCommand extends AbstractPunishCommand {

    public static final String NAME = "warn";

    public WarnCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_WARN, PunishmentType.WARN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Warn_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Warn_Desc.getMsg();
    }

    @Override
    protected boolean checkUserName(@NotNull CommandSender sender, @NotNull String userName) {
        SunUser user = plugin.getUserManager().getOrLoadUser(userName, false);
        if (user == null) {
            this.errorPlayer(sender);
            return false;
        }
        return super.checkUserName(sender, userName);
    }
}
