package local.asuper.localplayer.mediaplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import local.asuper.localplayer.R;

/**
 * MediaPlayer 相关
 */

public class MediaPlayActivity extends AppCompatActivity implements View.OnClickListener {
    Button mStart_localmusic, mStart_netmusic, mStart_localVideo, mStart_netVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);
        mStart_localmusic = (Button) findViewById(R.id.start_local_music);
        mStart_netmusic = (Button) findViewById(R.id.start_net_music);
        mStart_localVideo = (Button) findViewById(R.id.start_local_video);
        mStart_netVideo = (Button) findViewById(R.id.start_net_video);
        mStart_localmusic.setOnClickListener(this);
        mStart_netmusic.setOnClickListener(this);
        mStart_localVideo.setOnClickListener(this);
        mStart_netVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_local_music:
                break;
            case R.id.start_net_music:
                break;
            case R.id.start_local_video:
                startActivity(new Intent(this, LocalMediaPlayerActivity.class));
                break;
            case R.id.start_net_video:
                break;
            default:
                break;
        }
    }
}