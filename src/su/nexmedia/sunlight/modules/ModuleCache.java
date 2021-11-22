package su.nexmedia.sunlight.modules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.hooks.HookId;
import su.nexmedia.sunlight.modules.afk.AfkManager;
import su.nexmedia.sunlight.modules.bans.BanManager;
import su.nexmedia.sunlight.modules.chat.ChatManager;
import su.nexmedia.sunlight.modules.enhancements.EnhancementManager;
import su.nexmedia.sunlight.modules.fixer.FixerManager;
import su.nexmedia.sunlight.modules.menu.MenuManager;
import su.nexmedia.sunlight.modules.homes.HomeManager;
import su.nexmedia.sunlight.modules.kits.KitManager;
import su.nexmedia.sunlight.modules.rtp.RtpManager;
import su.nexmedia.sunlight.modules.scoreboard.ScoreboardManager;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;
import su.nexmedia.sunlight.modules.tab.TabManager;
import su.nexmedia.sunlight.modules.warps.WarpManager;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.sunlight.module.economy.SunLightEconomyPlugin;
import su.sunlight.module.economy.manager.EconomyManager;

public class ModuleCache {

    private final SunLight       plugin;
    private       ExternalLoader extLoader;

    private WorldManager       worldManager;
    private MenuManager        menuManager;
    private HomeManager        homeManager;
    private KitManager         kitManager;
    private SpawnManager       spawnManager;
    private WarpManager        warpManager;
    private ChatManager        chatManager;
    private AfkManager         afkManager;
    private EnhancementManager enhancementManager;
    private TabManager         tabManager;
    private ScoreboardManager  scoreboardManager;
    private FixerManager       fixerManager;
    private BanManager         banManager;
    private RtpManager         rtpManager;

    public ModuleCache(@NotNull SunLight plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        this.plugin.getModuleManager().register(this.worldManager = new WorldManager(plugin));
        if (Hooks.hasPlugin(HookId.MODULE_ECONOMY)) {
            try {
                this.extLoader = new ExternalLoader();
                this.extLoader.setup();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.plugin.getModuleManager().register(this.homeManager = new HomeManager(plugin));
        this.plugin.getModuleManager().register(this.kitManager = new KitManager(plugin));
        this.plugin.getModuleManager().register(this.warpManager = new WarpManager(plugin));
        this.plugin.getModuleManager().register(this.chatManager = new ChatManager(plugin));
        this.plugin.getModuleManager().register(this.menuManager = new MenuManager(plugin));
        this.plugin.getModuleManager().register(this.afkManager = new AfkManager(plugin));
        this.plugin.getModuleManager().register(this.enhancementManager = new EnhancementManager(plugin));
        this.plugin.getModuleManager().register(this.tabManager = new TabManager(plugin));
        this.plugin.getModuleManager().register(this.scoreboardManager = new ScoreboardManager(plugin));
        this.plugin.getModuleManager().register(this.fixerManager = new FixerManager(plugin));
        this.plugin.getModuleManager().register(this.banManager = new BanManager(plugin));
        this.plugin.getModuleManager().register(this.spawnManager = new SpawnManager(plugin));
        this.plugin.getModuleManager().register(this.rtpManager = new RtpManager(plugin));
    }

    public void shutdown() {
        this.banManager = null;
        this.chatManager = null;
        this.fixerManager = null;
        this.extLoader = null;
        this.menuManager = null;
        this.homeManager = null;
        this.kitManager = null;
        this.afkManager = null;
        this.enhancementManager = null;
        this.rtpManager = null;
        this.spawnManager = null;
        this.tabManager = null;
        this.warpManager = null;
        this.worldManager = null;
    }

    @Nullable
    public AfkManager getAfkManager() {
        return afkManager;
    }

    @Nullable
    public BanManager getBanManager() {
        return this.banManager;
    }

    @Nullable
    public ChatManager getChatManager() {
        return this.chatManager;
    }

    @Nullable
    public FixerManager getFixerManager() {
        return this.fixerManager;
    }

    @Nullable
    public MenuManager getMenuManager() {
        return this.menuManager;
    }

    @Nullable
    public HomeManager getHomeManager() {
        return this.homeManager;
    }

    @Nullable
    public EnhancementManager getEnhancementManager() {
        return this.enhancementManager;
    }

    @Nullable
    public KitManager getKitManager() {
        return this.kitManager;
    }

    @Nullable
    public RtpManager getRtpManager() {
        return rtpManager;
    }

    @Nullable
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Nullable
    public SpawnManager getSpawnManager() {
        return this.spawnManager;
    }

    @Nullable
    public TabManager getTabManager() {
        return tabManager;
    }

    @Nullable
    public WarpManager getWarpManager() {
        return this.warpManager;
    }

    @Nullable
    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    @Nullable
    public ExternalLoader getExternalLoader() {
        return this.extLoader;
    }

    class ExternalLoader {

        private EconomyManager eco;

        public void setup() {
            SunLightEconomyPlugin ecoMod = (SunLightEconomyPlugin) plugin.getPluginManager().getPlugin(HookId.MODULE_ECONOMY);
            if (ecoMod != null && plugin.cfg().isModuleEnabled(ecoMod.getEconomyManager())) {
                plugin.getModuleManager().register(this.eco = ecoMod.getEconomyManager());
            }
        }

        @Nullable
        public EconomyManager getEconomyManager() {
            return this.eco;
        }
    }
}
