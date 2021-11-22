package su.nexmedia.sunlight.modules.kits.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.modules.kits.KitManager;

public class KitMenuPreview extends AbstractMenu<SunLight> {

    private static int[] ITEM_SLOTS  = new int[]{};
    private static int[] ARMOR_SLOTS = new int[4];
    private final Kit kit;

    public KitMenuPreview(@NotNull SunLight plugin, @NotNull KitManager kitManager, @NotNull Kit kit) {
        super(plugin, JYML.loadOrExtract(plugin, kitManager.getPath() + KitManager.YML_PREVIEW_MENU), "");

        this.kit = kit;
        ITEM_SLOTS = cfg.getIntArray("Item_Slots");
        ARMOR_SLOTS = cfg.getIntArray("Armor_Slots");

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type == MenuItemType.RETURN) {
                    kit.getKitManager().getKitsMenu().open(player, 1);
                }
            }
        };

        for (String sId : cfg.getSection("Content")) {
            IMenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
        int count = 0;
        for (ItemStack item : this.kit.getItems()) {
            if (count >= ITEM_SLOTS.length || count >= inventory.getSize()) break;
            inventory.setItem(ITEM_SLOTS[count++], item);
        }

        int armorCount = 0;
        for (ItemStack armor : this.kit.getArmor()) {
            if (armorCount >= ARMOR_SLOTS.length || armorCount >= inventory.getSize()) break;
            inventory.setItem(ARMOR_SLOTS[armorCount++], armor);
        }
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
