package com.byids.hy.smarttable.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byids.hy.smarttable.MainActivity;
import com.byids.hy.smarttable.R;
import com.byids.hy.smarttable.bean.ItemFlag;
import com.byids.hy.smarttable.bean.TableModel;

import java.util.List;

/**
 * Created by hy on 2016/7/9.
 */
public class MyBaseAdapter extends BaseAdapter{

    private Context context;
    private List<TableModel> mlist;
    private LayoutInflater inflater;
    private List<ItemFlag> flagList;//用来判断item是否被点击，如果被点击，设置背景颜色
    private int mSelect = -1;
    private int save = -1;

    public MyBaseAdapter(Context context, List<TableModel> mlist) {
        this.context = context;
        this.mlist = mlist;
        inflater = LayoutInflater.from(context);
    }

    int SaveRoom = 1;
    public void changeSelected(int positon,int roomIndex){ //刷新方法
        if(positon != mSelect){
            mSelect = positon;
            SaveRoom = roomIndex;//
            Log.i("result", "changeSelected: ---------------as"+SaveRoom);
            notifyDataSetChanged();
        }
    }



    @Override
    public int getCount() {
        return mlist==null?0:mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView==null){
            convertView = inflater.inflate(R.layout.list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_model);
            viewHolder.linearItem = (LinearLayout) convertView.findViewById(R.id.linear_item);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textView.setText(mlist.get(position).getModelText());
        viewHolder.imageView.setImageResource(mlist.get(position).getModelImg());

        int Keting = position+10;
        int Woshi = position+20;
        int Ertongfang = position+30;
        if (mlist.get(position).getRoom()=="keting"){
            if(mSelect==Keting){
                convertView.setBackgroundResource(R.drawable.model_bak2);  //选中项背景
            }else{
                convertView.setBackgroundResource(R.drawable.model_bak);  //其他项背景
            }
        }else if (mlist.get(position).getRoom()=="woshi"){
            if(mSelect==Woshi){
                convertView.setBackgroundResource(R.drawable.model_bak2);  //选中项背景
            }else{
                convertView.setBackgroundResource(R.drawable.model_bak);  //其他项背景
            }
        }else if (mlist.get(position).getRoom()=="ertongfang"){
            if(mSelect==Ertongfang){
                convertView.setBackgroundResource(R.drawable.model_bak2);  //选中项背景
            }else{
                convertView.setBackgroundResource(R.drawable.model_bak);  //其他项背景
            }
        }

        return convertView;
    }

    class ViewHolder{
        private ImageView imageView;
        private TextView textView;
        private LinearLayout linearItem;
    }

}
