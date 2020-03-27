# MockUp
这是我完成Hencoder自定义绘制后的练习作品，具体内容访问[Hencoder官网仿写练习网页](https://hencoder.com/activity-mock-2/)。

## 2020-3-27 Flipboard仿写
<img src="/images/4parts.png" width="96">  
我经历几次尝试和失败后，在将Demo.gif文件转化为每一帧后找到了实现思路。首先将图像分为如图四个部分，图像动画可以分为如下步骤去（为了简便，向屏幕外旋转称为升起，向屏幕选择称为降落）：   


1. 2和4部分升起α角度  
2. 随后2， 4落下，然后1， 2升起  
3. 随后1， 2落下，然后1， 3升起  
4. 随后1， 3落下， 然后3， 4升起  
5. 最后， 1， 2升起90° - α角度

从hencoder practice 4最后的一个练习可以看出Step 1和5非常容易实现，使用canvas.clip方法和camera.rotate方法分块绘制，可以完成翻页角度的动画。关键是在Step2， 3， 和4。可以发现的是Step 2, 3, 4都在重复
一个落下升起的周期运动，只不过把这个运动再不同方向重复。从这一点，只需完成落下升起动画，然后将canvas旋转到不同方位绘制即可。如图， 此时动画中保持静止的部分一定是落在旋转后canvas的裁剪范围内。可以想象canvas沿着中心，做平面转动，此时落在屏幕中轴线左边的便是已经落下的区域，即静止区域
。图像剩下的部分就负责产生翻页的角度，如此就完了2，3， 4步骤。  
<img src="/images/explanation-flipboard.png" width="400">    
Step 1过后翻页角度是alpha，而Step 4过后，翻页角度同样是alpha。所以翻页角度的变化有如下两种可能：翻页角度保持alpha不变；或者翻页角度经历了从alpha到0再到alpha的多个循环。经过
实际动画效果的检验，发现第一种可能的动画效果最为流畅。以下是最终效果图。  
<img src="/images/demo-flipboard.png" width="400">     


// TO-DO:  


1. 我继承的是ImageView，希望能接用xml的src属性和测量，布局的方法。但是发现ImageView会在最终测量范围里裁剪超出图像的部分。这就是的旋转的部分形变会出现不完整，目前的解决办法是将
ImageView的padding设置到48dp，增大ImageView的尺寸，同时保持和屏幕的间距。  
2. 该动画预计是用来做开场动画，增强branding效果的。所以再动画完成后，应该register一个animationEndListener，在这个callback中，设置从渲染线程发出的Message，然后通过handler
使得UI线程采取transition到下一个页面。


##### 参考资料
[1] 贾元斌，https://github.com/sunnyxibei/HenCoderPractice  
[2] gif文件转化为帧图片工具网页，https://ezgif.com  
[3] Hencoder 自定义绘制 Practice 4, https://hencoder.com/ui-1-4/  

