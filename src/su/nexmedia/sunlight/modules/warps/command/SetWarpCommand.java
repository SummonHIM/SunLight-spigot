package su.nexmedia.sunlight.modules.warps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.WarpPerms;

public class SetWarpCommand extends SunModuleCommand<WarpManager> {

    public static final String NAME = "setwarp";

    public SetWarpCommand(@NotNull WarpManager warpManager) {
        super(warpManager, CommandConfig.getAliases(NAME), WarpPerms.CMD_SETWARP);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_SetWarp_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_SetWarp_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String warp = args[0];
        this.module.create(player, warp, false);
    }
}
