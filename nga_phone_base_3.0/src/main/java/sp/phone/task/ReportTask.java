package sp.phone.task;

import com.alibaba.fastjson.JSON;

import java.util.Map;

import gov.anzong.androidnga.http.OnHttpCallBack;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import com.justwen.androidnga.base.network.retrofit.RetrofitHelper;
import com.justwen.androidnga.base.network.retrofit.RetrofitService;
import sp.phone.rxjava.BaseSubscriber;

public class ReportTask {

    public static class ResultBean {

        /**
         * error : {"0":"你在217秒后方可举报"}
         * data : {"0":"操作成功"}
         * time : 1601530856
         */

        private Map<String, String> error;
        private Map<String, String> data;
        private int time;

        public Map<String, String> getError() {
            return error;
        }

        public Map<String, String> getData() {
            return data;
        }

        public void setData(Map<String, String> data) {
            this.data = data;
        }

        public void setError(Map<String, String> error) {
            this.error = error;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }

    private RetrofitService mService;

    public void pos(Map<String, String> queryMap, Map<String, String> fieldMap, OnHttpCallBack<String> callBack) {
        if (mService == null) {
            mService = RetrofitHelper.getInstance().getService();
        }
        mService.post(queryMap, fieldMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        ResultBean resultBean = JSON.parseObject(s, ResultBean.class);
                        if (resultBean.error != null) {
                            callBack.onError(resultBean.error.get("0"));
                        } else if (resultBean.data != null) {
                            callBack.onSuccess(resultBean.data.get("0"));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callBack.onError(throwable.getMessage());
                    }
                });
    }


}
