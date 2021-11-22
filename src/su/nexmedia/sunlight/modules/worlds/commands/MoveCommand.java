package su.nexmedia.sunlight.modules.worlds.commands;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.modules.SunModuleCommand;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;

import java.util.List;

public class MoveCommand extends SunModuleCommand<WorldManager> {

    public static final String NAME = "move";

    public MoveCommand(@NotNull WorldManager worldManager) {
        super(worldManager, CommandConfig.getAliases(NAME), WorldPerms.CMD_MOVE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.module.getLang().Command_Move_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.module.getLang().Command_Move_Desc.getMsg();
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
            return plugin.getServer().getWorlds().stream().map(World::getName).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            this.printUsage(sender);
            return;
        }

        Player player = plugin.getServer().getPlayer(args[0]);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        World world = plugin.getServer().getWorld(args[1]);
        if (world == null) {
            plugin.lang().Error_NoWorld.replace("%world%", args[1]).send(sender);
            return;
        }

        player.teleport(world.getSpawnLocation());
        this.module.getLang().Command_Move_Done
            .replace("%world%", world.getName())
            .replace("%player%", player.getName())
            .send(sender);
    }
}
