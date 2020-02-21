package com.baselibrary.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baselibrary.listener.OnceClickListener;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created By pq
 * on 2019/9/30
 */
public abstract class BaseFragment extends Fragment {

    private View mView;
    private Unbinder unbinder;

    protected abstract Integer contentView();

    protected abstract void initView();

    //protected abstract void initToolBar();

    protected abstract void initData();

    //点击事件
    protected abstract void onViewClick(View view);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(contentView(), container, false);
        unbinder = ButterKnife.bind(this, mView);
        initData();
        initView();
        return mView;
    }

    protected OnceClickListener clickListener = new OnceClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            onViewClick(v);
        }
    };

    @SuppressWarnings("unchecked")
    protected <T extends View> T bindViewWithClick(Integer idRes, Boolean isClick) {
        View view = null;
        try {
            view = mView.findViewById(idRes);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        if (isClick && view != null) {
            view.setOnClickListener(clickListener);
        }
        return (T) view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
