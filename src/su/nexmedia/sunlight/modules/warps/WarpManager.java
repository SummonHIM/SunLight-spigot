package su.nexmedia.sunlight.modules.warps;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.regex.RegexUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.warps.command.DeleteWarpCommand;
import su.nexmedia.sunlight.modules.warps.command.SetWarpCommand;
import su.nexmedia.sunlight.modules.warps.command.WarpCommand;
import su.nexmedia.sunlight.modules.warps.command.WarpEditorCommand;
import su.nexmedia.sunlight.modules.warps.config.WarpLang;
import su.nexmedia.sunlight.modules.warps.config.WarpConfig;
import su.nexmedia.sunlight.modules.warps.editor.WarpEditorInputHandler;
import su.nexmedia.sunlight.modules.warps.editor.WarpEditorMenuList;
import su.nexmedia.sunlight.modules.warps.menu.WarpMenuList;
import su.nexmedia.sunlight.modules.warps.menu.WarpMenuMain;
import su.nexmedia.sunlight.modules.warps.type.WarpSortType;
import su.nexmedia.sunlight.modules.warps.type.WarpType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarpManager extends SunModule {

    public static final String YML_WARPS_MAIN_MENU   = "warps.main.menu.yml";
    public static final String YML_WARPS_PLAYER_MENU = "warps.player.menu.yml";
    public static final String YML_WARPS_SERVER_MENU = "warps.server.menu.yml";
    private WarpLang lang;
    private WarpMenuMain      warpMenuMain;
    private WarpMenuList      warpMenuPlayer;
    private WarpMenuList      warpMenuServer;
    private Map<String, Warp> warps;
    private WarpEditorMenuList editor;

    public WarpManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.WARPS;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new WarpLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();
        WarpConfig.load(this);
        this.loadWarps();
        this.loadMenus();

        this.plugin.getSunEditorHandler().addInputHandler(Warp.class, new WarpEditorInputHandler(this));
        this.plugin.getCommandRegulator().register(new WarpCommand(this));
        this.plugin.getCommandRegulator().register(new WarpEditorCommand(this));
        this.plugin.getCommandRegulator().register(new SetWarpCommand(this));
        this.plugin.getCommandRegulator().register(new DeleteWarpCommand(this));
    }

    private void loadMenus() {
        JYML configMain = JYML.loadOrExtract(plugin, this.getPath() + YML_WARPS_MAIN_MENU);
        this.warpMenuMain = new WarpMenuMain(this, configMain);

        JYML configPlayer = JYML.loadOrExtract(plugin, this.getPath() + YML_WARPS_PLAYER_MENU);
        this.warpMenuPlayer = new WarpMenuList(this, configPlayer, WarpType.PLAYER);

        JYML configServer = JYML.loadOrExtract(plugin, this.getPath() + YML_WARPS_SERVER_MENU);
        this.warpMenuServer = new WarpMenuList(this, configServer, WarpType.SERVER);
    }

    private void loadWarps() {
        this.warps = new HashMap<>();

        for (JYML cfg : JYML.loadAll(this.getFullPath() + "warps", true)) {
            try {
                Warp warp = new Warp(this, cfg);
                this.warps.put(warp.getId(), warp);
            } catch (Exception ex) {
                plugin.error("Unable to load warp: " + cfg.getFile().getName());
                ex.printStackTrace();
            }
        }
        this.updateWarpRatings();
        this.info("Loaded " + this.warps.size() + " warps.");
    }

    @Override
    public void onShutdown() {
        if (this.warps != null) {
            this.warps.values().forEach(warp -> {
                warp.save();
                warp.clear();
            });
            this.warps.clear();
            this.warps = null;
        }
        if (this.warpMenuMain != null) {
            this.warpMenuMain.clear();
            this.warpMenuMain = null;
        }
        if (this.warpMenuPlayer != null) {
            this.warpMenuPlayer.clear();
            this.warpMenuPlayer = null;
        }
        if (this.warpMenuServer != null) {
            this.warpMenuServer.clear();
            this.warpMenuServer = null;
        }
        this.plugin.getSunEditorHandler().removeInputHandler(Warp.class);
    }

    @NotNull
    public WarpEditorMenuList getEditor() {
        if (this.editor == null) {
            this.editor = new WarpEditorMenuList(this);
        }
        return editor;
    }

    @NotNull
    public WarpLang getLang() {
        return lang;
    }

    public void delete(@NotNull Warp warp) {
        if (warp.getFile().delete()) {
            warp.clear();
            this.warps.remove(warp.getId());
        }
    }

    public boolean create(@NotNull Player player, @NotNull String id, boolean isForced) {
        if (!RegexUT.matchesEnRu(id)) {
            plugin.lang().Error_InvalidName.replace("%name%", id).send(player);
            return false;
        }

        if (!isForced) {
            int maxAllowed = this.getWarpsAmountPossible(player);
            if (maxAllowed >= 0 && this.getWarpsAmountHas(player) >= maxAllowed) {
                this.getLang().Command_SetWarp_Error_Limit.send(player);
                return false;
            }

            if (!player.hasPermission(SunPerms.CORE_ADMIN)) {
                String world = player.getWorld().getName();
                if (WarpConfig.WARP_SET_WORLD_BLACKLIST.contains(world)) {
                    this.getLang().Command_SetWarp_Error_World.send(player);
                    return false;
                }
            }
        }

        Warp warp = this.getWarpById(id);
        if (warp != null) {
            if (!warp.isOwner(player)) {
                this.getLang().Command_SetWarp_Error_Exists.send(player);
                return false;
            }
            else {
                warp.setLocation(player.getLocation());
                warp.save();
                this.getLang().Command_SetWarp_Done_Location.replace(warp.replacePlaceholders()).send(player);
            }
        }
        else {
            warp = new Warp(this, id, player);
            warp.save();
            this.warps.put(warp.getId(), warp);
            this.getLang().Command_SetWarp_Done_New.replace(warp.replacePlaceholders()).send(player);
        }
        return true;
    }

    public boolean isExists(@NotNull String id) {
        return this.getWarpById(id) != null;
    }

    @NotNull
    public WarpMenuMain getWarpMenuMain() {
        return warpMenuMain;
    }

    @NotNull
    public WarpMenuList getWarpMenuPlayer() {
        return warpMenuPlayer;
    }

    @NotNull
    public WarpMenuList getWarpMenuServer() {
        return warpMenuServer;
    }

    public int getWarpsAmountPossible(@NotNull Player player) {
        return Hooks.getGroupValueInt(player, WarpConfig.WARP_SET_AMOUNT_PER_GROUP, true);
    }

    public int getWarpsAmountHas(@NotNull Player player) {
        return (int) this.getWarps().stream().filter(warp -> warp.isOwner(player)).count();
    }

    @NotNull
    public Map<String, Warp> getWarpsMap() {
        return this.warps;
    }

    @NotNull
    public Collection<Warp> getWarps() {
        return this.getWarpsMap().values();
    }

    @NotNull
    public List<Warp> getWarps(@NotNull WarpType warpType) {
        return this.getWarps().stream().filter(warp -> warp.getType() == warpType).toList();
    }

    @NotNull
    public List<Warp> getWarps(@NotNull WarpSortType sortType) {
        return this.getWarps().stream().sorted(sortType.getComparator()).toList();
    }

    @NotNull
    public List<Warp> getWarps(@NotNull WarpType warpType, @NotNull WarpSortType sortType) {
        return this.getWarps(warpType).stream().sorted(sortType.getComparator()).toList();
    }

    @NotNull
    public List<String> getWarpIds() {
        return this.getWarpsMap().keySet().stream().toList();
    }

    @NotNull
    public List<String> getWarpIds(@NotNull WarpType warpType) {
        return this.getWarps(warpType).stream().map(Warp::getId).toList();
    }

    @NotNull
    public List<String> getWarpIdsFor(@NotNull Player player) {
        return this.getWarps().stream().filter(warp -> warp.hasPermission(player)).map(Warp::getId).toList();
    }

    @NotNull
    public List<String> getWarpIdsOf(@NotNull Player player) {
        return this.getWarps().stream().filter(warp -> warp.isOwner(player)).map(Warp::getId).toList();
    }

    @Nullable
    public Warp getWarpById(@NotNull String id) {
        return this.getWarpsMap().get(id.toLowerCase());
    }

    public void updateWarpRatings() {
        this.getWarps().forEach(Warp::updateRatingValue);
    }
}
