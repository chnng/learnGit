package com.learn.git.ui.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Button;

import com.aihui.lib.base.api.eventbus.EventMessage;
import com.aihui.lib.base.ui.BaseActivity;
import com.aihui.lib.base.util.LogUtils;
import com.aihui.lib.base.util.ToastUtils;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.learn.git.R;
import com.learn.git.app.MessageCons;
import com.learn.git.ui.fragment.EmailFragment;
import com.learn.git.ui.fragment.EventBusFragment;
import com.learn.git.ui.fragment.FFmpegFragment;
import com.learn.git.ui.fragment.GlideFragment;
import com.learn.git.ui.fragment.OkHttpFragment;
import com.learn.git.ui.fragment.PermissionFragment;
import com.learn.git.ui.fragment.PickerFragment;
import com.learn.git.ui.fragment.RetrofitFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder() {
            super(new Button(MainActivity.this));
        }
    }

    @Override
    public void initData() {
        LogUtils.e("onCreate");
        Class<?>[] fragmentList = {
                OkHttpFragment.class,
                RetrofitFragment.class,
                GlideFragment.class,
                PermissionFragment.class,
                EventBusFragment.class,
                PickerFragment.class,
                EmailFragment.class,
                FFmpegFragment.class,
        };
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder();
            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
                Class<?> fragmentClass = fragmentList[position];
                Button itemView = (Button) holder.itemView;
                itemView.setAllCaps(false);
                String simpleName = fragmentClass.getSimpleName();
//                itemView.setText(simpleName.substring(0, simpleName.lastIndexOf("Fragment")));
                itemView.setText(simpleName);
                itemView.setOnClickListener(v -> {
                    Fragment fragment = getFragment(fragmentClass);
                    if (fragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.layout_main, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }

            @Override
            public int getItemCount() {
                return fragmentList.length;
            }
        });
    }

    @Override
    public void initEvent() {

    }

    private Fragment getFragment(Class<?> fragmentClass) {
        try {
            return (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(new EventMessage<>(MessageCons.TEST_0, "activity pause"));
    }

    @Override
    public void onMessage(EventMessage event) {
        super.onMessage(event);
        LogUtils.e("onMessage:" + event);
        switch (event.getKey()) {
            case MessageCons.TEST_1:
                new Thread(() -> ToastUtils.toast(event.getValue().toString())).start();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.e("onKeyDown:" + KeyEvent.keyCodeToString(keyCode));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        LogUtils.e("onBackPressed");
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (fragmentManager.getBackStackEntryCount() == 1) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
