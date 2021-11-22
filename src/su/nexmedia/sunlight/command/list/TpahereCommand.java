package su.nexmedia.sunlight.command.list;

import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.ITeleportRequestCommand;
import su.nexmedia.sunlight.config.CommandConfig;

public class TpahereCommand extends ITeleportRequestCommand {

    public static final String NAME = "tpahere";

    public TpahereCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_TPAHERE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_TeleportRequest_Summon_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_TeleportRequest_Summon_Desc.getMsg();
    }

    @Override
    public boolean isSummon() {
        return true;
    }

    @Override
    @NotNull
    public ILangMsg getNotifyFromMessage() {
        return plugin.lang().Command_TeleportRequest_Summon_Notify_From;
    }

    @Override
    @NotNull
    public ILangMsg getNotifyToMessage() {
        return plugin.lang().Command_TeleportRequest_Summon_Notify_To;
    }
}
