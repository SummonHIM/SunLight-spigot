package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;
import java.util.stream.Stream;

public class BroadcastCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "broadcast";

    public BroadcastCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_BROADCAST);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Broadcast_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Broadcast_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.printUsage(sender);
            return;
        }

        String broadcast = StringUT.color(String.join(" ", Stream.of(args).toList())).trim();
        plugin.lang().Command_Broadcast_Format.replace("%msg%", broadcast).broadcast();
    }
}
