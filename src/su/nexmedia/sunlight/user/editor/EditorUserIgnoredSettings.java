package su.nexmedia.sunlight.user.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.user.IgnoredUser;

import java.util.function.UnaryOperator;

public class EditorUserIgnoredSettings extends AbstractMenu<SunLight> {

    private final IgnoredUser ignoredUser;

    public EditorUserIgnoredSettings(@NotNull SunLight plugin, @NotNull IgnoredUser ignoredUser) {
        super(plugin, EditorUserInputHandler.IGNORED_SETTINGS, "");
        this.ignoredUser = ignoredUser;

        IMenuClick click = (p, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.CLOSE) {
                    p.closeInventory();
                }
                else if (type2 == MenuItemType.RETURN) {
                    SunUser user = plugin.getUserManager().getOrLoadUser(p);
                    user.getEditorIgnoredList().open(p, 1);
                }
            }
            else if (type instanceof SunEditorType type2) {
                switch (type2) {
                    case USER_IGNORED_CHANGE_CHAT_MESSAGES -> ignoredUser.setIgnoreChatMessages(!ignoredUser.isIgnoreChatMessages());
                    case USER_IGNORED_CHANGE_PRIVATE_MESSAGES -> ignoredUser.setIgnorePrivateMessages(!ignoredUser.isIgnorePrivateMessages());
                    case USER_IGNORED_CHANGE_TELEPORT_REQUESTS -> ignoredUser.setIgnoreTeleportRequests(!ignoredUser.isIgnoreTeleportRequests());
                    default -> { return; }
                }
                this.open(p, 1); // Reopen to update item lores after changes.
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
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);

        UnaryOperator<String> replacer = str -> str
            .replace("%name%", ignoredUser.getName())
            .replace("%ignored-chat-messages%", plugin.lang().getBool(ignoredUser.isIgnoreChatMessages()))
            .replace("%ignored-private-messages%", plugin.lang().getBool(ignoredUser.isIgnorePrivateMessages()))
            .replace("%ignored-teleport-requests%", plugin.lang().getBool(ignoredUser.isIgnoreTeleportRequests()));
        ItemUT.replace(item, replacer);
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
