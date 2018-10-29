package com.example.hasee.lanyademo.adapter;

/**
 * Created by hasee on 2018/9/22.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hasee.lanyademo.R;
import com.example.hasee.lanyademo.bean.BlueDevice;

import java.util.ArrayList;

public class BlueListAdapter extends BaseAdapter {
    private static final String TAG = "BlueListAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<BlueDevice> mBlueList;
    private String[] mStateArray = {"未绑定", "绑定中", "已绑定", "已连接"};
    public static int CONNECTED = 3;

    public BlueListAdapter(Context context, ArrayList<BlueDevice> blue_list) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mBlueList = blue_list;
    }

    @Override  //获取listview长度
    public int getCount() {
        return mBlueList.size();
    }

    @Override //获取列表中的对应的值
    public Object getItem(int position) {
        return mBlueList.get(position);
    }

    @Override //点击时候选项返回类表对应的ID
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            //使用自定义的item_bluetooth作为布局
            convertView = mInflater.inflate(R.layout.item_bluetooth, null);
            holder = new ViewHolder();
            //初始化布局中的元素
            holder.tv_blue_name = (TextView) convertView.findViewById(R.id.tv_blue_name);
            holder.tv_blue_address = (TextView) convertView.findViewById(R.id.tv_blue_address);
            holder.tv_blue_state = (TextView) convertView.findViewById(R.id.tv_blue_state);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BlueDevice device = mBlueList.get(position);
        holder.tv_blue_name.setText(device.name);
        holder.tv_blue_address.setText(device.address);
        holder.tv_blue_state.setText(mStateArray[device.state]);
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_blue_name;
        public TextView tv_blue_address;
        public TextView tv_blue_state;
    }

}