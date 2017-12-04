package local.asuper.localplayer.mediaplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import local.asuper.localplayer.R;
import local.asuper.localplayer.view.OnItemClickListener;

/**
 * @author hxk <br/>
 *         功能：
 *         创建日期   2017/6/16
 *         修改者：
 *         修改日期：
 *         修改内容:
 */

public class VideoAdapter extends RecyclerView.Adapter {

    List<VideoBean> mVideos;
    private Context mContext;
    OnItemClickListener mOnItemClickListener;


    private void setContext(Context context) {
        this.mContext = context;
    }

    public VideoAdapter(List<VideoBean> mVideos) {
        this.mVideos = mVideos;
    }


    public void setmOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.videoitem, parent, false);
        return new VideoBeanHolder(itemview);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final VideoBeanHolder videoViewHolder = (VideoBeanHolder) holder;
        final VideoBean video = getItem(position);
        if (video != null) {
            videoViewHolder.title.setText(video.getTitle());
            videoViewHolder.size.setText(video.getSize());
            videoViewHolder.icon.setImageBitmap(video.getIcon());
        }
        if (mOnItemClickListener != null) {
            videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = videoViewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(videoViewHolder.itemView, pos);
                }
            });
        }
    }

    public VideoBean getItem(int position) {
        return mVideos.get(position);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }
}
