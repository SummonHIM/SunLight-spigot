package su.nexmedia.sunlight.modules.kits.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.modules.kits.KitManager;

import java.util.ArrayList;
import java.util.List;

public class KitMenuList extends AbstractMenuAuto<SunLight, Kit> {

    private final KitManager kitManager;

    private final String       objectName;
    private final List<String> objectLore;

    public KitMenuList(@NotNull SunLight plugin, @NotNull KitManager kitManager) {
        super(plugin, JYML.loadOrExtract(plugin, kitManager.getPath() + KitManager.YML_KITS_MENU), "");
        this.kitManager = kitManager;

        this.objectName = StringUT.color(cfg.getString("Kit.Name", Kit.PLACEHOLDER_ID));
        this.objectLore = StringUT.color(cfg.getStringList("Kit.Lore"));
        this.objectSlots = cfg.getIntArray("Kit.Slots");

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
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
    public void open(@NotNull Player player, int page) {
        if (this.getObjects(player).isEmpty()) {
            this.kitManager.getLang().Kit_Error_NoKits.send(player);
            return;
        }
        super.open(player, page);
    }

    @Override
    @NotNull
    protected List<Kit> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.kitManager.getKits());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack item = kit.getIcon();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(this.objectName);
        meta.setLore(this.objectLore);
        item.setItemMeta(meta);

        ItemUT.replace(item, kit.replacePlaceholders(player));
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull Kit kit) {
        return (player1, type, e) -> {
            if (e.isLeftClick()) {
                kit.give(player1, false);
                player1.closeInventory();
            }
            else if (e.isRightClick()) {
                kit.getPreview().open(player1, 1);
            }
        };
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
