package sp.phone.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import sp.phone.util.HttpUtil;
import sp.phone.util.ImageUtils;
import gov.anzong.androidnga.common.util.NLog;

public class ChangeAvatarLoadTask extends AsyncTask<String, Integer, Bitmap> {

    public interface ChangeAvatarLoadCompleteCallBack {
        void OnAvatarLoadStart(String url);

        void OnAvatarLoadComplete(String url, Bitmap result);
    }

    static final String TAG = ChangeAvatarLoadTask.class.getSimpleName();
    final ImageView view;
    final boolean downImg;
    final int floor;
    final ChangeAvatarLoadCompleteCallBack callBack;
    String uri = null;

    public ChangeAvatarLoadTask(ImageView view, int floor, ChangeAvatarLoadCompleteCallBack callBack) {
        super();
        this.view = view;
        this.downImg = true;
        this.floor = floor;
        this.callBack = callBack;
    }


    @SuppressWarnings("resource")
    @Override
    protected Bitmap doInBackground(String... params) {

        final String avatarUrl = params[0];
        final String avatarLocalPath = params[1];
        uri = avatarUrl;
        callBack.OnAvatarLoadStart(uri);

        Bitmap bitmap = null;
        InputStream is = null;
        HttpUtil.downImage(avatarUrl, avatarLocalPath);
        try {
            is = new FileInputStream(avatarLocalPath);
            //NLog.d(TAG,
            //		"download avatar from " + avatarUrl);

        } catch (FileNotFoundException e) {
            NLog.d(TAG,
                    "avatar " + avatarUrl + " is failed to download");
        }

        if (is != null) {
            //NLog.d(TAG,"load avatar from file: " + avatarLocalPath);
            bitmap = ImageUtils.loadAvatarFromSdcard(avatarLocalPath);
        }


        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            view.setImageBitmap(result);
        }
        callBack.OnAvatarLoadComplete(uri, result);
    }


    @Override
    protected void onCancelled(Bitmap result) {

        onCancelled();
    }


    @Override
    protected void onCancelled() {
        callBack.OnAvatarLoadComplete(uri, null);
    }


}
