package com.example.screenrecorderv2.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;


import com.example.screenrecorderv2.base.rx.CallBackRxBus;
import com.example.screenrecorderv2.base.rx.CallbackEventView;
import com.example.screenrecorderv2.base.rx.RxBus;
import com.example.screenrecorderv2.ui.main.MainActivity;
import com.example.screenrecorderv2.utils.Toolbox;
import com.example.screenrecorderv2.utils.ViewUtils;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseFragment<B extends ViewBinding> extends Fragment implements CallbackEventView {
    protected B binding = null;
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    protected Disposable rxBusDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = getViewBinding(inflater, container);
        Toolbox.hideSoftKeyboard(getActivity());
        ViewUtils.setupUI(binding.getRoot(), getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initControl();
        initRxBus();
    }

    private void initRxBus() {
        rxBusDisposable = RxBus.getInstance().subscribe(new CallBackRxBus(this));
    }

    protected void navigate(int resId, Bundle bundle) {
        NavHostFragment.findNavController(this)
                .navigate(resId, bundle);
    }

    protected void navigate(int resId) {
        navigate(resId, null);
    }

    protected void navigateUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }


    protected void setTitleFragment(String title) {
        if (getBaseActivity() != null)
            getBaseActivity().setTitleToolbar(title);

    }

    protected void setNavigationIcon(int res) {
        if (getBaseActivity() != null)
            getBaseActivity().setNavigationIcon(res);

    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public void toast(String content) {
        if (getBaseActivity() != null)
            getBaseActivity().toast(content);
    }

    public void toast(@StringRes int resId) {
        if (getBaseActivity() != null)
            getBaseActivity().toast(resId);
    }

    public void setStageDrawerLayout(boolean isLock) {
        if (getBaseActivity() != null && getBaseActivity() instanceof MainActivity)
            ((MainActivity) getBaseActivity()).setStageDrawerLayout(isLock);
    }

    public void setTitleToolbar(String title) {
        if (getBaseActivity() != null)
            getBaseActivity().setTitleToolbar(title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toolbox.hideSoftKeyboard(getActivity());
        compositeDisposable.dispose();
        if (rxBusDisposable != null) {
            rxBusDisposable.dispose();
        }
    }

    protected abstract void initView();

    protected abstract void initControl();

    /* Refresh @Fragment */
    protected abstract boolean isNeedRefresh();

    protected abstract B getViewBinding(LayoutInflater inflater, ViewGroup container);
}
