package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Collections;
import java.util.List;

public class RepairCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "repair";

    public RepairCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_REPAIR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Repair_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Repair_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Collections.singletonList("all");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        boolean all = args.length == 1 && args[0].equalsIgnoreCase("ALL");
        Player player = (Player) sender;

        if (!all) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!this.fix(item)) {
                this.errorItem(sender);
                return;
            }
            plugin.lang().Command_Repair_Done_Hand.send(sender);
        }
        else {
            for (ItemStack item : player.getInventory().getContents()) {
                if (!ItemUT.isAir(item)) {
                    this.fix(item);
                }
            }
            plugin.lang().Command_Repair_Done_All.send(sender);
        }
    }

    private boolean fix(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;

        damageable.setDamage(0);
        item.setItemMeta(meta);
        return true;
    }
}
