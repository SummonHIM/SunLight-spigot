package su.nexmedia.sunlight.modules.scoreboard.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.scoreboard.ScoreboardManager;
import su.nexmedia.sunlight.modules.scoreboard.ScoreboardPerms;

import java.util.List;

public class ScoreboardCommand extends SunModuleCommand<ScoreboardManager> {

    public static final String NAME = "scoreboard";

    public ScoreboardCommand(@NotNull ScoreboardManager scoreboardManager) {
        super(scoreboardManager, CommandConfig.getAliases(NAME), ScoreboardPerms.CMD_SCOREBOARD);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Scoreboard_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<@NotNull String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SunUser user = plugin.getUserManager().getOrLoadUser(player);

        this.module.toggleBoard(player);
        user.setSettingBoolean(ScoreboardManager.SETTING_SCOREBOARD, this.module.hasBoard(player));

        this.module.getLang().Command_Scoreboard_Toggle
            .replace("%state%", plugin.lang().getOnOff(this.module.isScoreboardEnabled(player)))
            .send(sender);
    }
}
