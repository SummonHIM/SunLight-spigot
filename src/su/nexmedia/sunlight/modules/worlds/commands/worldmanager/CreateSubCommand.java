package su.nexmedia.sunlight.modules.worlds.commands.worldmanager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.modules.worlds.world.SunWorld;
import su.nexmedia.sunlight.modules.worlds.WorldManager;
import su.nexmedia.sunlight.modules.worlds.WorldPerms;

public class CreateSubCommand extends AbstractWorldSubCommand {

    public static final String NAME = "create";

    public CreateSubCommand(@NotNull WorldManager worldManager) {
        super(worldManager, new String[]{NAME}, WorldPerms.CMD_WORLDMANAGER_CREATE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.worldManager.getLang().Command_WorldManager_Create_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.worldManager.getLang().Command_WorldManager_Create_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            this.printUsage(sender);
            return;
        }

        String name = args[1];
        SunWorld world = new SunWorld(this.worldManager, name);
        world.save();
        worldManager.getWorldsMap().put(world.getId(), world);

        if (sender instanceof Player player) {
            world.getEditor().open(player, 1);
        }

        this.worldManager.getLang().Command_WorldManager_Create_Done.replace(world.replacePlaceholders()).send(sender);
    }
}
