package su.nexmedia.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.IToggleCommand;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Set;

public class FlyCommand extends IToggleCommand {

    public static final String NAME = "fly";

    private final Set<String> deniedWorlds;

    public FlyCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_FLY);

        JYML cconfigg = CommandConfig.getConfig();
        this.deniedWorlds = cconfigg.getStringSet("Commands." + NAME + ".Disabled_Worlds");

        new RestrictionListener(plugin).registerListeners();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Fly_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Fly_Desc.getMsg();
    }

    public boolean isAllowedWorld(@NotNull World world) {
        return !this.deniedWorlds.contains(world.getName());
    }

    public boolean checkWorld(@NotNull Player player) {
        if (player.hasPermission(SunPerms.CMD_FLY_BYPASS_WORLDS)) {
            return true;
        }
        if (Hooks.isNPC(player) || this.isAllowedWorld(player.getWorld())) {
            return true;
        }

        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGliding(false);

        plugin.lang().Command_Fly_Error_BadWorld.send(player);
        return false;
    }

    @Override
    protected boolean canToggle(@NotNull Player player) {
        if (!this.checkWorld(player)) return false;

        return super.canToggle(player);
    }

    @Override
    protected boolean toggle(@NotNull Player player, boolean state, boolean reverse) {
        if (reverse) state = !player.getAllowFlight();
        player.setAllowFlight(state);
        return state;
    }

    @Override
    @NotNull
    protected String getPermissionOthers() {
        return SunPerms.CMD_FLY_OTHERS;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageSelf() {
        return plugin.lang().Command_Fly_Done_Self;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageOthers() {
        return plugin.lang().Command_Fly_Done_Others;
    }

    class RestrictionListener extends AbstractListener<SunLight> {

        RestrictionListener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onCommandFlyFlight(PlayerToggleFlightEvent e) {
            FlyCommand.this.checkWorld(e.getPlayer());
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onCommandFlyGlide(EntityToggleGlideEvent e) {
            if (e.getEntity() instanceof Player player) {
                FlyCommand.this.checkWorld(player);
            }
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onCommandFlyWorldChange(PlayerChangedWorldEvent e) {
            Player player = e.getPlayer();
            if (player.isFlying() || player.getAllowFlight() || player.isGliding()) {
                FlyCommand.this.checkWorld(player);
            }
        }
    }
}
