package su.nexmedia.sunlight.modules.bans.command.mute;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.command.api.AbstractPunishCommand;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class MuteCommand extends AbstractPunishCommand {

    public static final String NAME = "mute";

    public MuteCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_MUTE, PunishmentType.MUTE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return module.getLang().Command_Mute_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return module.getLang().Command_Mute_Desc.getMsg();
    }
}
