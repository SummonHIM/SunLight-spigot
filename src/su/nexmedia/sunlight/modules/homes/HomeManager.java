package su.nexmedia.sunlight.modules.homes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.external.WorldGuardHK;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.data.SunUser;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.homes.command.DeleteHomeCommand;
import su.nexmedia.sunlight.modules.homes.command.HomeCommand;
import su.nexmedia.sunlight.modules.homes.command.HomesCommand;
import su.nexmedia.sunlight.modules.homes.command.SetHomeCommand;
import su.nexmedia.sunlight.modules.homes.editor.EditorHandlerHome;
import su.nexmedia.sunlight.modules.homes.listener.HomeListener;
import su.nexmedia.sunlight.modules.homes.menu.HomesMenu;

import java.util.*;

public class HomeManager extends SunModule {

    public static final String YML_HOMES_MENU = "homes.menu.yml";
    public static final String YML_HOME_MENU  = "home.menu.yml";
    private Set<String>          sethomeWorldBlacklist;
    private Set<String>          sethomeWorldGuardRegionBlacklist;
    private Map<String, Integer> sethomeAmountPerGroup;
    private HomeLang  lang;
    private HomesMenu homesMenu;

    public HomeManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.HOMES;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.40";
    }

    @Override
    public void onLoad() {
        this.lang = new HomeLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        String path = "Sethome.";
        this.sethomeWorldBlacklist = cfg.getStringSet(path + "World_Blacklist");
        this.sethomeWorldGuardRegionBlacklist = cfg.getStringSet(path + "WorldGuard_Region_Blacklist");
        this.sethomeAmountPerGroup = new HashMap<>();
        for (String rank : cfg.getSection(path + "Homes_Amount_Per_Group")) {
            this.sethomeAmountPerGroup.put(rank.toLowerCase(), cfg.getInt(path + "Homes_Amount_Per_Group." + rank));
        }

        this.plugin.getSunEditorHandler().addInputHandler(Home.class, new EditorHandlerHome(this));
        this.plugin.getCommandRegulator().register(new HomesCommand(this));
        this.plugin.getCommandRegulator().register(new HomeCommand(this));
        this.plugin.getCommandRegulator().register(new SetHomeCommand(this));
        this.plugin.getCommandRegulator().register(new DeleteHomeCommand(this));

        this.addListener(new HomeListener(this));
    }

    @Override
    public void onShutdown() {
        if (this.homesMenu != null) {
            this.homesMenu.clear();
            this.homesMenu = null;
        }
        for (SunUser user : plugin.getUserManager().getActiveUsers()) {
            user.clearEditorHomes(); // Disable editors
        }
        this.plugin.getSunEditorHandler().removeInputHandler(Home.class);

        this.sethomeWorldBlacklist.clear();
        this.sethomeWorldBlacklist = null;

        this.sethomeAmountPerGroup.clear();
        this.sethomeAmountPerGroup = null;
    }

    @NotNull
    public HomeLang getLang() {
        return lang;
    }

    @NotNull
    public HomesMenu getHomesMenu() {
        if (this.homesMenu == null) {
            this.homesMenu = new HomesMenu(this);
        }
        return homesMenu;
    }

    public boolean canSetHome(@NotNull Player player, @NotNull Location location, boolean notify) {
        if (!player.hasPermission(HomePerms.HOMES_BYPASS_SET_WG_REGIONS)) {
            String world = player.getWorld().getName();
            if (this.sethomeWorldBlacklist.contains(world)) {
                if (notify) this.getLang().Command_SetHome_Error_World.send(player);
                return false;
            }
        }

        WorldGuardHK worldGuard = plugin.getWorldGuard();
        if (worldGuard != null && !player.hasPermission(HomePerms.HOMES_BYPASS_SET_WG_REGIONS)) {
            String region = worldGuard.getRegion(location);
            if (this.sethomeWorldGuardRegionBlacklist.contains(region)) {
                if (notify) this.getLang().Command_SetHome_Error_Region.send(player);
                return false;
            }
        }
        return true;
    }

    public boolean setHome(@NotNull Player player, @NotNull String id, boolean force) {
        if (!RegexUT.matchesEnRu(id)) {
            plugin.lang().Error_InvalidName.replace("%name%", id).send(player);
            return false;
        }

        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        Home homeHas = user.getHomeById(id);
        Location location = player.getLocation();

        if (!force) {
            int homesMax = this.getPlayerHomesAllowed(player);
            if (homesMax >= 0) {
                if (homeHas == null && this.getPlayerHomesAmount(player) >= homesMax) {
                    this.getLang().Command_SetHome_Error_Limit.send(player);
                    return false;
                }
            }

            if (!this.canSetHome(player, location, true)) {
                return false;
            }
        }

        if (homeHas != null) {
            homeHas.setLocation(location);
        }
        else {
            homeHas = new Home(player.getName(), id, location);
            user.getHomes().put(homeHas.getId(), homeHas);
        }

        this.getLang().Command_SetHome_Done.replace(homeHas.replacePlaceholders()).send(player);
        return true;
    }

    public boolean deleteHome(@NotNull Player player, @NotNull String id) {
        return this.deleteHome(player.getName(), id);
    }

    public boolean deleteHome(@NotNull String player, @NotNull String id) {
        SunUser user = plugin.getUserManager().getOrLoadUser(player, false);
        if (user == null) return false;

        return user.getHomes().remove(id) != null;
    }

    public boolean hasHome(@NotNull String player, @NotNull String id) {
        return this.getPlayerHome(player, id) != null;
    }

    public int getPlayerHomesAllowed(@NotNull Player player) {
        return Hooks.getGroupValueInt(player, this.sethomeAmountPerGroup, true);
    }

    public int getPlayerHomesAmount(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        return user.getHomes().size();
    }

    @Nullable
    public Home getPlayerHome(@NotNull Player player, @NotNull String id) {
        return this.getPlayerHome(player.getName(), id);
    }

    @Nullable
    public Home getPlayerHome(@NotNull String player, @NotNull String id) {
        SunUser user = plugin.getUserManager().getOrLoadUser(player, false);
        if (user == null) return null;

        return user.getHomeById(id);
    }

    @Nullable
    public Home getPlayerHomeRespawn(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        return user.getHomes().values().stream().filter(Home::isRespawnPoint).findFirst().orElse(null);
    }

    @NotNull
    public List<String> getPlayerHomeNames(@NotNull String player) {
        SunUser user = plugin.getUserManager().getOrLoadUser(player, false);
        if (user == null) return new ArrayList<>();

        return new ArrayList<>(user.getHomes().keySet());
    }
}
