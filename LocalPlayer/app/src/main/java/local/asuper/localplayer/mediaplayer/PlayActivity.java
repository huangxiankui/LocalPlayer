package local.asuper.localplayer.mediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import local.asuper.localplayer.R;

//参考自http://blog.csdn.net/u013366022/article/details/38536451

//参考自 http://blog.csdn.net/mark_yangs/article/details/48422619


//第三款实现思路   http://www.open-open.com/lib/view/open1495451042535.html#articleHeader4
public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlayActivity";
    public Button playBt, stopBt, pauseBt, repeatBt;
    public SeekBar mSeekBar;
    public SurfaceView mSurfaceView;
    public MediaPlayer mMediaplayer;
    public String mVideoPath;
    private int mCurrentPosition;
    private boolean isPlaying;
    public TextView mTVname;
    public Button mScreenBt;


    public static void startActivity(Context context, String path, String videoname) {
        context.startActivity(new Intent(context, PlayActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS).putExtra("path", path)
                .putExtra("videoname", videoname));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mVideoPath = getIntent().getStringExtra("path");
        mMediaplayer = new MediaPlayer();//Idle状态
        initView();
        initData();


    }

    private void initData() {
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                /**
                 * 当重新回到该视频应当视图的时候，调用该方法，获取到currentPosition，并从该currentPosition开始继续播放。
                 */
                if (mCurrentPosition > 0) {
                    playVideo(mVideoPath, mCurrentPosition);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                /**
                 * 当点击手机上home键（或其他使SurfaceView视图消失的键）时，调用该方法，获取到当前视频的播放值，currentPosition。
                 * 并停止播放。
                 */
                mCurrentPosition = mMediaplayer.getCurrentPosition();
                stop();

            }
        });
    }

    private void initView() {
        playBt = (Button) findViewById(R.id.button_play);
        stopBt = (Button) findViewById(R.id.button_stop);
        pauseBt = (Button) findViewById(R.id.button_pause);
        repeatBt = (Button) findViewById(R.id.button_replay);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mTVname = (TextView) findViewById(R.id.videoname);
        mScreenBt = (Button) findViewById(R.id.fullscreen);
        String videoname = getIntent().getStringExtra("videoname");
        mTVname.setText(videoname);
        playBt.setOnClickListener(this);
        stopBt.setOnClickListener(this);
        pauseBt.setOnClickListener(this);
        repeatBt.setOnClickListener(this);
        mScreenBt.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //该方法拖动进度条进度改变的时候调用
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            //该方法拖动进度条开始拖动的时候调用。
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch");
            }

            //该方法拖动进度条停止拖动的时候调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch");
                int progress = mSeekBar.getProgress();

                if (mMediaplayer != null && mMediaplayer.isPlaying()) {
                    mMediaplayer.seekTo(progress);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_play:
                play();
                break;
            case R.id.button_pause:
                pause();
                break;
            case R.id.button_replay:
                replay();
                break;
            case R.id.button_stop:
                stop();
                break;
            case R.id.fullscreen:
                setScreen();
                break;
            default:
                break;
        }
    }

    public void setScreen() {
        //surfaceview 全屏设置
        ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mSurfaceView.setLayoutParams(lp);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void play() {
        if (!mMediaplayer.isPlaying() && (mCurrentPosition == 0)) {
            playVideo(mVideoPath, 0);
            MediaPlayer.TrackInfo[] t = mMediaplayer.getTrackInfo();
            Log.d(TAG,"height:"+mMediaplayer.getVideoHeight() + "width:"+mMediaplayer.getVideoWidth());
        } else if(!mMediaplayer.isPlaying() && (mCurrentPosition != 0)){
            mMediaplayer.start();
            mMediaplayer.seekTo(mCurrentPosition);
        }
    }

    public void replay() {
        if (mMediaplayer.isPlaying()) {
            mMediaplayer.seekTo(0);
        } else {
            playVideo(mVideoPath, 0);
        }
    }

    public void stop() {
        if (mMediaplayer.isPlaying()) {
            mMediaplayer.stop();
            mMediaplayer.seekTo(0);
            mSeekBar.setProgress(0);
            mCurrentPosition = 0;
        }
    }

    public void pause() {
        if (mMediaplayer.isPlaying()) {
            mMediaplayer.pause();
            mCurrentPosition = mMediaplayer.getCurrentPosition();
        } else {
            mMediaplayer.start();
        }
    }

    public void playVideo(String videopath, final int currentposition) {
       // videopath = "http://videocdn.eebbk.net/1aa7a52533138714e43fb51191272dd5.avi";
        mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaplayer.setDisplay(mSurfaceView.getHolder());
        mSurfaceView.getHolder().setKeepScreenOn(true);
        try {
            Log.d(TAG, "videopath:" + videopath);
            mMediaplayer.setDataSource(videopath);
            mMediaplayer.prepareAsync();
            mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaplayer.start();
                    int max = mMediaplayer.getDuration();
                    mSeekBar.setMax(max);
                    mMediaplayer.seekTo(currentposition);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mMediaplayer.isPlaying() && mMediaplayer != null) {
                                int position = mMediaplayer.getCurrentPosition();
                                mSeekBar.setProgress(position);
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                }
            });
            mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion");
                    playBt.setEnabled(true);
                }
            });
            mMediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, "oonError");
                    playBt.setEnabled(true);
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void releaseMedia() {
        Log.d(TAG, "releaseMedia");
        if (mMediaplayer != null) {
            mMediaplayer.stop();
            mMediaplayer.release();
            mMediaplayer = null;
            mCurrentPosition = 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMedia();
    }
}
