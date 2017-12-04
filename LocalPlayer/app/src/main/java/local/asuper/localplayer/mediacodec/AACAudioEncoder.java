package local.asuper.localplayer.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2017/11/24.
 * pcm 编码成AAC（加adts头）
 */


public class AACAudioEncoder {
    private static final String TAG = "AACAudioEncoder";
    private MediaCodec mediaCodec;
    private ByteBuffer[] inputBuffers = null;
    private ByteBuffer[] outputBuffers = null;
    private int mSampleRate;
    private int mChannelCount = 1;
    private boolean mIsHeaderAdded = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public AACAudioEncoder(int sampleRate, int channelCount) {
        mSampleRate = sampleRate;
        mChannelCount = channelCount;
        final int kBitRates[] = {64000, 96000, 128000};
        String encodeType = MediaFormat.MIMETYPE_AUDIO_AAC;
        try {
            //创建编码器
            mediaCodec = MediaCodec.createEncoderByType(encodeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString(MediaFormat.KEY_MIME, encodeType);
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelCount);
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, kBitRates[1]);
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);// profile AACObjectLC
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 2 * 1024);//MediaCodec state:Uninitialized
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);//MediaCodec state:Configured
        mediaCodec.start();//MediaCodec state:Executing
        inputBuffers = mediaCodec.getInputBuffers();
        outputBuffers = mediaCodec.getOutputBuffers();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public synchronized int encode(byte[] input, byte[] output) {
        int inputBufferIndex;
        try {
            //请求一个输入缓存，timeoutUs代表等待时间，设置为-1代表无限等待
            inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);//出队
        } catch (IllegalStateException e) {
            return -1;
        }

        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();//清除缓存
            inputBuffer.limit(input.length);
            inputBuffer.put(input);//PCM数据填充给inputBuffer
            //接着就是把数据添加到输入缓存中，并调用queueInputBuffer(...)把缓存数据入队
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);//通知编码器 编码  入队
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex;
        try {
            //获取输出缓存和获取输入缓存类似，首先通过dequeueOutputBuffer(BufferInfo info, long timeoutUs)来请求一个输出缓存，
            // 这里需要传入一个BufferInfo对象，用于存储ByteBuffer的信息
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        } catch (IllegalStateException e) {
            return -1;
        }

        ByteBuffer outputBuffer;
        int outputIndex = 0;

        int outputSize = output.length;

        while (outputBufferIndex >= 0) {
            int outBitsSize = bufferInfo.size;
            Log.d(TAG, "outBitsSize:" + outBitsSize);
            int outPacketSize = outBitsSize + 7; // 7 is ADTS size
            outputBuffer = outputBuffers[outputBufferIndex];
            outputBuffer.position(bufferInfo.offset);
            outputBuffer.limit(bufferInfo.offset + outBitsSize);
            byte[] encodeData = new byte[outPacketSize];
            addADTStoPacket(encodeData, outPacketSize);
            outputBuffer.get(encodeData, 7, outBitsSize);
            outputBuffer.position(bufferInfo.offset);
            Log.d(TAG, "bufferInfo.offset:" + bufferInfo.offset);
            for (int j = 0; j < outPacketSize; j++) {
                if (outputIndex >= outputSize) {
                    break;
                }
                output[outputIndex] = encodeData[j];
                outputIndex++;
            }
            try {
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                //为什么这里又有一个duqueueoutputbuffer
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            } catch (IllegalStateException e) {
                return -1;
            }
        }
        return outputIndex;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
     * Add ADTS header at the beginning of each and every AAC packet. This is
     * needed as MediaCodec encoder generates a packet of raw AAC data.
     * Note the packetLen must count in the ADTS header itself.
     **/
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = getFreqIdx(mSampleRate);
        int chanCfg = mChannelCount; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    private int getFreqIdx(int sampleRate) {
        int freqIdx;
        switch (sampleRate) {
            case 96000:
                freqIdx = 0;
                break;
            case 88200:
                freqIdx = 1;
                break;
            case 64000:
                freqIdx = 2;
                break;
            case 48000:
                freqIdx = 3;
                break;
            case 44100:
                freqIdx = 4;
                break;
            case 32000:
                freqIdx = 5;
                break;
            case 24000:
                freqIdx = 6;
                break;
            case 22050:
                freqIdx = 7;
                break;
            case 16000:
                freqIdx = 8;
                break;
            case 12000:
                freqIdx = 9;
                break;
            case 11025:
                freqIdx = 10;
                break;
            case 8000:
                freqIdx = 11;
                break;
            case 7350:
                freqIdx = 12;
                break;
            default:
                freqIdx = 8;
                break;
        }

        return freqIdx;
    }

}
