package su.nexmedia.sunlight.modules.homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexEngine;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.core.config.CoreConfig;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.NumberUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.homes.event.PlayerTeleportHomeEvent;
import su.nexmedia.sunlight.modules.homes.menu.HomeMenu;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class Home implements ICleanable, IEditable, IPlaceholder {

    public static final String PLACEHOLDER_ID               = "%home_id%";
    public static final String PLACEHOLDER_NAME             = "%home_name%";
    public static final String PLACEHOLDER_OWNER            = "%home_owner%";
    public static final String PLACEHOLDER_INVITED_PLAYERS  = "%home_invited_players%";
    public static final String PLACEHOLDER_IS_RESPAWN_POINT = "%home_is_respawn_point%";
    public static final String PLACEHOLDER_ICON_MATERIAL    = "%home_icon_material%";
    public static final String PLACEHOLDER_LOCATION_X       = "%home_location_x%";
    public static final String PLACEHOLDER_LOCATION_Y       = "%home_location_y%";
    public static final String PLACEHOLDER_LOCATION_Z       = "%home_location_z%";
    public static final String PLACEHOLDER_LOCATION_WORLD   = "%home_location_world%";
    private final String id;
    private final String owner;
    private transient HomeMenu editor;
    private String      name;
    private Material    iconMaterial;
    private Location    location;
    private Set<String> invitedPlayers;
    private boolean     isRespawnPoint;

    public Home(@NotNull String owner, @NotNull String id, @NotNull Location location) {
        this(id, owner, id, location.getBlock().getRelative(BlockFace.DOWN).getType(), location, new HashSet<>(), false);
    }

    public Home(
        @NotNull String id,
        @NotNull String owner,
        @NotNull String name,
        @NotNull Material iconMaterial,
        @NotNull Location location,
        @NotNull Set<String> invitedPlayers,
        boolean isRespawnPoint) {
        this.id = id.toLowerCase();
        this.owner = owner;
        this.setName(name);
        this.setIconMaterial(iconMaterial);
        this.setLocation(location);
        this.setInvitedPlayers(invitedPlayers);
        this.setRespawnPoint(isRespawnPoint);
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        Location location = this.getLocation();
        String world = location.getWorld() == null ? "null" : CoreConfig.getWorldName(location.getWorld().getName());

        return str -> str
            .replace(PLACEHOLDER_ID, this.getId())
            .replace(PLACEHOLDER_NAME, this.getName())
            .replace(PLACEHOLDER_OWNER, this.getOwner())
            .replace(PLACEHOLDER_INVITED_PLAYERS, String.join(",", this.getInvitedPlayers()))
            .replace(PLACEHOLDER_IS_RESPAWN_POINT, NexEngine.get().lang().getBool(this.isRespawnPoint()))
            .replace(PLACEHOLDER_ICON_MATERIAL, NexEngine.get().lang().getEnum(this.getIconMaterial()))
            .replace(PLACEHOLDER_LOCATION_X, NumberUT.format(location.getX()))
            .replace(PLACEHOLDER_LOCATION_Y, NumberUT.format(location.getY()))
            .replace(PLACEHOLDER_LOCATION_Z, NumberUT.format(location.getZ()))
            .replace(PLACEHOLDER_LOCATION_WORLD, world)
            ;
    }

    @Override
    @NotNull
    public HomeMenu getEditor() {
        if (this.editor == null) {
            HomeManager homeManager = SunLight.getInstance().getModuleCache().getHomeManager();
            if (homeManager == null) throw new IllegalStateException("Attempt to use home editor while module is disabled!");

            this.editor = new HomeMenu(homeManager, this);
        }
        return editor;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getOwner() {
        return owner;
    }

    public boolean isOwner(@NotNull Player player) {
        return this.getOwner().equalsIgnoreCase(player.getName());
    }

    @NotNull
    public Material getIconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(@NotNull Material iconMaterial) {
        if (iconMaterial.isAir()) iconMaterial = Material.GRASS_BLOCK;
        this.iconMaterial = iconMaterial;
    }

    @NotNull
    public String getName() {
        return name;
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

    public boolean isPublic() {
        return this.getInvitedPlayers().contains(Constants.MASK_ANY);
    }

    public void setPublic(boolean isOpen) {
        if (isOpen) {
            this.getInvitedPlayers().add(Constants.MASK_ANY);
        }
        else {
            this.getInvitedPlayers().remove(Constants.MASK_ANY);
        }
    }

    @NotNull
    public Set<String> getInvitedPlayers() {
        return this.invitedPlayers;
    }

    public void setInvitedPlayers(@NotNull Set<String> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }

    public void addInvitedPlayer(@NotNull String name) {
        this.getInvitedPlayers().add(name.toLowerCase());
    }

    public boolean isInvitedPlayer(@NotNull Player player) {
        return this.isPublic() || this.getInvitedPlayers().contains(player.getName().toLowerCase());
    }

    public boolean isRespawnPoint() {
        return this.isRespawnPoint;
    }

    public void setRespawnPoint(boolean isRespawnPoint) {
        this.isRespawnPoint = isRespawnPoint;
    }

    public void teleport(@NotNull Player player) {
        player.teleport(this.getLocation());

        PlayerTeleportHomeEvent homeEvent = new PlayerTeleportHomeEvent(player, this);
        Bukkit.getPluginManager().callEvent(homeEvent);
    }
}