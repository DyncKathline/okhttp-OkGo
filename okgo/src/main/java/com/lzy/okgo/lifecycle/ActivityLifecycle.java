package com.lzy.okgo.lifecycle;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.lzy.okgo.OkGo;

/**
 * 使用没有实现LifecycleOwner的activity和fragment进行生命周期绑定
 */
public final class ActivityLifecycle implements
        LifecycleOwner, LifecycleEventObserver,
        Application.ActivityLifecycleCallbacks {

    private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

    private Activity mActivity;

    public ActivityLifecycle(Activity activity) {
        mActivity = activity;

        if (mActivity instanceof LifecycleOwner) {
            ((LifecycleOwner) mActivity).getLifecycle().addObserver(this);
        } else {
            mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    public ActivityLifecycle(Fragment fragment) {
        mActivity = fragment.getActivity();

        if (mActivity instanceof LifecycleOwner) {
            ((LifecycleOwner) mActivity).getLifecycle().addObserver(this);
        } else {
            mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }
    }

    /**
     * {@link LifecycleOwner}
     */

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycle;
    }

    /**
     * 判断宿主是否处于活动状态
     */
    public static boolean isLifecycleActive(LifecycleOwner lifecycleOwner) {
        return lifecycleOwner != null && lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED;
    }

    /**
     * {@link LifecycleEventObserver}
     */

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        mLifecycle.handleLifecycleEvent(event);
        switch (event) {
            case ON_DESTROY:
                OkGo.getInstance().cancelTag(source);
                source.getLifecycle().removeObserver(this);
                mActivity = null;
                break;
            default:
                break;
        }
    }

    /**
     * {@link Application.ActivityLifecycleCallbacks}
     */

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (mActivity == activity) {
            mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
            mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            mActivity = null;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
}
