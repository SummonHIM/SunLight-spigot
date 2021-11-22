package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Arrays;
import java.util.List;

public class AirCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "air";

    public AirCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_AIR);
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Air_Desc.getMsg();
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Air_Usage.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.asList("<amount>", "300", "1000");
        }
        if (i == 2) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2 || args.length < 1) {
            this.printUsage(sender);
            return;
        }

        int amount = StringUT.getInteger(args[0], 0);
        if (amount == 0) {
            this.errorNumber(sender, args[0]);
            return;
        }

        String pName = sender.getName();
        if (args.length == 2) {
            if (!sender.hasPermission(SunPerms.CMD_AIR_OTHERS)) {
                this.errorPlayer(sender);
                return;
            }
            pName = args[1];
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        pTarget.setMaximumAir(amount);
        pTarget.setRemainingAir(amount);

        if (!sender.equals(pTarget)) {
            plugin.lang().Command_Air_Done_Others.replace("%player%", pTarget.getName()).send(sender);
        }
        plugin.lang().Command_Air_Done_Self.send(pTarget);
    }
}
