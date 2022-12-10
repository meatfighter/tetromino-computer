package tetrominocomputer.tse.app;

import tetrominocomputer.sim.Structure;

public interface StructureListener {
    void clear();
    void structureLocked(final Structure lockedStructure);
}
