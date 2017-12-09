package local.asuper.localplayer.mediacodec;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * mp4extractor 分离器
 * 分离mp4 成h264 和 aac
 * 分别生成h264和aac文件
 */


public class MP4VideoExtractor {
    private static final String TAG = "MP4VideoExtractor";
//test3.mp4  h264,aac
    public static void exactorMedia(String sdcard_path) {
        FileOutputStream videoOutputStream = null;
        FileOutputStream audioOutputStream = null;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            //分离的视频文件
            File videoFile = new File(sdcard_path, "output_video.h264");
            //分离的音频文件
            File audioFile = new File(sdcard_path, "output_audio.aac");
            videoOutputStream = new FileOutputStream(videoFile);
            audioOutputStream = new FileOutputStream(audioFile);
            //输入文件,也可以是网络文件
            mediaExtractor.setDataSource(sdcard_path + "/test3.mp4");
            //信道总数
            int trackCount = mediaExtractor.getTrackCount();
            Log.d(TAG, "trackCount:" + trackCount);
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                //视频信道
                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i;
                }
                //音频信道
                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i;
                }
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(10 * 1024 * 1024);
            //切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                Log.d(TAG, "video:readSampleCount:" + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                //保存视频信道信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }
            //切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                Log.d(TAG, "audio:readSampleCount:" + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                //保存音频信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                audioOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "mediaExtractor.release!\n");
            mediaExtractor.release();
            try {
                videoOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
