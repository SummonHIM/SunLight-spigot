package su.nexmedia.sunlight.modules.worlds;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.worlds.commands.GotoCommand;
import su.nexmedia.sunlight.modules.worlds.commands.MoveCommand;
import su.nexmedia.sunlight.modules.worlds.commands.worldmanager.WorldManagerCommand;
import su.nexmedia.sunlight.modules.worlds.config.WorldsConfig;
import su.nexmedia.sunlight.modules.worlds.config.WorldsLang;
import su.nexmedia.sunlight.modules.worlds.editor.WorldEditorInputHandler;
import su.nexmedia.sunlight.modules.worlds.editor.WorldEditorMenuList;
import su.nexmedia.sunlight.modules.worlds.listener.WorldsListener;
import su.nexmedia.sunlight.modules.worlds.world.SunWorld;

import java.io.File;
import java.util.*;

public class WorldManager extends SunModule {

    private WorldsLang          lang;
    private WorldEditorMenuList editor;

    private Map<String, SunWorld>       worlds;
    private Map<String, WorldInventory> inventoryMap;

    public WorldManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.WORLDS;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new WorldsLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();
        WorldsConfig.load(this);

        this.worlds = new HashMap<>();
        for (JYML worldConfig : JYML.loadAll(this.getFullPath() + "/worlds/", true)) {
            try {
                SunWorld world = new SunWorld(this, worldConfig);
                this.worlds.put(world.getId(), world);
                if (world.isAutoLoad()) {
                    world.create();
                }
            } catch (Exception ex) {
                this.error("Could not load world: " + worldConfig.getFile().getName());
                ex.printStackTrace();
            }
        }

        if (WorldsConfig.INVENTORY_SPLIT_ENABLED) {
            this.inventoryMap = new HashMap<>();
        }

        this.plugin.getSunEditorHandler().addInputHandler(SunWorld.class, new WorldEditorInputHandler(this));

        this.plugin.getCommandRegulator().register(new WorldManagerCommand(this));
        this.plugin.getCommandRegulator().register(new GotoCommand(this));
        this.plugin.getCommandRegulator().register(new MoveCommand(this));

        this.addListener(new WorldsListener(this));
    }

    @Override
    public void onShutdown() {
        this.getWorlds().forEach(sunWorld -> {
            sunWorld.clear();
            sunWorld.save();
            sunWorld.unload();
        });

        if (WorldsConfig.INVENTORY_SPLIT_ENABLED) {
            this.inventoryMap.values().forEach(WorldInventory::save);
            this.inventoryMap.clear();
        }

        this.getWorlds().clear();
        this.plugin.getSunEditorHandler().removeInputHandler(SunWorld.class);
    }

    @NotNull
    public WorldsLang getLang() {
        return lang;
    }

    @NotNull
    public WorldEditorMenuList getEditor() {
        if (this.editor == null) {
            this.editor = new WorldEditorMenuList(this);
        }
        return editor;
    }

    @NotNull
    public Map<String, SunWorld> getWorldsMap() {
        return this.worlds;
    }

    @NotNull
    public Collection<SunWorld> getWorlds() {
        return this.worlds.values();
    }

    @Nullable
    public SunWorld getWorldById(@NotNull String id) {
        return this.worlds.get(id.toLowerCase());
    }

    public boolean isSunWorld(@NotNull World world) {
        return this.getWorldById(world.getName()) != null;
    }

    @NotNull
    public List<String> getWorldNames() {
        return new ArrayList<>(this.worlds.keySet());
    }

    @NotNull
    public Map<String, WorldInventory> getInventoryMap() {
        return inventoryMap;
    }

    @NotNull
    public WorldInventory getWorldInventory(@NotNull Player player) {
        String id = player.getUniqueId().toString();
        if (this.inventoryMap.containsKey(id)) {
            return this.inventoryMap.get(id);
        }

        File file = new File(this.getFullPath() + "/inventories/" + id + ".yml");
        WorldInventory worldInventory = new WorldInventory(this, new JYML(file));

        this.inventoryMap.put(worldInventory.getId(), worldInventory);
        return worldInventory;
    }

    public boolean isInventoryAffected(@NotNull Player player) {
        return this.isInventoryAffected(player.getWorld());
    }

    public boolean isInventoryAffected(@NotNull World world) {
        return this.getWorldGroup(world) != null;
    }

    @Nullable
    public String getWorldGroup(@NotNull World world) {
        String worldName = world.getName();

        return WorldsConfig.INVENTORY_SPLIT_WORLD_GROUPS.entrySet().stream()
            .filter(entry -> entry.getValue().contains(worldName))
            .map(Map.Entry::getKey).findFirst().orElse(null);
    }
}
