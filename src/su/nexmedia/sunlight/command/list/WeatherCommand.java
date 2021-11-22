package su.nexmedia.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ArrayUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.LocUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Arrays;
import java.util.List;

public class WeatherCommand extends GeneralCommand<SunLight> {

    public static final  String   NAME    = "weather";
    private static final String[] WEATHER = new String[]{"sun", "storm", "thunder"};

    public WeatherCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_WEATHER);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Weather_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Weather_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.asList(WEATHER);
        }
        if (i == 2) {
            return LocUT.getWorldNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1 || !ArrayUtils.contains(WEATHER, args[0])) {
            this.printUsage(sender);
            return;
        }

        World world;
        if (args.length < 2) {
            if (sender instanceof Player player) {
                world = player.getWorld();
            }
            else {
                world = plugin.getServer().getWorlds().get(0);
            }
        }
        else {
            world = plugin.getServer().getWorld(args[1]);
        }

        if (world == null) {
            plugin.lang().Error_NoWorld.send(sender);
            return;
        }

        boolean isThunder = args[0].equalsIgnoreCase(WEATHER[2]);
        if (isThunder) {
            world.setThundering(true);
        }
        else {
            boolean isStorm = args[0].equalsIgnoreCase(WEATHER[1]);
            world.setThundering(false);
            world.setStorm(isStorm);
        }

        plugin.lang().Command_Weather_Done
            .replace("%weather%", args[0])
            .replace("%world%", world.getName())
            .send(sender);
    }
}
