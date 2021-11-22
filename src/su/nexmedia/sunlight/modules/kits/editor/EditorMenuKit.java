package su.nexmedia.sunlight.modules.kits.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.api.menu.*;
import su.nexmedia.engine.hooks.external.VaultHK;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.modules.kits.KitManager;

public class EditorMenuKit extends AbstractMenu<SunLight> {

    private final Kit        kit;
    private final KitManager kitManager;

    public EditorMenuKit(@NotNull Kit kit) {
        super(kit.getKitManager().plugin(), EditorHandlerKit.YML_MAIN, "");
        this.kit = kit;
        this.kitManager = kit.getKitManager();

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    this.kitManager.getEditor().open(player, 1);
                }
            }
            else if (type instanceof SunEditorType type2) {
                switch (type2) {
                    case KIT_CHANGE_ARMOR -> {
                        new ContentEditor(this.kit, 9).open(player, 1);
                        return;
                    }
                    case KIT_CHANGE_INVENTORY -> {
                        new ContentEditor(this.kit, 36).open(player, 1);
                        return;
                    }
                    case KIT_CHANGE_COMMAND -> {
                        if (e.isLeftClick()) {
                            plugin.getSunEditorHandler().startEdit(player, kit, type2);
                            EditorUtils.tipCustom(player, kitManager.getLang().Editor_Enter_Command.getMsg());
                            EditorUtils.sendCommandTips(player);
                            player.closeInventory();
                            return;
                        }
                        else if (e.isRightClick()) {
                            kit.getCommands().clear();
                        }
                    }
                    case KIT_CHANGE_COOLDOWN -> {
                        if (e.isRightClick()) {
                            kit.setCooldown(-1);
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, kit, type2);
                        EditorUtils.tipCustom(player, kitManager.getLang().Editor_Enter_Cooldown.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_COST -> {
                        VaultHK vault = plugin.getVault();
                        if (vault == null || !vault.hasEconomy()) return;

                        plugin.getSunEditorHandler().startEdit(player, kit, type2);
                        EditorUtils.tipCustom(player, kitManager.getLang().Editor_Enter_Cost.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_ICON -> {
                        ItemStack cursor = e.getCursor();
                        if (cursor == null || ItemUT.isAir(cursor)) return;

                        kit.setIcon(cursor);
                        e.getWhoClicked().setItemOnCursor(null);
                    }
                    case KIT_CHANGE_NAME -> {
                        plugin.getSunEditorHandler().startEdit(player, kit, type2);
                        EditorUtils.tipCustom(player, kitManager.getLang().Editor_Enter_Name.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_DESCRIPTION -> {
                        if (e.isRightClick()) {
                            kit.getDescription().clear();
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, kit, type2);
                        EditorUtils.tipCustom(player, kitManager.getLang().Editor_Enter_Description.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_PRIORITY -> {
                        plugin.getSunEditorHandler().startEdit(player, kit, type2);
                        EditorUtils.tipCustom(player, kitManager.getLang().Editor_Enter_Priority.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_PERMISSION -> kit.setPermissionRequired(!kit.isPermissionRequired());
                    case KIT_DELETE -> {
                        if (!e.isShiftClick()) return;

                        player.closeInventory();
                        this.kitManager.delete(kit);
                        this.kitManager.getEditor().open(player, 1);
                        return;
                    }
                    default -> {
                        return;
                    }
                }
                kit.save();
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

    @Override
    @Nullable
    public MenuItemDisplay onItemDisplayPrepare(@NotNull Player player, @NotNull IMenuItem menuItem) {
        if (menuItem.getType() instanceof SunEditorType type2) {
            if (type2 == SunEditorType.KIT_CHANGE_PERMISSION) {
                return menuItem.getDisplay(String.valueOf(kit.isPermissionRequired() ? 1 : 0));
            }
        }
        return super.onItemDisplayPrepare(player, menuItem);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUT.replace(item, this.kit.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }

    static class ContentEditor extends AbstractMenu<SunLight> {

        private final Kit     kit;
        private final boolean isArmor;

        public ContentEditor(@NotNull Kit kit, int size) {
            super(kit.getKitManager().plugin(), "Kit Content", size);
            this.kit = kit;
            this.isArmor = size == 9;
        }

        @Override
        public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
            inventory.setContents(this.isArmor ? this.kit.getArmor() : this.kit.getItems());
        }

        @Override
        public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
            Inventory inventory = e.getInventory();
            ItemStack[] items = new ItemStack[this.isArmor ? 4 : 36];

            for (int slot = 0; slot < items.length; slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item == null) continue;

                items[slot] = item;
            }

            if (this.isArmor) this.kit.setArmor(items);
            else this.kit.setItems(items);

            this.kit.save();
            super.onClose(player, e);

            plugin.runTask(c -> this.kit.getEditor().open(player, 1), false);
        }

        @Override
        public boolean destroyWhenNoViewers() {
            return true;
        }

        @Override
        public boolean cancelClick(@NotNull SlotType slotType, int slot) {
            return false;
        }
    }
}
