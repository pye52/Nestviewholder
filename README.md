1. 实现固定的外层布局，如

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

2. viewholder实现接口INestviewholder，需要一个能装载其他view的viewgroup布局，并实现对应方法，例：

```java
public class BaseViewHolder extends RecyclerView.ViewHolder implements INestviewholder {
        private FrameLayout frameLayout;

        public BaseViewHolder(View itemView) {
            super(itemView);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.message_fl);
        }

        public void setView(MessageItem item) {

        }

        @Override
        public void addChild(View view) {
            frameLayout.addView(view);
        }

        @Override
        public View removeChild() {
            View view = frameLayout.getChildAt(0);
            frameLayout.removeViewAt(0);
            return view;
        }
    }
```

3. 实现内容区的布局及其工厂

   - Nestitemview构造方法传递的itemview是子view的根布局(非外层边框的framelayout)

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
    Nestitemview<MessageItem> itemView = cache.getItemView(item.getItemType(), position);
    itemView.dispatch(holder.itemView, item);
    holder.addChild(itemView.getItemView());
}
```

6. onViewRecycled时，通知cache回收指定holder的view

```java
@Override
public void onViewRecycled(BaseViewHolder holder) {
    super.onViewRecycled(holder);
    holder.removeChild();
    cache.detachView(holder.getAdapterPosition());
}
```

效果图：

![](https://github.com/pye52/Nestviewholder/blob/master/demo.png?raw=true)