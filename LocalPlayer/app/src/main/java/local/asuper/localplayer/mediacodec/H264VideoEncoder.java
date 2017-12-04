package local.asuper.localplayer.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2017/11/28.
 * MediaCodec
 * YUV 编码成 H264视频流
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class H264VideoEncoder {
    private final static String TAG = "H264VideoEncoder";
    private String mMime;//编码的MIME
    private int mRate;//波特率 256kb
    private int mFrameRate;//帧率
    private int mFrameInterval;//关键帧一秒一关键帧
    private int mWidth;//视频宽度
    private int mHeight;//视频高度
    private MediaCodec mediaCodec;
    private int COLOR_Format = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
    MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    public H264VideoEncoder(String mime, int rate, int framerate, int frameinterval, int width, int height) {
        mMime = mime;
        mRate = rate;
        mFrameRate = framerate;
        mFrameInterval = frameinterval;
        mWidth = width;
        mHeight = height;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void init() throws IOException {
        //和音频编码一样，设置编码格式，获取编码器实例
        MediaFormat format = MediaFormat.createVideoFormat(mMime, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mRate);//码率 600kbps-5000kbps，根据分辨率、网络情况而定
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);//帧率 15 — 30 fps
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, mFrameInterval);////关键帧时间间隔，即编码一次关键帧的时间间隔
        //这里需要注意，为了简单这里是写了个固定的ColorFormat
        //实际上，并不是所有的手机都支持COLOR_FormatYUV420Planar颜色空间
        //所以正确的做法应该是，获取当前设备支持的颜色空间，并从中选取
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, COLOR_Format);
        mediaCodec = MediaCodec.createEncoderByType(mMime);
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);//配置编码器
        mediaCodec.start();
    }

    /**
     * @param input  YUV数据
     * @param output H264数据
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public int startencode(byte[] input, byte[] output) {
        ByteBuffer[] inputbuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputbuffers = mediaCodec.getOutputBuffers();
        /************************输入***********************/
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        ByteBuffer inputBuffer = null;
        if (inputBufferIndex >= 0) {
            inputBuffer = inputbuffers[inputBufferIndex];
        }
        inputBuffer.clear();
        inputBuffer.put(input);
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, getPTSUs(), 0);
        /********************输出**************************/
        int outputsize = 0;
        int outputBufferIndex;
        ByteBuffer outputBuffer = null;
        // 返回一个输出缓存区句柄，当为-1时表示当前没有可用的输出缓存区
        // mBufferInfo参数包含被编码好的数据，timesOut参数为超时等待的时间
        outputBufferIndex = mediaCodec.dequeueOutputBuffer(mBufferInfo, 0);
        while (outputBufferIndex > -1) {

            outputBuffer = outputbuffers[outputBufferIndex];
            // 根据NALU类型判断关键帧
            int type = outputBuffer.get(4) & 0x1F;//00011111
            if (type == 7 || type == 8) {
                Log.i(TAG, "------PPS、SPS帧(非图像数据)，忽略-------");
            } else if (type == 5) {
                Log.i(TAG, "------I帧(关键帧)，添加到混合器-------");
            } else {
                Log.d(TAG, "------非I帧(type=1)，添加到混合器-------");
            }
            outputsize = mBufferInfo.size;
            for (int i = 0; i < outputsize; i++) {
                output[i] = outputBuffer.get(i);
            }
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
        }
        return outputsize;
    }

    public void close() {
        Log.d(TAG, "close!");
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取pts显示时间
     *
     * @return
     */
    long prePresenttationTimes = mBufferInfo.presentationTimeUs;

    private long getPTSUs() {
        long result = System.nanoTime() / 1000;
        if (result < prePresenttationTimes) {
            //没明白这样写为了什么，copy自：http://blog.csdn.net/AndrExpert/article/details/72523408?locationNum=11&fps=1
            result = (prePresenttationTimes - result) + result;
        }
        return result;

    }


    /**
     * RGBA转YUV的方法，这是最简单粗暴的方式，在使用的时候，一般不会选择在Java层，用这种方式做转换
     *
     * @param rgba
     * @param width
     * @param height
     * @param yuv
     */
    private void rgbaToYuv(byte[] rgba, int width, int height, byte[] yuv) {
        final int frameSize = width * height;
        int yIndex = 0;
        int uIndex = frameSize;
        int vIndex = frameSize + frameSize / 4;
        int R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                index = j * width + i;
                if (rgba[index * 4] > 127 || rgba[index * 4] < -128) {
                    Log.e("color", "-->" + rgba[index * 4]);
                }
                R = rgba[index * 4] & 0xFF;
                G = rgba[index * 4 + 1] & 0xFF;
                B = rgba[index * 4 + 2] & 0xFF;
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
                yuv[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv[uIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                    yuv[vIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                }
            }
        }
    }
}
