package net.roguelogix.phosphophyllite.gui.client.api;

public interface IHasButton {
    
    /**
     * Check for and execute a click (if applicable/available).
     *
     * @param mouseX      The X position when clicked.
     * @param mouseY      The Y position when clicked.
     * @param mouseButton The button ID used. See GLFW.java#L213 for more.
     */
    void doClick(int mouseX, int mouseY, int mouseButton);
}
