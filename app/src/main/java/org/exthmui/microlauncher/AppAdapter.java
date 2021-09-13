package org.exthmui.microlauncher;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ApplicationViewHolder> {
    private List<Application> mApplicationList;
    private static View mItemView;
    private int mLayoutMode;
    private static int mPosition = -1;

    /**
     * @param applicationList: 类型为Application的List集
     * @param layoutMode:      0，线性；1：网格
     */
    public AppAdapter(List<Application> applicationList, int layoutMode) {
        this.mApplicationList = applicationList;
        this.mLayoutMode = layoutMode;
    }

    public static int getPosition() {
        return mPosition;
    }

    

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (this.mLayoutMode) {
            case 0:
                this.mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item,parent, false);
                break;
            case 1:
                break;
        }
        return new ApplicationViewHolder(this.mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = this.mApplicationList.get(position);
        holder.mAppIconView.setImageDrawable(application.getAppIcon());
        holder.mTextButton.setText(application.getAppLabel());

//      设置单击监听事件
        holder.mTextButton.setOnClickListener(v -> {
            application.getAppIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(application.getAppIntent());
        });
        holder.itemView.setOnClickListener(v -> {
            application.getAppIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(application.getAppIntent());
        });

//      设置长按监听事件
        holder.mTextButton.setOnLongClickListener(v -> {
            mPosition = holder.getAdapterPosition();
            String tip = application.isSystemApp() ? "这是系统应用" : "这是用户应用";
            Toast.makeText(v.getContext(), tip, Toast.LENGTH_SHORT).show();
            return false;
        });
        holder.itemView.setOnLongClickListener(v -> {
            mPosition = holder.getAdapterPosition();
            String tip = application.isSystemApp() ? "这是系统应用" : "这是用户应用";
            Toast.makeText(v.getContext(), tip, Toast.LENGTH_SHORT).show();
            return false;
        });


    }

    @Override
    public int getItemCount() {
        return mApplicationList == null ? 0 : mApplicationList.size();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private ImageView mAppIconView;
        private Button mTextButton;

        public ApplicationViewHolder(@NonNull View view) {
            super(view);
            this.mAppIconView = view.findViewById(R.id.app_icon);
            this.mTextButton = view.findViewById(R.id.app_title);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(this.mTextButton.getText());
            menu.add(0, 0, Menu.NONE, R.string.app_menu_open);
            menu.add(0, 1, Menu.NONE,  R.string.app_menu_uninstall);
            menu.add(0, 2, Menu.NONE,  R.string.app_menu_info);
            menu.add(0, 3, Menu.NONE,  R.string.app_menu_manage);
        }
    }

}
