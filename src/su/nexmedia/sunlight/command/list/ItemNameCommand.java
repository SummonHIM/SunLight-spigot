package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class ItemNameCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "itemname";

    public ItemNameCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_ITEMNAME);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Itemname_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Itemname_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
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

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (ItemUT.isAir(item)) {
            this.errorItem(sender);
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        StringBuilder nameBuild = new StringBuilder();
        for (String arg : args) {
            nameBuild.append(arg);
            nameBuild.append(" ");
        }
        String name = StringUT.color(nameBuild.toString().trim());

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        plugin.lang().Command_Itemname_Done.send(sender);
    }
}
