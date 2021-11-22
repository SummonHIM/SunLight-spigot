package su.nexmedia.sunlight.modules.enhancements.module.welcome;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.actions.ActionManipulator;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.enhancements.EnhancementManager;

public class WelcomeManager extends AbstractManager<SunLight> {

    private final EnhancementManager enhancementManager;

    private String            newbieBroadcast;
    private ActionManipulator newbieActions;
    private ActionManipulator oldActions;

    public WelcomeManager(@NotNull EnhancementManager enhancementManager) {
        super(enhancementManager.plugin());
        this.enhancementManager = enhancementManager;
    }

    @Override
    public void onLoad() {
        JYML cfg = JYML.loadOrExtract(plugin, enhancementManager.getPath() + "welcome.yml");

        String path = "Newbies.";
        this.newbieBroadcast = StringUT.color(cfg.getString(path + "Broadcast", ""));
        this.newbieActions = new ActionManipulator(plugin, cfg, path + "Join_Actions");

        path = "Users.";
        this.oldActions = new ActionManipulator(plugin, cfg, path + "Join_Actions");

        this.addListener(new Listener(this.plugin));
    }

    @Override
    public void onShutdown() {
        this.newbieActions = null;
        this.oldActions = null;
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.LOW)
        public void onWelcomeJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            boolean isOldUser = plugin.getData().isUserExists(player.getUniqueId().toString(), true);
            ActionManipulator actions = isOldUser ? oldActions : newbieActions;

            if (!isOldUser && !newbieBroadcast.isEmpty()) {
                String broadcast = newbieBroadcast.replace("%player%", player.getName());
                if (Hooks.hasPlaceholderAPI()) broadcast = PlaceholderAPI.setPlaceholders(player, broadcast);
                plugin.getServer().broadcastMessage(broadcast);
            }

            actions.process(player);
        }
    }
}
