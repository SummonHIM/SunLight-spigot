package su.nexmedia.sunlight.modules.chat.module;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.chat.ChatManager;

import java.util.HashMap;
import java.util.Map;

public class ChatJoinManager extends AbstractManager<SunLight> {

    private final ChatManager chatManager;

    private Map<String, String> joinInGroups;
    private Map<String, String> joinOutGroups;

    public ChatJoinManager(@NotNull ChatManager chatManager) {
        super(chatManager.plugin());
        this.chatManager = chatManager;
    }

    @Override
    public void onLoad() {
        JYML cfg = JYML.loadOrExtract(plugin, chatManager.getPath() + "join.quit.messages.yml");

        this.joinInGroups = new HashMap<>();
        this.joinOutGroups = new HashMap<>();

        for (String group : cfg.getSection("Join_Groups")) {
            String msg = StringUT.color(cfg.getString("Join_Groups." + group, ""));
            this.joinInGroups.put(group.toLowerCase(), msg);
        }

        for (String group : cfg.getSection("Quit_Groups")) {
            String msg = StringUT.color(cfg.getString("Quit_Groups." + group, ""));
            this.joinOutGroups.put(group.toLowerCase(), msg);
        }

        this.addListener(new Listener(this.plugin));
    }

    @Override
    public void onShutdown() {
        if (this.joinInGroups != null) {
            this.joinInGroups.clear();
            this.joinInGroups = null;
        }
        if (this.joinOutGroups != null) {
            this.joinOutGroups.clear();
            this.joinOutGroups = null;
        }
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onChatJoinMessage(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            String group = Hooks.getPermGroup(player);
            String msg = joinInGroups.getOrDefault(group, "");

            if (Hooks.hasPlaceholderAPI()) {
                msg = PlaceholderAPI.setPlaceholders(player, msg);
            }
            e.setJoinMessage(msg.replace("%player%", player.getDisplayName()));
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onChatQuitMessage(PlayerQuitEvent e) {
            Player player = e.getPlayer();
            String group = Hooks.getPermGroup(player);
            String msg = joinOutGroups.getOrDefault(group, "");

            if (Hooks.hasPlaceholderAPI()) {
                msg = PlaceholderAPI.setPlaceholders(player, msg);
            }
            e.setQuitMessage(msg.replace("%player%", player.getDisplayName()));
        }
    }
}
