package su.nexmedia.sunlight.modules.worlds.world.generator;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class SimpleChunkGenerator extends ChunkGenerator {

    @Override
    @NotNull
    public ChunkData generateChunkData(
        World world,
        Random random,
        int x,
        int z,
        ChunkGenerator.BiomeGrid biomes) {

        Biome b = this.getBiome();
        if (b != null) {
            for (int xCounter = 0; xCounter < 16; xCounter++) {
                for (int yCounter = 0; yCounter < world.getMaxHeight(); yCounter++) {
                    for (int zCounter = 0; zCounter < 16; zCounter++) {
                        biomes.setBiome(x, yCounter, z, b);
                    }
                }
            }
        }
        return createChunkData(world);
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    public Biome getBiome() {
        return null;
    }
}
