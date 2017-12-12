package local.asuper.localplayer.mediacodec;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import local.asuper.localplayer.R;
import local.asuper.localplayer.camera.Camear_MainActivity;
import local.asuper.localplayer.mediacodec.others.MediaExtractorManager;
import local.asuper.localplayer.mediacodec.others.OthersActivity;
import local.asuper.localplayer.mediaplayer.MediaPlayActivity;
import local.asuper.localplayer.utils.FileUtils;

public class Main_MediaCodec_Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "hxk_Main_MediaCodec";
    Button mPcmtoAACBt, mMP3toPCMBt, mYUVtoH264Bt, mOthersYUVtoH264Bt, mMP4ExtractorBt, mstart_YUV_H264Bt;
    AACAudioEncoder aacencoder;
    H264VideoEncoder h264encoder;
    private MediaExtractorManager mMediaExtractorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__media_codec_);
        mPcmtoAACBt = (Button) findViewById(R.id.start_mediacodec_pcmtoaac);
        mMP3toPCMBt = (Button) findViewById(R.id.start_mediacodec_mp3topcm);
        mYUVtoH264Bt = (Button) findViewById(R.id.start_mediacodec_yuvtoh264);
        mOthersYUVtoH264Bt = (Button) findViewById(R.id.start_mediacodec_others_yuvtoh264);
        mMP4ExtractorBt = (Button) findViewById(R.id.start_mp4extractor_h264_aac);
        mstart_YUV_H264Bt = (Button) findViewById(R.id.start_YUV_H264);
        mPcmtoAACBt.setOnClickListener(this);
        mMP3toPCMBt.setOnClickListener(this);
        mYUVtoH264Bt.setOnClickListener(this);
        mOthersYUVtoH264Bt.setOnClickListener(this);
        mMP4ExtractorBt.setOnClickListener(this);
        mstart_YUV_H264Bt.setOnClickListener(this);
        mMediaExtractorManager = new MediaExtractorManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.scanFile(getApplicationContext(), "/sdcard");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_mediacodec_pcmtoaac://pcm编码成aac
                aacencoder = new AACAudioEncoder(16000, 1);
                File file = new File(Environment.getExternalStorageDirectory(),
                        "new16k.aac");

                String aacfilepath = file.getAbsolutePath();
                byte[] buffer = new byte[1024];
                byte[] outBuffer = new byte[1024];
                InputStream in = null;
                try {
                    File file1 = new File(Environment.getExternalStorageDirectory(),
                            "test16k.pcm");
                    if (file1 == null) {
                        Log.e(TAG, "file1 is null");
                        return;
                    }
                    String filepath = file1.getAbsolutePath();
                    in = new FileInputStream(filepath);
                    while ((in.read(buffer)) != -1) {
                        int size = aacencoder.encode(buffer, outBuffer);
                        if (size > 0) {
                            try {
                                FileUtils.createFile(aacfilepath, outBuffer, size);
                            } catch (IOException e) {
                                Log.e(TAG, "createFile failed");
                                e.printStackTrace();
                            }
                        }
                    }
                    aacencoder.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.start_mediacodec_mp3topcm://MP3解码成pcm
                break;
            case R.id.start_mediacodec_yuvtoh264://yuv编码成h264文件
                h264encoder = new H264VideoEncoder("video/avc", 256000, 24, 1, 320, 180);
                try {
                    h264encoder.init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File file_h264 = new File(Environment.getExternalStorageDirectory(),
                        "new.h264");
                String filepath_h264 = file_h264.getAbsolutePath();
                byte[] buffer_yuv = new byte[1024];
                byte[] outBuffer_h264 = new byte[1024];
                InputStream in_yuv = null;
                try {
                    File file_yuv = new File(Environment.getExternalStorageDirectory(),
                            "test_w320_h180.yuv");
                    if (file_yuv == null) {
                        Log.e(TAG, "file1 is null");
                        return;
                    }
                    String filepath_yuv = file_yuv.getAbsolutePath();
                    in_yuv = new FileInputStream(filepath_yuv);
                    while ((in_yuv.read(buffer_yuv)) != -1) {
                        int size = h264encoder.startencode(buffer_yuv, outBuffer_h264);
                        if (size > 0) {
                            try {
                                FileUtils.createFile(filepath_h264, outBuffer_h264, size);
                            } catch (IOException e) {
                                Log.e(TAG, "createFile failed");
                                e.printStackTrace();
                            }
                        }
                    }
                    h264encoder.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.start_mediacodec_others_yuvtoh264:
                startActivity(new Intent(this, OthersActivity.class));

            case R.id.start_mp4extractor_h264_aac://mp4 分离器 分离成h264  aac

                String sdcardpath = Environment.getExternalStorageDirectory().getPath();
                Log.d(TAG, "sdcardpath:" + sdcardpath);
                MP4VideoExtractor.exactorMedia(sdcardpath);
                break;
            case R.id.start_YUV_H264:
                startActivity(new Intent(this, Camear_MainActivity.class));
                break;
            default:
                break;
        }
    }
}
