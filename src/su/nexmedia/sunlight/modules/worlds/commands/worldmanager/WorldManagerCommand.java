package su.nexmedia.sunlight.modules.worlds.commands.worldmanager;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;

public class WorldManagerCommand extends SunModuleCommand<WorldManager> {

    public static final String NAME = "worldmanager";

    public WorldManagerCommand(@NotNull WorldManager worldManager) {
        super(worldManager, CommandConfig.getAliases(NAME), WorldPerms.CMD_WORLDMANAGER);
        this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
        this.addChildren(new CreateSubCommand(worldManager));
        this.addChildren(new DeleteSubCommand(worldManager));
        this.addChildren(new EditorSubCommand(worldManager));
        this.addChildren(new LoadSubCommand(worldManager));
        this.addChildren(new UnloadSubCommand(worldManager));
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    public @NotNull String getDescription() {
        return this.module.getLang().Command_WorldManager_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

    }
}
