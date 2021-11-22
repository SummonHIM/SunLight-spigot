package su.nexmedia.sunlight.modules.tab;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.core.Version;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.manager.api.task.ITask;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.engine.utils.Reflex;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.utils.SimpleTextAnimator;
import su.nexmedia.sunlight.modules.tab.packets.TabPacketManager;

import java.util.*;
import java.util.Map.Entry;

public class TabManager extends SunModule {

    private static final String PLACEHOLDER_NAME         = "%player_name%";
    private static final String PLACEHOLDER_DISPLAY_NAME = "%player_display_name%";
    private Map<String, SimpleTextAnimator> animationMap;
    private long updateIntervalTab;
    private long updateIntervalTag;
    private Map<String, TabListFormat> listFormatMap;
    private Map<String, TabListName>   listNameMap;
    private Map<String, TabNametag>    nametagMap;
    private UpdateTabTask   updateTabTask;
    private UpdateGroupTask updateGroupTask;
    private TabPacketManager tabPacketManager;

    public TabManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.TAB;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void onLoad() {
        this.listFormatMap = new HashMap<>();
        this.listNameMap = new HashMap<>();
        this.nametagMap = new HashMap<>();

        this.setupAnimations();

        // Setup Tab Settings
        this.updateIntervalTab = cfg.getLong("Tablist.Update_Interval", 1L);
        this.updateIntervalTag = cfg.getLong("Nametags.Update_Interval", 300L);

        for (String formatId : cfg.getSection("Tablist.Format")) {
            String path2 = "Tablist.Format." + formatId + ".";

            int priority = cfg.getInt(path2 + "Priority");
            Set<String> worlds = cfg.getStringSet(path2 + "Worlds");
            Set<String> groups = cfg.getStringSet(path2 + "Groups");

            String header = String.join("\n", cfg.getStringList(path2 + "Header"));
            String footer = String.join("\n", cfg.getStringList(path2 + "Footer"));

            TabListFormat listFormat = new TabListFormat(formatId, priority, worlds, groups, header, footer);
            this.listFormatMap.put(listFormat.getId(), listFormat);
        }

        for (String rankId : cfg.getSection("Tablist.Player_Names")) {
            String path2 = "Tablist.Player_Names." + rankId + ".";

            int priority = cfg.getInt(path2 + "Priority");
            String format = cfg.getString(path2 + "Format", "%display_name%");

            this.listNameMap.put(rankId.toLowerCase(), new TabListName(priority, format));
        }

        for (String rankId : cfg.getSection("Nametags.Groups")) {
            String path2 = "Nametags.Groups." + rankId + ".";

            String teamIndex = String.valueOf(this.nametagMap.size());
            String teamId = teamIndex + "SL" + rankId;
            if (teamId.length() > 16) teamId = teamId.substring(0, 16);

            TabNametag nametag = new TabNametag(teamId);

            int teamPriority = cfg.getInt(path2 + "Priority");
            String teamPrefix = cfg.getString(path2 + "Prefix", "");
            String teamSuffix = cfg.getString(path2 + "Suffix", "");
            ChatColor teamColor = ChatColor.of(cfg.getString(path2 + "Color", "&7"));

            nametag.setPriority(teamPriority);
            nametag.setColor(teamColor);
            nametag.setPrefix(teamPrefix);
            nametag.setSuffix(teamSuffix);

            this.nametagMap.put(rankId.toLowerCase(), nametag);
        }

        if (!this.setupPacketManager()) {
            this.error("Could not setup Packet Manager! Some features will be disabled.");
        }
        else {
            this.updateGroupTask = new UpdateGroupTask(plugin);
            this.updateGroupTask.start();
        }

        this.updateTabTask = new UpdateTabTask(plugin);
        this.updateTabTask.start();
    }

