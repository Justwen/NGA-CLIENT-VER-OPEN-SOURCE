package sp.phone.mvp.model;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

import gov.anzong.androidnga.common.util.ForumUtils;

/**
 * Created by Justwen on 2017/11/25.
 */

public abstract class BaseModel  {

    private volatile LifecycleProvider<FragmentEvent> mProvider;

    private String mDomain;

    public BaseModel() {
        mDomain = ForumUtils.getAvailableDomain();
    }

    public void detach() {
        mProvider = null;
    }

    public void setLifecycleProvider(LifecycleProvider<FragmentEvent> provider) {
        mProvider = provider;
    }

    protected LifecycleProvider<FragmentEvent> getLifecycleProvider() {
        return mProvider;
    }

    protected String getAvailableDomain() {
        return mDomain;
    }
}
