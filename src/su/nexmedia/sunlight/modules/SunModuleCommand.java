package su.nexmedia.sunlight.modules;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.module.AbstractModuleCommand;
import su.nexmedia.sunlight.SunLight;

public abstract class SunModuleCommand<M extends SunModule> extends AbstractModuleCommand<SunLight, M> {

    public SunModuleCommand(@NotNull M module, @NotNull String[] aliases) {
        this(module, aliases, null);
    }

    public SunModuleCommand(@NotNull M module, @NotNull String[] aliases, @Nullable String permission) {
        super(module, aliases, permission);
    }

}
