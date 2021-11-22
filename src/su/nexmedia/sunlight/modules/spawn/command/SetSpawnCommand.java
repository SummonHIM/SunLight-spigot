package su.nexmedia.sunlight.modules.spawn.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Constants;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;
import su.nexmedia.sunlight.modules.spawn.SpawnPerms;

import java.util.List;

public class SetSpawnCommand extends SunModuleCommand<SpawnManager> {

    public static final String NAME = "setspawn";

    public SetSpawnCommand(@NotNull SpawnManager spawnManager) {
        super(spawnManager, CommandConfig.getAliases(NAME), SpawnPerms.CMD_SETSPAWN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_SetSpawn_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_SetSpawn_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
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
        Player player = (Player) sender;
        this.module.setSpawn(player, id);
    }
}
