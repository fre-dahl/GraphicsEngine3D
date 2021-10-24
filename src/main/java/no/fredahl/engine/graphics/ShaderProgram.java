package no.fredahl.engine.graphics;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author Frederik Dahl
 * 17/10/2021
 */


public class ShaderProgram {
    
    private final int program;
    private final Map<String,Integer> uniforms;
    
    
    public ShaderProgram() throws Exception {
        program = glCreateProgram();
        if (program == GL_FALSE)
            throw new Exception("Could not create program");
        uniforms = new HashMap<>();
    }
    
    
    public void attach(ShaderSource... shaders) {
        for (ShaderSource source : shaders) {
            int shader = glCreateShader(source.type());
            glShaderSource(shader,source.get());
            glAttachShader(program, shader);
        }
    }
    
    public void compile() throws Exception {
        final int[] count = {0};
        final int[] shaders = new int[16];
        glGetAttachedShaders(program,count,shaders);
        try {
            for (int i = 0; i < count[0]; i++) {
                final int shader = shaders[i];
                glCompileShader(shader);
                int status = glGetShaderi(shader, GL_COMPILE_STATUS);
                if (status == GL_FALSE) {
                    throw new Exception(glGetShaderInfoLog(shader));
                }
            }
        }catch (Exception e) {
            disposeShaders();
            throw new Exception(e);
        }
    }
    
    public void link() throws Exception {
        int status = glGetProgrami(program, GL_LINK_STATUS);
        int attachedCount = glGetProgrami(program,GL_ATTACHED_SHADERS);
        if (status == GL_TRUE || attachedCount == 0) {
            System.out.println("Program already linked OR no shaders attached");
            return;
        }
        try {
            glLinkProgram(program);
            status = glGetProgrami(program, GL_LINK_STATUS);
            if (status == GL_FALSE)
                throw new Exception("Failed to link shaders: \n"
                + glGetProgramInfoLog(program));
        } catch (Exception e) {
            delete();
            throw new Exception(e.getMessage());
        }
        disposeShaders();
        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(program,512));
        }
    }
    
    private void disposeShaders() {
        int[] count = {0};
        int[] shaders = new int[16];
        glGetAttachedShaders(program,count,shaders);
        for (int i = 0; i < count[0]; i++) {
            final int shader = shaders[i];
            glDetachShader(program,shader);
            glDeleteShader(shader);
        }
    }
    
    public void createUniform(String name) {
        int uniformLocation = glGetUniformLocation(program, name);
        if (uniformLocation < 0)
            throw new RuntimeException("No such uniform:" + name);
        uniforms.put(name, uniformLocation);
    }
    
    public void setUniform(String name, Vector2f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            buffer.put(value.x).put(value.y);
            buffer.flip();
            glUniform2fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            buffer.put(value.x).put(value.y).put(value.z);
            buffer.flip();
            glUniform3fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Vector4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            buffer.put(value.x).put(value.y).put(value.z).put(value.w);
            buffer.flip();
            glUniform4fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Matrix3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(9);
            value.get(buffer);
            glUniformMatrix3fv(uniforms.get(name), false, buffer);
        }
    }
    
    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(uniforms.get(name), false, buffer);
        }
    }
    
    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }
    
    public void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }
    
    public void setUniform(String name, int[] array) {
        glUniform1iv(uniforms.get(name),array);
    }
    
    public void setUniform(String name, IntBuffer buffer) {
        glUniform1iv(uniforms.get(name),buffer);
    }
    
    public void bind() {
        glUseProgram(program);
    }
    
    public void unBind() {
        glUseProgram(0);
    }
    
    public void delete() {
        unBind();
        if (glGetProgrami(program,GL_ATTACHED_SHADERS) > 0)
            disposeShaders();
        if (glGetProgrami(program,GL_DELETE_STATUS) == GL_FALSE)
            glDeleteProgram(program);
    }
}