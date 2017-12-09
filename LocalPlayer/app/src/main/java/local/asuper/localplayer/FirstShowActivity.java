package local.asuper.localplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

import local.asuper.localplayer.mediacodec.Main_MediaCodec_Activity;
import local.asuper.localplayer.mediaplayer.MediaPlayActivity;

/**
 * 首页
 */
public class FirstShowActivity extends AppCompatActivity implements View.OnClickListener {
    Button mAbout_mediacodec, mAbout_mediaplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_show);
        mAbout_mediaplay = (Button) findViewById(R.id.about_mediaplay);
        mAbout_mediacodec = (Button) findViewById(R.id.about_mediacodec);
        mAbout_mediaplay.setOnClickListener(this);
        mAbout_mediacodec.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.about_mediaplay:
                startActivity(new Intent(this,MediaPlayActivity.class));
                break;
            case R.id.about_mediacodec:
                startActivity(new Intent(this,Main_MediaCodec_Activity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        scanFile(getApplicationContext(), "/sdcard");
        super.onDestroy();
    }
    public static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }
}
