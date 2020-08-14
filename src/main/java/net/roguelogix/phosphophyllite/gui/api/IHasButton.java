package net.roguelogix.phosphophyllite.gui.api;

public interface IHasButton {
    
    /**
     * Check if a button is pressed
     *
     * @param button The button to test for.
     * @return True if the button is pressed
     */
    boolean isPressed(int button);
    
    /**
     * Perform an action on click.
     *
     * @param mouseX      The X position when clicked.
     * @param mouseY      The Y position when clicked.
     * @param mouseButton The button ID used. See GLFW.java#L213 for more.
     */
    void onClick(int mouseX, int mouseY, int mouseButton);
}
