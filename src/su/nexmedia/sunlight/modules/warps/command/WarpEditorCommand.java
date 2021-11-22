package su.nexmedia.sunlight.modules.warps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.WarpPerms;

public class WarpEditorCommand extends SunModuleCommand<WarpManager> {

    public static final String NAME = "warpeditor";

    public WarpEditorCommand(@NotNull WarpManager module) {
        super(module, CommandConfig.getAliases(NAME), WarpPerms.CMD_WARP_EDITOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_WarpEditor_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        this.module.getEditor().open(player, 1);
    }
}
