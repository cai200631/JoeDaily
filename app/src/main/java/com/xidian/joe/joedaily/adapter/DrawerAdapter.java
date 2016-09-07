package com.xidian.joe.joedaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xidian.joe.joedaily.R;
import com.xidian.joe.joedaily.bean.Theme;

import java.util.List;

/**
 * Created by Administrator on 2016/8/13.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MenuViewHolder>
        implements View.OnClickListener {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Theme> mThemeItems;
    public onClickListener mOnClickListener;

    public interface onClickListener{
        void onClick(View view, int position);
    }

    public void setOnClickListener (onClickListener listener){
        mOnClickListener = listener;
    }

    public DrawerAdapter(Context context, List<Theme> items) {
        mContext = context;
        this.mThemeItems = items;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.menu_layout,parent,false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.mTextView.setText(mThemeItems.get(position).getName());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mThemeItems.size();
    }

    @Override
    public void onClick(View v) {
        if(mOnClickListener != null){
            int position = (int) v.getTag();
            mOnClickListener.onClick(v,position);
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public MenuViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.menu_layout_text_view);
        }
    }
}
