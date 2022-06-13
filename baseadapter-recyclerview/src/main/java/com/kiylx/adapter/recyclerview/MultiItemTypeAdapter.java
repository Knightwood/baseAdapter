package com.kiylx.adapter.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.kiylx.adapter.recyclerview.base.DelegatePair;
import com.kiylx.adapter.recyclerview.base.ItemViewDelegate;
import com.kiylx.adapter.recyclerview.base.ItemViewDelegateManager;
import com.kiylx.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by zhy on 16/4/9.
 */
public class MultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;

    protected ItemViewDelegateManager mItemViewDelegateManager;

    @Deprecated
    protected OnItemClickListener mOnItemClickListener;

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    //点击事件和长按事件分开
    protected ItemClickListener mItemClickListener;
    protected ItemLongClickListener mItemLongClickListener;

    /**
     * 用新数据替换旧数据
     *
     * @param data 新数据
     */
    public void setNewData(List<T> data) {
        this.mDatas = data;
    }

    public MultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    /**
     * @param context
     * @param datas
     * @param itemViewDelegates 多个类型的itemviewDelegates
     */
    public MultiItemTypeAdapter(Context context, List<T> datas, @NotNull ItemViewDelegate<T>... itemViewDelegates) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
        for (ItemViewDelegate<T> d : itemViewDelegates) {
            mItemViewDelegateManager.addDelegate(d);
        }
    }

    /**
     * @param context
     * @param datas
     * @param itemViewDelegates 多个类型的itemviewDelegates
     */
    public MultiItemTypeAdapter(Context context, List<T> datas, @NotNull DelegatePair<T>... itemViewDelegates) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
        for (DelegatePair<T> d : itemViewDelegates) {
            mItemViewDelegateManager.addDelegate(d.getType(), d.getDelegate());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!useItemViewDelegateManager()) return super.getItemViewType(position);
        return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ViewHolder holder = createHolderInternal(mContext, parent, layoutId);
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        setLongListener(parent, holder, viewType);
        return holder;
    }

    /**
     * 创建viewholder,可以重写，以自己控制viewholder的创建。
     * 比如在使用gridLayoutManager时，希望item评分屏幕宽度，就在创建viewholder时手动控制item的宽高
     * 注意：重写时不要super(context, parent, layoutId)
     *
     * @param context
     * @param parent
     * @param layoutId
     * @return
     */
    public ViewHolder createHolderInternal(Context context, ViewGroup parent, int layoutId) {
        return ViewHolder.createViewHolder(context, parent, layoutId);
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {

    }

    public void convert(ViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    /**
     * item点击事件
     *
     * @param parent
     * @param viewHolder
     * @param viewType
     */
    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弃用，未来将会删除
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }

                if (mItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });
    }

    /**
     * item的长按事件
     *
     * @param parent
     * @param viewHolder
     * @param viewType
     */
    protected void setLongListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                if (mItemLongClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mItemLongClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = mDatas.size();
        return itemCount;
    }


    public List<T> getDatas() {
        return mDatas;
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface ItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public interface ItemLongClickListener {
        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    /**
     * 已经将点击事件和长按事件分开
     * 使用ItemClickListener和ItemLongClickListener代替此接口
     */
    @Deprecated
    public interface OnItemClickListener {
        /**
         * 使用ItemClickListener中的onItemClick代替
         * @param view
         * @param holder
         * @param position
         */
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);
        /**
         * 使用ItemLongClickListener中的onItemLongClick代替
         * @param view
         * @param holder
         * @param position
         */
        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    @Deprecated
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
