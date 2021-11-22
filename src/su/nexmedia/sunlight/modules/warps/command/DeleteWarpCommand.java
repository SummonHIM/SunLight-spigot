package su.nexmedia.sunlight.modules.warps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.warps.Warp;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.WarpPerms;

import java.util.List;

public class DeleteWarpCommand extends SunModuleCommand<WarpManager> {

    public static final String NAME = "deletewarp";

    public DeleteWarpCommand(@NotNull WarpManager warpManager) {
        super(warpManager, CommandConfig.getAliases(NAME), WarpPerms.CMD_DELWARP);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_DeleteWarp_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_DeleteWarp_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            if (player.hasPermission(WarpPerms.CMD_DELWARP_OTHERS)) {
                return this.module.getWarpIds();
            }
            else {
                return this.module.getWarpIdsOf(player);
            }
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        Warp warp = this.module.getWarpById(args[0]);
        if (warp == null) {
            this.module.getLang().Warp_Error_Invalid.replace(Warp.PLACEHOLDER_ID, args[0]).send(sender);
            return;
        }

        if (sender instanceof Player player) {
            if (!warp.isOwner(player)) {
                if (!sender.hasPermission(WarpPerms.CMD_DELWARP_OTHERS)) {
                    this.errorPermission(sender);
                    return;
                }
            }
        }

        this.module.delete(warp);
        this.module.getLang().Command_DeleteWarp_Done.replace(warp.replacePlaceholders()).send(sender);
    }
}
