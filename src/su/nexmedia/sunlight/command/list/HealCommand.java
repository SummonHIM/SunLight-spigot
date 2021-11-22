package su.nexmedia.sunlight.command.list;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.EntityUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.List;

public class HealCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "heal";

    public HealCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_HEAL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Heal_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Heal_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1 && player.hasPermission(SunPerms.CMD_HEAL_OTHERS)) {
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

        String pName;
        if (args.length == 1) {
            if (!sender.hasPermission(SunPerms.CMD_HEAL_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
            pName = args[0];
        }
        else {
            pName = sender.getName();
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        double max = EntityUT.getAttribute(pTarget, Attribute.GENERIC_MAX_HEALTH);
        pTarget.setHealth(max);

        for (PotionEffect potionEffect : pTarget.getActivePotionEffects()) {
            pTarget.removePotionEffect(potionEffect.getType());
        }

        if (!sender.equals(pTarget)) {
            plugin.lang().Command_Heal_Done_Others
                .replace("%player%", pTarget.getName())
                .send(sender);
        }

        plugin.lang().Command_Heal_Done_Self.send(pTarget);
    }
}
