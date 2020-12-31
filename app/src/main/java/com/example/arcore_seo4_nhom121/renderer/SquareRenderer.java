package com.example.arcore_seo4_nhom121.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.ar.core.examples.java.helloar.rendering.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class SquareRenderer {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    //số tọa độ trong mảng này
    static final int COORDS_PER_VERTEX = 3;
    static float coords[] = {
            -0.05f,  0.05f, 0.0f,   // trên cùng bên trái
            -0.05f, -0.05f, 0.0f,   // dưới cùng bên trái
            0.05f, -0.05f, 0.0f,   // dưới cùng bên phải
            0.05f,  0.05f, 0.0f }; // trên cùng bên phải
    private short drawOrder[] = {0, 1, 2, 0, 2, 3};
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public void setVerts(float v0, float v1, float v2,
                         float v3, float v4, float v5,
                         float v6, float v7, float v8,
                         float v9, float v10, float v11) {
        coords[0] = v0;
        coords[1] = v1;
        coords[2] = v2;

        coords[3] = v3;
        coords[4] = v4;
        coords[5] = v5;

        coords[6] = v6;
        coords[7] = v7;
        coords[8] = v8;

        coords[9] = v9;
        coords[10] = v10;
        coords[11] = v11;

        vertexBuffer.put(coords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }

    public void setColor(float red, float green, float blue, float alpha) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
    }



    private final int mProgram;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Sử dụng để truy cập và đặt chuyển đổi chế độ xem
    private int mMVPMatrixHandle;

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = coords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public SquareRenderer(){

        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);


        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);   // 2 bytes per short
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);



        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //tạo Chương trình OpenGL ES trống
        mProgram = GLES20.glCreateProgram();

        // thêm shader vào chương trình
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        //tạo các tệp thực thi chương trình OpenGL ES
        GLES20.glLinkProgram(mProgram);
        Matrix.setIdentityM(mModelMatrix, 0);
    }


    private float[] mModelMatrix = new float[16];
    private float[] mModelViewMatrix = new float[16];
    private float[] mModelViewProjectionMatrix = new float[16];
    final String TAG = "Rectangle";

    public void draw(float[] cameraView, float[] cameraPerspective) { // pass in the calculated transformation matrix

        ShaderUtil.checkGLError(TAG, "Before draw");


        // Xây dựng ma trận ModelView và ModelViewProjection
        // để tính toán vị trí đối tượng và ánh sáng.
        Matrix.multiplyMM(mModelViewMatrix, 0, cameraView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mModelViewProjectionMatrix, 0, cameraPerspective, 0, mModelViewMatrix, 0);

        // thêm chương trình vào môi trường OpenGL ES
        GLES20.glUseProgram(mProgram);


        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // cho phép xử lý các đỉnh
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // chuẩn bị dữ liệu tọa độ
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);


        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // đặt màu để vẽ hình
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        //xử lý ma trận
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Chuyển phép chiếu và chuyển đổi chế độ xem sang trình đổ bóng
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelViewProjectionMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Gỡ mảng
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
