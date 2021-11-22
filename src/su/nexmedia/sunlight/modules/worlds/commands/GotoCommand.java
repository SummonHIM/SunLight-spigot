package su.nexmedia.sunlight.modules.worlds.commands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;

import java.util.List;

public class GotoCommand extends SunModuleCommand<WorldManager> {

    public static final String NAME = "goto";

    public GotoCommand(@NotNull WorldManager worldManager) {
        super(worldManager, CommandConfig.getAliases(NAME), WorldPerms.CMD_GOTO);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_Goto_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Goto_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return plugin.getServer().getWorlds().stream().map(World::getName).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            this.printUsage(sender);
            return;
        }

        World world = plugin.getServer().getWorld(args[0]);
        if (world == null) {
            plugin.lang().Error_NoWorld.replace("%world%", args[0]).send(sender);
            return;
        }

        Player player = (Player) sender;
        player.teleport(world.getSpawnLocation());
        this.module.getLang().Command_Goto_Done.send(sender);
    }
}
