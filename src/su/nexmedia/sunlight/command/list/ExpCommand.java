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

public class ExpCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "exp";

    public ExpCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_EXP);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Exp_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Exp_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.asList("give", "set");
        }
        if (i == 2) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length <= 1) {
            String name = args.length == 1 ? args[0] : sender.getName();
            Player pTarget = plugin.getServer().getPlayer(name);
            if (pTarget == null) {
                this.errorPlayer(sender);
                return;
            }

            int total = PlayerUT.getTotalExperience(pTarget);
            int lvl = pTarget.getLevel();
            int up = PlayerUT.getExpUntilNextLevel(pTarget);
            plugin.lang().Command_Exp_Show
                .replace("%player%", pTarget.getDisplayName())
                .replace("%up%", String.valueOf(up))
                .replace("%lvl%", String.valueOf(lvl))
                .replace("%total%", String.valueOf(total))
                .send(sender);
            return;
        }

        if (!args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("give")) {
            this.printUsage(sender);
            return;
        }

        String permSelf = args[0].equalsIgnoreCase("set") ? SunPerms.CMD_EXP_SET : SunPerms.CMD_EXP_GIVE;
        String permOther = args[0].equalsIgnoreCase("set") ? SunPerms.CMD_EXP_SET_OTHERS : SunPerms.CMD_EXP_GIVE_OTHERS;

        if (!sender.hasPermission(permSelf) || (args.length >= 3 && !sender.hasPermission(permOther))) {
            this.errorPermission(sender);
            return;
        }

        String strAmount = args.length >= 3 ? args[2] : args[1];
        String name = args.length >= 3 ? args[1] : sender.getName();
        Player pTarget = plugin.getServer().getPlayer(name);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        boolean give = args[0].equalsIgnoreCase("give");

        long amount;
        if (strAmount.endsWith("l")) {
            int inputLevel = StringUT.getInteger(strAmount.replace("l", ""), 0);
            amount = PlayerUT.getExpToLevel(give ? inputLevel + pTarget.getLevel() : inputLevel);
            PlayerUT.setTotalExperience(pTarget, 0);
        }
        else {
            amount = StringUT.getInteger(strAmount, 0);
        }

        amount = Math.max(0, Math.min(Integer.MAX_VALUE, give ? amount + PlayerUT.getTotalExperience(pTarget) : amount));
        PlayerUT.setTotalExperience(pTarget, (int) amount);

        plugin.lang().Command_Exp_Done
            .replace("%exp%", amount)
            .replace("%player%", pTarget.getName())
            .send(sender);
    }
}
