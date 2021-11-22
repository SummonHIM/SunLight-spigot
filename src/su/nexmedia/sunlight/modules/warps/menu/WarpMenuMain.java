package su.nexmedia.sunlight.modules.warps.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.warps.type.WarpType;

public class WarpMenuMain extends AbstractMenu<SunLight> {

    private static final String PLACEHOLDER_WARPS_AMOUNT_SERVER = "%warps_amount_server%";
    private static final String PLACEHOLDER_WARPS_AMOUNT_PLAYER = "%warps_amount_player%";
    private final WarpManager warManager;

    public WarpMenuMain(@NotNull WarpManager warpManager, @NotNull JYML cfg) {
        super(warpManager.plugin(), cfg, "");
        this.warManager = warpManager;

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
            else if (type instanceof WarpMainType type2) {
                switch (type2) {
                    case OPEN_PLAYER_WARPS -> this.warManager.getWarpMenuPlayer().open(player, 1);
                    case OPEN_SERVER_WARPS -> this.warManager.getWarpMenuServer().open(player, 1);
                    default -> {}
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
            IMenuItem menuItem = cfg.getMenuItem("Special." + sId, WarpMainType.class);

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
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUT.replace(item, line -> line
            .replace(PLACEHOLDER_WARPS_AMOUNT_SERVER, String.valueOf(warManager.getWarps(WarpType.SERVER).size()))
            .replace(PLACEHOLDER_WARPS_AMOUNT_PLAYER, String.valueOf(warManager.getWarps(WarpType.PLAYER).size()))
        );
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }

    enum WarpMainType {
        OPEN_PLAYER_WARPS, OPEN_SERVER_WARPS,
    }
}
