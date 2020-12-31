package com.example.arcore_seo4_nhom121.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.ar.core.examples.java.helloar.rendering.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;



public class RectanglePolygonRenderer {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    // số tọa độ trong mảng
    static final int COORDS_PER_VERTEX = 3;
    static float coords[] = {
            -0.6f,  0.5f, 0.2f,   // trên cùng bên trái
            -0.4f, -0.5f, 0.2f,   // dưới cùng bên trái
            0.5f, -0.5f, 0.2f,   // dưới cùng bên phải
            0.5f,  0.5f, 0.2f,   // trên cùng bên phải

            -0.5f,  0.6f, 0.0f,   // trên cùng bên trái
            -0.5f, -0.8f, 0.0f,   // dưới cùng bên trái
            0.5f, -0.5f, 0.0f,   // dưới cùng bên phải
            0.5f,  0.5f, 0.0f   // trên cùng bên phải
    };
    private short drawOrder[] = {
            0, 1, 2,  0, 2, 3,
            3, 2, 6,  3, 6, 7,
            4, 5, 6,  4, 6, 7,
            0, 1, 5,  0, 5, 4,
            4, 0, 3,  4, 3, 7,
            5, 1, 2,  5, 2, 6
    }; // để vẽ đỉnh

    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public void setVerts(float v0, float v1, float v2,
                         float v3, float v4, float v5,
                         float v6, float v7, float v8,
                         float v9, float v10, float v11,

                         float v12, float v13, float v14,
                         float v15, float v16, float v17,
                         float v18, float v19, float v20,
                         float v21, float v22, float v23
    ) {
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

        coords[12] = v12;
        coords[13] = v13;
        coords[14] = v14;

        coords[15] = v15;
        coords[16] = v16;
        coords[17] = v17;

        coords[18] = v18;
        coords[19] = v19;
        coords[20] = v20;

        coords[21] = v21;
        coords[22] = v22;
        coords[23] = v23;

        vertexBuffer.put(coords);
        // đặt bộ đệm để đọc tọa độ đầu tiên
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

    public RectanglePolygonRenderer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);


        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // tạo Chương trình OpenGL ES trống
        mProgram = GLES20.glCreateProgram();

        // thêm shader vào chương trình
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // tạo các tệp thực thi chương trình OpenGL ES
        GLES20.glLinkProgram(mProgram);
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    // Ma trận tạm thời được phân bổ ở đây để giảm số lượng phân bổ cho mỗi khung.
    private float[] mModelMatrix = new float[16];
    private float[] mModelViewMatrix = new float[16];
    private float[] mModelViewProjectionMatrix = new float[16];
    final String TAG = "RectanglePolygon";

    public void draw(float[] cameraView, float[] cameraPerspective) {
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

        // nhận xử lý để phân mảnh vColor của shader
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // đặt màu để vẽ hình
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // xử lý ma trận chuyển đổi của hình dạng
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        //Chuyển phép chiếu và chuyển đổi chế độ xem sang trình đổ bóng
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mModelViewProjectionMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // gỡ mảng
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
