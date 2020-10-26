package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Ball {

    private static final String TAG = "Ball";
	private IntBuffer   mVertexBuffer;//缓冲顶点坐标数据
    private IntBuffer   mNormalBuffer;//缓冲顶点法向量数据
    private FloatBuffer mTextureBuffer;//缓冲顶点纹理数据
    public float mAngleX;//沿x转角度
    public float mAngleY;//沿y转的角度
    public float mAngleZ;//沿z转的角度
    int vCount=0;//设置的顶点数量
    int texId;//表示纹理的ID

    public Ball(int scale,int texId)
    {
        this.texId=texId;
        final int UNIT_SIZE=10000;
        final float angleSpan=11.25f;//切分球的角度

        //定义获取切分图的纹理数组
        float[] texCoorArray=
                generateTexCoor
                        (
                                (int)(360/angleSpan), //列数
                                (int)(180/angleSpan)  //行数
                        );
        int tc=0;//纹理数组计数器
        int ts=texCoorArray.length;//纹理数组长度

        ArrayList<Integer> alVertix=new ArrayList<Integer>();//存放顶点坐标的ArrayList
        ArrayList<Float> alTexture=new ArrayList<Float>();//存放纹理坐标的ArrayList

        for(float vAngle=90;vAngle>-90;vAngle=vAngle-angleSpan)//垂直方向angleSpan度一份
        {
            for(float hAngle=360;hAngle>0;hAngle=hAngle-angleSpan)//水平方向angleSpan度一份
            {
                //构建三角形的顶点和坐标
                double xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
                int x1=(int)(xozLength*Math.cos(Math.toRadians(hAngle)));
                int z1=(int)(xozLength*Math.sin(Math.toRadians(hAngle)));
                int y1=(int)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));

                xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
                int x2=(int)(xozLength*Math.cos(Math.toRadians(hAngle)));
                int z2=(int)(xozLength*Math.sin(Math.toRadians(hAngle)));
                int y2=(int)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));

                xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
                int x3=(int)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
                int z3=(int)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
                int y3=(int)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));

                xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
                int x4=(int)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
                int z4=(int)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
                int y4=(int)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));

                //构建第一三角形
                alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
                alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
                alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
                alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
                alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
                alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                
                //Log.i(TAG, "z1,z2,z3,z4: " + z1+"," + z2+"," + z3+"," + z4+",");
            }
        }
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        //将alVertix中的坐标值转存到一个int数组中
        int vertices[]=new int[vCount*3];
        for(int i=0;i<alVertix.size();i++)
        {
            vertices[i]=alVertix.get(i);
        }

        //创建绘制顶点数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asIntBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //创建顶点法向量数据缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = vbb.asIntBuffer();//转换为int型缓冲
        mNormalBuffer.put(vertices);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置

        //创建纹理坐标缓冲
        float textureCoors[]=new float[alTexture.size()];//顶点纹理值数组
        for(int i=0;i<alTexture.size();i++)
        {
            textureCoors[i]=alTexture.get(i);
        }

        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer = tbb.asFloatBuffer();//转换为float型缓冲
        mTextureBuffer.put(textureCoors);//向缓冲区中放入顶点纹理数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
    }

    public void drawSelf(GL10 gl)
    {
        gl.glRotatef(mAngleZ, 0, 0, 1);//沿Z旋转
        gl.glRotatef(mAngleX, 1, 0, 0);//沿X旋转
        gl.glRotatef(mAngleY, 0, 1, 0);//沿Y旋转

        //允许使用顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //为画笔指定顶点坐标数据
        gl.glVertexPointer
                (
                        3,
                        GL10.GL_FIXED,
                        0,
                        mVertexBuffer
                );

        //法向量数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        //为画笔指定顶点法向量数据
        gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);

        //打开纹理
        gl.glEnable(GL10.GL_TEXTURE_2D);
        //使用纹理ST坐标缓冲
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //使用纹理ST坐标缓冲
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        //绑定当前纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);

        //绘制图形
        gl.glDrawArrays
                (
                        GL10.GL_TRIANGLES,
                        0,
                        vCount
                );
    }

    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
        float[] result=new float[bw*bh*6*2];
        float sizew=1.0f/bw;//列数
        float sizeh=1.0f/bh;//行数
        int c=0;
        for(int i=0;i<bh;i++)
        {
            for(int j=0;j<bw;j++)
            {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s=j*sizew;
                float t=i*sizeh;

                result[c++]=s;
                result[c++]=t;

                result[c++]=s;
                result[c++]=t+sizeh;

                result[c++]=s+sizew;
                result[c++]=t;


                result[c++]=s+sizew;
                result[c++]=t;

                result[c++]=s;
                result[c++]=t+sizeh;

                result[c++]=s+sizew;
                result[c++]=t+sizeh;
            }
        }
        return result;
    }
}
