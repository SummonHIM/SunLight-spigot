package su.nexmedia.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Collections;
import java.util.List;

public class TpposCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "tppos";

    public TpposCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_TPPOS);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Tppos_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Tppos_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Collections.singletonList("<x>");
        }
        if (i == 2) {
            return Collections.singletonList("<y>");
        }
        if (i == 3) {
            return Collections.singletonList("<z>");
        }
        if (i == 4 && player.hasPermission(SunPerms.CMD_TPPOS_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 4 && !(sender instanceof Player)) {
            this.errorSender(sender);
            return;
        }
        if (args.length < 3 || args.length > 4) {
            this.printUsage(sender);
            return;
        }

        String pName = sender.getName();

        if (args.length == 4) {
            if (!sender.hasPermission(SunPerms.CMD_TPPOS_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            pName = args[3];
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        World world = pTarget.getWorld();
        double x = StringUT.getDouble(args[0], 0, true);
        double y = StringUT.getDouble(args[1], 0, true);
        double z = StringUT.getDouble(args[2], 0, true);

        Location loc = new Location(world, x, y, z);
        pTarget.teleport(loc);
        plugin.lang().Command_Tppos_Done_Self.send(pTarget);

        if (!pTarget.equals(sender)) {
            plugin.lang().Command_Tppos_Done_Others
                .replace("%player%", pTarget.getName())
                .replace("%x%", String.valueOf(x))
                .replace("%y%", String.valueOf(y))
                .replace("%z%", String.valueOf(z))
                .replace("%w%", world.getName())
                .send(sender);
        }
    }
}
