package su.nexmedia.sunlight.modules.worlds.world.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmptyGenerator extends SimpleChunkGenerator {

    @Override
    @NotNull
    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> list = new ArrayList<BlockPopulator>();
        list.add(new BlockPopulator() {
            @Override
            public void populate(World world, Random random, Chunk chunk) {
                chunk.getBlock(1, 1, 1).setType(Material.AIR, false);
            }
        });
        return list;
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }
}
