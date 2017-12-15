# 使用两种方式完成四种弹性动画
### 前言
&emsp;&emsp;最近这段时间对弹性动画比较感兴趣，空闲就做了一下弹性动画的实现。网上对弹性动画的实现其实是有3种，属性动画设置spring插值器、facebook出的rebound以及google出的SpringAnimation。考虑到android的google背景以及想重温一下属性动画的使用，本博实现了第一种和第三种。

### 最终实现效果

![](https://github.com/yizhanzjz/ImageRepo/raw/master/springAni.gif)

&emsp;&emsp;注意上述动态图界面的title，第一个界面是属性动画差值器的实现，第二个界面是SpringAnimation的实现。每一种实现都做了四种常见的动画操作：缩放、平移、旋转、淡入淡出。

### 思路及代码
&emsp;&emsp;所谓“弹性动画”，其实就是控件的某个属性值在到达某个值之后在该值的左右来回变化（变得比改之大，或变得比该值小），最终稳定在该值的效果。这种变化的动画很像弹簧，所以就叫做“弹性动画”。这种变化作用在view的缩放参数（scaleX、scaleY）、平移参数（transactionX、transactionY）、旋转参数（rotation）、透明度参数（alpha）上会有物理运动的那种平滑过渡的效果，比直接到达该值的那种生硬好很多很多，这也是“弹性动画”的意义了。

#### &emsp;&emsp;插值器实现
&emsp;&emsp;属性动画的弹性效果实现，是利用插值器。而选择合适的插值器函数至关重要，网上的一篇文章直接给出了函数：pow(2, -10 * x) * sin((x - factor / 4) * (2 * PI) / factor) + 1，我们就可以利用这个函数创建自己的插值器实现此效果：
```
public class SpringInterpolator implements Interpolator {

    private float factor;

    public SpringInterpolator(float factor) {
        this.factor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        //factor = 0.4
//        pow(2, -10 * x) * sin((x - factor / 4) * (2 * PI) / factor) + 1

        return (float) (Math.pow(2, -10 * input) * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
    }
}
```
&emsp;&emsp;需要注意的是，此插值器除了在0~1之间线性变化的input这个输入参数外，还有一个factor的输入参数。那此参数是做什么的呢？我们可以做一个实验，做实验的地方是在[这个网站](http://inloop.github.io/interpolator/)，此处的Library选择Spring，然后Equation中的内容就变成了我们上面说的那个方程。我们看到当factor为0.4时，曲线图是这样的：

![](https://github.com/yizhanzjz/ImageRepo/raw/master/factor0.4.png)

&emsp;&emsp;我们修改factor的值为0.1，曲线图变成了这样：

![](https://github.com/yizhanzjz/ImageRepo/raw/master/factor0.1.png)

&emsp;&emsp;再次修改factor的值为0.9，曲线图是这样：

![](https://github.com/yizhanzjz/ImageRepo/raw/master/factor0.9.png)

&emsp;&emsp;由上述三图就可以得出结论：factor的值越小，值来回变化的次数越多，对应到具体的动画就是：factor值越小，view来回缩放的次数越多，平移到指定位置后在指定位置上下或左右摆动的次数也越多，旋转和淡入淡出类似。

&emsp;&emsp;下面看一下属性动画的实现部分，因为属性动画的使用都是差不多，这里只列出其中的一次使用：
```
//创建两个对象动画的实例和将这个实例组合起来的组合对象实例
ObjectAnimator objectAnimator0 = null;
ObjectAnimator objectAnimator1 = null;
AnimatorSet animatorSet = new AnimatorSet();

//指定修改view的哪个属性及属性的起始值和结束值
objectAnimator0 = ObjectAnimator.ofFloat(imageview, "scaleX", 1.0f, 2.0f);
objectAnimator1 = ObjectAnimator.ofFloat(imageview, "scaleY", 1.0f, 2.0f);

//指定两个对象动画的执行顺序
animatorSet.playTogether(objectAnimator0, objectAnimator1);
//指定两个动画组合之后的执行时间
animatorSet.setDuration(2500);
//指定插值器
animatorSet.setInterpolator(new SpringInterpolator(0.3f));
//启动动画
animatorSet.start();
        
```
&emsp;&emsp;属性动画插值器值得说的就这么多（插值器函数中factor的作用、属性动画的基本使用），后面会给出完整源码。

#### &emsp;&emsp;SpringAnimation实现
&emsp;&emsp;Google有专门的一个包用于实现此弹性效果，在gradle脚本文件中添加"compile 'com.android.support:support-dynamic-animation:25.+'"引入此包。这个包的使用也很简单，这里给出一个其中一种动画的实现代码：
```
//创建两个弹性动画对象
SpringAnimation springAnimation0 = null;
SpringAnimation springAnimation1 = null;

//指定处理view的哪个属性，以及view此属性的最终值(2.0f)
springAnimation0 = new SpringAnimation(imageview, new FloatPropertyCompat<ImageView>("scaleX") {
        @Override
        public float getValue(ImageView object) {
            float scaleX = object.getScaleX();
            return scaleX;
        }
    
        @Override
        public void setValue(ImageView object, float value) {
            object.setScaleX(value);
        }
    }, 2.0f);
    springAnimation1 = new SpringAnimation(imageview, new FloatPropertyCompat<ImageView>("scaleY") {
        @Override
        public float getValue(ImageView object) {
            float scaleY = object.getScaleY();
            return scaleY;
        }
    
        @Override
        public void setValue(ImageView object, float value) {
            object.setScaleY(value);
        }
    }, 2.0f);
    
//作一些设置，要不然肉眼看不出来生效
springAnimation0.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_ALPHA);
springAnimation1.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_ALPHA);

//指定此弹性动画的弹性阻尼
springAnimation0.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
//指定此弹性动画的弹性生硬度
springAnimation0.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

//如上
springAnimation1.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
springAnimation1.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

//启动动画
springAnimation0.start();
springAnimation0.start();
        
```
&emsp;&emsp;需要说的几个点：

- 此处的弹性动画只指定了属性的最终值，而没有指定属性的起始值。因为该包会根据当前view的位置自动获取起始值，所以无需我们指定；
- FloatPropertyCompat，虽然代码量看着多，其实不难。构造函数中传入相关view属性，两个抽象方法完成对此属性的读写；
- springAnimation0.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_ALPHA);在做缩放动画时，setMinimumVisibleChange方法的调用是必须的，如果不调用缩放就没有弹性效果。我也是看了一部分此包的实现源码才发现这点的。后面的完整代码里会针对不同的动画给出不同的setMinimumVisibleChange方法调用；
- setDampingRatio，就是设置摩擦力的，摩擦力越大弹起来越费劲，摩擦力越小弹起来越轻松；
- setStiffness，这个是设置弹性生硬度的。显示中弹簧给人的感觉是，如果它钢性越强弹起来也越费劲钢性不那么强的弹起来轻松；
- 查了一篇资料，资料中说，dampingRatio（即摩擦力）越大，摆动次数越少，反之则越多；stiffness（即生硬度、钢性）越大，摆动时间越短，反之则越长。通俗解释，可感知。

### 总结
&emsp;&emsp;使用插值器实现弹性动画，最核心的是找到弹性插值器的函数，而此函数已经是前人栽树后人乘凉了，查资料可以查得；而SpringAnimation的实现，重点是要懂SpringAnimation的基本使用和知道怎么使用setMinimumVisibleChange使弹性效果肉眼可见。

