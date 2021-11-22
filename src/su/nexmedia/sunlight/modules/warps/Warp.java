package su.nexmedia.sunlight.modules.warps;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.external.VaultHK;
import su.nexmedia.engine.utils.NumberUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.warps.config.WarpConfig;
import su.nexmedia.sunlight.modules.warps.editor.WarpEditorWarp;
import su.nexmedia.sunlight.modules.warps.type.WarpType;

import java.util.*;
import java.util.function.UnaryOperator;

public class Warp extends AbstractLoadableItem<SunLight> implements ICleanable, IEditable, IPlaceholder {

    public static final String PLACEHOLDER_ID                  = "%warp_id%";
    public static final String PLACEHOLDER_NAME                = "%warp_name%";
    public static final String PLACEHOLDER_DESCRIPTION         = "%warp_description%";
    public static final String PLACEHOLDER_TYPE                = "%warp_type%";
    public static final String PLACEHOLDER_PERMISSION_REQUIRED = "%warp_permission_required%";
    public static final String PLACEHOLDER_PERMISSION_NODE     = "%warp_permission_node%";
    public static final String PLACEHOLDER_COST_MONEY          = "%warp_teleport_cost_money%";
    public static final String PLACEHOLDER_OWNER_NAME          = "%warp_owner_name%";
    public static final String PLACEHOLDER_WELCOME_MESSAGE     = "%warp_welcome_message%";
    public static final String PLACEHOLDER_ICON                = "%warp_icon%";
    public static final String PLACEHOLDER_VISITS              = "%warp_visits%";
    public static final String PLACEHOLDER_RATING              = "%warp_rating%";
    public static final String PLACEHOLDER_USER_ACCESS = "%warp_user_access%";
    private final WarpManager    warpManager;
    private final UUID   ownerId;
    private final String ownerName;
    private final Map<String, Long> visitCooldowns;
    private       WarpEditorWarp editor;
    private WarpType     type;
    private Location     location;
    private String       name;
    private List<String> description;
    private boolean      isPermissionRequired;
    private double       teleportCostMoney;
    private String       welcomeMessage;
    private ItemStack    icon;
    private       long              visits;
    private       double            ratingValue;

    public Warp(@NotNull WarpManager warpManager, @NotNull String id, @NotNull Player creator) {
        this(warpManager, id, creator.getUniqueId(), creator.getLocation(), WarpType.PLAYER);
    }

    public Warp(
        @NotNull WarpManager warpManager, @NotNull String id, @NotNull UUID ownerId,
        @NotNull Location location, @NotNull WarpType type) {
        super(warpManager.plugin(), warpManager.getFullPath() + "warps/" + id + ".yml");
        this.warpManager = warpManager;
        this.visitCooldowns = new HashMap<>();

        this.ownerId = ownerId;
        this.ownerName = this.getOwner().getName();

        this.setLocation(location);
        this.setIcon(new ItemStack(Material.COMPASS));
        this.setType(type);
        this.setName("&6" + id);
        this.setDescription(new ArrayList<>());
        this.setPermissionRequired(false);
        this.setTeleportCostMoney(0D);
        this.setWelcomeMessage("");
    }

