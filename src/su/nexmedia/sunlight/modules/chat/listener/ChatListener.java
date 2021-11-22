package su.nexmedia.sunlight.modules.chat.listener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.chat.ChatChannel;
import su.nexmedia.sunlight.modules.chat.config.ChatConfig;
import su.nexmedia.sunlight.modules.chat.ChatManager;
import su.nexmedia.sunlight.modules.chat.ChatUtils;

public class ChatListener extends AbstractListener<SunLight> {

    private final ChatManager chatManager;

    public ChatListener(@NotNull ChatManager chatManager) {
        super(chatManager.plugin());
        this.chatManager = chatManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        this.chatManager.getChannelsAvailable(player).stream().filter(ChatChannel::isAutoJoin).forEach(channel -> {
            this.chatManager.joinChannel(player, channel);
        });
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ChatUtils.clear(player);
        ChatChannel.PLAYER_CHANNEL_ACTIVE.remove(player.getName());
        ChatChannel.PLAYER_CHANNELS.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChatProcessMessageLowest(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY != EventPriority.LOWEST) return;
        this.chatManager.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChatProcessMessageLow(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY != EventPriority.LOW) return;
        this.chatManager.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChatProcessMessageNormal(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY != EventPriority.NORMAL) return;
        this.chatManager.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChatProcessMessageHigh(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY != EventPriority.HIGH) return;
        this.chatManager.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChatProcessMessageHighest(AsyncPlayerChatEvent e) {
        if (ChatConfig.CHAT_EVENT_PRIORITY != EventPriority.HIGHEST) return;
        this.chatManager.handleChatEvent(e);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChatProcessCommand(PlayerCommandPreprocessEvent e) {
        this.chatManager.handleCommandEvent(e);
    }
}
