package no.fredahl.engine.window.events;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class MousePosition extends GLFWCursorPosCallback {
    
    private final Vector2d position = new Vector2d();
    
    @Override
    public void invoke(long window, double x, double y) {
        position.set(x,y);
    }
    
    public double x() {
        return position.x;
    }
    
    public double y() {
        return position.y;
    }
}