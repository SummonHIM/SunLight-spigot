package su.nexmedia.sunlight.modules.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.SunModule;
import su.nexmedia.sunlight.modules.menu.command.MenuCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager extends SunModule {

    private MenuLang             lang;
    private Map<String, SunMenu> menus;

    public MenuManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public String getId() {
        return ModuleId.MENU;
    }

    @Override
    @NotNull
    public String getVersion() {
        return "2.0";
    }

    @Override
    public void onLoad() {
        this.lang = new MenuLang(plugin, JYML.loadOrExtract(plugin, this.getPath() + "lang/messages_" + plugin.cfg().lang + ".yml"));
        this.lang.setup();

        this.menus = new HashMap<>();
        this.plugin.getConfigManager().extractFullPath(this.getFullPath() + "menus", "yml", false);

        for (JYML cfg : JYML.loadAll(this.getFullPath() + "menus", true)) {
            try {
                SunMenu gui = new SunMenu(this, cfg);
                this.menus.put(gui.getId(), gui);
            } catch (Exception ex) {
                plugin.error("Could not load menu: '" + cfg.getFile().getName() + "' !");
                ex.printStackTrace();
            }
        }
        this.info("Loaded " + this.menus.size() + " GUIs!");

        this.plugin.getCommandRegulator().register(new MenuCommand(this));
    }

    @Override
    public void onShutdown() {
        if (this.menus != null) {
            this.menus.values().forEach(SunMenu::clear);
            this.menus.clear();
            this.menus = null;
        }
    }

    @NotNull
    public MenuLang getLang() {
        return lang;
    }

    @Nullable
    public SunMenu getMenuByCommand(@NotNull String cmd) {
        return this.menus.values().stream().filter(menu -> menu.getAliases().contains(cmd)).findFirst().orElse(null);
    }

    @NotNull
    public List<SunMenu> getMenus(@NotNull Player player) {
        return this.menus.values().stream().filter(sunMenu -> sunMenu.hasPermission(player)).toList();
    }

    @Nullable
    public SunMenu getMenuById(@NotNull String id) {
        return this.menus.get(id.toLowerCase());
    }
}
