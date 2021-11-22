package su.nexmedia.sunlight.modules.spawn;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.NumberUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.spawn.editor.EditorMenuSpawn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class Spawn extends AbstractLoadableItem<SunLight> implements ICleanable, IEditable, IPlaceholder {

    public static final String PLACEHOLDER_ID                  = "%spawn_id%";
    public static final String PLACEHOLDER_NAME                = "%spawn_name%";
    public static final String PLACEHOLDER_LOCATION            = "%spawn_location%";
    public static final String PLACEHOLDER_PERMISSION_REQUIRED = "%spawn_permission_required%";
    public static final String PLACEHOLDER_PERMISSION_NODE     = "%spawn_permission_node%";
    public static final String PLACEHOLDER_IS_DEFAULT          = "%spawn_is_default%";
    public static final String PLACEHOLDER_PRIORITY            = "%spawn_priority%";
    public static final String PLACEHOLDER_TELEPORT_LOGIN      = "%spawn_teleport_login%";
    public static final String PLACEHOLDER_TELEPORT_DEATH      = "%spawn_teleport_death%";
    public static final String PLACEHOLDER_TELEPORT_LOGIN_NEW  = "%spawn_teleport_login_newbie%";
    public static final String PLACEHOLDER_GROUPS_LOGIN        = "%spawn_groups_login%";
    public static final String PLACEHOLDER_GROUPS_DEATH        = "%spawn_groups_death%";
    private final SpawnManager spawnManager;
    private final Set<String> groupsLoginTp;
    private final Set<String> groupsDeathTp;
    private       String      name;
    private       Location    location;
    private       boolean     isPermission;
    private       boolean     isDefault;
    private       int         priority;
    private       boolean     isTpOnLogin;
    private       boolean     isTpOnFirstLogin;
    private       boolean     isTpOnDeath;
    private EditorMenuSpawn editor;

    public Spawn(@NotNull SpawnManager spawnManager, @NotNull String id, @NotNull Location loc) {
        super(spawnManager.plugin(), spawnManager.getFullPath() + "spawns/" + id + ".yml");
        this.spawnManager = spawnManager;

        this.setName("&7" + id);
        this.setLocation(loc);
        this.setPermissionRequired(true);
        this.setDefault(false);
        this.setPriority(0);
        this.setTeleportOnLogin(false);
        this.setTeleportOnFirstLogin(false);
        this.setTeleportOnDeath(false);
        this.groupsLoginTp = new HashSet<>();
        this.groupsDeathTp = new HashSet<>();
    }

    public Spawn(@NotNull SpawnManager spawnManager, @NotNull JYML cfg) {
        super(spawnManager.plugin(), cfg);
        this.spawnManager = spawnManager;

        this.setName(cfg.getString("Name", "&7" + this.getId()));
        Location location = cfg.getLocation("Location");
        if (location == null) {
            throw new IllegalArgumentException("Invalid " + getId() + " spawn location!");
        }
        this.setLocation(location);

        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));
        this.setDefault(cfg.getBoolean("Is_Default"));
        this.setPriority(cfg.getInt("Priority"));
        this.setTeleportOnLogin(cfg.getBoolean("Teleport_On_Login.Enabled"));
        this.setTeleportOnFirstLogin(cfg.getBoolean("Teleport_On_Login.For_New_Players"));
        this.setTeleportOnDeath(cfg.getBoolean("Teleport_On_Death.Enabled"));
        this.groupsLoginTp = new HashSet<>(cfg.getStringList("Teleport_On_Login.Groups").stream()
            .map(String::toLowerCase).toList());
        this.groupsDeathTp = new HashSet<>(cfg.getStringList("Teleport_On_Death.Groups").stream()
            .map(String::toLowerCase).toList());
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        Location location = this.getLocation();
        String world = location.getWorld() == null ? "null" : location.getWorld().getName();
        String locX = NumberUT.format(location.getX());
        String locY = NumberUT.format(location.getY());
        String locZ = NumberUT.format(location.getZ());

        return str -> str
            .replace(PLACEHOLDER_ID, this.getId())
            .replace(PLACEHOLDER_NAME, this.getName())
            .replace(PLACEHOLDER_LOCATION, locX + ", " + locY + ", " + locZ + ", " + world)
            .replace(PLACEHOLDER_PERMISSION_REQUIRED, plugin.lang().getBool(this.isPermission))
            .replace(PLACEHOLDER_PERMISSION_NODE, SpawnPerms.SPAWN + this.getId())
            .replace(PLACEHOLDER_PRIORITY, String.valueOf(this.getPriority()))
            .replace(PLACEHOLDER_TELEPORT_LOGIN, plugin.lang().getBool(this.isTeleportOnLogin()))
            .replace(PLACEHOLDER_TELEPORT_LOGIN_NEW, plugin.lang().getBool(this.isTeleportOnFirstLogin()))
            .replace(PLACEHOLDER_TELEPORT_DEATH, plugin.lang().getBool(this.isTeleportOnDeath()))
            .replace(PLACEHOLDER_GROUPS_LOGIN, String.join(DELIMITER_DEFAULT, this.getTeleportOnLoginGroups()))
            .replace(PLACEHOLDER_GROUPS_DEATH, String.join(DELIMITER_DEFAULT, this.getTeleportOnDeathGroups()))
            ;
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Location", this.getLocation());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.set("Is_Default", this.isDefault());
        cfg.set("Priority", this.getPriority());
        cfg.set("Teleport_On_Login.Enabled", this.isTeleportOnLogin());
        cfg.set("Teleport_On_Login.For_New_Players", this.isTeleportOnFirstLogin());
        cfg.set("Teleport_On_Death.Enabled", this.isTeleportOnDeath());
        cfg.set("Teleport_On_Login.Groups", new ArrayList<>(this.getTeleportOnLoginGroups()));
        cfg.set("Teleport_On_Death.Groups", new ArrayList<>(this.getTeleportOnDeathGroups()));
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
    public EditorMenuSpawn getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMenuSpawn(this.spawnManager, this);
        }
        return this.editor;
    }


    @NotNull
    public SpawnManager getSpawnManager() {
        return this.spawnManager;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUT.color(name);
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    public boolean isPermissionRequired() {
        return this.isPermission;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.isPermission = isPermission;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired() || this.isDefault()) return true;
        return player.hasPermission(SpawnPerms.SPAWN + this.getId());
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isTeleportOnLogin() {
        return this.isTpOnLogin;
    }

    public void setTeleportOnLogin(boolean isTpOnLogin) {
        this.isTpOnLogin = isTpOnLogin;
    }

    public boolean isTeleportOnFirstLogin() {
        return this.isTpOnFirstLogin;
    }

    public void setTeleportOnFirstLogin(boolean isTpOnFirstLogin) {
        this.isTpOnFirstLogin = isTpOnFirstLogin;
    }

    @NotNull
    public Set<String> getTeleportOnLoginGroups() {
        return this.groupsLoginTp;
    }

    public boolean isTeleportOnLogin(@NotNull Player player) {
        if (!this.isTeleportOnLogin()) return false;
        if (this.groupsLoginTp.contains(Constants.MASK_ANY)) return true;

        return Hooks.getPermissionGroups(player).stream().anyMatch(this.groupsLoginTp::contains);
    }

    public boolean isTeleportOnDeath() {
        return this.isTpOnDeath;
    }

    public void setTeleportOnDeath(boolean isTpOnDeath) {
        this.isTpOnDeath = isTpOnDeath;
    }

    @NotNull
    public Set<String> getTeleportOnDeathGroups() {
        return this.groupsDeathTp;
    }

    public boolean isTeleportOnDeath(@NotNull Player player) {
        if (!this.isTeleportOnDeath()) return false;
        if (this.groupsDeathTp.contains(Constants.MASK_ANY)) return true;

        return Hooks.getPermissionGroups(player).stream().anyMatch(this.groupsDeathTp::contains);
    }

    public void teleport(@NotNull CommandSender sender, @NotNull Player player) {
        if (!sender.equals(player)) {
            this.spawnManager.getLang().Command_Spawn_Done_Others
                .replace(this.replacePlaceholders()).replace("%player%", player.getName())
                .send(sender);
        }
        this.teleport(player, true);
    }

    public boolean teleport(@NotNull Player player, boolean isForce) {
        if (!isForce && !this.hasPermission(player)) {
            plugin.lang().Error_NoPerm.send(player);
            return false;
        }

        player.teleport(this.getLocation());
        this.spawnManager.getLang().Command_Spawn_Done_Self.replace(this.replacePlaceholders()).send(player);
        return true;
    }
}