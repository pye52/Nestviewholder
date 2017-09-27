## 介绍

MultiType是一个很好的多类型列表视图库，它在实现一些复杂的recyclerview列表时非常方便，而作者所举的例子也确实适合大部分场景，但看过timemachine的源码后，我认为multitype仍然不太适合聊天应用的列表布局，毕竟timemachine聊天布局的adapter也只是用int值来区分布局类型。

主要原因在于multitype是通过类型与布局绑定，而在聊天应用列表里，一般都只有一个message类型，我们需要通过两种标识，来区分左右布局以及内容布局，而adapter的viewtype就是个int值。当然也可以在开发之初就定义好信息内容的子类型，但这样就变成项目必须去“适应”第三方库了，对于任何一个开发者来说这都是不好的习惯。不过multitype提出了一个很好的布局实现思路，参考[仿造微博的数据结构和二级ItemViewBinder](http://drakeet.me/multitype/#更多示例)。

<!-- more -->

这里插入介绍一下baserecyclerview(下称brvah)的multi布局，brvah是对adapter的一个封装，因此仍然是通过int值来区分每一个布局，但没办法和multitype搭配一起使用，这样每一个布局xml都需要携带所有要显示的控件，而部分控件冗余，给修改和扩展都带来不便，而且存在大量不同的布局，在同一屏幕上显示有限，实际viewholder是无法被有效缓存的(真机测试中也会发现onCreateViewHolder是会不断被调用)。

那么为什么不能将两种方式结合起来呢？先来分析一般情况下聊天界面的布局：

![接收的信息布局](https://github.com/pye52/Nestviewholder/blob/master/img/receive.png?raw=true)

![发送的信息布局](https://github.com/pye52/Nestviewholder/blob/master/img/send.png?raw=true)

![其余布局1](https://github.com/pye52/Nestviewholder/blob/master/img/other1.png?raw=true)

![其余布局2](https://github.com/pye52/Nestviewholder/blob/master/img/other2.png?raw=true)

如上，无论是发送出去的消息还是接收的消息，基本可以划分为：头像、名字、内容区。每一条消息的头像和名字(微信发送者是没有名字的)是固定的。至于采取发送的还是接收的信息布局，取决于消息体的发送者id是否与登录用户id一致(在getItemViewType作判断)，而内容区则根据消息的类型变化。由于用户发送的信息(向左布局)还需要携带发送状态，因此只要实现一左一右的两个固定外层布局即可(xml示例参考上面给出的multitype链接)。

对于内容区(以下将内容区的view称为itemview，即framelayout所添加的布局)，若不考虑缓存的话，简单利用工厂模式，在onBindViewHolder的时候根据消息体类型去生成对应的itemview并添加到framelayout上即可，如此其性能表现已经比为每一种viewtype创建一个布局要好得多了，而且对头像等固定要素只需要在onBindViewHolder处设置一遍。

当然我们不能止步于此，而是要实现像viewholder一样的缓存已创建好的itemview并复用，需要解决以下几个问题：

1. 如何获知viewholder绑定了哪个itemview
2. viewholder被回收后itemview的处理

针对第一个问题，则从viewholder本身入手，在onBindViewHolder时添加itemview并记录到viewholder的一个变量当中.

```java
@Override
public void onBindViewHolder(BaseViewHolder holder, int position) {
    MessageItem item = list.get(position);
    holder.setView(item);
    Nestitemview<MessageItem> itemView = cache.getItemView(item.getItemType(), holder);
    holder.addChild(itemView, item);
}
```

由于viewholder被回收时会调用onViewRecycled，在此方法处removeView，并将保存的itemview归还给cache缓存。

```java
@Override
public void onViewRecycled(BaseViewHolder holder) {
    cache.detachView(holder);
    super.onViewRecycled(holder);
}
```

大致流程如下图所示：

![发送的信息布局](https://github.com/pye52/Nestviewholder/blob/master/img/nestviewholder.png?raw=true)

这其实是非常简单的东西…但实际使用的时候发现了一个问题…当用户滑动速度非常快的时候，会出现viewholder未执行removeview就被再次显示到屏幕上了，这将会导致

1. itemview被添加到两个framelayout上导致出错
2. 某个framelayout添加了两个itemview导致重叠

因此在获取缓存中的itemview和绑定时，framelayout需要判断childviewcount是否为0，itemview需要判断parent是否为null

(这么简单的东西都能凑个1k多字，明显就是来骗star的Orz)

## 用法

1. 实现固定的外层布局，如图


![demo](https://github.com/pye52/Nestviewholder/blob/master/img/demo_xml.png?raw=true)


```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/message_time"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        tools:text="2017-09-01"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginTop="8dp"
        android:layout_marginRight="8dp"
        app:layout_constraintRight_toRightOf="parent"
        android:layout_marginLeft="8dp"
        app:layout_constraintLeft_toLeftOf="parent" />

    <ImageView
        android:id="@+id/avatar"
        android:layout_width="70dp"
        android:layout_height="70dp"
        android:src="@drawable/default_face"
        android:layout_marginRight="0dp"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginBottom="0dp"
        android:layout_marginTop="8dp"
        app:layout_constraintTop_toBottomOf="@id/message_time"
        app:layout_constraintVertical_bias="0.0" />

    <FrameLayout
        android:id="@+id/message_fl"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintRight_toLeftOf="@id/avatar"
        android:layout_marginRight="-1dp"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginBottom="8dp"/>

    <ImageView
        android:id="@+id/message_state"
        android:layout_width="25dp"
        android:layout_height="25dp"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginBottom="8dp"
        app:layout_constraintRight_toLeftOf="@id/message_fl"
        android:layout_marginRight="8dp" />
</android.support.constraint.ConstraintLayout>
```

2. viewholder继承Nestviewholder，构造方法初始化framelayout，例：

```java
public class BaseViewHolder extends Nestviewholder<MessageItem> {
    public BaseViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.message_fl);
    }

    public void setView(MessageItem item) {

    }
}
```

3. 实现内容区的布局及其工厂

   - 构造方法的itemview非framelayout，是具体的子布局view(可以是viewgroup，也可以只是一个view)

   - 工厂方法类似于创建viewholder，可以加载一个布局文件，也可以像下面一样直接创建view(因为显示文字信息需要只要一个textview)

     例：

```java
public class TextItemView extends Nestitemview<MessageItem> {
    private TextView textView;

    public TextItemView(View itemView) {
        super(itemView);
        textView = (TextView) itemView;
    }

    @Override
    public void dispatch(View itemView, MessageItem item) {
        textView.setText(item.getContent());
    }

    @Override
    public int getType() {
        return MessageType.TEXT;
    }
}

public class TextItemViewFactory implements NestitemviewFactory<MessageItem> {
    @Override
    public Nestitemview<MessageItem> create(Context context) {
        TextView view = new TextView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        return new TextItemView(view);
    }
}
```

4. 创NestviewholderCache实例，并注册对应的工厂，将cache传递给adapter管理

```java
NestviewholderCache<MessageItem> cache = new NestviewholderCache<>(this);
cache.registerFactory(MessageType.TEXT, new TextItemViewFactory());
...
MessageAdapter adapter = new MessageAdapter(this, list, cache);
```

5. onBindViewHolder时，根据message的类型获取对应的子view，并添加到viewholder的framelayout上(要对子view进行数据绑定，则调用dispatch方法)

```java
@Override
public void onBindViewHolder(BaseViewHolder holder, int position) {
    MessageItem item = list.get(position);
    holder.setView(item);
    Nestitemview<MessageItem> itemView = cache.getItemView(item.getItemType(), holder);
    holder.addChild(itemView, item);
}
```

6. onViewRecycled时，通知cache回收指定holder的view

```java
@Override
public void onViewRecycled(BaseViewHolder holder) {
    cache.detachView(holder);
    super.onViewRecycled(holder);
}
```

效果图：

![](https://github.com/pye52/Nestviewholder/blob/master/demo.png?raw=true)