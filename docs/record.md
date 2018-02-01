* clone from https://github.com/zxing/zxing
* create App application, improt module 'android' as 'zxing-android'
* 生成android-core和core的jar包，mvn compile/mvn package
* add as library for 'zxing-android'
* 提取config，用建造者传入配置

* 自定义二维码ImageView控件
* postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);指定范围内重绘