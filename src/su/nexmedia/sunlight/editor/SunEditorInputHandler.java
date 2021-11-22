package su.nexmedia.sunlight.editor;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorInputHandler;
import su.nexmedia.sunlight.SunLight;

public abstract class SunEditorInputHandler<T> implements EditorInputHandler<SunEditorType, T> {

    protected SunLight plugin;

    public SunEditorInputHandler(@NotNull SunLight plugin) {
        this.plugin = plugin;
    }
}
