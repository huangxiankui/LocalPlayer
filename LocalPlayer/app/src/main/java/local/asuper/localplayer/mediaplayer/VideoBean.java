package local.asuper.localplayer.mediaplayer;

import android.graphics.Bitmap;

/**
 * @author hxk <br/>
 *         功能：
 *         创建日期   2017/6/16
 *         修改者：
 *         修改日期：
 *         修改内容:
 */

public class VideoBean {

    public String title;
    public String sizet;
    private Bitmap icon;
    private String videopath;

    public VideoBean(String title, String path, String sizet, Bitmap icon) {
        this.title = title;
        this.icon = icon;
        this.sizet = sizet;
        this.videopath = path;
    }


    public String getVideopath() {
        return videopath;
    }

    public void setVideopath(String videopath) {
        this.videopath = videopath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return sizet;
    }

    public void setSizet(String sizet) {
        this.sizet = sizet;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
