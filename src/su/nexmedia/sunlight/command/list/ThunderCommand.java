package su.nexmedia.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class ThunderCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "thunder";

    public ThunderCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_THUNDER);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Thunder_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Thunder_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(SunPerms.CMD_THUNDER_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            this.errorSender(sender);
            return;
        }
        if (args.length > 1) {
            this.printUsage(sender);
            return;
        }

        Player pTarget = null;
        if (args.length == 1) {
            if (!sender.hasPermission(SunPerms.CMD_THUNDER_OTHERS)) {
                this.errorPermission(sender);
                return;
            }

            pTarget = plugin.getServer().getPlayer(args[0]);
            if (pTarget == null) {
                this.errorPlayer(sender);
                return;
            }
        }

        if (pTarget != null) {
            pTarget.getWorld().strikeLightning(pTarget.getLocation());
            plugin.lang().Command_Thunder_Done_Player
                .replace("%player%", pTarget.getName())
                .send(sender);
        }
        else {
            Player p = (Player) sender;
            Location loc = p.getTargetBlock(null, 100).getLocation();
            p.getWorld().strikeLightning(loc);
            plugin.lang().Command_Thunder_Done_Block.send(p);
        }
    }
}
