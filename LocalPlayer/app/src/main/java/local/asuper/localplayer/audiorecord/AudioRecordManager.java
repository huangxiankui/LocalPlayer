package local.asuper.localplayer.audiorecord;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/11/27.
 */

public class AudioRecordManager {
    private int mSampleRate;//采样率
    private int mChannelCount;//通道
    private int mAudioFormat;//格式
    private int bufferSize;
    byte[] buffer;
    AudioRecord mRecorder;
    private FileOutputStream mFos; //用于保存录音文件
    volatile boolean isRecording = false;

    public AudioRecordManager(int sampleRate, int channelCount, int AudioFormat) {
        mSampleRate = sampleRate;
        mChannelCount = channelCount;
        mAudioFormat = AudioFormat;
        //音频录制实例化和录制过程中需要用到的数据
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, mChannelCount, mAudioFormat) * 2;
        buffer = new byte[bufferSize];
        //实例化AudioRecord
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, mChannelCount,
                mAudioFormat, bufferSize);
    }

    public void startRecord() throws IOException {
        //开始录制
        isRecording = true;
        mRecorder.startRecording();
        while (isRecording) {
            //循环读取数据到buffer中，并保存buffer中的数据到文件中
            int length = mRecorder.read(buffer, 0, bufferSize);
            mFos.write(buffer, 0, length);
        }
    }

    public void closeRecord() {
        //中止循环并结束录制
        isRecording = false;
        mRecorder.stop();
    }
}
