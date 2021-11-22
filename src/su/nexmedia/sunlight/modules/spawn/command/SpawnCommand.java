package su.nexmedia.sunlight.modules.spawn.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.constants.JStrings;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.spawn.Spawn;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;
import su.nexmedia.sunlight.modules.spawn.SpawnPerms;

import java.util.List;

public class SpawnCommand extends SunModuleCommand<SpawnManager> {

    public static final String NAME = "spawn";

    public SpawnCommand(@NotNull SpawnManager spawnManager) {
        super(spawnManager, CommandConfig.getAliases(NAME), SpawnPerms.CMD_SPAWN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_Spawn_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Spawn_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return this.module.getSpawnIds();
        }
        if (i == 2 && player.hasPermission(SpawnPerms.CMD_SPAWN_OTHERS)) {
            return PlayerUT.getPlayerNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if ((args.length < 1 && !(sender instanceof Player)) || args.length > 2) {
            this.printUsage(sender);
            return;
        }

        if (args.length >= 2 && !sender.hasPermission(SpawnPerms.CMD_SPAWN_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String spawnId = args.length >= 1 ? args[0] : JStrings.DEFAULT;
        Spawn spawn = this.module.getSpawnById(spawnId);
        if (spawn == null) {
            this.module.getLang().Command_Spawn_Error_Empty.replace(Spawn.PLACEHOLDER_ID, spawnId).send(sender);
            return;
        }

        String pName = args.length >= 2 ? args[1] : sender.getName();
        Player pTarget = this.plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        spawn.teleport(sender, pTarget);
    }
}
