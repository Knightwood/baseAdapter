package com.kiylx.adapter.recyclerview.utils.simpletool

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.kiylx.adapter.recyclerview.CommonAdapter
import com.kiylx.adapter.recyclerview.MultiItemTypeAdapter
import com.kiylx.adapter.recyclerview.base.DelegatePair
import com.kiylx.adapter.recyclerview.base.ViewHolder

/**
 * 构建单一种item类型的recyclerview
 */
fun <T : Any> RecyclerViewX(
    rv: RecyclerView,
    context: Context,
    lmType: LayoutManagerType,
    commonAdapter: CommonAdapter<T>,
    config: ((
        layoutManager: RecyclerView.LayoutManager
    ) -> Unit)? = null,
    process: (RVHolder<T>.() -> Unit)? = null
): RVHolder<T> {
    val rvHolder = RVHolder<T>(rv, lmType, context).apply {
        this.adapter = commonAdapter
        rv.adapter = commonAdapter
        config?.let { it(mLayoutManager) }
        rv.layoutManager = mLayoutManager
        process?.let { it() }
    }
    return rvHolder
}

/**
 * 构建多种item类型的recyclerview
 */
fun <T : Any> RecyclerViewX(
    rv: RecyclerView,
    context: Context,
    lmType: LayoutManagerType,
    adapterDelegates: Array<DelegatePair<T>>,
    data: MutableList<T>,
    createHolder: ((
        context: Context?,
        parent: ViewGroup?,
        layoutId: Int
    ) -> ViewHolder)? = null,//这个用于令外界重写multiadapter里createHolderInternal方法
    config: ((
        layoutManager: RecyclerView.LayoutManager
    ) -> Unit)? = null,//配置布局管理器
    process: (RVHolder<T>.() -> Unit)? = null//这里是配置完成recyclerview，比如在这里添加点击事件，用接收者使调用起来不用"it"参数，更像dsl
): RVHolder<T> {
    val rvHolder = RVHolder<T>(rv, lmType, context).apply {
        adapter = object : MultiItemTypeAdapter<T>(context, data, *adapterDelegates) {
            override fun createHolderInternal(
                context: Context?,
                parent: ViewGroup?,
                layoutId: Int
            ): ViewHolder {
                return if (createHolder != null)
                    createHolder(context, parent, layoutId)
                else
                    super.createHolderInternal(context, parent, layoutId)
            }
        }
        rv.adapter = adapter
        config?.let { it(mLayoutManager) }
        rv.layoutManager = mLayoutManager
        process?.let {
            it()
        }
    }
    return rvHolder
}

/**
 * 简化给adapter设置点击事件的方法。
 */
fun <T> MultiItemTypeAdapter<T>.onClick(block: (view: View, holder: RecyclerView.ViewHolder, position: Int) -> Unit) {
    this.setItemClickListener { view, holder, position -> block(view, holder, position) }
}

fun <T> MultiItemTypeAdapter<T>.onLongClick(block: (view: View, holder: RecyclerView.ViewHolder, position: Int) -> Boolean) {
    this.setItemLongClickListener { view, holder, position ->
        return@setItemLongClickListener block(view, holder, position)
    }
}


/**
 * @param rv recylerview
 * @param lmType 描述布局管理器类型
 * @param context 上下文
 */
class RVHolder<T : Any?>(
    var rv: RecyclerView,
    var lmType: LayoutManagerType,
    var context: Context
) {
    lateinit var mLayoutManager: RecyclerView.LayoutManager
    lateinit var adapter: MultiItemTypeAdapter<T>

    init {
        initLayoutManager()
    }

    /**
     * 根据layoutmanagetType解析出来生成哪种布局管理器
     */
    private fun initLayoutManager() {
        when (lmType.kinds) {
            LayoutManagerType.Type_StaggeredGridLayoutManager -> {
                mLayoutManager = StaggeredGridLayoutManager(
                    lmType.spanCount,
                    lmType.orientation
                )
            }
            LayoutManagerType.Type_GridLayoutManager -> {
                mLayoutManager = GridLayoutManager(
                    context,
                    lmType.spanCount,
                    lmType.orientation,
                    lmType.reverseLayout
                )
            }
            LayoutManagerType.Type_LinearLayoutManager -> {
                mLayoutManager =
                    LinearLayoutManager(context, lmType.orientation, lmType.reverseLayout)
            }
        }
    }

    /**
     * 更新所有数据
     */
    fun update(datas: MutableList<T>) {
        adapter.setNewData(datas)
        adapter.notifyDataSetChanged()
    }

}

/**
 * 用于描述使用哪种布局管理器
 * @param orientation 方向，只对部分布局管理器有用
 * @param spanCount 列数，只对部分布局管理器有用
 * @param kinds companion object 中定义的几种类型
 */
data class LayoutManagerType(
    @RecyclerView.Orientation val orientation: Int,
    val spanCount: Int,
    val kinds: Int,
    val reverseLayout: Boolean = false
) {

    companion object {
        internal const val Type_LinearLayoutManager = 1
        internal const val Type_GridLayoutManager = 2
        internal const val Type_StaggeredGridLayoutManager = 3

        fun getLinearLayoutManager(@RecyclerView.Orientation orientation: Int): LayoutManagerType {
            return LayoutManagerType(orientation, -1, Type_LinearLayoutManager)
        }

        fun getGridLayoutManager(
            @RecyclerView.Orientation orientation: Int,
            spanCount: Int
        ): LayoutManagerType {
            return LayoutManagerType(orientation, spanCount, Type_GridLayoutManager)
        }

        fun getStaggeredGridLayoutManager(
            @RecyclerView.Orientation orientation: Int,
            spanCount: Int
        ): LayoutManagerType {
            return LayoutManagerType(orientation, spanCount, Type_StaggeredGridLayoutManager)
        }

    }
}
