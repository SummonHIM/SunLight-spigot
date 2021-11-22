package su.nexmedia.sunlight.modules.warps.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.warps.Warp;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.WarpPerms;

import java.util.ArrayList;
import java.util.List;

public class WarpEditorMenuList extends AbstractMenuAuto<SunLight, Warp> {

    private final WarpManager warpManager;

    private final String       objectName;
    private final List<String> objectLore;

    public WarpEditorMenuList(@NotNull WarpManager warpManager) {
        super(warpManager.plugin(), WarpEditorInputHandler.LIST, "");
        this.warpManager = warpManager;

        this.objectName = StringUT.color(cfg.getString("Object.Name", Warp.PLACEHOLDER_ID));
        this.objectLore = StringUT.color(cfg.getStringList("Object.Lore"));

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
    @NotNull
    protected List<Warp> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.warpManager.getWarps());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Warp warp) {
        ItemStack item = new ItemStack(warp.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(this.objectName);
        meta.setLore(this.objectLore);
        item.setItemMeta(meta);

        ItemUT.replace(item, warp.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull Warp warp) {
        return (player1, type, e) -> {
            if (warp.isOwner(player1) || player1.hasPermission(WarpPerms.EDITOR_OTHERS)) {
                warp.getEditor().open(player1, 1);
                return;
            }
            plugin.lang().Error_NoPerm.send(player1);
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
