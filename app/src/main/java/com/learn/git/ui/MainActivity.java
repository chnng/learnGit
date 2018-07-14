package com.learn.git.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Button;

import com.learn.git.R;
import com.learn.git.ui.base.BaseActivity;
import com.learn.git.ui.fragment.GlideFragment;
import com.learn.git.ui.fragment.OkHttpFragment;
import com.learn.git.ui.fragment.PermissionFragment;
import com.learn.git.ui.fragment.RetrofitFragment;
import com.learn.git.util.LogUtil;

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
        };
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
                itemView.setText(fragmentClass.getSimpleName());
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d("onKeyDown:" + KeyEvent.keyCodeToString(keyCode));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        LogUtil.d("onBackPressed");
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
