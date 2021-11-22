package su.nexmedia.sunlight.modules.worlds.world;

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.FileUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.spawn.Spawn;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.editor.WorldEditorMenuWorld;
import su.nexmedia.sunlight.modules.worlds.world.generator.EWorldGenerator;

import java.io.File;
import java.util.function.UnaryOperator;

public class SunWorld extends AbstractLoadableItem<SunLight> implements IEditable, ICleanable, IPlaceholder {

    public static final String PLACEHOLDER_AUTO_LOAD   = "%world_auto_load%";
    public static final String PLACEHOLDER_IS_LOADED   = "%world_is_loaded%";
    public static final String PLACEHOLDER_ID          = "%world_id%";
    public static final String PLACEHOLDER_GENERATOR   = "%world_generator%";
    public static final String PLACEHOLDER_TYPE        = "%world_type%";
    public static final String PLACEHOLDER_ENVIRONMENT = "%world_environment%";
    public static final String PLACEHOLDER_DIFFICULTY  = "%world_difficulty%";
    public static final String PLACEHOLDER_SEED        = "%world_seed%";
    public static final String PLACEHOLDER_STRUCTURES  = "%world_structures%";
    private final WorldManager worldManager;
    private boolean     autoLoad;
    private String      generator;
    private WorldType   type;
    private Environment environment;
    private Difficulty  difficulty;
    private long        seed;
    private boolean     structures;
    private WorldEditorMenuWorld editor;

    public SunWorld(@NotNull WorldManager worldManager, @NotNull String id) {
        super(worldManager.plugin(), worldManager.getFullPath() + "/worlds/" + id.toLowerCase() + ".yml");
        this.worldManager = worldManager;

        this.setAutoLoad(false);
        this.setEnvironment(Environment.NORMAL);
        this.setGenerator(null);
        this.setType(WorldType.NORMAL);
        this.setDifficulty(Difficulty.NORMAL);
        this.setSeed(0L);
        this.setStructuresEnabled(true);
    }

    public SunWorld(@NotNull WorldManager worldManager, @NotNull JYML cfg) {
        super(worldManager.plugin(), cfg);
        this.worldManager = worldManager;

        this.setAutoLoad(cfg.getBoolean("Auto_Load"));
        this.setGenerator(cfg.getString("Generator"));
        this.setType(cfg.getEnum("Type", WorldType.class, WorldType.NORMAL));
        this.setEnvironment(cfg.getEnum("Environment", Environment.class, Environment.NORMAL));
        this.setDifficulty(cfg.getEnum("Difficulty", Difficulty.class, Difficulty.NORMAL));
        this.setSeed(cfg.getLong("Seed", 0L));
        this.setStructuresEnabled(cfg.getBoolean("Structures_Enabled"));
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(PLACEHOLDER_AUTO_LOAD, plugin.lang().getBool(this.isAutoLoad()))
            .replace(PLACEHOLDER_IS_LOADED, plugin.lang().getBool(this.isLoaded()))
            .replace(PLACEHOLDER_ID, this.getId())
            .replace(PLACEHOLDER_GENERATOR, String.valueOf(this.getGenerator()))
            .replace(PLACEHOLDER_TYPE, worldManager.getLang().getEnum(this.getType()))
            .replace(PLACEHOLDER_ENVIRONMENT, worldManager.getLang().getEnum(this.getEnvironment()))
            .replace(PLACEHOLDER_DIFFICULTY, worldManager.getLang().getEnum(this.getDifficulty()))
            .replace(PLACEHOLDER_SEED, String.valueOf(this.getSeed()))
            .replace(PLACEHOLDER_STRUCTURES, plugin.lang().getBool(this.isStructuresEnabled()))
            ;
    }

