package su.nexmedia.sunlight.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.data.AbstractUser;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.commands.CommandRegister;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.homes.Home;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.user.IgnoredUser;
import su.nexmedia.sunlight.user.TeleportRequest;
import su.nexmedia.sunlight.user.editor.EditorUserIgnoredList;

import java.util.*;
import java.util.Map.Entry;

public class SunUser extends AbstractUser<SunLight> implements ICleanable {

    public static final String SETTING_BALANCE           = "BALANCE";
    public static final String SETTING_SOCIAL_SPY        = "SOCIAL_SPY";
    public static final String SETTING_GOD_MODE          = "GOD_MODE";
    public static final String SETTING_ANTI_PHANTOM      = "ANTI_PHANTOM";
    public static final String SETTING_TELEPORT_REQUESTS = "TELEPORT_REQUESTS";
    public static final String SETTING_VANISHED          = "VANISHED";
    private final Map<String, Home>        homes;
    private final Map<String, Long>        kitCooldowns;
    private final Map<String, Long>        cooldownCommands;
    private final List<TeleportRequest>    teleportRequests;
    private final Map<String, IgnoredUser> ignoredUsers;
    private final Map<String, Boolean>     settingsBool;
    private final Map<String, Double>      settingsNum;
    private       String                   ip;
    private       String                   nick;
    private       Location                 previousLocation;
    private       Location                 deathLocation;
    private transient EditorUserIgnoredList editorIgnoredList;

    public SunUser(@NotNull SunLight plugin, @NotNull Player player) {
        this(
            plugin,
            player.getUniqueId(),                // UUID
            player.getName(),                    // Player Name
            System.currentTimeMillis(),    // Last Login
            PlayerUT.getIP(player),                // User IP
            player.getName(),                    // Custom Nick
            new HashMap<>(),                // Homes
            new HashMap<>(),                // Kit Cooldowns
            new HashMap<>(),                // Command Cooldowns
            new HashMap<>(),                // Ignore List
            new HashMap<>(), // Settings Boolean
            new HashMap<>()  // Settings Number
        );
    }

    public SunUser(
        @NotNull SunLight plugin,
        @NotNull UUID uuid,
        @NotNull String name,
        long lastOnline,
        @NotNull String ip,
        @NotNull String nick,
        @NotNull Map<String, Home> homes,
        @NotNull Map<String, Long> kitCooldowns,
        @NotNull Map<String, Long> commandCooldowns,
        @NotNull Map<String, IgnoredUser> ignoredUsers,
        @NotNull Map<String, Boolean> settingsBool,
        @NotNull Map<String, Double> settingsNum
    ) {
        super(plugin, uuid, name, lastOnline);

        // Fix for the nickname field, where it can use old user name
        // in case when they change their license account name, but
        // in the database it was stored as a old one.
        String nameUpdated = this.getName(); // Already updated name after the .super constructor.
        if (!name.equalsIgnoreCase(nameUpdated) && nick.equalsIgnoreCase(name)) {
            nick = nameUpdated;
        }

        this.setIp(ip);
        this.setCustomNick(nick);
        this.homes = new HashMap<>(homes);
        this.kitCooldowns = new HashMap<>(kitCooldowns);
        this.cooldownCommands = new HashMap<>(commandCooldowns);
        this.teleportRequests = new ArrayList<>();
        this.ignoredUsers = new HashMap<>(ignoredUsers);
        this.setPreviousLocation(null);
        this.setDeathLocation(null);
        this.settingsBool = new HashMap<>(settingsBool);
        this.settingsNum = new HashMap<>(settingsNum);

        this.setGodMode(false);
        this.setVanished(false);
    }

    @Override
    public void clear() {
        this.clearEditorHomes();
        this.clearEditorIgnored();
    }

    public void clearEditorHomes() {
        for (Home home : this.getHomes().values()) {
            home.clear();
        }
    }

    public void clearEditorIgnored() {
        if (this.editorIgnoredList != null) {
            this.editorIgnoredList.clear();
            this.editorIgnoredList = null;
        }
        this.getIgnoredUsers().values().forEach(IgnoredUser::clear);
    }

    @NotNull
    public EditorUserIgnoredList getEditorIgnoredList() {
        if (this.editorIgnoredList == null) {
            this.editorIgnoredList = new EditorUserIgnoredList(this.plugin, this);
        }
        return this.editorIgnoredList;
    }

