package local.asuper.localplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/11/24.
 * 文件操作类
 */

public class FileUtils {
    private static final String TAG = "FileUtils";
    /**
     * 通知媒体库更新文件
     * @param context
     * @param filePath 文件全路径
     *
     * */
    public static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }



    /**
     * 将byte数组写入文件
     */
    public  static void createFile(String path, byte[] content, int size) throws IOException {
        FileOutputStream fos = new FileOutputStream(path, true);//true  不覆盖  添加到末尾
        byte[] newarray = new byte[size];
        for (int i = 0; i < size; i++) {
            newarray[i] = content[i];
        }
        fos.write(newarray);
        fos.close();
    }

    /**
     * 获取文件路径
     */
    public static String getilePath() {
        String filepath = "";
        File file = new File(Environment.getExternalStorageDirectory(),
                "test11.pcm");
        if (file.exists()) {
            Log.d(TAG, "file  exist!");
            filepath = file.getAbsolutePath();
        } else {
            Log.d(TAG, "file is not exist!");
        }
        return filepath;
    }

    /**
     * 把pcm转成byte[]
     */
    private byte[] file_to_bytearray(String filepath) throws IOException {
        InputStream in = new FileInputStream(filepath);
        byte[] pcmdata = toByteArray(in);
        in.close();
        return pcmdata;
    }

    private byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

}
