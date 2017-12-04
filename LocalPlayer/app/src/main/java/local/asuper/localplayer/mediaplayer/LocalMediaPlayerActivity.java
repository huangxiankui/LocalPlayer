package local.asuper.localplayer.mediaplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import local.asuper.localplayer.R;
import local.asuper.localplayer.view.DividerItemDecoration;
import local.asuper.localplayer.view.OnItemClickListener;
import local.asuper.localplayer.utils.VideoUtils;

public class LocalMediaPlayerActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context mContext;
    public VideoAdapter mAdapter;
    public ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        //异步加载   手机视频比较多时，主线程加载容易阻塞
        new LoadVideoAsyncTask("loadvideoasyncstask").execute();
    }

    private void initRecycleView(List<VideoBean> videoBeen) {
        /**
         * RecycleView配置
         */
        RecyclerView rececleView = (RecyclerView) findViewById(R.id.recycleview);
        rececleView.setLayoutManager(new LinearLayoutManager(this));
        rececleView.setHasFixedSize(true);
        //分割线
        rececleView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
         mAdapter = new VideoAdapter(videoBeen);
        mAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VideoBean video = mAdapter.getItem(position);
                //传递video 跳转 太大了  选择性传递
                if (video != null) {
                    PlayActivity.startActivity(mContext, video.getVideopath(), video.getTitle());
                } else {
                    Toast.makeText(mContext, "video is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rececleView.setAdapter(mAdapter);
    }
    public class LoadVideoAsyncTask extends AsyncTask<Void,Integer,List<VideoBean>> {
        private String name = "LoadVideoAsyncTask";
        public LoadVideoAsyncTask(String name) {
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"onPreExecute");
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<VideoBean> doInBackground(Void... params) {
            return VideoUtils.getList(mContext);
        }

        @Override
        protected void onPostExecute(List<VideoBean> videoBeen) {
            super.onPostExecute(videoBeen);
            mProgressBar.setVisibility(View.GONE);
            initRecycleView(videoBeen);

        }
    }
}
