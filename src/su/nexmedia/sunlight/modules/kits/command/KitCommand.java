package su.nexmedia.sunlight.modules.kits.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.kits.Kit;
import su.nexmedia.sunlight.modules.kits.KitManager;
import su.nexmedia.sunlight.modules.kits.KitPerms;

import java.util.List;

public class KitCommand extends SunModuleCommand<KitManager> {

    public static final String NAME = "kit";

    public KitCommand(@NotNull KitManager kitManager) {
        super(kitManager, CommandConfig.getAliases(NAME), KitPerms.KITS_CMD_KIT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_Kit_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Kit_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return this.module.getKitIds(player);
        }
        if (i == 2) {
            if (player.hasPermission(KitPerms.KITS_CMD_KIT_OTHERS)) {
                return PlayerUT.getPlayerNames();
            }
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                this.errorSender(sender);
                return;
            }
            this.module.getKitsMenu().open(player, 1);
            return;
        }

        String kitId = args[0];
        Kit kit = this.module.getKitById(kitId);
        if (kit == null) {
            this.module.getLang().Kit_Error_InvalidKit.replace(Kit.PLACEHOLDER_ID, kitId).send(sender);
            return;
        }

        String pName = args.length >= 2 ? args[1] : sender.getName();
        if (args.length >= 2 && !sender.hasPermission(KitPerms.KITS_CMD_KIT_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        Player pTarget = plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        boolean isForce = !pTarget.equals(sender);
        kit.give(pTarget, isForce);

        if (isForce) {
            this.module.getLang().Kit_Notify_Give_Others
                .replace(kit.replacePlaceholders())
                .replace("%player%", pTarget.getName())
                .send(sender);
        }
    }
}
