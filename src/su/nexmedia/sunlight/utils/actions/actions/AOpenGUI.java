package su.nexmedia.sunlight.utils.actions.actions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.actions.actions.IActionExecutor;
import su.nexmedia.engine.utils.actions.params.IParamResult;
import su.nexmedia.engine.utils.actions.params.IParamType;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.menu.MenuManager;
import su.nexmedia.sunlight.modules.menu.SunMenu;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AOpenGUI extends IActionExecutor {

    public AOpenGUI(@NotNull SunLight plugin) {
        super(plugin, "OPEN_GUI");
    }

    @Override
    @NotNull
    public List<String> getDescription() {
        return Collections.singletonList("Opens certain GUI.");
    }

    @Override
    public void registerParams() {
        this.registerParam(IParamType.DELAY);
        this.registerParam(IParamType.TARGET);
        this.registerParam(IParamType.NAME);
    }

    @Override
    public boolean mustHaveTarget() {
        return true;
    }

    @Override
    protected void execute(Entity exe, Set<Entity> targets, IParamResult result) {
        SunLight plugin = (SunLight) this.plugin;
        MenuManager menuManager = plugin.getModuleCache().getMenuManager();
        if (menuManager == null) return;

        String id = result.getParamValue(IParamType.NAME).getString(null);
        if (id == null) return;

        SunMenu menu = menuManager.getMenuById(id);
        if (menu == null) return;

        for (Entity e : targets) {
            if (e.getType() == EntityType.PLAYER) {
                Player player = (Player) e;
                menu.open(player, true);
            }
        }
    }
}
