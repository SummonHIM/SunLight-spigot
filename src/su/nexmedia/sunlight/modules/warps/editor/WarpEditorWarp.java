package su.nexmedia.sunlight.modules.warps.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.warps.Warp;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.WarpPerms;

public class WarpEditorWarp extends AbstractMenu<SunLight> {

    private final Warp warp;

    public WarpEditorWarp(@NotNull Warp warp) {
        super(warp.getWarpManager().plugin(), WarpEditorInputHandler.WARP, "");
        this.warp = warp;

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    warp.getWarpManager().getEditor().open(player, 1);
                }
            }
            else if (type instanceof SunEditorType type2) {
                WarpManager warpManager = warp.getWarpManager();
                switch (type2) {
                    case WARP_CHANGE_TELEPORT_COST_MONEY -> {
                        if (!player.hasPermission(WarpPerms.EDITOR_TELEPORT_COST_MONEY)) {
                            plugin.lang().Error_NoPerm.send(player);
                            return;
                        }
                        if (e.isRightClick()) {
                            warp.setTeleportCostMoney(0);
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, warp, type2);
                        EditorUtils.tipCustom(player, warpManager.getLang().Editor_Enter_CostMoney.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case WARP_CHANGE_ICON -> {
                        ItemStack icon = e.getCursor();
                        if (icon == null || ItemUT.isAir(icon)) return;

                        warp.setIcon(icon);
                        e.getWhoClicked().setItemOnCursor(null);
                    }
                    case WARP_CHANGE_NAME -> {
                        plugin.getSunEditorHandler().startEdit(player, warp, type2);
                        EditorUtils.tipCustom(player, warpManager.getLang().Editor_Enter_Name.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case WARP_CHANGE_PERMISSION -> {
                        if (!player.hasPermission(SunPerms.CORE_ADMIN)) {
                            plugin.lang().Error_NoPerm.send(player);
                            return;
                        }
                        warp.setPermissionRequired(!warp.isPermissionRequired());
                    }
                    case WARP_CHANGE_TYPE -> {
                        if (!player.hasPermission(SunPerms.CORE_ADMIN)) {
                            plugin.lang().Error_NoPerm.send(player);
                            return;
                        }
                        warp.setType(CollectionsUT.toggleEnum(warp.getType()));
                    }
                    case WARP_DELETE -> {
                        if (!e.isShiftClick()) return;

                        player.closeInventory();
                        warp.getWarpManager().delete(warp);
                        warp.getWarpManager().getEditor().open(player, 1);
                        return;
                    }
                    case WARP_CHANGE_DESCRIPTION -> {
                        if (e.isRightClick()) {
                            warp.getDescription().clear();
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, warp, type2);
                        EditorUtils.tipCustom(player, warpManager.getLang().Editor_Enter_Description.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case WARP_CHANGE_WELCOME_MESSAGE -> {
                        if (e.isRightClick()) {
                            warp.setWelcomeMessage("");
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, warp, type2);
                        EditorUtils.tipCustom(player, warpManager.getLang().Editor_Enter_Welcome.getMsg());
                        player.closeInventory();
                        return;
                    }
                    default -> {
                        return;
                    }
                }
                warp.save();
                this.open(player, 1);
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
    public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

	/*@Override
	@Nullable
	public MenuItemDisplay onItemDisplayPrepare(@NotNull Player player, @NotNull IMenuItem menuItem) {
		if (menuItem.getType() instanceof SunEditorType type) {
			if (type == SunEditorType.WARP_CHANGE_PERMISSION) {
				return menuItem.getDisplay(String.valueOf(warp.isPermissionRequired() ? 1 : 0));
			}
		}
		return super.onItemDisplayPrepare(player, menuItem);
	}*/

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUT.replace(item, warp.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
