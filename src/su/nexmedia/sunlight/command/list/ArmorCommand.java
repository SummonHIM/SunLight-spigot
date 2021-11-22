package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class ArmorCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "armor";

    public ArmorCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_ARMOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Armor_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Armor_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.printUsage(sender);
            return;
        }

        Player pSender = (Player) sender;
        Player pTarget = plugin.getServer().getPlayer(args[0]);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        new ArmorMenu(plugin, pTarget).open(pSender, 1);
    }

    static class ArmorMenu extends AbstractMenu<SunLight> {

        private final Player target;

        public ArmorMenu(@NotNull SunLight plugin, @NotNull Player target) {
            super(plugin, target.getName(), 9);
            this.target = target;
        }

        @Override
        public boolean cancelClick(@NotNull SlotType slotType, int slot) {
            return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER && slot >= 4;
        }

        @Override
        public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
            inventory.setContents(this.target.getInventory().getArmorContents());
        }

        @Override
        public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public void onClick(@NotNull Player player, @Nullable ItemStack item, int slot, @NotNull InventoryClickEvent e) {
            if (this.target == null) {
                player.closeInventory();
                return;
            }

            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                ItemStack[] cont = new ItemStack[4];
                for (int i = 0; i < 4; i++) {
                    cont[i] = e.getInventory().getItem(i);
                }
                this.target.getInventory().setArmorContents(cont);
                this.target.updateInventory();
            });
        }

        @Override
        public boolean destroyWhenNoViewers() {
            return true;
        }
    }
}
