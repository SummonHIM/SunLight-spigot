package su.nexmedia.sunlight.modules.spawn.editor;

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
import su.nexmedia.sunlight.modules.spawn.Spawn;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;

public class EditorMenuSpawn extends AbstractMenu<SunLight> {

    private final Spawn spawn;

    public EditorMenuSpawn(@NotNull SpawnManager spawnManager, @NotNull Spawn spawn) {
        super(spawn.getSpawnManager().plugin(),
            JYML.loadOrExtract(spawnManager.plugin(), spawnManager.getPath() + "editor/spawn.yml"), "");
        this.spawn = spawn;

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                switch (type2) {
                    case CLOSE -> player.closeInventory();
                    case RETURN -> spawnManager.getEditor().open(player, 1);
                    default -> { }
                }
            }
            else if (type instanceof SunEditorType type2) {
                switch (type2) {
                    case SPAWN_CHANGE_NAME -> {
                        plugin.getSunEditorHandler().startEdit(player, spawn, type2);
                        EditorUtils.tipCustom(player, spawnManager.getLang().Spawn_Editor_Tip_Name.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case SPAWN_CHANGE_LOCATION -> spawn.setLocation(player.getLocation());
                    case SPAWN_CHANGE_PERMISSION -> spawn.setPermissionRequired(!spawn.isPermissionRequired());
                    case SPAWN_CHANGE_DEFAULT -> spawn.setDefault(!spawn.isDefault());
                    case SPAWN_CHANGE_PRIORITY -> {
                        plugin.getSunEditorHandler().startEdit(player, spawn, type2);
                        EditorUtils.tipCustom(player, spawnManager.getLang().Spawn_Editor_Tip_Priority.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case SPAWN_CHANGE_LOGIN -> {
                        if (e.isRightClick()) {
                            spawn.setTeleportOnFirstLogin(!spawn.isTeleportOnFirstLogin());
                        }
                        else {
                            spawn.setTeleportOnLogin(!spawn.isTeleportOnLogin());
                        }
                    }
                    case SPAWN_CHANGE_DEATH -> spawn.setTeleportOnDeath(!spawn.isTeleportOnDeath());
                    case SPAWN_ADD_LOGIN_GROUP -> {
                        if (e.isRightClick()) {
                            spawn.getTeleportOnLoginGroups().clear();
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, spawn, type2);
                        EditorUtils.tipCustom(player, spawnManager.getLang().Spawn_Editor_Tip_AddGroup.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case SPAWN_ADD_DEATH_GROUP -> {
                        if (e.isRightClick()) {
                            spawn.getTeleportOnDeathGroups().clear();
                            break;
                        }
                        plugin.getSunEditorHandler().startEdit(player, spawn, type2);
                        EditorUtils.tipCustom(player, spawnManager.getLang().Spawn_Editor_Tip_AddGroup.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case SPAWN_DELETE -> {
                        if (!e.isShiftClick()) return;

                        player.closeInventory();
                        spawn.getSpawnManager().deleteSpawn(spawn);
                        spawnManager.getEditor().open(player, 1);
                        return;
                    }
                    default -> {}
                }

                spawn.save();
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
        if (menuItem.getType() instanceof SunEditorType type) {
            if (type == SunEditorType.SPAWN_CHANGE_DEATH) {
                return menuItem.getDisplay(String.valueOf(spawn.isTeleportOnDeath() ? 1 : 0));
            }
            else if (type == SunEditorType.SPAWN_CHANGE_LOGIN) {
                return menuItem.getDisplay(String.valueOf(spawn.isTeleportOnLogin() ? 1 : 0));
            }
            else if (type == SunEditorType.SPAWN_CHANGE_DEFAULT) {
                return menuItem.getDisplay(String.valueOf(spawn.isDefault() ? 1 : 0));
            }
            else if (type == SunEditorType.SPAWN_CHANGE_PERMISSION) {
                return menuItem.getDisplay(String.valueOf(spawn.isPermissionRequired() ? 1 : 0));
            }
        }
        return super.onItemDisplayPrepare(player, menuItem);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUT.replace(item, this.spawn.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
