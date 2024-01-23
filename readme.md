## 介绍

`DialogQueue` 是一个可保活的弹窗队列库，可以在其他activity绑定显示，也可以在其他fragment绑定显示，还可以在其他activity和fragment都满足才显示；
只要设置保活变量isKeepALive，以及对应绑定activity或fragment的class；同时android应用一般情况下提供4种不同的dialog形式，（1）dialog（2）fragmentDialog
（3）viewDialog（4）activityDialog
可以同时使用，sdk已经构建了对应4种dialog的工厂模式，您只要实现具体dialog创建方法，同时你可以根据情况创建自己dialog的工厂模式，
去构建自己专属模式的dialog。

## 效果图

![image](https://github.com/zzechao/DialogQueue/blob/master/demo_display.png)

## 用法

根目录build.gradle中添加mavenCentral()

```groovy
    repositories {
    mavenCentral()
}
```

model build.gradle添加

```groovy
     implementation "io.github.zzechao:dialogqueue:1.0.0"
```

当前版本：1.0.0

### dialog创建队列弹窗class，都要继承IDialog

```kotlin
    /**
 * dialog 的实现类要继承IDialogQ接口,用于判断当前生命周期的activity或者fragment是不是队列的弹窗
 */
class ActivityDialog : AppCompatActivity(), IDialogQ {}
class CommonDialog(context: Context) : BaseDialog(context), IDialogQ {}
class FragmentDialog : DialogFragment(), IDialogQ {}
class ViewDialog(context: Context, val content: String) : CenterPopupView(context), IDialogQ {}
```

#### 添加activityDialog

```kotlin
    /**
 * 调用DialogEx的addActivityDialog方法添加ActivityDialog进队列,
 * builder要用到Coroutines的suspendCancellableCoroutine返回activity的对象（com.zhouz.dialogqueue.DialogEx.addActivityDialog）
 * @param extra 传递信息字段
 * @param builder 弹窗对象构建的闭包方法
 * @return 工厂id
 */
fun addActivityDialog(
    extra: String = "",
    builder: suspend (Activity, String) -> ComponentActivity
): Int {
}

DialogEx.addActivityDialog("${index + 1}") { activity, extra ->
    withTimeout(2000L) {
        suspendCancellableCoroutine {
            val callbacks = object : DefaultActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    it.resume(activity as ComponentActivity)
                    activity.application.unregisterActivityLifecycleCallbacks(this)
                }
            }
            val content = "测试 addActivityDialog $extra"
            activity.application.registerActivityLifecycleCallbacks(callbacks)
            val intent = Intent(activity, ActivityDialog::class.java)
            intent.putExtra("content", content)
            activity.startActivity(intent)
            it.invokeOnCancellation {
                activity.application.unregisterActivityLifecycleCallbacks(callbacks)
            }
        }
    }
}
```

#### 添加fragmentDialog

```kotlin
    /**
 * 调用DialogEx的addFragmentDialog方法添加FragmentDialog进队列（com.zhouz.dialogqueue.DialogEx.addFragmentDialog）
 * @param extra 传递信息字段
 * @param builder 弹窗对象构建的闭包方法
 * @return 工厂id
 */
fun addFragmentDialog(
    extra: String = "",
    builder: suspend (Activity, String) -> DialogFragment
): Int

DialogEx.addFragmentDialog("${index + 1}") { activity, extra ->
    val content = "测试 addFragmentDialog $extra"
    val fragmentDialog = FragmentDialog.newInstance(extra, content)
    fragmentDialog.show((activity as FragmentActivity).supportFragmentManager, "FragmentDialog")
    fragmentDialog
}
```

#### 添加commonDialog

```kotlin
    /**
 * 调用DialogEx的addCommonDialog方法添加CommonDialog进队列（com.zhouz.dialogqueue.DialogEx.addCommonDialog）
 * @param extra 传递信息字段
 * @param builder 弹窗对象构建的闭包方法
 * @return 工厂id
 */
fun addCommonDialog(extra: String = "", builder: suspend (Activity, String) -> Dialog): Int

DialogEx.addCommonDialog("${index + 1}") { activity, extra ->
    val dialog = CommonDialog(activity)
    dialog.setTitle("CommonDialog")
    dialog.setContent("测试 addCommonDialog $extra")
    dialog.show()
    dialog
}
```

#### 添加viewDialog

```kotlin
    /**
 * 调用DialogEx的addViewDialog方法添加ViewDialog进队列,
 * builder要用到Coroutines的suspendCancellableCoroutine在doOnAttach返回view的对象，
 * 这里用了XPopup快速开发（com.zhouz.dialogqueue.DialogEx.addViewDialog）
 * @param extra 传递信息字段
 * @param builder 弹窗对象构建的闭包方法
 * @return 工厂id
 */
fun addViewDialog(extra: String = "", builder: suspend (Activity, String) -> View): Int

DialogEx.addViewDialog("${index + 1}") { activity, extra ->
    withTimeout(2000L) {
        suspendCancellableCoroutine { con ->
            val content = "测试 addViewDialog $extra"
            val view = ViewDialog(activity, content)
            view.doOnAttach {
                con.resume(it)
            }
            XPopup.Builder(activity)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .isViewMode(true)
                .isLightStatusBar(true)// 是否是亮色状态栏，默认false;亮色模式下，状态栏图标和文字是黑色
                .customHostLifecycle((activity as AppCompatActivity).lifecycle)
                .asCustom(view)
                .show()
            con.invokeOnCancellation {
                view.dismiss()
            }
        }
    }
}
```

### 保活dialog构造，IBuildFactory自定义dialog构造工厂，T泛型是dialog的class

不保活factory，已经构建对应的4个，分别是BaseDialogActivityBuilderFactory、BaseDialogCommonBuilderFactory、
BaseDialogFragmentBuilderFactory、BaseDialogViewBuilderFactory

保活factory，可以参考demo中ActivityDialogFactory、CommonDialogFactory、
FragmentDialogFactory、ViewDialogFactory等

```kotlin
    /**
     * 自定义dialog的工厂构建接口，
     * 1、dialogID用于队列中factory移除，自定义时保持单一原则
     * 2、extra透传参数，在fun buildDialog中作为参数
     * 3、fun priority：dialog的构造的在队列执行优先级，倒序
     * 4、fun bindActivity：绑定的activity，isKeepALive是true下起作用
     * 5、fun bindFragment：绑定的fragment，isKeepALive是true下起作用
     * 6、fun isKeepALive：是否保活
     * 7、dialogPartInterceptors：拦截器，处理自定义dialog在特定场景下不显示，暂时跳过
     * 8、suspend fun buildDialog：创建dialog，挂起回调dialog的对象(activity、view中要到挂起)
     * 9、suspend fun attachDialogDismiss：构建dialog的dismiss监听，成功返回true
     */
    interface IBuildFactory<T> {
        /**
         * 弹窗ID
         */
        val dialogID: Int
    
        /**
         * 透传字段
         */
        var extra: String
    
        /**
         * 优先级
         */
        fun priority(): Int {
            return 1
        }
    
        /**
         * 绑定的activity
         */
        fun bindActivity(): Array<KClass<out Activity>> {
            return arrayOf()
        }
    
        /**
         * 绑定的fragment
         */
        fun bindFragment(): Array<KClass<out Fragment>> {
            return arrayOf()
        }
    
        /**
         * 是否保活
         */
        fun isKeepALive(): Boolean {
            return false
        }
    
        /**
         * 数据处理或者拦截器
         */
        fun dialogPartInterceptors(): Array<out IPartInterceptor> {
            return arrayOf()
        }
    
        /**
         * 构建对应的dialog信息
         */
        suspend fun buildDialog(activity: Activity, extra: String): T?
    
        /**
         * 构建不同类似的dialog的消失方法
         */
        suspend fun attachDialogDismiss(): Boolean
    }
```