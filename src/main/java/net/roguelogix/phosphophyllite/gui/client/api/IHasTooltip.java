package net.roguelogix.phosphophyllite.gui.client.api;

public interface IHasTooltip {
    
    /**
     * Perform an action on click.
     *
     * @param mouseX The cursor's X position.
     * @param mouseY The cursor's Y position.
     */
    void drawTooltip(int mouseX, int mouseY);
}
