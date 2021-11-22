package su.nexmedia.sunlight.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;

import java.util.Arrays;
import java.util.List;

public abstract class IToggleCommand extends GeneralCommand<SunLight> {

    public IToggleCommand(@NotNull SunLight plugin, @NotNull String[] aliases, @NotNull String permission) {
        super(plugin, aliases, permission);
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return Arrays.asList("0", "1");
        }
        if (i == 2 && player.hasPermission(this.getPermissionOthers())) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public final void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 2 || (args.length == 0 && !(sender instanceof Player))) {
            this.printUsage(sender);
            return;
        }

        String targetName = sender.getName();
        boolean reverse = true;
        boolean state = false;

        if (args.length >= 1) {
            state = StringUT.parseCustomBoolean(args[0]);
            reverse = false;

            if (args.length == 2) {
                targetName = args[1];
            }
            else {
                if (!StringUT.isCustomBoolean(args[0])) {
                    targetName = args[0];
                    reverse = true;
                }
            }
        }

        if (!targetName.equalsIgnoreCase(sender.getName())) {
            if (!sender.hasPermission(this.getPermissionOthers())) {
                this.errorPermission(sender);
                return;
            }
        }

        Player targetPlayer = plugin.getServer().getPlayer(targetName);
        if (targetPlayer == null) {
            this.errorPlayer(sender);
            return;
        }

        if (!this.canToggle(targetPlayer)) {
            return;
        }

        state = this.toggle(targetPlayer, state, reverse);

        if (!sender.equals(targetPlayer)) {
            this.getMessageOthers()
                .replace("%state%", plugin.lang().getOnOff(state))
                .replace("%player%", targetPlayer.getName())
                .send(sender);
        }
        this.getMessageSelf().replace("%state%", plugin.lang().getOnOff(state)).send(targetPlayer);
    }

    protected boolean canToggle(@NotNull Player player) {
        return true;
    }

    protected abstract boolean toggle(@NotNull Player player, boolean state, boolean reverse);

    @NotNull
    protected abstract String getPermissionOthers();

    @NotNull
    protected abstract ILangMsg getMessageSelf();

    @NotNull
    protected abstract ILangMsg getMessageOthers();
}
