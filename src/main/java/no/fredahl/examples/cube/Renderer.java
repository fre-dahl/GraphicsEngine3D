package no.fredahl.examples.cube;

import no.fredahl.engine.Camera;
import no.fredahl.engine.graphics.ShaderProgram;
import no.fredahl.engine.graphics.ShaderSource;
import no.fredahl.engine.utility.IO;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 * @author Frederik Dahl
 * 28/10/2021
 */


public class Renderer {
    
    private final ShaderProgram program;
    private final Matrix4f projectionMatrix;
    private final Matrix4f modelView;
    
    public Renderer() throws Exception{
    
        String vertexShaderPath = IO.projectPath("vertex.glsl","res","cube","shaders");
        String fragmentShaderPath = IO.projectPath("fragment.glsl","res","cube","shaders");
        ShaderSource fragmentShader = new ShaderSource(GL_FRAGMENT_SHADER,fragmentShaderPath);
        ShaderSource vertexShader = new ShaderSource(GL_VERTEX_SHADER,vertexShaderPath);
        program = new ShaderProgram();
        program.attach(fragmentShader,vertexShader);
        program.compile();
        program.link();
        program.bind();
    
        program.createUniform("projectionMatrix");
        program.createUniform("modelViewMatrix");
        program.createUniform("texture_sampler");
        
        projectionMatrix = new Matrix4f();
        modelView = new Matrix4f();
        glEnable(GL_DEPTH_TEST);
    
    }
    
    public void render(List<Cube> entities, Camera camera) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        program.bind();
        program.setUniform("texture_sampler", 0);
        camera.perspective(projectionMatrix);
        program.setUniform("projectionMatrix", projectionMatrix);
        for (Cube cube : entities) {
            camera.modelView(cube.transform.model(),modelView);
            program.setUniform("modelViewMatrix", modelView);
            cube.mesh.render();
        }
        program.unBind();
    }
    
    
    public void free() {
        if (program != null)
            program.delete();
    }
}