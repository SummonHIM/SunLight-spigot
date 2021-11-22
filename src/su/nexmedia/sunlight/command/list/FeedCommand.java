package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class FeedCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "feed";
    // TODO Add saturation command.

    public FeedCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_FEED);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Feed_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Feed_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(SunPerms.CMD_FEED_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            this.printUsage(sender);
            return;
        }

        String pName = sender.getName();
        if (args.length == 1) {
            if (!sender.hasPermission(SunPerms.CMD_FEED_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            pName = args[0];
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        pTarget.setFoodLevel(20);
        pTarget.setSaturation(20F);

        if (!sender.equals(pTarget)) {
            plugin.lang().Command_Feed_Done_Others
                .replace("%player%", pTarget.getName())
                .send(sender);
        }
        plugin.lang().Command_Feed_Done_Self.send(pTarget);
    }
}
