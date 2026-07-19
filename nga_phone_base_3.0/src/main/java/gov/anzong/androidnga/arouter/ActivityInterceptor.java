package gov.anzong.androidnga.arouter;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.launcher.ARouter;

import sp.phone.common.UserManagerImpl;

/**
 * Created by Justwen on 2017/11/27.
 */
@Interceptor(priority = 8)
public class ActivityInterceptor implements IInterceptor {

    @Override
    public void process(Postcard postcard, InterceptorCallback interceptorCallback) {

        if (!UserManagerImpl.getInstance().hasValidUser()) {
            String path = postcard.getPath();
            for (String activity : ARouterConstants.ACTIVITY_NEED_LOGIN) {
                if (activity.equals(path)) {
                    interceptorCallback.onInterrupt(new Exception("未登录"));
                    ARouter.getInstance().build(ARouterConstants.ACTIVITY_LOGIN).navigation();
                    return;
                }
            }
        }
        interceptorCallback.onContinue(postcard);
    }

    @Override
    public void init(Context context) {

    }
}
