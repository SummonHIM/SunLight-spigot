package su.nexmedia.sunlight.modules.worlds.commands.worldmanager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;
import su.nexmedia.sunlight.modules.worlds.world.SunWorld;

import java.util.Arrays;
import java.util.List;

public class DeleteSubCommand extends AbstractWorldSubCommand {

    public static final String NAME = "delete";

    public DeleteSubCommand(@NotNull WorldManager worldManager) {
        super(worldManager, new String[]{NAME}, WorldPerms.CMD_WORLDMANAGER_DELETE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.worldManager.getLang().Command_WorldManager_Delete_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.worldManager.getLang().Command_WorldManager_Delete_Desc.getMsg();
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
        if (i == 2) {
            return Arrays.asList(String.valueOf(true), String.valueOf(false));
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            this.printUsage(sender);
            return;
        }

        SunWorld world = this.worldManager.getWorldById(args[1]);
        if (world == null) {
            plugin.lang().Error_NoWorld.replace("%world%", args[1]).send(sender);
            return;
        }

        boolean withFolder = args.length >= 3 && Boolean.parseBoolean(args[2]);
        if (!world.delete(withFolder)) {
            this.worldManager.getLang().Command_WorldManager_Delete_Error.send(sender);
            return;
        }

        this.worldManager.getLang().Command_WorldManager_Delete_Done.replace(world.replacePlaceholders()).send(sender);
    }
}
