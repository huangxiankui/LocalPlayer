package local.asuper.localplayer.mediaplayer;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import local.asuper.localplayer.R;

/**
 * @author hxk <br/>
 *         功能：
 *         创建日期   2017/6/16
 *         修改者：
 *         修改日期：
 *         修改内容:
 */

public class VideoBeanHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView size;
    public ImageView icon;

    public VideoBeanHolder(View itemView) {
        super(itemView);
        size = (TextView) itemView.findViewById(R.id.size);
        title = (TextView) itemView.findViewById(R.id.title);
        icon = (ImageView) itemView.findViewById(R.id.icon);
    }
}