    @NotNull
    public String getCustomNick() {
        return this.nick;
    }

    public void setCustomNick(@NotNull String nick) {
        this.nick = (nick.isEmpty() || nick.equalsIgnoreCase("null")) ? this.getName() : StringUT.color(nick);
    }

    @NotNull
    public String getIp() {
        return this.ip;
    }

    public void setIp(@NotNull String ip) {
        this.ip = ip.replace("\\/", "").replace("/", "");
    }

    @Nullable
    public Location getPreviousLocation() {
        return this.previousLocation;
    }

    public void setPreviousLocation(@Nullable Location previousLocation) {
        this.previousLocation = previousLocation;
    }

    @Nullable
    public Location getDeathLocation() {
        return deathLocation;
    }

    public void setDeathLocation(@Nullable Location deathLocation) {
        this.deathLocation = deathLocation;
    }

    public boolean isTeleportRequestsEnabled() {
        return this.getSettingBoolean(SETTING_TELEPORT_REQUESTS);
    }

    public void setTeleportRequestsEnabled(boolean teleportAllowed) {
        this.setSettingBoolean(SETTING_TELEPORT_REQUESTS, teleportAllowed);
    }

    @NotNull
    public Map<String, Home> getHomes() {
        return this.homes;
    }

    @Nullable
    public Home getHomeById(@NotNull String id) {
        return this.getHomes().get(id.toLowerCase());
    }

    @NotNull
    public Map<String, Long> getCommandCooldowns() {
        this.cooldownCommands.values().removeIf(date -> date >= 0 && System.currentTimeMillis() > date);
        return this.cooldownCommands;
    }

    public long getCommandCooldown(@NotNull String command) {
        return this.getCommandCooldowns().entrySet().stream().filter(entry -> {
            return CommandRegister.getAliases(command, true).contains(entry.getKey());
        }).map(Entry::getValue).findFirst().orElse(0L);
    }

    public boolean isCommandOnCooldown(@NotNull String command) {
        return this.getCommandCooldown(command) < 0 || this.getCommandCooldown(command) > System.currentTimeMillis();
    }

    public void setCommandCooldown(@NotNull String cmd, int seconds) {
        if (seconds == 0) return;

        long expireDate = seconds < 0 ? -1L : System.currentTimeMillis() + seconds * 1000L;
        this.getCommandCooldowns().put(cmd, expireDate);
    }

    @NotNull
    public Map<String, Long> getKitCooldowns() {
        this.kitCooldowns.values().removeIf(date -> date > 0 && date < System.currentTimeMillis());
        return this.kitCooldowns;
    }

    public long getKitCooldown(@NotNull Kit kit) {
        return this.getKitCooldown(kit.getId());
    }

    public long getKitCooldown(@NotNull String id) {
        return this.getKitCooldowns().getOrDefault(id.toLowerCase(), 0L);
    }

    public boolean isKitOnCooldown(@NotNull Kit kit) {
        return this.isKitOnCooldown(kit.getId());
    }

    public boolean isKitOnCooldown(@NotNull String id) {
        return this.getKitCooldown(id) != 0L;
    }

    public void setKitCooldown(@NotNull Kit kit) {
        int cooldown = kit.getCooldown();
        if (cooldown == 0) return;

        long expireDate = cooldown < 0 ? -1L : System.currentTimeMillis() + kit.getCooldown() * 1000L;
        this.getKitCooldowns().put(kit.getId(), expireDate);
    }

    @NotNull
    public Map<String, IgnoredUser> getIgnoredUsers() {
        return this.ignoredUsers;
    }

    public boolean isIgnoredUser(@NotNull Player player) {
        return this.getIgnoredUser(player) != null;
    }

    public boolean isIgnoredUser(@NotNull String name) {
        return this.getIgnoredUser(name.toLowerCase()) != null;
    }

    public boolean addIgnoredUser(@NotNull Player player) {
        return this.addIgnoredUser(player.getName());
    }

    public boolean addIgnoredUser(@NotNull String name) {
        if (this.isIgnoredUser(name) || name.equalsIgnoreCase(this.getName())) return false;

        if (!RegexUT.matchesEnRu(name)) {
            return false;
        }

        IgnoredUser ignoredUser = new IgnoredUser(name);
        this.getIgnoredUsers().put(name.toLowerCase(), ignoredUser);
        return true;
    }

