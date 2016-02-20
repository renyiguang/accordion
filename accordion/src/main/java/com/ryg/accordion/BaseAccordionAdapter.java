package com.ryg.accordion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: renyiguang on 16/2/16 15:53.
 * Email: yiguangren1988@gmail.com
 */
public abstract class BaseAccordionAdapter<M extends BaseAccordionItem> extends BaseAdapter {

    private final static String TAG = "BaseAccordionAdapter";

    private Context context;
    private List<M> items = new ArrayList<>();

    private LayoutInflater layoutInflater;

    public BaseAccordionAdapter(Context context, List<M> items) {

        layoutInflater = LayoutInflater.from(context);

        if(items !=null) {
            this.items = items;
        }else {
            Log.d(TAG,"data may not be null");
        }
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public M getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view == null){
            view  = layoutInflater.inflate(R.layout.accordion_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView)view.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView)view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        M item = getItem(i);
        viewHolder.iv_icon.setImageBitmap(item.icon);
        viewHolder.tv_name.setText(item.name);


        viewHolder.iv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("=========","================onClick name = "+viewHolder.tv_name.getText());
            }
        });

        return view;
    }


    public void clearItems(){
        if(items!=null && items.size()>0){
            items.clear();
        }
    }


    public void refreshItems(List<M> items){
        if(items!=null){
            this.items = items;
        }

        notifyDataSetChanged();
    }


    class ViewHolder{

        ImageView iv_icon;
        TextView tv_name;
    }

}
