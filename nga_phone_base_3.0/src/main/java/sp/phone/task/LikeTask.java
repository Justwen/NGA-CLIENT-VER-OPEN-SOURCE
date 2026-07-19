package sp.phone.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sp.phone.http.OnSimpleHttpCallBack;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;

/**
 * 赞或者踩
 * Created by elrond on 2017/9/1.
 */

public class LikeTask {

    public static final int SUPPORT = 1;

    public static final int OPPOSE = -1;

    private RetrofitService mService;

    private Map<String, String> mParamMap;

    public LikeTask() {
        mService = RetrofitHelper.getInstance().getService();
        mParamMap = new HashMap<>();
        mParamMap.put("__lib", "topic_recommend");
        mParamMap.put("__act", "add");
        mParamMap.put("raw", "3");
        mParamMap.put("pid", "0");
        mParamMap.put("__output", "8");
    }

    public void execute(int tid, int pid, int like, OnSimpleHttpCallBack<String> callBack) {
        Map<String, String> map = new HashMap<>(mParamMap);
        map.put("value", String.valueOf(like));
        map.put("tid", String.valueOf(tid));
        map.put("pid", String.valueOf(pid));
        mService.post(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        try {
                            JSONObject obj = JSON.parseObject(s).getJSONObject("data");
                            callBack.onResult(obj.getString("0"));
                        } catch (Exception e) {
                            callBack.onResult(ContextUtils.getString(R.string.network_error));
                        }
                    }
                });

    }
}
