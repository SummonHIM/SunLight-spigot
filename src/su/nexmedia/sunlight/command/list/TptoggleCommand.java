package su.nexmedia.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.IToggleCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

public class TptoggleCommand extends IToggleCommand {

    public static final String NAME = "tptoggle";

    public TptoggleCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_TPTOGGLE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_TpToggle_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_TpToggle_Desc.getMsg();
    }

    @Override
    protected boolean toggle(@NotNull Player player, boolean state, boolean reverse) {
        SunUser userTarget = plugin.getUserManager().getOrLoadUser(player);

        if (reverse) state = !userTarget.isTeleportRequestsEnabled();
        userTarget.setTeleportRequestsEnabled(state);

        return state;
    }

    @Override
    @NotNull
    protected String getPermissionOthers() {
        return SunPerms.CMD_TPTOGGLE_OTHERS;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageSelf() {
        return plugin.lang().Command_TpToggle_Toggle_Self;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageOthers() {
        return plugin.lang().Command_TpToggle_Toggle_Others;
    }
}
