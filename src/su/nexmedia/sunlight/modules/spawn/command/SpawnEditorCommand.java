package su.nexmedia.sunlight.modules.spawn.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.spawn.SpawnManager;
import su.nexmedia.sunlight.modules.spawn.SpawnPerms;

public class SpawnEditorCommand extends SunModuleCommand<SpawnManager> {

    public static final String NAME = "spawneditor";

    public SpawnEditorCommand(@NotNull SpawnManager module) {
        super(module, CommandConfig.getAliases(NAME), SpawnPerms.CMD_SPAWN_EDITOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_SpawnEditor_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        this.module.getEditor().open(player, 1);
    }
}
