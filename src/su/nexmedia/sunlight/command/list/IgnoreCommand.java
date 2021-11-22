package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.data.SunUser;

import java.util.List;

public class IgnoreCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "ignore";

    public IgnoreCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_IGNORE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Ignore_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Ignore_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player pBanner = (Player) sender;
        SunUser userBanner = plugin.getUserManager().getOrLoadUser(pBanner);

        // Open GUI editor if no args provided.
        if (args.length == 0) {
            userBanner.getEditorIgnoredList().open(pBanner, 1);
            return;
        }

        // Check if target player is online.
        Player pTarget = plugin.getServer().getPlayer(args[0]);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        // Check if player trying to block himself.
        if (pTarget.getName().equalsIgnoreCase(pBanner.getName())) {
            plugin.lang().Error_Self.send(sender);
            return;
        }

        // Check if target player is unblockable.
        if (pTarget.hasPermission(SunPerms.CMD_IGNORE_BYPASS)) {
            plugin.lang().Command_Ignore_Error_Bypass
                .replace("%player%", pTarget.getName())
                .send(sender);
            return;
        }

        // Check if target player is already blocked.
        if (!userBanner.addIgnoredUser(pTarget.getName())) {
            plugin.lang().Command_Ignore_Error_Already
                .replace("%player%", pTarget.getName())
                .send(sender);
            return;
        }

        plugin.lang().Command_Ignore_Done
            .replace("%player%", pTarget.getName())
            .send(sender);
    }
}
