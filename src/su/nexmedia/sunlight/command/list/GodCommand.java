package su.nexmedia.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.command.IToggleCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

import java.util.Set;

public class GodCommand extends IToggleCommand implements ICleanable {

    public static final String NAME = "god";

    // TODO Damage types
    private final Set<String>         deniedWorlds;
    private final RestrictionListener listener;

    public GodCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_GOD);

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";
        this.deniedWorlds = cfg.getStringSet(path + "World_Blacklist");

        (this.listener = new RestrictionListener(plugin)).registerListeners();
    }

    @Override
    public void clear() {
        this.listener.unregisterListeners();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_God_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_God_Desc.getMsg();
    }

    private boolean isAllowedWorld(@NotNull World world) {
        return !this.deniedWorlds.contains(world.getName());
    }

    private boolean checkWorld(@NotNull Player player) {
        if (player.hasPermission(SunPerms.CMD_GOD_BYPASS_WORLDS)) {
            return true;
        }
        if (Hooks.isNPC(player) || this.isAllowedWorld(player.getWorld())) {
            return true;
        }

        SunUser user = plugin.getUserManager().getOrLoadUser(player);
        user.setGodMode(false);

        plugin.lang().Command_God_Error_World.send(player);
        return false;
    }

    @Override
    protected boolean canToggle(@NotNull Player player) {
        if (!this.checkWorld(player)) return false;

        return super.canToggle(player);
    }

    @Override
    protected boolean toggle(@NotNull Player player, boolean state, boolean reverse) {
        SunUser userTarget = plugin.getUserManager().getOrLoadUser(player);
        if (reverse) state = !userTarget.isGodMode();
        userTarget.setGodMode(state);

        return state;
    }

    @Override
    @NotNull
    protected String getPermissionOthers() {
        return SunPerms.CMD_GOD_OTHERS;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageSelf() {
        return plugin.lang().Command_God_Toggle_Self;
    }

    @Override
    @NotNull
    protected ILangMsg getMessageOthers() {
        return plugin.lang().Command_God_Toggle_Others;
    }

    class RestrictionListener extends AbstractListener<SunLight> {

        RestrictionListener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onGodCommandWorldChange(PlayerChangedWorldEvent e) {
            Player player = e.getPlayer();
            GodCommand.this.checkWorld(player);
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onGodCommandDamage(EntityDamageEvent e) {
            //if (!(e.getEntity() instanceof Player) && damageToMobs) return;

            Player damager = null;
            if (e.getEntity() instanceof Player) {
                damager = (Player) e.getEntity();
            }
            else if (e instanceof EntityDamageByEntityEvent ede) {
                if (ede.getDamager() instanceof Player) {
                    damager = (Player) ede.getDamager();
                }
                else if (ede.getDamager() instanceof Projectile projectile) {
                    ProjectileSource shooter = projectile.getShooter();
                    if (!(shooter instanceof Player)) return;

                    damager = (Player) shooter;
                }
            }
            if (damager == null || Hooks.isNPC(damager)) return;

            SunUser user = this.plugin.getUserManager().getOrLoadUser(damager);
            if (user.isGodMode()) {
                e.setCancelled(true);
            }
        }
    }
}
