# pager_indicator
结合ViewPager2使用的指示器

root build.gradle添加

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  
引用

	dependencies {
	        implementation 'com.github.wukonget:pager_indicator:v0.2'
	}


用法

    <com.p.indicator.P2Indicator
        android:id="@+id/indicator"
        android:layout_width="match_parent"
        android:background="#33000000"
        android:layout_height="wrap_content"
        android:layout_marginTop="44dp"
        android:layout_marginBottom="50dp"
        android:padding="10dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:p2ItemGap="20dp"
        app:p2ItemHeight="10dp"
        app:p2ItemWidth="10dp"
        app:p2Mode="dot"
        app:p2NormalColor="@android:color/white"
        app:p2NormalDrawable="@drawable/ic_launcher_foreground"
        app:p2Orientation="horizontal"
        tools:layout_editor_absoluteX="0dp" />


属性

    p2Mode : 每个标识的样式 有 dot(圆点)   line(线段)  drawable(图片)
    p2ItemWidth : 每个标识宽度
    p2ItemHeight : 每个标识高度
    p2ItemGap : 两个标识之间距离
    p2NormalColor : 固定的标识的颜色
    p2SelectedColor : 移动的标识,即当前标识颜色,如果type是drawable并且没有设置p2SelectedDrawable,将会使用该颜色为p2NormalDrawable着色
    p2NormalDrawable : type为drawable时,固定标识的图片
    p2SelectedDrawable : type为drawable时,移动标识的图片
    p2Orientation : 控件方向  horizontal  vertical


提供一个方法供用户自定义绘制当前标识

    /**
         * 用户自己完成item绘制
         * @param mode item形状 DOT,LINE,DRAWABLE
         * @param focusPosition 当前页面
         * @param focusOffset  当前页面偏移量  0~1f
         * @param itemHeight
         * @param itemWidth
         * @param orientation 方向,1水平  2垂直
         */
        open fun customDrawFocusItem(
            canvas: Canvas?,
            paint: Paint,
            mode: Int,
            focusPosition: Int,
            focusOffset: Float,
            itemWidth: Int,
            itemHeight: Int,
            orientation:Int
        ): Boolean {
            return false
        }