    public boolean removeIgnoredUser(@NotNull Player player) {
        return this.removeIgnoredUser(player.getName());
    }

    public boolean removeIgnoredUser(@NotNull String name) {
        IgnoredUser ignoredUser = this.getIgnoredUsers().remove(name.toLowerCase());
        if (ignoredUser == null) return false;

        ignoredUser.clear();
        return true;
    }

    @Nullable
    public IgnoredUser getIgnoredUser(@NotNull Player player) {
        return this.getIgnoredUser(player.getName());
    }

    @Nullable
    public IgnoredUser getIgnoredUser(@NotNull String name) {
        return this.getIgnoredUsers().get(name.toLowerCase());
    }

    public boolean isGodMode() {
        return this.getSettingBoolean(SETTING_GOD_MODE);
    }

    public void setGodMode(boolean godMode) {
        this.setSettingBoolean(SETTING_GOD_MODE, godMode);
    }

    public boolean isSocialSpy() {
        return this.getSettingBoolean(SETTING_SOCIAL_SPY);
    }

    public void setSocialSpy(boolean socialSpy) {
        this.setSettingBoolean(SETTING_SOCIAL_SPY, socialSpy);
    }

    public boolean isVanished() {
        return this.getSettingBoolean(SETTING_VANISHED);
    }

    public void setVanished(boolean vanished) {
        this.setSettingBoolean(SETTING_VANISHED, vanished);
    }

    public boolean isAntiPhantom() {
        return this.getSettingBoolean(SETTING_ANTI_PHANTOM);
    }

    public void setAntiPhantom(boolean antiPhantom) {
        this.setSettingBoolean(SETTING_ANTI_PHANTOM, antiPhantom);
    }

    @NotNull
    public Map<String, Boolean> getSettingsBoolean() {
        return settingsBool;
    }

    @NotNull
    public Map<String, Double> getSettingsNumber() {
        return settingsNum;
    }

    public void setSettingBoolean(@NotNull String setting, boolean value) {
        this.getSettingsBoolean().put(setting.toUpperCase(), value);
    }

    public boolean getSettingBoolean(@NotNull String setting) {
        return this.getSettingsBoolean().getOrDefault(setting.toUpperCase(), false);
    }

    public void setSettingNumber(@NotNull String setting, double value) {
        this.getSettingsNumber().put(setting.toUpperCase(), value);
    }

    public double getSettingNumber(@NotNull String setting) {
        return this.getSettingsNumber().getOrDefault(setting.toUpperCase(), 0D);
    }

    /**
     * Adds TeleportRequest to player's requests list.
     * This method does NOT calls PlayerTeleportRequestEvent event.
     *
     * @param request  - TeleportRequest object
     * @param override - To override or not existing request from the same sender.
     * @return true if request is added, false if it's not.
     */
    public boolean addTeleportRequest(@NotNull TeleportRequest request, boolean override) {
        TeleportRequest has = this.getTeleportRequest(request.getSender());

        if (has != null) {
            if (!override) return false;
            this.getTeleportRequests().remove(has);
        }
        return this.getTeleportRequests().add(request);
    }

    /**
     * Get TeleportRequest from specified player. Expired requests are not included.
     *
     * @param sender - Player name to get request for.
     * @return TeleportRequest from specified player or NULL if there is no request or it's expired.
     */
    @Nullable
    public TeleportRequest getTeleportRequest(@NotNull String sender) {
        return this.getTeleportRequests().stream()
            .filter(request -> request.getSender().equalsIgnoreCase(sender)).findFirst().orElse(null);
    }

    /**
     * Get latest added TeleportRequest. Expired requests are not included.
     *
     * @return Latest TeleportRequest or NULL if there are no requests.
     */
    @Nullable
    public TeleportRequest getTeleportRequest() {
        if (this.getTeleportRequests().isEmpty()) return null;
        return this.teleportRequests.get(this.teleportRequests.size() - 1);
    }

    /**
     * Get all active player's Teleport Requests. Expired requests will be removed.
     *
     * @return List of all player's Teleport Requests.
     */
    @NotNull
    public List<TeleportRequest> getTeleportRequests() {
        this.teleportRequests.removeIf(TeleportRequest::isExpired);
        return this.teleportRequests;
    }
}
