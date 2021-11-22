package su.nexmedia.sunlight.modules.chat.module;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.chat.ChatManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDeathManager extends AbstractManager<SunLight> {

    private final ChatManager chatManager;

    private double                    range;
    private Map<String, List<String>> deathMsgCause;
    private Map<String, List<String>> deathMsgEntity;

    public ChatDeathManager(@NotNull ChatManager chatManager) {
        super(chatManager.plugin());
        this.chatManager = chatManager;
    }

    @Override
    public void onLoad() {
        this.deathMsgCause = new HashMap<>();
        this.deathMsgEntity = new HashMap<>();

        JYML cfg = JYML.loadOrExtract(plugin, chatManager.getPath() + "death.messages.yml");

        this.range = cfg.getDouble("Range", -1D);

        for (String cause : cfg.getSection("By_Damage_Cause")) {
            String path2 = "By_Damage_Cause." + cause;
            List<String> vars = StringUT.color(cfg.getStringList(path2));
            this.deathMsgCause.put(cause.toUpperCase(), vars);
        }

        for (String cause : cfg.getSection("By_Entity")) {
            String path2 = "By_Entity." + cause;
            List<String> vars = StringUT.color(cfg.getStringList(path2));
            this.deathMsgEntity.put(cause.toUpperCase(), vars);
        }

        this.addListener(new Listener(this.plugin));
    }

    @Override
    public void onShutdown() {
        if (this.deathMsgCause != null) {
            this.deathMsgCause.clear();
            this.deathMsgCause = null;
        }
        if (this.deathMsgEntity != null) {
            this.deathMsgEntity.clear();
            this.deathMsgEntity = null;
        }
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onChatDeathMessage(PlayerDeathEvent e) {
            Player player = e.getEntity();
            EntityDamageEvent lastEvent = player.getLastDamageCause();
            if (lastEvent == null) {
                e.setDeathMessage("");
                return;
            }

            DamageCause cause = lastEvent.getCause();
            String deathMsg;

            if (lastEvent instanceof EntityDamageByEntityEvent ede) {
                Entity damager = ede.getDamager();
                String damagerType = ede.getDamager().getType().name();
                String damagerName = damager instanceof Player player1 ? player1.getDisplayName() : damager.getName();

                List<String> list = deathMsgEntity.getOrDefault(damagerType, deathMsgEntity.getOrDefault(Constants.DEFAULT.toUpperCase(), Collections.emptyList()));
                deathMsg = Rnd.get(list);
                if (deathMsg != null) {
                    deathMsg = deathMsg.replace("%damager%", damagerName);
                }
            }
            else {
                String causeRaw = cause.name();
                List<String> list = deathMsgCause.getOrDefault(causeRaw, deathMsgCause.getOrDefault(Constants.DEFAULT.toUpperCase(), Collections.emptyList()));
                deathMsg = Rnd.get(list);
            }
            deathMsg = deathMsg == null ? "" : deathMsg.replace("%player%", player.getDisplayName());

            if (Hooks.hasPlaceholderAPI()) {
                deathMsg = PlaceholderAPI.setPlaceholders(player, deathMsg);
            }

            if (range <= 0) {
                e.setDeathMessage(deathMsg);
            }
            else {
                e.setDeathMessage(null);

                final String deathMsg2 = deathMsg;
                player.getNearbyEntities(range, range, range).forEach(entity -> {
                    MsgUT.sendWithJSON(entity, deathMsg2);
                });
            }
        }
    }
}
