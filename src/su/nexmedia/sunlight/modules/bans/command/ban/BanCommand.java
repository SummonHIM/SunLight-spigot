package su.nexmedia.sunlight.modules.bans.command.ban;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.command.api.AbstractPunishCommand;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class BanCommand extends AbstractPunishCommand {

    public static final String NAME = "ban";

    public BanCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_BAN, PunishmentType.BAN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Ban_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Ban_Desc.getMsg();
    }
}
