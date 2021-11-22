package su.nexmedia.sunlight.user.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.user.IgnoredUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class EditorUserIgnoredList extends AbstractMenuAuto<SunLight, IgnoredUser> {

    private static final ItemStack USER_ICON = EditorUserInputHandler.IGNORED_LIST.getItem("Object.Icon");
    private final        SunUser   user;

    public EditorUserIgnoredList(@NotNull SunLight plugin, @NotNull SunUser user) {
        super(plugin, EditorUserInputHandler.IGNORED_LIST, "");
        this.user = user;

        IMenuClick click = (p, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.CLOSE) {
                    p.closeInventory();
                }
                else if (type2 == MenuItemType.RETURN) {
                    // TODO User Global Settings Menu.
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
    protected @NotNull List<IgnoredUser> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.user.getIgnoredUsers().values());
    }

    @Override
    protected @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull IgnoredUser ignoredUser) {
        ItemStack item = new ItemStack(USER_ICON);

        UnaryOperator<String> replacer = str -> str
            .replace("%name%", ignoredUser.getName())
            .replace("%ignored-chat-messages%", plugin.lang().getBool(ignoredUser.isIgnoreChatMessages()))
            .replace("%ignored-private-messages%", plugin.lang().getBool(ignoredUser.isIgnorePrivateMessages()))
            .replace("%ignored-teleport-requests%", plugin.lang().getBool(ignoredUser.isIgnoreTeleportRequests()));
        ItemUT.replace(item, replacer);

        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta == null) return item;

            SunUser ignored = plugin.getUserManager().getOrLoadUser(ignoredUser.getName(), false);
            if (ignored == null) return item;

            meta.setOwningPlayer(ignored.getOfflinePlayer());
            item.setItemMeta(meta);
        }

        return item;
    }

    @Override
    protected @NotNull IMenuClick getObjectClick(@NotNull Player player, @NotNull IgnoredUser ignoredUser) {
        return (player1, type, e) -> {
            if (e.isRightClick()) {
                user.removeIgnoredUser(ignoredUser.getName());
                this.open(player1, this.getPage(player1));
                return;
            }
            ignoredUser.getEditor().open(player1, 1);
        };
    }

    @Override
    public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
        super.onPrepare(player, inventory);
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
