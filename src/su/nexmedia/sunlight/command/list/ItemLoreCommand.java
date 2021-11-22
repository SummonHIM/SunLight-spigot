package su.nexmedia.sunlight.command.list;

import org.apache.commons.lang.StringUtils;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemLoreCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "itemlore";

    public ItemLoreCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_ITEMLORE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_ItemLore_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_ItemLore_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.asList("add", "del", "clear");
        }

        if (i >= 2) {
            String arg2 = args[0];
            if (arg2.equalsIgnoreCase("add")) {
                return Collections.singletonList("<text>");
            }
            if (i == 2 && arg2.equalsIgnoreCase("del")) {
                return Collections.singletonList("<position>");
            }
        }

        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (args.length < 1) {
            this.printUsage(p);
            return;
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        if (ItemUT.isAir(item)) {
            this.errorItem(sender);
            return;
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("add")) {
                StringBuilder line = new StringBuilder();
                int argLength = args.length;
                int lines = argLength;
                int pos = -1;

                if (StringUtils.isNumeric(args[argLength - 1])) {
                    lines = lines - 1;
                    pos = Integer.parseInt(args[argLength - 1]);
                }
                for (int i = 1; i < lines; i++) {
                    line.append(args[i]).append(" ");
                }

                this.addLoreLine(item, line.toString().trim(), pos);
            }
            else if (args[0].equalsIgnoreCase("del")) {
                int pos = StringUT.getInteger(args[1], -1);
                if (pos < 0) {
                    this.errorNumber(sender, args[1]);
                    return;
                }

                this.delLoreLine(item, pos);
            }
            else {
                this.printUsage(p);
                return;
            }
        }
        else if (args[0].equalsIgnoreCase("clear")) {
            this.clearLore(item);
        }
        else {
            this.printUsage(p);
            return;
        }

        plugin.lang().Command_ItemLore_Done.send(sender);
    }


    private void addLoreLine(@NotNull ItemStack item, @NotNull String line, int pos) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        line = StringUT.color(line);
        if (pos > 0 && pos < lore.size()) {
            lore.add(pos, line);
        }
        else {
            lore.add(line);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void delLoreLine(@NotNull ItemStack item, int pos) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore();
        if (lore == null) return;

        if (pos >= lore.size() || pos < 0) {
            pos = lore.size() - 1;
        }

        lore.remove(pos);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void clearLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return;

        meta.setLore(null);
        item.setItemMeta(meta);
    }
}
