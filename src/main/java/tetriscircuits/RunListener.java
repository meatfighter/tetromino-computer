package tetriscircuits;

import java.util.List;

public interface RunListener {
    void runCompleted(List<Point> inputs, List<Point> outputs, List<LockedTetrimino> lockedTetriminos);
}
