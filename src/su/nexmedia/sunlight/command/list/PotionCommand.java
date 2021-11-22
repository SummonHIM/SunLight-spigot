package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Arrays;
import java.util.List;

public class PotionCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "potion";

    public PotionCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_POTION);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Potion_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Potion_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).toList();
        }
        if (i == 2) {
            return Arrays.asList("<amplifier>", "0", "1", "5", "10", "127");
        }
        if (i == 3) {
            return Arrays.asList("<duration>", "60", "300", "600", "3600");
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 3) {
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
        if (!(meta instanceof PotionMeta potionMeta)) {
            plugin.lang().Command_Potion_Error_NotAPotion.send(player);
            return;
        }

        PotionEffectType pType = PotionEffectType.getByName(args[0].toUpperCase());
        if (pType == null) {
            plugin.lang().Command_Potion_Error_InvalidEffect.send(player);
            return;
        }

        int amplifier = StringUT.getInteger(args[1], 0) - 1;
        if (amplifier < 0) {
            potionMeta.removeCustomEffect(pType);
        }
        else {
            int duration = StringUT.getInteger(args[2], 0) * 20;
            if (duration <= 0) return;

            PotionEffect pEffect = new PotionEffect(pType, duration, amplifier);
            potionMeta.addCustomEffect(pEffect, true);
        }

        plugin.lang().Command_Potion_Done.send(player);
        item.setItemMeta(meta);
    }
}
