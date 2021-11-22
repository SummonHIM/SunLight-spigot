package su.nexmedia.sunlight.modules.worlds.commands.worldmanager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.worlds.WorldManager;

public abstract class AbstractWorldSubCommand extends AbstractCommand<SunLight> {

    protected WorldManager worldManager;

    public AbstractWorldSubCommand(@NotNull WorldManager worldManager, @NotNull String[] aliases, @Nullable String permission) {
        super(worldManager.plugin(), aliases, permission);
        this.worldManager = worldManager;
    }
}
