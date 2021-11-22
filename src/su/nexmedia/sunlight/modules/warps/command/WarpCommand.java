package su.nexmedia.sunlight.modules.warps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.warps.Warp;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.WarpPerms;

import java.util.List;

public class WarpCommand extends SunModuleCommand<WarpManager> {

    public static final String NAME = "warp";

    public WarpCommand(@NotNull WarpManager warpManager) {
        super(warpManager, CommandConfig.getAliases(NAME), WarpPerms.CMD_WARP);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_Warp_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Warp_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return this.module.getWarpIdsFor(player);
        }
        if (i == 2) {
            if (player.hasPermission(WarpPerms.CMD_WARP_OTHERS)) {
                return PlayerUT.getPlayerNames();
            }
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                this.printUsage(sender);
                return;
            }
            this.module.getWarpMenuMain().open(player, 1);
            return;
        }

        if (args.length >= 2 && !sender.hasPermission(WarpPerms.CMD_WARP_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String warpId = args[0];
        Warp warp = this.module.getWarpById(warpId);
        if (warp == null) {
            this.module.getLang().Warp_Error_Invalid.replace(Warp.PLACEHOLDER_ID, warpId).send(sender);
            return;
        }

        String pName = args.length >= 2 ? args[1] : sender.getName();
        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        boolean isForced = !(sender.equals(pTarget));
        if (isForced) {
            this.module.getLang().Command_Warps_Done_Others
                .replace("%player%", pTarget.getName()).replace(warp.replacePlaceholders()).send(sender);
        }
        warp.teleport(pTarget, isForced);
    }
}
