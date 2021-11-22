package su.nexmedia.sunlight.modules.enhancements.module.chestsort;

import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.enhancements.EnhancementManager;
import su.nexmedia.sunlight.modules.enhancements.module.chestsort.command.ChestSortCommand;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ChestSortManager extends AbstractManager<SunLight> {

    public static final  String       USER_SETTING_CHEST_SORT = "chest_sort";
    private static final List<String> MATERIAL_COLORS;

    static {
        MATERIAL_COLORS = Arrays.stream(DyeColor.values()).map(DyeColor::name).sorted(String::compareTo).toList();
    }

    private final EnhancementManager enhancementManager;
    private String sortOrder;

    public ChestSortManager(@NotNull EnhancementManager enhancementManager) {
        super(enhancementManager.plugin());
        this.enhancementManager = enhancementManager;
    }

    @Override
    public void onLoad() {
        JYML cfg = JYML.loadOrExtract(plugin, this.enhancementManager.getPath() + "chestsort.yml");

        StringBuilder idBuilder = new StringBuilder();
        cfg.getStringList("Sort_Order").forEach(idBuilder::append);
        this.sortOrder = idBuilder.toString();

        this.addListener(new Listener(this.plugin));
        this.plugin.getCommandRegulator().register(new ChestSortCommand(this.enhancementManager));
    }

    @Override
    public void onShutdown() {

    }

    @NotNull
    private String getItemSortedId(@NotNull ItemStack item) {
        Material material = item.getType();
        String matName = material.name();
        boolean isBlock = material.isBlock() && material.isSolid();
        String color = MATERIAL_COLORS.stream().filter(matName::startsWith).findAny().orElse("");

        return this.sortOrder
            .replace("%BLOCK%", isBlock ? "A" : "B")
            .replace("%ITEM%", !isBlock ? "A" : "B")
            .replace("%MATERIAL%", matName.replace(color + "_", "")) // Colored items all together
            .replace("%COLOR%", color)
            .replace("%AMOUNT%", String.valueOf(64 - item.getAmount())) // Item count up -> down
            .replace("%NAME%", ItemUT.getItemName(item));
    }

    public void sortInventory(@NotNull Inventory inventory) {
        List<ItemStack> sorted = Arrays.stream(inventory.getContents()).filter(Predicate.not(ItemUT::isAir))
            .sorted(Comparator.comparing(this::getItemSortedId)).toList();

        inventory.clear();

        for (int slot = 0; slot < sorted.size(); slot++) {
            inventory.setItem(slot, sorted.get(slot));
        }
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onSortInventoryClose(InventoryCloseEvent e) {
            Player player = (Player) e.getPlayer();
            if (player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }

            Inventory inventory = e.getInventory();
            InventoryHolder holder = inventory.getHolder();
            if (!(holder instanceof Chest) && !(holder instanceof DoubleChest) && !(holder instanceof ShulkerBox)) {
                return;
            }

            SunUser user = plugin.getUserManager().getOrLoadUser(player);
            if (!user.getSettingBoolean(USER_SETTING_CHEST_SORT)) return;

            sortInventory(inventory);
        }
    }
}
