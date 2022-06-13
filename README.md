# 新增特性

1. 可以干预viewholder的创建
2. 使用kotlin编写了便捷方法

* 多种viewholder情况：

```
fun initRV() {
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
            adapter.clickEvent { view, holder, position ->
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
```

* 单一viewholder情况

```
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
```



# base-adapter

Android 万能的Adapter for RecyclerView,  ~~ListView, GridView~~等，支持多种Item类型的情况。已添加androidX支持。


## 引入

 1.引入jitpack
 ```
 allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
 ```
2.添加以下库
```
implementation 'com.github.Knightwood:baseAdapter:dev-1.0'
```



## 使用

##（1）简单的数据绑定(ListView与其使用方式一致)

首先看我们最常用的单种Item的书写方式：

```
mRecyclerView.setAdapter(new CommonAdapter<String>(this, R.layout.item_list, mDatas)
{
    @Override
    public void convert(ViewHolder holder, String s)
    {
        holder.setText(R.id.id_item_list_title, s);
    }
});
```
是不是相当方便，在convert方法中完成数据、事件绑定即可。


只需要简单的将Adapter继承CommonAdapter，复写convert方法即可。省去了自己编写ViewHolder等大量的重复的代码。

* 可以通过holder.getView(id)拿到任何控件。
* ViewHolder中封装了大量的常用的方法，比如holder.setText(id,text)，holder.setOnClickListener(id,listener)等，可以支持使用。

效果图：

<img src="screenshot/single.png" width="320px"/>

##（2）多种ItemViewType(ListView与其使用方式一致)

对于多中itemviewtype的处理参考：https://github.com/sockeqwe/AdapterDelegates ，具有极高的扩展性。


```
MultiItemTypeAdapter adapter = new MultiItemTypeAdapter(this,mDatas);
adapter.addItemViewDelegate(new MsgSendItemDelagate());
adapter.addItemViewDelegate(new MsgComingItemDelagate());
```

每种Item类型对应一个ItemViewDelegete，例如：

```
public class MsgComingItemDelagate implements ItemViewDelegate<ChatMessage>
{

    @Override
    public int getItemViewLayoutId()
    {
        return R.layout.main_chat_from_msg;
    }

    @Override
    public boolean isForViewType(ChatMessage item, int position)
    {
        return item.isComMeg();
    }

    @Override
    public void convert(ViewHolder holder, ChatMessage chatMessage, int position)
    {
        holder.setText(R.id.chat_from_content, chatMessage.getContent());
        holder.setText(R.id.chat_from_name, chatMessage.getName());
        holder.setImageResource(R.id.chat_from_icon, chatMessage.getIcon());
    }
}
```

贴个效果图：

<img src="screenshot/rvadapter_01.png" width="360px"/>




##(3) 添加HeaderView、FooterView

```
mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);

TextView t1 = new TextView(this);
t1.setText("Header 1");
TextView t2 = new TextView(this);
t2.setText("Header 2");
mHeaderAndFooterWrapper.addHeaderView(t1);
mHeaderAndFooterWrapper.addHeaderView(t2);

mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
mHeaderAndFooterWrapper.notifyDataSetChanged();
```

类似装饰者模式，直接将原本的adapter传入，初始化一个HeaderAndFooterWrapper对象，然后调用相关API添加。

##(4) 添加LoadMore

```
mLoadMoreWrapper = new LoadMoreWrapper(mOriginAdapter);
mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener()
{
    @Override
    public void onLoadMoreRequested()
    {
    }
});

mRecyclerView.setAdapter(mLoadMoreWrapper);

```
直接将原本的adapter传入，初始化一个LoadMoreWrapper对象，然后调用相关API即可。

##(5)添加EmptyView

```
mEmptyWrapper = new EmptyWrapper(mAdapter);
mEmptyWrapper.setEmptyView(R.layout.empty_view);

mRecyclerView.setAdapter(mEmptyWrapper );

```

直接将原本的adapter传入，初始化一个EmptyWrapper对象，然后调用相关API即可。


支持链式添加多种功能，示例代码：

```
mAdapter = new EmptyViewWrapper(
	new LoadMoreWrapper(
	new HeaderAndFooterWrapper(mOriginAdapter)));
```


## 一些回调

### onViewHolderCreated

```
mListView.setAdapter(new CommonAdapter<String>(this, R.layout.item_list, mDatas)
{
    @Override
    public void convert(ViewHolder holder, String o, int pos)
    {
        holder.setText(R.id.id_item_list_title, o);
    }

    @Override
    public void onViewHolderCreated(ViewHolder holder, View itemView)
    {
        super.onViewHolderCreated(holder, itemView);
       //AutoUtil.autoSize(itemView)
    }
});
```


## 感谢


* [https://github.com/JoanZapata/base-adapter-helper](https://github.com/JoanZapata/base-adapter-helper)

    应该是我所知道的最早的对listview,gridview封装的adapter了，参考了其中很多，比如在`ViewHolder.setXXX`类的辅助方法。

* [https://github.com/sockeqwe/AdapterDelegates](https://github.com/sockeqwe/AdapterDelegates)
	
	参考该库多种ItemType绑定数据方式

* [https://github.com/ragunathjawahar/simple-section-adapter](https://github.com/ragunathjawahar/simple-section-adapter)

    `SectionAdapter`对其进行了参考。

