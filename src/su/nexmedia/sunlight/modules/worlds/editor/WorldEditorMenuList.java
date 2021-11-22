package su.nexmedia.sunlight.modules.worlds.editor;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.IMenuClick;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.worlds.world.SunWorld;
import su.nexmedia.sunlight.modules.worlds.WorldManager;

import java.util.ArrayList;
import java.util.List;

public class WorldEditorMenuList extends AbstractMenuAuto<SunLight, SunWorld> {

    private final WorldManager worldManager;

    private final String       objectName;
    private final List<String> objectLore;

    public WorldEditorMenuList(@NotNull WorldManager worldManager) {
        super(worldManager.plugin(), WorldEditorInputHandler.YML_LIST, "");
        this.worldManager = worldManager;

        this.objectName = StringUT.color(cfg.getString("Object.Name", SunWorld.PLACEHOLDER_ID));
        this.objectLore = StringUT.color(cfg.getStringList("Object.Lore"));

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

    @Override
    @NotNull
    protected List<SunWorld> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.worldManager.getWorlds());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull SunWorld world) {
        Material material = Material.GRASS_BLOCK;
        if (world.getEnvironment() == World.Environment.NETHER) material = Material.NETHERRACK;
        else if (world.getEnvironment() == World.Environment.THE_END) material = Material.END_STONE;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(this.objectName);
        meta.setLore(this.objectLore);
        item.setItemMeta(meta);

        ItemUT.replace(item, world.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull SunWorld world) {
        return (player1, type, e) -> {
            world.getEditor().open(player1, 1);
        };
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
