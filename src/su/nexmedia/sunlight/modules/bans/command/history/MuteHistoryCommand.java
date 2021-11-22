package su.nexmedia.sunlight.modules.bans.command.history;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.bans.BanPerms;
import su.nexmedia.sunlight.modules.bans.command.api.AbstractHistoryCommand;
import su.nexmedia.sunlight.modules.bans.punishment.PunishmentType;

public class MuteHistoryCommand extends AbstractHistoryCommand {

    public static final String NAME = "mutehistory";

    public MuteHistoryCommand(@NotNull BanManager banManager) {
        super(banManager, CommandConfig.getAliases(NAME), BanPerms.CMD_MUTEHISTORY, PunishmentType.MUTE);
    }

    @Override
    @NotNull
    protected String getPermissionOthers() {
        return BanPerms.CMD_MUTEHISTORY_OTHERS;
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_History_Mute_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_History_Mute_Desc.getMsg();
    }
}
