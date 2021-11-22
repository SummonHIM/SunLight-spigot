package su.nexmedia.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.manager.api.Cleanable;
import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.IToggleCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

import java.util.Arrays;
import java.util.List;

public class NoPhantomCommand extends IToggleCommand implements Cleanable {

    public static final String NAME = "nophantom";
    private PhantomTask phantomTask;

    public NoPhantomCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_NOPHANTOM);

        this.phantomTask = new PhantomTask(plugin);
        this.phantomTask.start();
    }

    @Override
    public void clear() {
        if (this.phantomTask != null) {
            this.phantomTask.stop();
            this.phantomTask = null;
        }
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_NoPhantom_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_NoPhantom_Desc.getMsg();
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.asList("0", "1");
        }
        if (i == 2) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected boolean toggle(@NotNull Player player, boolean state, boolean reverse) {
        SunUser userTarget = plugin.getUserManager().getOrLoadUser(player);

        if (reverse) state = !userTarget.isAntiPhantom();
        userTarget.setAntiPhantom(state);
        return state;
    }

    @Override
    @NotNull
    protected String getPermissionOthers() {
        return SunPerms.CMD_NOPHANTOM_OTHERS;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageSelf() {
        return plugin.lang().Command_NoPhantom_Toggle_Self;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageOthers() {
        return plugin.lang().Command_NoPhantom_Toggle_Others;
    }

    static class PhantomTask extends ITask<SunLight> {

        public PhantomTask(@NotNull SunLight plugin) {
            super(plugin, 600, false);
        }

        @Override
        public void action() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                SunUser user = plugin.getUserManager().getOrLoadUser(player);
                if (!user.isAntiPhantom()) continue;

                plugin.getSunNMS().resetSleepTime(player);
            }
        }
    }
}
