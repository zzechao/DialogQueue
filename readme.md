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
     * 调用DialogEx的addActivityDialog方法添加ActivityDialog进队列, builder要用到Coroutines的suspendCancellableCoroutine返回activity的对象
     * @param extra 传递信息字段
     * @param builder 弹窗对象构建的闭包方法
     * @return 工厂id
     */
    fun addActivityDialog(extra: String = "", builder: suspend (Activity, String) -> ComponentActivity): Int{}

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
     * 调用DialogEx的addFragmentDialog方法添加FragmentDialog进队列
     * @param extra 传递信息字段
     * @param builder 弹窗对象构建的闭包方法
     * @return 工厂id
     */
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
     * 调用DialogEx的addCommonDialog方法添加CommonDialog进队列
     * @param extra 传递信息字段
     * @param builder 弹窗对象构建的闭包方法
     * @return 工厂id
     */
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
     * builder要用到Coroutines的suspendCancellableCoroutine在doOnAttach返回view的对象，这里用了XPopup快速开发
     * @param extra 传递信息字段
     * @param builder 弹窗对象构建的闭包方法
     * @return 工厂id
     */
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
