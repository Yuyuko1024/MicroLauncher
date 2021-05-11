package org.exthmui.microlauncher;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    private ListView menu_view;
    private final static int[] title = {R.string.menu_app, R.string.menu_set_wallpaper,R.string.menu_volume_dash,R.string.menu_settings_system,R.string.menu_settings_launcher,R.string.menu_start,R.string.menu_about_me};
    private final static int[] summary = {R.string.menu_app_sum,R.string.menu_set_wallpaper_sum,R.string.menu_volume_sum,R.string.menu_settings_system_sum,R.string.menu_settings_launcher_sum,R.string.menu_start_sum,R.string.menu_about_sum};
    private int[] icon = {R.drawable.ic_apps,R.drawable.ic_wallpaper,R.drawable.ic_volume,R.drawable.ic_settings_system,R.drawable.ic_settings_launcher,R.drawable.ic_start,R.drawable.ic_home};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_view);
        menu_view=findViewById(R.id.menu_list);
        //创建一个Adapter的实例
        MyBaseAdapter mAdapter = new MyBaseAdapter();
        //设置Adapter
        menu_view.setAdapter(mAdapter);
    }
    //创建一个类继承BaseAdapter
    class MyBaseAdapter extends BaseAdapter {
        //得到item的总数
        @Override
        public int getCount() {
            //返回ListView Item条目的总数
            return title.length;
        }
        //得到Item代表的对象
        @Override
        public Object getItem(int position) {
            //返回ListView Item条目代表的对象
            return title[position];
        }
        //得到Item的id
        @Override
        public long getItemId(int position) {
            //返回ListView Item的id
            return position;
        }
        //得到Item的View视图
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //使用了懒汉模式
            if(convertView == null){
                //将list_item.xml文件找出来并转换成View对象
                convertView  = View.inflate(MenuActivity.this, R.layout.list_item, null);
                //找到list_item.xml中创建的TextView
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.menu_title);
                holder.summary = (TextView) convertView.findViewById(R.id.menu_summary);
                holder.icon = (ImageView) convertView.findViewById(R.id.menu_icon);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(title[position]);
            holder.summary.setText(summary[position]);
            holder.icon.setBackgroundResource(icon[position]);
            return convertView;
        }
    }
    static class ViewHolder{
        TextView title;
        TextView summary;
        ImageView icon;
    }
}
