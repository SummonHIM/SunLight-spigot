package su.nexmedia.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.utils.SunUtils;

import java.util.Arrays;
import java.util.List;

public class ItemCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "item";

    public ItemCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_ITEM);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Item_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Item_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && args[0].length() > 0) {
            return StringUT.getByFirstLetters(args[0], SunUtils.MATERIAL_NAMES);
        }
        if (i == 2) {
            return Arrays.asList("1", "16", "32", "64");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1 || args.length > 2) {
            this.printUsage(sender);
            return;
        }

        Material material = Material.getMaterial(args[0].toUpperCase());
        if (material == null) {
            plugin.lang().Error_Material.send(sender);
            return;
        }

        Player player = (Player) sender;
        int amount = args.length == 2 ? StringUT.getInteger(args[1], 0) : 64;
        if (amount == 0) {
            this.errorNumber(sender, args[1]);
            return;
        }

        ItemStack item = new ItemStack(material, amount);
        ItemUT.addItem(player, item);

        plugin.lang().Command_Item_Done
            .replace("%amount%", String.valueOf(amount))
            .replace("%item%", plugin.lang().getEnum(material))
            .send(player);
    }
}
