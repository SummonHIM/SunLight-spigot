package su.nexmedia.sunlight.modules.warps.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.warps.Warp;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.WarpPerms;
import su.nexmedia.sunlight.modules.warps.type.WarpSortType;
import su.nexmedia.sunlight.modules.warps.type.WarpType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpMenuList extends AbstractMenuAuto<SunLight, Warp> {

    private static final String PLACEHOLDER_SORT_TYPE = "%warps_sort_type%";
    private final WarpManager warpManager;
    private final String       objectName;
    private final List<String> objectLore;
    private final WarpType     warpType;
    private final WarpSortType sortType;
    private final Map<String, WarpSortType> userSortCache;

    public WarpMenuList(@NotNull WarpManager warpManager, @NotNull JYML cfg, @NotNull WarpType warpType) {
        super(warpManager.plugin(), cfg, "");
        this.warpManager = warpManager;
        this.warpType = warpType;
        this.userSortCache = new HashMap<>();

        this.sortType = cfg.getEnum("Default_Sorting", WarpSortType.class, WarpSortType.WARP_ID);
        this.objectName = StringUT.color(cfg.getString("Warp.Name", Warp.PLACEHOLDER_ID));
        this.objectLore = StringUT.color(cfg.getStringList("Warp.Lore"));
        this.objectSlots = cfg.getIntArray("Warp.Slots");

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    warpManager.getWarpMenuMain().open(player, 1);
                    return;
                }
                this.onItemClickDefault(player, type2);
            }
            else if (type instanceof WarpItemType type2) {
                if (type2 == WarpItemType.WARP_SORTING) {
                    WarpSortType userSort = this.getUserSortType(player);
                    userSort = CollectionsUT.toggleEnum(userSort);
                    this.userSortCache.put(player.getUniqueId().toString(), userSort);
                    this.open(player, this.getPage(player));
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

        for (String sId : cfg.getSection("Special")) {
            IMenuItem menuItem = cfg.getMenuItem("Special." + sId, WarpItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClick(click);
            }
            this.addItem(menuItem);
        }
    }

    @NotNull
    private WarpSortType getUserSortType(@NotNull Player player) {
        return this.userSortCache.getOrDefault(player.getUniqueId().toString(), this.sortType);
    }

    @Override
    @NotNull
    protected List<Warp> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.warpManager.getWarps(this.warpType, this.getUserSortType(player)));
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

        ItemUT.replace(item, warp.replacePlaceholders(player));
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull Warp warp) {
        return (player1, type, e) -> {
            if (e.isLeftClick()) {
                warp.teleport(player1, false);
                return;
            }

            if (e.isRightClick()) {
                if (warp.isOwner(player1) || player1.hasPermission(WarpPerms.EDITOR_OTHERS)) {
                    warp.getEditor().open(player1, 1);
                    return;
                }
                plugin.lang().Error_NoPerm.send(player1);
            }
        };
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
        super.onClose(player, e);
        this.userSortCache.remove(player.getUniqueId().toString());
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);

        WarpSortType userSort = this.getUserSortType(player);
        ItemUT.replace(item, line -> line.replace(PLACEHOLDER_SORT_TYPE, warpManager.getLang().getEnum(userSort)));
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }

    enum WarpItemType {
        WARP_SORTING,
    }
}
