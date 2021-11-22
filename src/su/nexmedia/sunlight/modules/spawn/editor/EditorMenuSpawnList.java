package su.nexmedia.sunlight.modules.spawn.editor;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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
import su.nexmedia.sunlight.modules.spawn.Spawn;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;

import java.util.ArrayList;
import java.util.List;

public class EditorMenuSpawnList extends AbstractMenuAuto<SunLight, Spawn> {

    private final SpawnManager spawnManager;

    private final String       objectName;
    private final List<String> objectLore;

    public EditorMenuSpawnList(@NotNull SpawnManager spawnManager) {
        super(spawnManager.plugin(),
            JYML.loadOrExtract(spawnManager.plugin(), spawnManager.getPath() + "editor/list.yml"), "");
        this.spawnManager = spawnManager;

        this.objectName = StringUT.color(cfg.getString("Object.Name", Spawn.PLACEHOLDER_ID));
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
    protected IMenuClick getObjectClick(@NotNull Player player, @NotNull Spawn spawn) {
        return (player1, type, e) -> {
            spawn.getEditor().open(player1, 1);
        };
    }

    @Override
    @NotNull
    protected List<Spawn> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.spawnManager.getSpawns());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Spawn spawn) {
        Material material = spawn.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        if (material.isAir()) material = Material.GRASS_BLOCK;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(this.objectName);
        meta.setLore(this.objectLore);
        item.setItemMeta(meta);

        ItemUT.replace(item, spawn.replacePlaceholders());
        return item;
    }

    @Override
    public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

    }

    @Override
    public boolean cancelClick(@NotNull SlotType slotType, int slot) {
        return true;
    }
}
