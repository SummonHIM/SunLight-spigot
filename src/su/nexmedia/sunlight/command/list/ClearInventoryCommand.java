package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class ClearInventoryCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "clearinventory";

    public ClearInventoryCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_CLEARINVENTORY);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_ClearInv_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_ClearInv_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(SunPerms.CMD_CLEARINVENTORY_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            this.printUsage(sender);
            return;
        }

        String pName = sender.getName();
        if (args.length == 1) {
            if (!sender.hasPermission(SunPerms.CMD_CLEARINVENTORY_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            pName = args[0];
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        pTarget.getInventory().clear();
        pTarget.getInventory().setArmorContents(new ItemStack[4]);

        if (!sender.equals(pTarget)) {
            plugin.lang().Command_ClearInv_Done_Others
                .replace("%player%", pTarget.getName())
                .send(sender);
        }
        plugin.lang().Command_ClearInv_Done_Self.send(pTarget);
    }
}
