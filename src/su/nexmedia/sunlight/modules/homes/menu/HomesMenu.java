package su.nexmedia.sunlight.modules.homes.menu;

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
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.homes.Home;
import su.nexmedia.sunlight.modules.homes.HomeManager;
import su.nexmedia.sunlight.modules.homes.HomePerms;

import javax.annotation.Nullable;
import java.util.*;

public class HomesMenu extends AbstractMenuAuto<SunLight, Home> {

    private final String       objName;
    private final List<String> objLore;

    private final Map<String, String> others;

    public HomesMenu(@NotNull HomeManager homeManager) {
        super(homeManager.plugin(),
            JYML.loadOrExtract(homeManager.plugin(), homeManager.getPath() + HomeManager.YML_HOMES_MENU),
            "");

        this.objName = StringUT.color(cfg.getString("Object.Name", Home.PLACEHOLDER_ID));
        this.objLore = StringUT.color(cfg.getStringList("Object.Lore"));
        this.others = new HashMap<>();

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

    public void open(@NotNull Player player, @NotNull String userName) {
        this.others.put(player.getName(), userName);
        this.open(player, 1);
    }

    @Nullable
    public String getOtherHolder(@NotNull Player player) {
        return this.others.get(player.getName());
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
        super.onClose(player, e);
        this.others.remove(player.getName());
    }

    @Override
    @NotNull
    protected List<Home> getObjects(@NotNull Player player) {
        String name = this.others.getOrDefault(player.getName(), player.getName());
        SunUser user = plugin.getUserManager().getOrLoadUser(name, false);
        if (user == null) return Collections.emptyList();

        return new ArrayList<>(user.getHomes().values());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Home home) {
        ItemStack item = new ItemStack(home.getIconMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(this.objName);
        meta.setLore(this.objLore);
        item.setItemMeta(meta);

        ItemUT.replace(item, home.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull Home home) {
        return (player1, type, e) -> {
            if (e.isRightClick()) {
                if (!home.isOwner(player1) && !player1.hasPermission(HomePerms.HOMES_CMD_HOME_OTHERS)) {
                    plugin.lang().Error_NoPerm.send(player1);
                    return;
                }
                home.getEditor().open(player1, 1);
            }
            else {
                home.teleport(player1);
            }
        };
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
