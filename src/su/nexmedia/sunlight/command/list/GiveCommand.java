package su.nexmedia.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.utils.SunUtils;

import java.util.Arrays;
import java.util.List;

public class GiveCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "give";

    public GiveCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_GIVE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Give_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Give_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUT.getPlayerNames();
        }
        if (i == 2 && args[1].length() > 0) {
            return StringUT.getByFirstLetters(args[1], SunUtils.MATERIAL_NAMES);
        }
        if (i == 3) {
            return Arrays.asList("1", "16", "32", "64");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2 || args.length > 3) {
            this.printUsage(sender);
            return;
        }

        Player pTarget = plugin.getServer().getPlayer(args[0]);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        Material mat = Material.getMaterial(args[1].toUpperCase());
        if (mat == null) {
            plugin.lang().Error_Material.send(sender);
            return;
        }

        int amount = args.length == 3 ? StringUT.getInteger(args[2], 0) : 64;
        if (amount == 0) {
            this.errorNumber(sender, args[2]);
            return;
        }

        ItemStack item = new ItemStack(mat, amount);
        ItemUT.addItem(pTarget, item);

        plugin.lang().Command_Give_Done_Self
            .replace("%amount%", String.valueOf(amount))
            .replace("%item%", plugin.lang().getEnum(mat))
            .send(pTarget);

        if (!pTarget.equals(sender)) {
            plugin.lang().Command_Give_Done_Others
                .replace("%amount%", String.valueOf(amount))
                .replace("%player%", pTarget.getName())
                .replace("%item%", plugin.lang().getEnum(mat))
                .send(sender);
        }
    }
}
