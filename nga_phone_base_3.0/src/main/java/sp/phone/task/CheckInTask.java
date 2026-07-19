package sp.phone.task;

import static sp.phone.util.ActivityUtils.showToast;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.StringUtils;

/**
 * 签到
 * Created by mlzzen on 2023/7/7.
 */
public class CheckInTask {

    private static final String CHECK_IN_URL = Utils.getNGAHost() + "nuke.php?__lib=check_in&__act=check_in&lite=js";

    public static void autoCheckIn(Context context) {
        if (!PreferenceUtils.getData(context.getString(gov.anzong.androidnga.common.R.string.pref_auto_check_in), false)) {
            return;
        }
        checkIn(true);
    }

    public static void checkIn(boolean auto) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        long checkInLastTime = PreferenceUtils.getData(PreferenceKey.CHECK_IN_LAST_TIME, System.currentTimeMillis() - 24 * 3600 * 1000);
        cal1.setTime(new Date(checkInLastTime));
        cal2.setTime(new Date());
        if (cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)) {
            RetrofitService service = RetrofitHelper.getInstance().getService();
            service.post(CHECK_IN_URL)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<String>() {
                        @Override
                        public void onNext(@NonNull String result) {
                            String msg = StringUtils.getStringBetween(result, 0, "{\"0\":\"", "\"},\"time\"").result;
                            if (msg.contains("签到成功")) {
                                showToast("签到成功");
                            }
                            if (msg.contains("签到成功") || msg.contains("今天已经签到")) {
                                PreferenceUtils.putData(PreferenceKey.CHECK_IN_LAST_TIME, System.currentTimeMillis());
                            }
                        }
                    });
        } else if (!auto) {
            ToastUtils.info("今天已经签过到了！");
        }
    }
}
