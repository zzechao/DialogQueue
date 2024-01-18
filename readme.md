## 介绍

`DialogQueue` 是一个可保活的弹窗队列库，可以在其他activity绑定显示，也可以在其他fragment绑定显示，还可以在其他activity和fragment都满足才显示；
只要设置保活变量isKeepALive，以及对应绑定activity或fragment的class；同时android应用一般情况下提供4种不同的dialog形式，（1）dialog（2）fragmentDialog
（3）viewDialog（4）activityDialog 可以同时使用，sdk已经构建了对应4种dialog的工厂模式，您只要实现具体dialog创建方法，同时你可以根据情况创建自己dialog的工厂模式，
去构建自己专属模式的dialog。

## 效果图

![image](https://github.com/zzechao/DialogQueue/blob/master/demo_display.png)

## 用法

clone 项目，自行导出aar库，（之后提供build.gradle支持）
