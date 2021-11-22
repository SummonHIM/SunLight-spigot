package su.nexmedia.sunlight.modules.bans.command.ban;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.command.api.AbstractUnPunishCommand;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class UnbanCommand extends AbstractUnPunishCommand {

    public static final String NAME = "unban";

    public UnbanCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_UNBAN, PunishmentType.BAN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Unban_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Unban_Desc.getMsg();
    }
}
