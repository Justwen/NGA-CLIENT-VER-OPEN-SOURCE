package gov.anzong.androidnga;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

import gov.anzong.androidnga.common.util.ForumUtils;

/**
 * Created by liuboyu on 2015/8/25.
 */
public class Utils {

    private static final String DOMAIN = "bbs.ngacn.cc";

    public static String getNGAHost() {
        return ForumUtils.getAvailableDomain() + "/";
    }

    public static String getNGADomain() {
        return DOMAIN;
    }

    /**
     * 保存图片成功后，更新系统图库
     *
     * @param context
     * @param file
     */
    public static void updateSystemGallery(Context context, File file) {
        if (context == null || file == null)
            return;
        try {// 其次把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath()));
            intent.putExtra("is_system_broadcast", true);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