    private boolean setupPacketManager() {
        String pack = TabPacketManager.class.getPackage().getName();
        Class<?> clazz = Reflex.getClass(pack, Version.CURRENT.name());
        if (clazz != null) {
            try {
                this.tabPacketManager = (TabPacketManager) clazz
                    .getConstructor(SunLight.class, TabManager.class)
                    .newInstance(this.plugin, this);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        return this.tabPacketManager != null;
    }

    private void setupAnimations() {
        JYML cfgAnim = JYML.loadOrExtract(plugin, this.getPath() + "animations.yml");
        this.animationMap = new HashMap<>();

        // Setup Animations
        for (String animId : cfgAnim.getSection("")) {
            int aInter = cfgAnim.getInt(animId + ".Update_Interval_MS");
            if (aInter <= 0) continue;

            List<String> lines = cfgAnim.getStringList(animId + ".Texts");
            SimpleTextAnimator animator = new SimpleTextAnimator(animId, lines, aInter);
            this.animationMap.put(animator.getId(), animator);
        }
    }

    @Override
    public void onShutdown() {
        if (this.updateTabTask != null) {
            this.updateTabTask.stop();
            this.updateTabTask = null;
        }
        if (this.updateGroupTask != null) {
            this.updateGroupTask.stop();
            this.updateGroupTask = null;
        }
        if (this.tabPacketManager != null) {
            this.tabPacketManager = null;
        }
        if (this.animationMap != null) {
            this.animationMap.clear();
            this.animationMap = null;
        }
        if (this.nametagMap != null) {
            this.nametagMap.clear();
        }
    }

    private void updateAll(@NotNull Player player) {
        this.constructListName(player);
        this.constructTab(player);
        this.constructList(player);
        this.tabPacketManager.constructTeam();
    }

    @Nullable
    public TabListFormat getPlayerListFormat(@NotNull Player player) {
        Set<String> groups = Hooks.getPermissionGroups(player);
        return this.listFormatMap.values().stream()
            .filter(format -> format.getWorlds().contains(player.getWorld().getName()) || format.getWorlds().contains(Constants.MASK_ANY))
            .filter(format -> groups.stream().anyMatch(pRank -> format.getGroups().contains(pRank)) || format.getGroups().contains(Constants.MASK_ANY))
            .max(Comparator.comparingInt(TabListFormat::getPriority))
            .orElse(null);
    }

    @Nullable
    public TabListName getPlayerListName(@NotNull Player player) {
        Set<String> groups = Hooks.getPermissionGroups(player);
        return this.listNameMap.entrySet().stream()
            .filter(entry -> groups.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(Constants.DEFAULT))
            .map(Entry::getValue).max(Comparator.comparingInt(TabListName::getPriority))
            .orElse(null);
    }

    @Nullable
    public TabNametag getPlayerNametag(@NotNull Player player) {
        Set<String> groups = Hooks.getPermissionGroups(player);
        return this.nametagMap.entrySet().stream()
            .filter(entry -> groups.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(Constants.DEFAULT))
            .map(Entry::getValue).max(Comparator.comparingInt(TabNametag::getPriority))
            .orElse(null);
    }

    private void constructTab(@NotNull Player player) {
        TabListFormat format = this.getPlayerListFormat(player);
        if (format == null) return;

        String header = format.getHeader();
        String footer = format.getFooter();

        for (SimpleTextAnimator animator : this.animationMap.values()) {
            header = animator.replace(header);
            footer = animator.replace(footer);
        }
        if (Hooks.hasPlaceholderAPI()) {
            header = PlaceholderAPI.setPlaceholders(player, header);
            footer = PlaceholderAPI.setPlaceholders(player, footer);
        }

        player.setPlayerListHeaderFooter(header, footer);
    }

    private void constructListName(@NotNull Player player) {
        TabListName listName = this.getPlayerListName(player);
        if (listName == null) return;

        String format = listName.getFormat()
            .replace(PLACEHOLDER_NAME, player.getName())
            .replace(PLACEHOLDER_DISPLAY_NAME, player.getDisplayName());

        if (Hooks.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        player.setPlayerListName(format);
    }

    private void constructList(@NotNull Player player) {
        List<Player> sorted = new ArrayList<>(this.plugin.getServer().getOnlinePlayers().stream().map(p -> (Player) p).sorted((p1, p2) -> {
            TabListName tag1 = this.getPlayerListName(p1);
            TabListName tag2 = this.getPlayerListName(p2);
            int pr1 = tag1 != null ? tag1.getPriority() : 0;
            int pr2 = tag2 != null ? tag2.getPriority() : 0;
            return pr2 - pr1;
        }).toList());

        this.tabPacketManager.constructList(player, sorted);
    }

    // E V E N T S
    // +--------------------------------------------------------------+

    @EventHandler(priority = EventPriority.HIGH)
    public void onTabTagJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        this.updateAll(player);
    }

    // C L A S S E S
    // +--------------------------------------------------------------+

    class UpdateTabTask extends ITask<SunLight> {

        UpdateTabTask(@NotNull SunLight plugin) {
            super(plugin, updateIntervalTab, true);
        }

        @Override
        public void action() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                constructTab(player);
            }
        }
    }

    class UpdateGroupTask extends ITask<SunLight> {

        UpdateGroupTask(@NotNull SunLight plugin) {
            super(plugin, updateIntervalTag, true);
        }

        @Override
        public void action() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                constructListName(player);
                //constructList(player);
            }
            tabPacketManager.constructTeam();
        }
    }
}
