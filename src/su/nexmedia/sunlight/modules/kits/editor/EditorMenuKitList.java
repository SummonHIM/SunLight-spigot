package su.nexmedia.sunlight.modules.kits.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.modules.kits.KitManager;

import java.util.ArrayList;
import java.util.List;

public class EditorMenuKitList extends AbstractMenuAuto<SunLight, Kit> {

    private final KitManager kitManager;

    private final String       objectName;
    private final List<String> objectLore;

    public EditorMenuKitList(@NotNull KitManager kitManager) {
        super(kitManager.plugin(), EditorHandlerKit.YML_LIST, "");

        this.kitManager = kitManager;
        this.objectName = StringUT.color(cfg.getString("Object.Name", Kit.PLACEHOLDER_ID));
        this.objectLore = StringUT.color(cfg.getStringList("Object.Lore"));

        IMenuClick click = (player, type, e) -> {

            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
            else if (type instanceof SunEditorType type2) {
                if (type2 == SunEditorType.KIT_CREATE) {
                    plugin.getSunEditorHandler().startEdit(player, kitManager, type2);
                    EditorUtils.tipCustom(player, kitManager.getLang().Editor_Enter_KitId.getMsg());
                    player.closeInventory();
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

        for (String sId : cfg.getSection("Editor")) {
            IMenuItem menuItem = cfg.getMenuItem("Editor." + sId, SunEditorType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
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
            kit.getEditor().open(player1, 1);
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
