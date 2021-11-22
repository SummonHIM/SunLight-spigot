package su.nexmedia.sunlight.modules.homes.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.api.menu.*;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.homes.Home;
import su.nexmedia.sunlight.modules.homes.HomeManager;

public class HomeMenu extends AbstractMenu<SunLight> {

    private final Home home;

    public HomeMenu(@NotNull HomeManager homeManager, @NotNull Home home) {
        super(homeManager.plugin(),
            JYML.loadOrExtract(homeManager.plugin(), homeManager.getPath() + HomeManager.YML_HOME_MENU),
            "");
        this.home = home;

        IMenuClick click = (player, type, e) -> {
            if (type == null) return;

            if (type instanceof MenuItemType type2) {
                switch (type2) {
                    case CLOSE -> player.closeInventory();
                    case RETURN -> {
                        if (!home.isOwner(player)) {
                            homeManager.getHomesMenu().open(player, home.getOwner());
                        }
                        else {
                            homeManager.getHomesMenu().open(player, 1);
                        }
                    }
                    default -> {}
                }
            }
            else if (type instanceof SunEditorType type2) {
                switch (type2) {
                    case HOME_CHANGE_LOCATION -> home.setLocation(player.getLocation());
                    case HOME_CHANGE_RESPAWN_POINT -> home.setRespawnPoint(!home.isRespawnPoint());
                    case HOME_CHANGE_PUBLIC -> home.setPublic(!home.isPublic());
                    case HOME_CHANGE_ICON_MATERIAL -> {
                        ItemStack item = e.getCursor();
                        if (item == null || ItemUT.isAir(item)) return;

                        home.setIconMaterial(item.getType());
                        e.getView().setCursor(null);
                        this.open(player, 1);
                        e.getView().setCursor(item);
                    }
                    case HOME_CHANGE_INVITED_PLAYERS -> {
                        if (e.isRightClick()) {
                            home.getInvitedPlayers().clear();
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, home, type2);
                        EditorUtils.tipCustom(player, homeManager.getLang().Editor_Enter_InvitedPlayer.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case HOME_CHANGE_NAME -> {
                        plugin.getSunEditorHandler().startEdit(player, home, type2);
                        EditorUtils.tipCustom(player, homeManager.getLang().Editor_Enter_Name.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case HOME_DELETE -> {
                        if (!e.isShiftClick()) return;

                        player.closeInventory();
                        homeManager.deleteHome(player.getName(), home.getId());
                        homeManager.getHomesMenu().open(player, 1);
                        return;
                    }
                    default -> {
                        return;
                    }
                }
                this.open(player, this.getPage(player));
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

    @Override
    @Nullable
    public MenuItemDisplay onItemDisplayPrepare(@NotNull Player player, @NotNull IMenuItem menuItem) {
        if (menuItem.getType() instanceof SunEditorType type2) {
            if (type2 == SunEditorType.HOME_CHANGE_PUBLIC) {
                return menuItem.getDisplay(String.valueOf(home.isPublic() ? 1 : 0));
            }
            if (type2 == SunEditorType.HOME_CHANGE_RESPAWN_POINT) {
                return menuItem.getDisplay(String.valueOf(home.isRespawnPoint() ? 1 : 0));
            }
        }
        return super.onItemDisplayPrepare(player, menuItem);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        ItemUT.replace(item, home.replacePlaceholders());
        super.onItemPrepare(player, menuItem, item);
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return slotType == SlotType.EMPTY_MENU || slotType == SlotType.MENU;
    }
}