    public Warp(@NotNull WarpManager warpManager, @NotNull JYML cfg) {
        super(warpManager.plugin(), cfg);
        this.warpManager = warpManager;
        this.visitCooldowns = new HashMap<>();

        this.ownerId = UUID.fromString(cfg.getString("Owner.Id", ""));
        this.ownerName = this.getOwner().getName();

        Location location = cfg.getLocation("Location");
        if (location == null) {
            throw new IllegalArgumentException("Invalid warp location or world!");
        }
        this.setLocation(location);
        this.setIcon(cfg.getItem("Icon"));
        this.setType(cfg.getEnum("Type", WarpType.class, WarpType.SERVER));
        this.setName(cfg.getString("Name", this.getId()));
        this.setDescription(cfg.getStringList("Description"));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));
        this.setTeleportCostMoney(cfg.getDouble("Teleport.Cost.Money"));
        this.setWelcomeMessage(cfg.getString("Welcome.Message", ""));

        this.setVisits(cfg.getLong("Rating.Visits_Amount"));
        cfg.getSection("Rating.Visits_Cooldowns").forEach(uuid -> {
            long date = cfg.getLong("Rating.Visits_Cooldowns." + uuid);

            // Skip visit cooldown for one-timed visits when it was changed to timed.
            if (date < 0 && WarpConfig.RATING_VISIT_COOLDOWN >= 0) return;

            this.setVisitCooldown(uuid, date);
        });
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(PLACEHOLDER_ID, this.getId())
            .replace(PLACEHOLDER_NAME, this.getName())
            .replace(PLACEHOLDER_DESCRIPTION, String.join("\n", this.getDescription()))
            .replace(PLACEHOLDER_TYPE, warpManager.getLang().getEnum(this.getType()))
            .replace(PLACEHOLDER_PERMISSION_REQUIRED, plugin.lang().getBool(this.isPermissionRequired()))
            .replace(PLACEHOLDER_PERMISSION_NODE, WarpPerms.WARP + this.getId())
            .replace(PLACEHOLDER_COST_MONEY, NumberUT.format(this.getTeleportCostMoney()))
            .replace(PLACEHOLDER_OWNER_NAME, this.getOwnerName())
            .replace(PLACEHOLDER_ICON, plugin.lang().getEnum(this.getIcon().getType()))
            .replace(PLACEHOLDER_VISITS, String.valueOf(this.getVisits()))
            .replace(PLACEHOLDER_WELCOME_MESSAGE, this.getWelcomeMessage())
            .replace(PLACEHOLDER_RATING, NumberUT.format(this.getRatingValue()))
            ;
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders(@NotNull Player player) {
        return str -> this.replacePlaceholders().apply(str
            .replace(PLACEHOLDER_USER_ACCESS, plugin.lang().getBool(this.hasPermission(player)))
        );
    }

    @Override
    public void onSave() {
        cfg.set("Owner.Id", this.getOwnerId().toString());
        cfg.set("Location", this.getLocation());
        cfg.set("Type", this.getType().name());
        cfg.set("Name", this.getName());
        cfg.set("Description", this.getDescription());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.set("Teleport.Cost.Money", this.getTeleportCostMoney());
        cfg.set("Welcome.Message", this.getWelcomeMessage());
        cfg.setItem("Icon", this.getIcon());

        cfg.set("Rating.Visits_Amount", this.getVisits());
        cfg.set("Rating.Visits_Cooldowns", null);
        this.getVisitCooldowns().forEach((uuid, date) -> {
            cfg.set("Rating.Visits_Cooldowns." + uuid, date);
        });
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
    public WarpEditorWarp getEditor() {
        if (this.editor == null) {
            this.editor = new WarpEditorWarp(this);
        }
        return this.editor;
    }

    @NotNull
    public WarpManager getWarpManager() {
        return this.warpManager;
    }

    @NotNull
    public WarpType getType() {
        return type;
    }

    public void setType(@NotNull WarpType type) {
        this.type = type;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUT.color(name);
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = StringUT.color(description);
    }

    public boolean isPermissionRequired() {
        return this.isPermissionRequired;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.isPermissionRequired = isPermission;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!isPermissionRequired() || this.isOwner(player)) return true;
        return player.hasPermission(WarpPerms.WARP + this.getId());
    }

    public double getTeleportCostMoney() {
        return this.teleportCostMoney;
    }

    public void setTeleportCostMoney(double teleportCostMoney) {
        this.teleportCostMoney = teleportCostMoney;
    }

    @NotNull
    public OfflinePlayer getOwner() {
        return this.plugin.getServer().getOfflinePlayer(this.getOwnerId());
    }

    @NotNull
    public UUID getOwnerId() {
        return ownerId;
    }

    @NotNull
    public String getOwnerName() {
        return this.ownerName;
    }

    @NotNull
    public String getWelcomeMessage() {
        return this.welcomeMessage;
    }

    public void setWelcomeMessage(@NotNull String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public long getVisits() {
        return visits;
    }

    public void setVisits(long visits) {
        if (WarpConfig.RATING_VISIT_MAXIMUM > 0) visits = Math.min(WarpConfig.RATING_VISIT_MAXIMUM, visits);
        this.visits = visits;
        this.getWarpManager().updateWarpRatings();
    }

    public void addVisit(@NotNull Player player) {
        if (!this.isVisitCooldownExpired(player)) return;

        this.setVisits(this.getVisits() + 1);
        this.setVisitCooldown(player);
    }

    @NotNull
    public Map<String, Long> getVisitCooldowns() {
        this.visitCooldowns.values().removeIf(date -> date >= 0 && System.currentTimeMillis() >= date);
        return this.visitCooldowns;
    }

    public void setVisitCooldown(@NotNull Player player) {
        this.setVisitCooldown(player.getUniqueId().toString());
    }

    public void setVisitCooldown(@NotNull String uuid) {
        if (WarpConfig.RATING_VISIT_COOLDOWN == 0) return;

        long unlock = WarpConfig.RATING_VISIT_COOLDOWN < 0 ? -1L : System.currentTimeMillis() + (WarpConfig.RATING_VISIT_COOLDOWN * 1000L);
        this.setVisitCooldown(uuid, unlock);
    }

    public void setVisitCooldown(@NotNull String uuid, long date) {
        this.getVisitCooldowns().put(uuid, date);
    }

    public long getVisitCooldownExpireDate(@NotNull Player player) {
        return this.getVisitCooldowns().getOrDefault(player.getUniqueId().toString(), 0L);
    }

    public boolean isVisitCooldownExpired(@NotNull Player player) {
        long date = this.getVisitCooldownExpireDate(player);
        return date >= 0 && System.currentTimeMillis() > date;
    }

    public double getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }

    public void updateRatingValue() {
        double visits = this.getVisits();
        double visitsAll = this.getWarpManager().getWarps().stream().mapToLong(Warp::getVisits).sum();
        double scale = WarpConfig.RATING_SCALE_OF;

        double rating = visitsAll <= 0 ? 0D : scale * (visits / visitsAll);
        this.setRatingValue(rating);
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    public boolean isOwner(@NotNull Player player) {
        return this.getOwnerName().equalsIgnoreCase(player.getName());
    }

    public void teleport(@NotNull Player player, boolean isForced) {
        if (!isForced && !this.hasPermission(player)) {
            this.getWarpManager().getLang().Warp_Error_NoPerm.send(player);
            return;
        }

        // Check for safe location and prevent errors.
        Location location = this.getLocation();
        if (location.getWorld() == null) {
            this.getWarpManager().getLang().Warp_Error_Invalid.replace(PLACEHOLDER_ID, this.getId()).send(player);
            return;
        }

        if (!isForced) {
            boolean isSafe = true;
            Block warpBlock = location.getBlock();
            // Check if warp location is filled with solid or liquid block that will suffer the player.
            if (!warpBlock.isEmpty() || warpBlock.getType().isSolid() || warpBlock.isLiquid()) {
                Block above = warpBlock.getRelative(BlockFace.UP);
                if (!above.isEmpty() || above.isLiquid() || above.getType().isSolid()) {
                    isSafe = false;
                }
            }

            // Check if block under the warp block is empty or liquid, potential fall/lava trap.
            Block under = warpBlock.getRelative(BlockFace.DOWN);
            if (under.isEmpty() || under.isLiquid() || !under.getType().isSolid()) {
                isSafe = false;
            }

            if (!isSafe) {
                this.getWarpManager().getLang().Warp_Teleport_Error_Unsafe.send(player);
                if (!this.isOwner(player) && !player.hasPermission(WarpPerms.BYPASS_UNSAFE)) {
                    return;
                }
            }
        }

        // Check teleportation costs.
        double costMoney = (isForced || player.hasPermission(WarpPerms.BYPASS_WARP_TELEPORT_COST)) ? 0 : this.getTeleportCostMoney();
        VaultHK vault = plugin.getVault();
        if (costMoney > 0 && vault != null && vault.hasEconomy()) {
            double balance = vault.getBalance(player);
            if (balance < costMoney) {
                this.getWarpManager().getLang().Warp_Teleport_Error_NotEnoughFunds.replace(this.replacePlaceholders()).send(player);
                return;
            }
            vault.take(player, costMoney);
            if (WarpConfig.WARP_TELEPORT_COST_MONEY_TO_OWNER) {
                vault.give(this.getOwner(), costMoney);
            }
        }

        player.teleport(location);
        this.getWarpManager().getLang().Command_Warps_Done_Self.replace(this.replacePlaceholders()).send(player);
        this.addVisit(player);
    }
}