package su.nexmedia.sunlight.modules.spawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.spawn.command.DeleteSpawnCommand;
import su.nexmedia.sunlight.modules.spawn.command.SetSpawnCommand;
import su.nexmedia.sunlight.modules.spawn.command.SpawnCommand;
import su.nexmedia.sunlight.modules.spawn.command.SpawnEditorCommand;
import su.nexmedia.sunlight.modules.spawn.config.SpawnLang;
import su.nexmedia.sunlight.modules.spawn.editor.EditorHandlerSpawn;
import su.nexmedia.sunlight.modules.spawn.editor.EditorMenuSpawnList;
import su.nexmedia.sunlight.modules.spawn.listener.SpawnListener;

import java.util.*;

public class SpawnManager extends SunModule {

    private SpawnLang lang;

    private Map<String, Spawn> spawns;

    private EditorMenuSpawnList editor;

    public SpawnManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.SPAWN;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new SpawnLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        this.spawns = new HashMap<>();
        for (JYML cfg : JYML.loadAll(this.getFullPath() + "spawns", true)) {
            try {
                Spawn spawn = new Spawn(this, cfg);
                this.spawns.put(spawn.getId(), spawn);
            } catch (Exception ex) {
                plugin.error("Unable to load spawn " + cfg.getFile().getName());
                ex.printStackTrace();
            }
        }
        this.info("Loaded " + this.spawns.size() + " spawns!");

        this.plugin.getSunEditorHandler().addInputHandler(Spawn.class, new EditorHandlerSpawn(this));

        this.plugin.getCommandRegulator().register(new SpawnCommand(this));
        this.plugin.getCommandRegulator().register(new SpawnEditorCommand(this));
        this.plugin.getCommandRegulator().register(new SetSpawnCommand(this));
        this.plugin.getCommandRegulator().register(new DeleteSpawnCommand(this));

        this.addListener(new SpawnListener(this));
    }

    @Override
    public void onShutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.spawns != null) {
            this.spawns.values().forEach(Spawn::clear);
            this.spawns.clear();
            this.spawns = null;
        }
        this.plugin.getSunEditorHandler().removeInputHandler(Spawn.class);
    }

    @NotNull
    public SpawnLang getLang() {
        return lang;
    }

    @NotNull
    public EditorMenuSpawnList getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMenuSpawnList(this);
        }
        return this.editor;
    }

    @NotNull
    public Collection<Spawn> getSpawns() {
        return this.spawns.values();
    }

    @NotNull
    public List<String> getSpawnIds() {
        return new ArrayList<>(this.spawns.keySet());
    }

    @Nullable
    public Spawn getSpawnById(@NotNull String id) {
        return this.spawns.get(id.toLowerCase());
    }

    @Nullable
    public Spawn getSpawnByDefault() {
        return this.getSpawns().stream().filter(Spawn::isDefault).findFirst().orElse(null);
    }

    @Nullable
    public Spawn getSpawnByLogin(@NotNull Player player) {
        boolean hasPlayed = plugin.getData().isUserExists(player.getUniqueId().toString(), true);

        Optional<Spawn> opt = this.getSpawns().stream()
            .filter(spawn -> {
                if (spawn.isTeleportOnFirstLogin() && !hasPlayed) return true;
                if (spawn.isTeleportOnLogin(player) && spawn.hasPermission(player)) return true;
                return false;
            })
            .max(Comparator.comparingInt(Spawn::getPriority));

        return opt.orElse(null);
    }

    @Nullable
    public Spawn getSpawnByDeath(@NotNull Player player) {
        return this.getSpawns().stream()
            .filter(spawn -> spawn.isTeleportOnDeath(player) && spawn.hasPermission(player))
            .max(Comparator.comparingInt(Spawn::getPriority)).orElse(null);
    }

    public boolean setSpawn(@NotNull Player player, @NotNull String id) {
        Location location = player.getLocation();
        id = id.replace(" ", "_").toLowerCase();

        if (!RegexUT.matchesEnRu(id)) {
            plugin.lang().Error_InvalidName.replace("%name%", id).send(player);
            return false;
        }

        Spawn spawn = this.getSpawnById(id);
        if (spawn != null) {
            spawn.setLocation(location);
        }
        else {
            spawn = new Spawn(this, id, location);
        }
        if (this.spawns.isEmpty()
            || (id.equalsIgnoreCase(Constants.DEFAULT) && this.getSpawnByDefault() == null)) {
            spawn.setDefault(true);
        }

        spawn.save();
        this.getLang().Command_SetSpawn_Done.replace(spawn.replacePlaceholders()).send(player);
        this.spawns.put(spawn.getId(), spawn);
        return true;
    }

    public void deleteSpawn(@NotNull Spawn spawn) {
        if (spawn.getFile().delete()) {
            spawn.clear();
            this.spawns.remove(spawn.getId());
        }
    }
}
