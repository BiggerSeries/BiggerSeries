package net.roguelogix.phosphophyllite.gui.client.api;

import javax.annotation.Nonnull;

public interface IHasUpdatableState<T> {
    /**
     * @return The current state of the tile.
     */
    @Nonnull
    T getState();
    
    /**
     * Call for an update to the current state information.
     */
    void updateState();
}
