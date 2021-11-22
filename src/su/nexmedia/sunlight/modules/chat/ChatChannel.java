package su.nexmedia.sunlight.modules.chat;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.core.config.CoreConfig;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.StringUT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ChatChannel implements IPlaceholder {

    public static final String PLACEHOLDER_ID     = "%channel_id%";
    public static final String PLACEHOLDER_NAME   = "%channel_name%";
    public static final String PLACEHOLDER_RADIUS = "%channel_radius%";
    public static final Map<String, String>      PLAYER_CHANNEL_ACTIVE = new HashMap<>();
    public static final Map<String, Set<String>> PLAYER_CHANNELS       = new HashMap<>();
    private static final String FORMAT_PLAYER_PREFIX       = "{player_prefix}";
    private static final String FORMAT_PLAYER_SUFFIX       = "{player_suffix}";
    private static final String FORMAT_PLAYER_NAME         = "{player_name}";
    private static final String FORMAT_PLAYER_DISPLAY_NAME = "{player_display_name}";
    private static final String FORMAT_PLAYER_WORLD        = "{player_world}";
    private static final String FORMAT_MESSAGE             = "{message}";
    private final String  id;
    private final String  name;
    private final boolean isDefault;
    private final boolean isAutoJoin;
    private final boolean isPermissionRequiredHear;
    private final boolean isPermissionRequiredSpeak;
    private final int     distance;
    private final int     messageCooldown;
    private final String  commandAlias;
    private final String  messagePrefix;
    private final String  format;

    public ChatChannel(@NotNull String id, @NotNull String name, boolean isDefault, boolean isAutoJoin,
                       boolean isPermissionRequiredHear, boolean isPermissionRequiredSpeak,
                       int distance, int messageCooldown,
                       @NotNull String commandAlias, @NotNull String messagePrefix, @NotNull String format) {
        this.id = id.toLowerCase();
        this.name = StringUT.color(name);
        this.isDefault = isDefault;
        this.isAutoJoin = isAutoJoin;
        this.isPermissionRequiredHear = isPermissionRequiredHear;
        this.isPermissionRequiredSpeak = isPermissionRequiredSpeak;
        this.distance = distance;
        this.messageCooldown = messageCooldown;
        this.commandAlias = commandAlias;
        this.messagePrefix = messagePrefix;
        this.format = StringUT.color(format);
    }

    @NotNull
    public static String getChannelActiveId(@NotNull Player player) {
        return PLAYER_CHANNEL_ACTIVE.computeIfAbsent(player.getName(), k -> Constants.DEFAULT);
    }

    public static void setChannelActiveId(@NotNull Player player, @NotNull ChatChannel channel) {
        PLAYER_CHANNEL_ACTIVE.put(player.getName(), channel.getId());
    }

    @NotNull
    public static Set<String> getChannels(@NotNull Player player) {
        return PLAYER_CHANNELS.computeIfAbsent(player.getName(), k -> new HashSet<>());
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(PLACEHOLDER_ID, this.getId())
            .replace(PLACEHOLDER_NAME, this.getName())
            .replace(PLACEHOLDER_RADIUS, String.valueOf(this.getDistance()))
            ;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isAutoJoin() {
        return isAutoJoin;
    }

    public boolean isPermissionRequiredHear() {
        return isPermissionRequiredHear;
    }

    public boolean isPermissionRequiredSpeak() {
        return isPermissionRequiredSpeak;
    }

    public int getDistance() {
        return distance;
    }

    public int getMessageCooldown() {
        return messageCooldown;
    }

    @NotNull
    public String getMessagePrefix() {
        return messagePrefix;
    }

    @NotNull
    public String getCommandAlias() {
        return commandAlias;
    }

    @NotNull
    public String getFormat() {
        return this.replacePlaceholders().apply(this.format);
    }

    @NotNull
    public String getFormat(@NotNull Player player, @NotNull String msg) {
        String format = this.getFormat()
            .replace(FORMAT_PLAYER_PREFIX, Hooks.getPrefix(player))
            .replace(FORMAT_PLAYER_SUFFIX, Hooks.getSuffix(player))
            .replace(FORMAT_PLAYER_DISPLAY_NAME, player.getDisplayName())
            .replace(FORMAT_PLAYER_NAME, player.getName())
            .replace(FORMAT_PLAYER_WORLD, CoreConfig.getWorldName(player.getWorld().getName()))
            .replace(FORMAT_MESSAGE, msg);
        if (Hooks.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setBracketPlaceholders(player, format);
        }
        return format;
    }

    public boolean canSpeak(@NotNull Player player) {
        return this.isDefault() || !this.isPermissionRequiredSpeak()
            || player.hasPermission(ChatPerms.CHANNEL_SPEAK + this.getId());
    }

    public boolean canHear(@NotNull Player player) {
        return this.canSpeak(player) || !this.isPermissionRequiredHear()
            || player.hasPermission(ChatPerms.CHANNEL_HEAR + this.getId());
    }

    public boolean isInRadius(@NotNull Player speaker, @NotNull Player recipient) {
        if (this.getDistance() <= 0) return true;
        if (recipient.hasPermission(ChatPerms.BYPASS_CHANNEL_DISTANCE)) return true;

        if (!speaker.getWorld().equals(recipient.getWorld())) return false;
        return speaker.getLocation().distance(recipient.getLocation()) <= this.getDistance();
    }
}
