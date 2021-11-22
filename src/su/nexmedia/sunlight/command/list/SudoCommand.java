package su.nexmedia.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import java.util.Collections;
import java.util.List;

public class SudoCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "sudo";

    public SudoCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_SUDO);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Sudo_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Sudo_Desc.getMsg();
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
        if (i == 2) {
            return CollectionsUT.getEnumsList(Type.class);
        }
        if (i == 3) {
            return Collections.singletonList("<message>");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 3) {
            this.printUsage(sender);
            return;
        }

        Player pTarget = plugin.getServer().getPlayer(args[0]);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        Type type = CollectionsUT.getEnum(args[1], Type.class);
        if (type == null) {
            this.errorType(sender, Type.class);
            return;
        }

        if (pTarget.hasPermission(SunPerms.CMD_SUDO_BYPASS)) {
            this.errorPermission(sender);
            return;
        }

        StringBuilder sudoBuild = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            sudoBuild.append(args[i]).append(" ");
        }
        String sudoText = sudoBuild.toString().trim();

        if (type == Type.CHAT) {
            pTarget.chat(sudoText);
        }
        else {
            pTarget.performCommand(sudoText);
        }

        plugin.lang().Command_Sudo_Done
            .replace("%message%", sudoText).replace("%player%", pTarget.getName())
            .send(sender);
    }

    enum Type {
        COMMAND, CHAT
    }
}
