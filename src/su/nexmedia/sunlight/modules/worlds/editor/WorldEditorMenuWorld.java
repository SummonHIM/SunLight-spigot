package su.nexmedia.sunlight.modules.worlds.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.EditorUtils;
import su.nexmedia.engine.api.menu.*;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.editor.SunEditorType;
import su.nexmedia.sunlight.modules.worlds.world.SunWorld;
import su.nexmedia.sunlight.modules.worlds.WorldManager;

public class WorldEditorMenuWorld extends AbstractMenu<SunLight> {

    private final SunWorld world;

    public WorldEditorMenuWorld(@NotNull WorldManager worldManager, @NotNull SunWorld world) {
        super(world.plugin(), WorldEditorInputHandler.YML_WORLD, "");
        this.world = world;

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    worldManager.getEditor().open(player, 1);
                }
            }
            else if (type instanceof SunEditorType type2) {
                switch (type2) {
                    case WORLD_AUTO_LOAD -> world.setAutoLoad(!world.isAutoLoad());
                    case WORLD_LOAD -> {
                        if (world.isLoaded()) return;
                        world.create();
                    }
                    case WORLD_UNLOAD -> {
                        if (!world.isLoaded()) return;
                        world.unload();
                    }
                    case WORLD_DELETE -> {
                        if (world.isLoaded()) return;
                        if (!e.isRightClick()) return;
                        world.delete(e.isShiftClick());
                        worldManager.getEditor().open(player, 1);
                        return;
                    }
                    case WORLD_CHANGE_GENERATOR -> {
                        if (e.isRightClick()) {
                            world.setGenerator(null);
                            return;
                        }
                        plugin.getSunEditorHandler().startEdit(player, world, type2);
                        EditorUtils.tipCustom(player, worldManager.getLang().Editor_Enter_Generator.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case WORLD_CHANGE_TYPE -> world.setType(CollectionsUT.toggleEnum(world.getType()));
                    case WORLD_CHANGE_ENVIRONMENT -> world.setEnvironment(CollectionsUT.toggleEnum(world.getEnvironment()));
                    case WORLD_CHANGE_DIFFICULTY -> world.setDifficulty(CollectionsUT.toggleEnum(world.getDifficulty()));
                    case WORLD_CHANGE_SEED -> {
                        if (e.isRightClick()) {
                            world.setSeed(0L);
                            return;
                        }
                        plugin.getSunEditorHandler().startEdit(player, world, type2);
                        EditorUtils.tipCustom(player, worldManager.getLang().Editor_Enter_Seed.getMsg());
                        player.closeInventory();
                        return;
                    }
                    case WORLD_CHANGE_STRUCTURES -> world.setStructuresEnabled(!world.isStructuresEnabled());
                }
                world.save();
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
        if (menuItem.getType() instanceof SunEditorType type) {
            if (type == SunEditorType.WORLD_AUTO_LOAD) {
                return menuItem.getDisplay(String.valueOf(world.isAutoLoad() ? 1 : 0));
            }
            if (type == SunEditorType.WORLD_LOAD || type == SunEditorType.WORLD_UNLOAD || type == SunEditorType.WORLD_DELETE) {
                return menuItem.getDisplay(String.valueOf(world.isLoaded() ? 1 : 0));
            }
        }
        return super.onItemDisplayPrepare(player, menuItem);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUT.replace(item, this.world.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
