package su.nexmedia.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.CollectionsUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;
import su.nexmedia.sunlight.utils.SunUtils;

import java.util.List;

// TODO Spawners module
public class SpawnerCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "spawner";

    public SpawnerCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_SPAWNER);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.lang().Command_Spawner_Usage.getMsg();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_Spawner_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return SunUtils.ENTITY_TYPES.stream()
                .filter(type -> player.hasPermission(SunPerms.CMD_SPAWNER + "." + type)).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 100);
        if (block.getType() != Material.SPAWNER) {
            plugin.lang().Command_Spawner_Error_Block.send(sender);
            return;
        }

        EntityType entityType = CollectionsUT.getEnum(args[0], EntityType.class);
        if (entityType == null || !entityType.isSpawnable()) {
            plugin.lang().Command_Spawner_Error_Type.send(sender);
            return;
        }

        if (!sender.hasPermission(SunPerms.CMD_SPAWNER + "." + entityType.name().toLowerCase())) {
            this.errorPermission(sender);
            return;
        }

        BlockState state = block.getState();
        CreatureSpawner spawner = (CreatureSpawner) state;
        try {
            spawner.setSpawnedType(entityType);
        } catch (IllegalArgumentException ex) {
            plugin.lang().Command_Spawner_Error_Type.send(sender);
            return;
        }
        state.update(true);

        plugin.lang().Command_Spawner_Done
            .replace("%type%", plugin.lang().getEnum(entityType))
            .send(sender);
    }
}
