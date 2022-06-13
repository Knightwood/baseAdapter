package com.kiylx.simple

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.kiylx.adapter.recyclerview.CommonAdapter
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.kiylx.adapter.recyclerview.MultiItemTypeAdapter
import com.kiylx.adapter.recyclerview.base.DelegatePair
import com.kiylx.adapter.recyclerview.base.ItemViewDelegate
import com.kiylx.adapter.recyclerview.base.ViewHolder
import com.kiylx.adapter.recyclerview.utils.simpletool.LayoutManagerType
import com.kiylx.adapter.recyclerview.utils.simpletool.RecyclerViewX
import com.kiylx.adapter.recyclerview.utils.simpletool.onClick
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRV2()
    }

    //单个类型item
    fun initRV1() {
        val rv = findViewById<View>(R.id.rv) as RecyclerView
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = object : CommonAdapter<String?>(
            this,
            android.R.layout.simple_list_item_1,
            Arrays.asList("a", "b", "c")
        ) {
            //非必须重写，这个可以自己控制viewholder的创建
            override fun createHolderInternal(
                context: Context,
                parent: ViewGroup,
                layoutId: Int
            ): ViewHolder {
                return super.createHolderInternal(context, parent, layoutId)
            }

            override fun convert(holder: ViewHolder?, t: String?, position: Int) {
                holder!!.setText(android.R.id.text1, t)
            }
        }
    }

    //单个类型item
    fun initRV1_1() {
        val adapter = object : CommonAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            Arrays.asList("a", "b", "c")
        ) {
            //非必须重写，这个可以自己控制viewholder的创建
            override fun createHolderInternal(
                context: Context,
                parent: ViewGroup,
                layoutId: Int
            ): ViewHolder {
                return super.createHolderInternal(context, parent, layoutId)
            }

            override fun convert(holder: ViewHolder?, t: String?, position: Int) {
                holder!!.setText(android.R.id.text1, t)
            }
        }

        RecyclerViewX(
            findViewById<View>(R.id.rv) as RecyclerView,
            this,
            LayoutManagerType.getLinearLayoutManager(RecyclerView.VERTICAL),
            adapter
        )

    }

    /**
     * 多种item情况
     */
    fun initRV2() {
        //预定义数据，实际项目中可以传入空list初始化
        val datas: MutableList<String> = mutableListOf()
        datas.addAll(listOf("a", "b", "c"))
        //item种类
        val delegates =
            arrayOf(DelegatePair<String>(1, delegate1()), DelegatePair<String>(2, delegate2()))
        //recyclerview
        val rv = findViewById<View>(R.id.rv) as RecyclerView
        //初始化recyclerview
        val rvHolder = RecyclerViewX(
            rv = rv,
            context = this,
            lmType = LayoutManagerType.getLinearLayoutManager(RecyclerView.VERTICAL),
            adapterDelegates = delegates,
            data = datas,
            createHolder = { context, parent, layoutId ->
                //自定义处理创建viewholder，也可以传null使用默认方法创建
                return@RecyclerViewX ViewHolder.createViewHolder(context, parent, layoutId)
            },
            config = { layoutManager ->
                //做一些其他处理
                layoutManager.apply {

                }
            }) {
            rv.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.HORIZONTAL
                )
            )
            adapter.onClick { view, holder, position ->
                Toast.makeText(
                    this@MainActivity.applicationContext,
                    "点击事件${adapter.datas[position]}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //更新数据
        rvHolder.update(datas)
    }

    /**
     * 不需要很多配置时
     */
    fun initRV3() {
        //预定义数据，实际项目中可以传入空list初始化
        val datas: MutableList<String> = mutableListOf()
        datas.addAll(listOf("a", "b", "c"))
        //item种类
        val delegates =
            arrayOf(DelegatePair<String>(1, delegate1()), DelegatePair<String>(2, delegate2()))
        //初始化recyclerview
        val rvHolder = RecyclerViewX(
            rv = findViewById<View>(R.id.rv) as RecyclerView,
            context = this,
            lmType = LayoutManagerType.getLinearLayoutManager(RecyclerView.VERTICAL),
            adapterDelegates = delegates,
            data = datas
        )
        //更新数据
        //rvHolder.update(datas)
    }

    inner class delegate1 : ItemViewDelegate<String> {
        override fun getItemViewLayoutId(): Int = R.layout.item_1

        override fun isForViewType(item: String?, position: Int): Boolean {
            val tmp = position % 2
            return tmp == 0
        }

        override fun convert(holder: ViewHolder?, t: String?, position: Int) {
            holder?.getView<TextView>(R.id.tv1)?.text = t.toString()
        }

    }

    inner class delegate2 : ItemViewDelegate<String> {
        override fun getItemViewLayoutId(): Int = R.layout.item_3

        override fun isForViewType(item: String?, position: Int): Boolean {
            val tmp = position % 2
            return tmp != 0
        }

        override fun convert(holder: ViewHolder?, t: String?, position: Int) {
            holder?.getView<TextView>(R.id.tv2)?.text = t.toString()
        }

    }


}