package su.nexmedia.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.IToggleCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

public class SocialSpyCommand extends IToggleCommand {

    public static final String NAME = "socialspy";

    public SocialSpyCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_SOCIALSPY);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_SocialSpy_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_SocialSpy_Desc.getMsg();
    }

    @Override
    protected boolean toggle(@NotNull Player player, boolean state, boolean reverse) {
        SunUser userTarget = plugin.getUserManager().getOrLoadUser(player);

        if (reverse) state = !userTarget.isSocialSpy();
        userTarget.setSocialSpy(state);
        return state;
    }

    @Override
    @NotNull
    protected String getPermissionOthers() {
        return SunPerms.CMD_SOCIALSPY_OTHERS;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageSelf() {
        return plugin.lang().Command_SocialSpy_Toggle_Self;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageOthers() {
        return plugin.lang().Command_SocialSpy_Toggle_Others;
    }
}
