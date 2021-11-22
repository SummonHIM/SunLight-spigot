package su.nexmedia.sunlight.modules.worlds.world.generator;

import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public enum EWorldGenerator {

    EMPTY(new EmptyGenerator(), "Empty"),

    ;

    private final ChunkGenerator chunkGenerator;
    private final String         name;

    EWorldGenerator(@NotNull ChunkGenerator chunkGenerator, @NotNull String name) {
        this.chunkGenerator = chunkGenerator;
        this.name = name;
    }

    @NotNull
    public ChunkGenerator getGenerator() {
        return this.chunkGenerator;
    }

    @NotNull
    public String getName() {
        return this.name;
    }
}
