package su.nexmedia.sunlight.modules.enhancements;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.enhancements.module.chairs.ChairsManager;
import su.nexmedia.sunlight.modules.enhancements.module.chestsort.ChestSortManager;
import su.nexmedia.sunlight.modules.enhancements.module.physics.PhysicsExplosionListener;
import su.nexmedia.sunlight.modules.enhancements.module.welcome.WelcomeManager;

import java.util.List;

public class EnhancementManager extends SunModule {

    private EnhancementLang lang;

    private ChairsManager    chairsManager;
    private ChestSortManager chestSortManager;
    private WelcomeManager   welcomeManager;

    private boolean      signsColors;
    private boolean      anvilsColors;
    private List<String> motdText;

    public EnhancementManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.ENHANCEMENTS;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new EnhancementLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        if (cfg.getBoolean("Modules.Welcome")) {
            this.welcomeManager = new WelcomeManager(this);
            this.welcomeManager.setup();
        }
        if (cfg.getBoolean("Modules.Chairs")) {
            this.chairsManager = new ChairsManager(this);
            this.chairsManager.setup();
        }
        if (cfg.getBoolean("Modules.Chest_Sort")) {
            this.chestSortManager = new ChestSortManager(this);
            this.chestSortManager.setup();
        }
        if (cfg.getBoolean("Modules.Physics.Explosions")) {
            this.addListener(new PhysicsExplosionListener(plugin));
        }
        if (cfg.getBoolean("Modules.Motd.Enabled")) {
            this.motdText = StringUT.color(cfg.getStringList("Modules.Motd.Texts"));
        }

        this.signsColors = cfg.getBoolean("Modules.Signs.Colors");
        this.anvilsColors = cfg.getBoolean("Modules.Anvils.Colors");
        this.addListener(new Listener(this.plugin));
    }

    @Override
    public void onShutdown() {
        if (this.chairsManager != null) {
            this.chairsManager.shutdown();
            this.chairsManager = null;
        }
        if (this.chestSortManager != null) {
            this.chestSortManager.shutdown();
            this.chestSortManager = null;
        }
    }

    @NotNull
    public EnhancementLang getLang() {
        return lang;
    }

    @Nullable
    public ChairsManager getChairsManager() {
        return chairsManager;
    }

    @Nullable
    public ChestSortManager getChestSortManager() {
        return chestSortManager;
    }

    @Nullable
    public WelcomeManager getWelcomeManager() {
        return welcomeManager;
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onSignsColor(SignChangeEvent e) {
            if (!signsColors) return;

            Player player = e.getPlayer();
            if (!player.hasPermission(EnhancementPerms.SIGNS_COLOR)) return;

            for (int index = 0; index < e.getLines().length; index++) {
                String line = e.getLine(index);
                if (line != null) {
                    e.setLine(index, StringUT.color(line));
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void onAnvilsColor(PrepareAnvilEvent e) {
            if (!anvilsColors) return;
            if (e.getViewers().isEmpty()) return;

            ItemStack result = e.getResult();
            if (result == null || ItemUT.isAir(result)) return;

            Player player = (Player) e.getViewers().get(0);
            if (!player.hasPermission(EnhancementPerms.ANVILS_COLOR)) return;

            ItemMeta meta = result.getItemMeta();
            if (meta == null) return;

            meta.setDisplayName(StringUT.color(meta.getDisplayName()));
            result.setItemMeta(meta);
            e.setResult(result);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerDeath(PlayerDeathEvent e) {
            Player player = e.getEntity();
            if (Hooks.isNPC(player)) {
                return;
            }

            if (player.hasPermission(SunPerms.CORE_SAVE_LEVEL)) {
                e.setKeepLevel(true);
                e.setDroppedExp(0);
            }
            if (player.hasPermission(SunPerms.CORE_SAVE_ITEMS)) {
                e.setKeepInventory(true);
                e.getDrops().clear();
            }
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onMotdServer(ServerListPingEvent e) {
            if (motdText == null || motdText.isEmpty()) return;

            String motd = Rnd.get(motdText);
            if (motd == null) return;

            e.setMotd(motd);
        }
    }
}
