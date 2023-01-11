# MVI

###### 1、回顾MVVM

想要MVVM的数据单向流动我们我们可能会碰到如下问题：

- 为了保证数据流的单向流动，LiveData向外暴露时需要转化成immutable的，这需要添加不少模板代码并且容易遗忘。
- View层通过调用ViewModel层的方法来交互的，View层与ViewModel的交互比较分散，不成体系。

###### 2、MVI简介

MVI与MVVM很相似，其借鉴了前端框架的思想，更加强调数据的单向流动和唯一数据源。

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvi1.png)

- Model：与MVVM中的Model不同的是，MVI的Model主要指UI状态（State）。例如页面加载状态、控件位置等都是一种UI状态
- View：Ui视图
- I：Intent，此Intent不是Activity的Intent，用户的任何操作都被包装成Intent后发送给Model层进行数据请求。

不过在设计时我们使用ViewModel来承担MVI的model层，这样整体架构与MVVM类似，主要区别是Model与View的交互部分

![](https://gitee.com/sunnnydaydev/my-pictures/raw/master/architecture/mvi2.png)

MVI强调数据的单向流动，主要分为以下几步，数据永远在一个环形结构中单向流动，不能反向流动：

（1）用户操作以Intent的形式通知ViewModel（向viewModel发送个Event事件）

（2）Model基于Intent更新State（ViewModel接受到事件处理事件，获取事件结果）

（3）View接收到State变化刷新UI（ViewModel吧事件结果回调给View，View中监听更新UI）

###### 3、MVI实现



