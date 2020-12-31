package com.example.arcore_seo4_nhom121.renderer;


import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.ar.core.examples.java.helloar.rendering.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class LineRenderer {

    private final int mProgram;

    private final String vertexShaderCode =
            // Biến thành viên ma trận này cung cấp một hook để thao tác
            // tọa độ của các đối tượng sử dụng công cụ đổ bóng đỉnh này
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // ma trận phải được đưa vào làm công cụ sửa đổi của gl_Position
                    // Lưu ý rằng yếu tố uMVPMatrix * phải đứng đầu * theo thứ tự
                    // cho tích của phép nhân ma trận là đúng.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Sử dụng để truy cập và thiết lập chuyển đổi chế độ xem
    private int mMVPMatrixHandle;

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    public LineRenderer(int mProgram, int mMVPMatrixHandle) {
        this.mProgram = mProgram;
        this.mMVPMatrixHandle = mMVPMatrixHandle;
    }
    private FloatBuffer vertexBuffer;

    static final int COORDS_PER_VERTEX = 3;
    static float coordinates[] = { //theo thu tu nguoc chieu kim dong ho:
            0.0f,  0.0f, 0.0f, // point 1
            1.0f,  0.0f, 0.0f, // point 2
    };

    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = coordinates.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public LineRenderer() {

        ByteBuffer bb = ByteBuffer.allocateDirect(coordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();  //Tao phan dau cham dong tu ByteBuffer
        vertexBuffer.put(coordinates);  // them toa do vao FloatBuffer
        vertexBuffer.position(0);   // dat buffer de doc toa do dau tien

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();    //Tạo OpenGL ES trống
        GLES20.glAttachShader(mProgram, vertexShader);  //them shader vao chuong trinh
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram); // tao tep thuc thi chuong trinh OpenGL ES
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    public void setVerts(float v0, float v1, float v2, float v3, float v4, float v5) {
        coordinates[0] = v0;
        coordinates[1] = v1;
        coordinates[2] = v2;
        coordinates[3] = v3;
        coordinates[4] = v4;
        coordinates[5] = v5;

        vertexBuffer.put(coordinates);
        // dat buffer de doc toa do dau tien
        vertexBuffer.position(0);
    }

    public void setColor(float red, float green, float blue, float alpha) {
        color[0] = red;
        color[1] = green;
        color[2] = blue;
        color[3] = alpha;
    }

    private float[] mModelMatrix = new float[16];
    private float[] mModelViewMatrix = new float[16];
    private float[] mModelViewProjectionMatrix = new float[16];
    final String TAG = "Line";

    public void draw(float[] cameraView, float[] cameraPerspective) {
        ShaderUtil.checkGLError(TAG, "Before draw");

        // xay dung ModelView and ModelViewProjection matrix
        // tinh toan doi tuong va vi tri anh sang
        Matrix.multiplyMM(mModelViewMatrix, 0, cameraView, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mModelViewProjectionMatrix, 0, cameraPerspective, 0, mModelViewMatrix, 0);

        // Them chuong trinh vao moi truong OpenGL ES
        GLES20.glUseProgram(mProgram);
        ShaderUtil.checkGLError(TAG, "After glBindBuffer");

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        ShaderUtil.checkGLError(TAG, "After glBindBuffer");

        // xu ly dinh shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        ShaderUtil.checkGLError(TAG, "After glGetAttribLocation");

        // cho phep xu ly cac dinh
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        ShaderUtil.checkGLError(TAG, "After glEnableVertexAttribArray");

        // chuan bi data toa do
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        ShaderUtil.checkGLError(TAG, "After glVertexAttribPointer");

        // xu ly phan manh shader cua vColor
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // dat mau cho hinh
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        ShaderUtil.checkGLError(TAG, "After glUniform4fv");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // chuyen phep chieu va chuyen doi che do xem sang trinh do bong
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelViewProjectionMatrix, 0);
        ShaderUtil.checkGLError(TAG, "After glUniformMatrix4fv");

        // ve duong
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);
        ShaderUtil.checkGLError(TAG, "After glDrawArrays");

        // vo hieu hoa dinh Array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        ShaderUtil.checkGLError(TAG, "After draw");
    }
}