    @Override
    public void onSave() {
        cfg.set("Auto_Load", this.isAutoLoad());
        cfg.set("Generator", this.getGenerator());
        cfg.set("Type", this.getType().name());
        cfg.set("Environment", this.getEnvironment().name());
        cfg.set("Difficulty", this.getDifficulty().name());
        cfg.set("Seed", this.getSeed());
        cfg.set("Structures_Enabled", this.isStructuresEnabled());
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    @Override
    public WorldEditorMenuWorld getEditor() {
        if (this.editor == null) {
            this.editor = new WorldEditorMenuWorld(this.worldManager, this);
        }
        return editor;
    }

    @Nullable
    public World getWorld() {
        return this.plugin.getServer().getWorld(this.getId());
    }

    public boolean isLoaded() {
        return this.getWorld() != null;
    }

    public boolean isAutoLoad() {
        return this.autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    @Nullable
    public String getGenerator() {
        return this.generator;
    }

    public void setGenerator(@Nullable String generator) {
        this.generator = generator;
    }

    @NotNull
    public WorldType getType() {
        return this.type;
    }

    public void setType(@Nullable WorldType type) {
        this.type = (type == null ? WorldType.NORMAL : type);
    }

    @NotNull
    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(@Nullable Environment environment) {
        this.environment = (environment == null || environment == Environment.CUSTOM ? Environment.NORMAL : environment);
    }

    @NotNull
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(@Nullable Difficulty difficulty) {
        this.difficulty = (difficulty == null ? Difficulty.NORMAL : difficulty);
    }

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean isStructuresEnabled() {
        return this.structures;
    }

    public void setStructuresEnabled(boolean structures) {
        this.structures = structures;
    }

    public boolean create() {
        if (this.isLoaded()) return false;

        WorldCreator creator = new WorldCreator(this.getId());
        if (this.getSeed() != 0L) {
            creator.seed(this.getSeed());
        }

        if (this.getGenerator() != null) {
            EWorldGenerator genType = CollectionsUT.getEnum(this.getGenerator(), EWorldGenerator.class);
            ChunkGenerator chunkGen = genType != null ? genType.getGenerator() : this.getPluginGenerator(this.getGenerator());

            if (chunkGen != null) {
                creator.generator(chunkGen);
            }
        }

        creator.type(this.getType());
        creator.environment(this.getEnvironment());
        creator.generateStructures(this.isStructuresEnabled());

        World bukkitWorld = creator.createWorld();
        if (bukkitWorld == null) return false;

        bukkitWorld.setDifficulty(this.getDifficulty());

        // Fix world seed if it was not specified on create.
        if (this.getSeed() == 0L) {
            this.setSeed(bukkitWorld.getSeed());
        }
        return true;
    }

    public boolean delete(boolean withFolder) {
        if (this.isLoaded()) return false;
        if (!this.getFile().delete()) return false;

        if (withFolder) {
            File dir = new File(plugin.getServer().getWorldContainer() + "/" + this.getId());
            FileUT.deleteRecursive(dir); // Delete bukkit world folder.
        }

        this.worldManager.getWorldsMap().remove(this.getId());
        return true;
    }

    public boolean unload() {
        World bukkitWorld = this.getWorld();
        if (bukkitWorld == null) return false;

        Location defSpawn = null;
        SpawnManager spawnManager = this.plugin.getModuleCache().getSpawnManager();
        if (spawnManager != null && spawnManager.isLoaded()) {
            Spawn spawn = spawnManager.getSpawnByDefault();
            if (spawn != null) {
                defSpawn = spawn.getLocation();
            }
        }
        if (defSpawn == null) {
            defSpawn = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
        }

        for (Player player : bukkitWorld.getPlayers()) {
            player.teleport(defSpawn);
        }

        return this.plugin.getServer().unloadWorld(bukkitWorld, true);
    }

    @Nullable
    private ChunkGenerator getPluginGenerator(@NotNull String name) {
        String[] split = name.split(":", 2);
        String id = split.length > 1 ? split[1] : null;
        Plugin plugin = Bukkit.getPluginManager().getPlugin(split[0]);
        if (plugin == null || !plugin.isEnabled()) {
            return null;
        }
        return plugin.getDefaultWorldGenerator(this.getId(), id);
    }
}
