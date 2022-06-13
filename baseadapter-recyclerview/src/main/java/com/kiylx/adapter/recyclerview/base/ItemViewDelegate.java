package com.kiylx.adapter.recyclerview.base;


/**
 * Created by zhy on 16/6/22.
 */
public interface ItemViewDelegate<T>
{

    int getItemViewLayoutId();

    /**
     * @param item
     * @param position
     * @return 对item(也就是dateList[position])判断，如果是显示这个类型的itemview,就返回true,否则返回false
     */
    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}
