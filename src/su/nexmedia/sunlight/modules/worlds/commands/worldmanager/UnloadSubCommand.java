package su.nexmedia.sunlight.modules.worlds.commands.worldmanager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.modules.worlds.world.SunWorld;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;

import java.util.List;

public class UnloadSubCommand extends AbstractWorldSubCommand {

    public static final String NAME = "unload";

    public UnloadSubCommand(@NotNull WorldManager worldManager) {
        super(worldManager, new String[]{NAME}, WorldPerms.CMD_WORLDMANAGER_UNLOAD);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.worldManager.getLang().Command_WorldManager_Unload_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.worldManager.getLang().Command_WorldManager_Unload_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return this.worldManager.getWorldNames();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            this.printUsage(sender);
            return;
        }

        SunWorld world = this.worldManager.getWorldById(args[1]);
        if (world == null) {
            plugin.lang().Error_NoWorld.replace("%world%", args[1]).send(sender);
            return;
        }
        if (!world.unload()) {
            this.worldManager.getLang().Command_WorldManager_Unload_Error.send(sender);
            return;
        }

        this.worldManager.getLang().Command_WorldManager_Unload_Done.replace(world.replacePlaceholders()).send(sender);
    }
}
