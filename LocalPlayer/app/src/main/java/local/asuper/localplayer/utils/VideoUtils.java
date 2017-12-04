package local.asuper.localplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import local.asuper.localplayer.mediaplayer.VideoBean;

/**
 * @author hxk <br/>
 *         功能：
 *         创建日期   2017/6/16
 *         修改者：
 *         修改日期：
 *         修改内容:
 */

public class VideoUtils {

    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     *
     * @param milliseconds
     * @param format       格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符串
     */
    public static String getStringByFormat(long milliseconds, String format) {
        String thisDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            thisDateTime = mSimpleDateFormat.format(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thisDateTime;
    }



    public static List<VideoBean> getList(Context context) {
        List<VideoBean> list = null;
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                list = new ArrayList<VideoBean>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    Log.d("VideoUtils", "id:" + id + " \ntitle:" + title + " " + "\n album:" + album + " \nartist:" + artist + "\ndisplayName:" + displayName + "\nmimeType:" + mimeType + "\npath:" + path + "\nduraction:" + duration);
                    VideoBean video = new VideoBean(displayName, path, calculateSize(size), getVideoThumbnail(context, context.getContentResolver(), path));
                    list.add(video);
                }
                cursor.close();
            }
        }
        return list;
    }

    public static String calculateSize(long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        String fileSizeString = "";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;

    }

    public static Bitmap getVideoThumbnail(Context context, ContentResolver cr, String Videopath) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID,};
        String whereClause = MediaStore.Video.Media.DATA + " = '" + Videopath + "'";
        Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        String videoPath = "";
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {

            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            do {
                _id = cursor.getInt(_idColumn);
                videoPath = cursor.getString(_dataColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, _id, MediaStore.Images.Thumbnails.MINI_KIND,
                options);
        return bitmap;
    }


}
