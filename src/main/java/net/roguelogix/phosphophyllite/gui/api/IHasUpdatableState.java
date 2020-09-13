package net.roguelogix.phosphophyllite.gui.api;

public interface IHasUpdatableState<T> {
    /**
     * @return The current state of the tile.
     */
    T getState();
    
    /**
     * Call for an update to the current state information.
     */
    void updateState();
}
