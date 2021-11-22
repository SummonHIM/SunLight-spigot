package su.nexmedia.sunlight.modules.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.commands.CommandRegister;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.ClickText;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.TimeUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.chat.commands.ShortChannelCommand;
import su.nexmedia.sunlight.modules.chat.commands.channel.ChatChannelCommand;
import su.nexmedia.sunlight.modules.chat.config.ChatConfig;
import su.nexmedia.sunlight.modules.chat.config.ChatLang;
import su.nexmedia.sunlight.modules.chat.listener.ChatListener;
import su.nexmedia.sunlight.modules.chat.module.ChatAnnounceManager;
import su.nexmedia.sunlight.modules.chat.module.ChatDeathManager;
import su.nexmedia.sunlight.modules.chat.module.ChatJoinManager;
import su.nexmedia.sunlight.modules.chat.rule.ChatRuleManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatManager extends SunModule {

    private ChatLang                 lang;
    private ChatChannel              channelDefault;
    private Map<String, ChatChannel> channels;

    private ChatDeathManager    deathManager;
    private ChatJoinManager     joinManager;
    private ChatRuleManager     ruleManager;
    private ChatAnnounceManager announceManager;

    public ChatManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.CHAT;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new ChatLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        ChatConfig.load(this);

        this.channels = new HashMap<>();
        JYML channelConfig = JYML.loadOrExtract(plugin, this.getPath() + "channels.yml");
        for (String sId : channelConfig.getSection("")) {
            String path2 = sId + ".";

            String name = channelConfig.getString(path2 + "Name", sId);
            boolean isDefault = channelConfig.getBoolean(path2 + "Default");
            boolean isAutoJoin = channelConfig.getBoolean(path2 + "Auto_Join");
            boolean isPermissionRequiredHear = channelConfig.getBoolean(path2 + "Permission_Required.Hear");
            boolean isPermissionRequiredSpeak = channelConfig.getBoolean(path2 + "Permission_Required.Speak");
            int radius = channelConfig.getInt(path2 + "Radius");
            int messageCooldown = channelConfig.getInt(path2 + "Message_Cooldown");
            String commandAlias = channelConfig.getString(path2 + "Command_Alias", "");
            String messagePrefix = channelConfig.getString(path2 + "Message_Prefix", "");
            String format = channelConfig.getString(path2 + "Format", "");
            if (format.isEmpty()) {
                this.error("Empty format for '" + sId + "' channel! Skipping...");
                continue;
            }

            ChatChannel channel = new ChatChannel(sId, name, isDefault, isAutoJoin,
                isPermissionRequiredHear, isPermissionRequiredSpeak,
                radius, messageCooldown,
                commandAlias, messagePrefix, format);

            if (channel.isDefault()) this.channelDefault = channel;
            this.channels.put(channel.getId(), channel);
        }

        if (this.channelDefault == null) {
            this.interruptLoad("No default chat channel defined! Chat can not function without the default channel.");
            return;
        }

        if (this.cfg.getBoolean("Modules.Join_Quit_Messages")) {
            this.joinManager = new ChatJoinManager(this);
            this.joinManager.setup();
        }
        if (this.cfg.getBoolean("Modules.Death_Messages")) {
            this.deathManager = new ChatDeathManager(this);
            this.deathManager.setup();
        }
        if (this.cfg.getBoolean("Modules.Announcer")) {
            this.announceManager = new ChatAnnounceManager(this);
            this.announceManager.setup();
        }

        this.ruleManager = new ChatRuleManager(this);
        this.ruleManager.setup();

        this.plugin.getCommandRegulator().register(new ChatChannelCommand(this));
        this.getChannels().forEach(channel -> {
            this.plugin.getCommandRegulator().register(new ShortChannelCommand(this, channel));
        });

        this.addListener(new ChatListener(this));
    }

    @Override
    public void onShutdown() {
        if (this.announceManager != null) {
            this.announceManager.shutdown();
            this.announceManager = null;
        }
        if (this.deathManager != null) {
            this.deathManager.shutdown();
            this.deathManager = null;
        }
        if (this.joinManager != null) {
            this.joinManager.shutdown();
            this.joinManager = null;
        }
        if (this.ruleManager != null) {
            this.ruleManager.shutdown();
            this.ruleManager = null;
        }
        if (this.channels != null) {
            this.channels.clear();
            this.channels = null;
        }
    }

    @NotNull
    public ChatLang getLang() {
        return lang;
    }

    @NotNull
    public Collection<ChatChannel> getChannels() {
        return this.channels.values();
    }

    @Nullable
    public ChatChannel getChannel(@NotNull String id) {
        return this.channels.get(id.toLowerCase());
    }

    @NotNull
    public ChatChannel getChannelDefault() {
        return this.channelDefault;
    }

    @NotNull
    public Set<ChatChannel> getChannelsAvailable(@NotNull Player player) {
        return this.getChannels().stream().filter(channel -> channel.canHear(player)).collect(Collectors.toSet());
    }

    @Nullable
    public ChatChannel getChannelByPrefix(@NotNull String message) {
        return this.getChannels().stream()
            .filter(channel -> !channel.getMessagePrefix().isEmpty() && message.startsWith(channel.getMessagePrefix()))
            .findFirst().orElse(null);
    }

    @Nullable
    public ChatChannel getChannelByCommand(@NotNull String command) {
        return this.getChannels().stream().filter(channel -> !channel.getCommandAlias().equalsIgnoreCase(command))
            .findFirst().orElse(null);
    }

    @NotNull
    public Set<Player> getChannelPlayers(@NotNull ChatChannel channel) {
        return plugin.getServer().getOnlinePlayers().stream()
            .filter(player -> channel.canSpeak(player) || channel.canHear(player))
            .filter(player -> ChatChannel.getChannels(player).contains(channel.getId()))
            .collect(Collectors.toSet());
    }

    @NotNull
    public Collection<Player> getChannelRecipients(@NotNull Player speaker, @NotNull ChatChannel channel) {
        return this.getChannelPlayers(channel).stream()
            .filter(recipient -> channel.isInRadius(speaker, recipient)).collect(Collectors.toSet());
    }

    public boolean setChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (!channel.canSpeak(player)) {
            this.getLang().Channel_Join_Error_NoPermission.replace(channel.replacePlaceholders()).send(player);
            return false;
        }
        if (!this.isInChannel(player, channel)) {
            this.joinChannel(player, channel);
        }
        ChatChannel.setChannelActiveId(player, channel);
        this.getLang().Channgel_Set_Success.replace(channel.replacePlaceholders()).send(player);
        return true;
    }

    public boolean isInChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        return ChatChannel.getChannels(player).contains(channel.getId());
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (!channel.canHear(player) && !channel.canSpeak(player)) {
            this.getLang().Channel_Join_Error_NoPermission.replace(channel.replacePlaceholders()).send(player);
            return false;
        }

        if (ChatChannel.getChannels(player).add(channel.getId())) {
            this.getLang().Channel_Join_Success.replace(channel.replacePlaceholders()).send(player);
            return true;
        }

        this.getLang().Channel_Join_Error_AlreadyIn.replace(channel.replacePlaceholders()).send(player);
        return false;
    }

    public boolean leaveChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (ChatChannel.getChannels(player).remove(channel.getId())) {
            this.getLang().Channel_Leave_Success.replace(channel.replacePlaceholders()).send(player);
            return true;
        }

        this.getLang().Channel_Leave_Error_NotIn.replace(channel.replacePlaceholders()).send(player);
        return false;
    }

    public void handleChatEvent(@NotNull AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getOrLoadUser(player);

        // Apply message colors for permission.
        String msgRaw = StringUT.colorOff(e.getMessage());
        String msgReal = player.hasPermission(ChatPerms.COLOR) ? StringUT.color(e.getMessage()) : msgRaw;

        // Check message caps percentage.
        if (!player.hasPermission(ChatPerms.BYPASS_ANTICAPS)) {
            msgReal = ChatUtils.doAntiCaps(msgReal);
        }

        // Check for the spam of the similar messages.
        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM) && !ChatUtils.checkSpamSimilarMessage(player, msgRaw)) {
            this.getLang().Chat_AntiSpam_Similar_Msg.send(player);
            e.setCancelled(true);
            return;
        }

        ChatChannel channelPrefix = this.getChannelByPrefix(msgRaw);
        ChatChannel channelActive = this.getChannel(ChatChannel.getChannelActiveId(player));
        if (channelPrefix != null && channelPrefix.canSpeak(player)) {
            channelActive = channelPrefix;
        }
        if (channelActive == null || !channelActive.canSpeak(player)) {
            channelActive = this.getChannelDefault();
        }

        // Check for the channel message cooldown.
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_MESSAGE) && !ChatUtils.isNextMessageAvailable(player, channelActive)) {
            String time = TimeUT.formatTimeLeft(ChatUtils.getNextMessageTime(player, channelActive));
            this.getLang().Chat_AntiSpam_Delay_Msg.replace("%time%", time).send(player);
            e.setCancelled(true);
            return;
        }

        // Join the channel, where player is sending message, if it wasn't there.
        if (!this.isInChannel(player, channelActive)) {
            this.joinChannel(player, channelActive);
        }

        // Extract message without the channel prefix.
        if (msgReal.startsWith(channelActive.getMessagePrefix())) {
            msgReal = msgReal.substring(channelActive.getMessagePrefix().length()).trim();
        }
        // Do no send empty messages.
        if (msgReal.isEmpty()) {
            e.setCancelled(true);
            return;
        }
        // Check for custom regex rules.
        if (!player.hasPermission(ChatPerms.BYPASS_RULES)) {
            msgReal = this.ruleManager.checkRules(player, msgReal, msgRaw, e);
        }

        // Retain recipients for the channel.
        e.getRecipients().retainAll(this.getChannelRecipients(player, channelActive));

        boolean isItemLink = ChatConfig.ITEM_SHOW_ENABLED && msgReal.contains(ChatConfig.ITEM_SHOW_PLACEHOLDER);
        String format = channelActive.getFormat(player, isItemLink ? msgReal : "%2$s");

        e.setMessage(msgReal);
        e.setFormat(format);

        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM)) {
            ChatUtils.setLastMessage(player, msgReal);
        }
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_MESSAGE)) {
            ChatUtils.setNextMessageTime(player, channelActive);
        }

        if (isItemLink) {
            ItemStack item = player.getInventory().getItemInMainHand();
            String replacer = ChatConfig.ITEM_SHOW_FORMAT.replace("%item%", ItemUT.getItemName(item));

            ClickText clickText = new ClickText(format);
            clickText.createPlaceholder(ChatConfig.ITEM_SHOW_PLACEHOLDER, replacer).showItem(item);
            clickText.send(e.getRecipients());

            e.setCancelled(true);
        }
    }

    public void handleCommandEvent(@NotNull PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        // Check for command cooldown.
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND) && !ChatUtils.isNextCommandAvailable(player)) {
            String time = TimeUT.formatTimeLeft(ChatUtils.getNextCommandTime(player));
            this.getLang().Chat_AntiSpam_Delay_Cmd.replace("%time%", time).send(player);
            e.setCancelled(true);
            return;
        }

        String msgRaw = StringUT.colorOff(e.getMessage());
        String msgCmd = StringUT.extractCommandName(msgRaw);
        String msgReal = player.hasPermission(ChatPerms.COLOR) ? StringUT.color(e.getMessage()) : msgRaw;

        // Check command similarity.
        boolean doCheckSimilar = CommandRegister.getAliases(msgCmd, true).stream().noneMatch(cmd -> {
            return ChatConfig.ANTI_SPAM_COMMAND_WHITELIST.contains(cmd);
        });
        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM) && doCheckSimilar &&
            !ChatUtils.checkSpamSimilarCommand(player, msgRaw)) {
            this.getLang().Chat_AntiSpam_Similar_Cmd.send(player);
            e.setCancelled(true);
            return;
        }

        // Check for caps in command.
        boolean doCheckCaps = CommandRegister.getAliases(msgCmd, true).stream().anyMatch(cmd -> {
            return ChatConfig.ANTI_CAPS_AFFECTED_COMMANDS.contains(cmd);
        });
        if (!player.hasPermission(ChatPerms.BYPASS_ANTICAPS) && doCheckCaps) {
            msgReal = ChatUtils.doAntiCaps(msgReal);
        }

        // Check for chat regex rules.
        if (!player.hasPermission(ChatPerms.BYPASS_RULES)) {
            String msgRawNoCmd = msgRaw.replace(msgCmd, "");
            String msgRealNoCmd = msgReal.replace(msgCmd, "");

            String msgRealNoCmdRuled = this.ruleManager.checkRules(player, msgRealNoCmd, msgRawNoCmd, e);
            msgReal = msgReal.replace(msgRealNoCmd, msgRealNoCmdRuled);
        }

        e.setMessage(msgReal);

        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM)) {
            ChatUtils.setLastCommand(player, msgRaw);
        }
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND)) {
            ChatUtils.setNextCommandTime(player);
        }
    }
}
