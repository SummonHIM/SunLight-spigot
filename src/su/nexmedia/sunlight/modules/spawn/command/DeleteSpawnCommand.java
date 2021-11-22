package su.nexmedia.sunlight.modules.spawn.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.spawn.Spawn;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;
import su.nexmedia.sunlight.modules.spawn.SpawnPerms;

import java.util.List;

public class DeleteSpawnCommand extends SunModuleCommand<SpawnManager> {

    public static final String NAME = "deletespawn";

    public DeleteSpawnCommand(@NotNull SpawnManager spawnManager) {
        super(spawnManager, CommandConfig.getAliases(NAME), SpawnPerms.CMD_DELSPAWN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_DelSpawn_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_DelSpawn_Desc.getMsg();
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
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            this.printUsage(sender);
            return;
        }

        String id = args.length >= 1 ? args[0] : Constants.DEFAULT;
        Spawn spawn = this.module.getSpawnById(id);
        if (spawn == null) {
            this.module.getLang().Command_Spawn_Error_Empty.replace(Spawn.PLACEHOLDER_ID, id).send(sender);
            return;
        }

        this.module.deleteSpawn(spawn);
        this.module.getLang().Command_DelSpawn_Done.replace(spawn.replacePlaceholders()).send(sender);
    }
}
