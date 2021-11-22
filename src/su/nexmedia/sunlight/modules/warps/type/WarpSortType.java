package su.nexmedia.sunlight.modules.warps.type;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.modules.warps.Warp;

import java.util.Comparator;

public enum WarpSortType {

    WARP_NAME((warp1, warp2) -> {
        return StringUT.colorOff(warp1.getName()).compareTo(StringUT.colorOff(warp2.getName()));
    }),
    WARP_ID((warp1, warp2) -> {
        return warp1.getId().compareTo(warp2.getId());
    }),
    RATING((warp1, warp2) -> {
        return (int) (warp2.getRatingValue() - warp1.getRatingValue());
    }),
    ;

    private final Comparator<? super Warp> comparator;

    WarpSortType(@NotNull Comparator<? super Warp> comparator) {
        this.comparator = comparator;
    }

    @NotNull
    public Comparator<? super Warp> getComparator() {
        return comparator;
    }
}
