package com.learn.git.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.learn.git.R;
import com.learn.git.cons.MessageCons;
import com.learn.git.api.eventbus.MessageEvent;
import com.learn.git.ui.base.BaseActivity;
import com.learn.git.ui.fragment.EventBusFragment;
import com.learn.git.ui.fragment.GlideFragment;
import com.learn.git.ui.fragment.OkHttpFragment;
import com.learn.git.ui.fragment.PermissionFragment;
import com.learn.git.ui.fragment.RetrofitFragment;
import com.learn.git.util.LogUtil;
import com.learn.git.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.recycler_view_main)
    RecyclerView recyclerView;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder() {
            super(new Button(MainActivity.this));
        }
    }

    @Override
    public void onCreate() {
        LogUtil.d("onCreate");
        Class<?>[] fragmentList = {
                OkHttpFragment.class,
                RetrofitFragment.class,
                GlideFragment.class,
                PermissionFragment.class,
                EventBusFragment.class,
                PermissionFragment.class,
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
        EventBus.getDefault().post(new MessageEvent.Builder<>()
                .what(MessageCons.TEST_0)
                .obj("activity pause")
                .build());
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        LogUtil.d("onMessageEvent:" + event);
        switch (event.what) {
            case MessageCons.TEST_1:
                new Thread(() -> ToastUtil.toast(event.obj.toString())).start();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d("onKeyDown:" + KeyEvent.keyCodeToString(keyCode));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        LogUtil.d("onBackPressed");
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
