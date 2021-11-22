package su.nexmedia.sunlight.modules.bans.command.warn;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.command.api.AbstractUnPunishCommand;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class UnwarnCommand extends AbstractUnPunishCommand {

    public static final String NAME = "unwarn";

    public UnwarnCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_UNWARN, PunishmentType.WARN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Unwarn_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Unwarn_Desc.getMsg();
    }
}
