package su.nexmedia.sunlight.command.list;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
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
import java.util.List;

public class EnchantCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "enchant";

    private final List<String> enchants;

    public EnchantCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_ENCHANT);

        this.enchants = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.values()) {
            this.enchants.add(enchantment.getKey().getKey());
        }
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Enchant_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Enchant_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return this.enchants;
        }
        if (i == 2) {
            return Arrays.asList("0", "1", "5", "10", "127");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta1 = item.getItemMeta();
        if (ItemUT.isAir(item) || meta1 == null) {
            this.errorItem(sender);
            return;
        }

        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
        if (enchant == null) {
            plugin.lang().Error_Enchant.send(sender);
            return;
        }

        int level = StringUT.getInteger(args[1], -1);
        if (level < 0) {
            this.errorNumber(sender, args[1]);
            return;
        }

        if (meta1 instanceof EnchantmentStorageMeta meta) {
            if (level > 0) {
                meta.addStoredEnchant(enchant, level, true);
            }
            else {
                meta.removeStoredEnchant(enchant);
            }
            item.setItemMeta(meta);
        }
        else {
            if (level > 0) {
                meta1.addEnchant(enchant, level, true);
            }
            else {
                meta1.removeEnchant(enchant);
            }
            item.setItemMeta(meta1);
        }

        plugin.lang().Command_Enchant_Done.send(sender);
    }
}
