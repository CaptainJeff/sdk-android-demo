# 轻牛蓝牙Android SDK 

## 最新版本 `v0.5.6` [下载地址](https://github.com/YolandaQingniu/sdk-android-demo/releases/download/v0.5.6/qnsdk-0.5.6-Android.zip)

## SDK文件说明
### 统一引入
#### 导入demo项目的lib中的jar文件(qnsdk-xxx.jar)和对应的so文件

### 混淆配置(proguard-rules)
+ -keep class com.qingniu.scale.model.BleScaleData{*;}

### 操作
#### QNBleApi
该类为SDK的主要工作类，提供SDK的各种方法的操作

### 配置
#### QNConfig
该类为SDK的设置类，包括扫描的配置、连接的配置、秤单位显示等配置

### 测量过程主要使用方法
#### QNBleDeviceDiscoveryListener
提供扫描秤时的回调,回调的秤数据对象为QNBleDevice
#### QNBleDevice
扫描设备的对象

#### QNUser
由app向SDK提供的用户信息对象，设置连接时需要用到

#### QNBleConnectionChangeListener
提供秤在使用过程中各种蓝牙状态的回调以及测量过程中秤的测量状态

#### QNDataListener
提供测量数据的回调，包括实时体重、测量结果、存储数据
#### QNScaleData
测量结果数据对象
#### QNScaleStoreData
存储数据对象,可以通过`(QNScaleStoreData)generateScaleData` 获取`QNScaleData`对象
#### QNScaleItemData
每个指标的详细数据对象


### 错误信息
#### CheckStatus
展示了SDK中所有的错误信息


## SDK调用步骤

1. 初始化SDK `(QNBleApi)initSdk`
2. 开启扫描 `(QNBleApi)startBleDeviceDiscovery`
	+ 开启扫描需要先设置进行扫描设备的监听 `(QNBleApi)setBleDeviceDiscoveryListener`
3. 设置扫描配置 （未设置对象时，使用默认配置）
 	+ 获取配置信息 `(QNBleApi)getConfig()`
	+ 设置是否只扫描开机的秤 `onlyScreenOn`
	+ 设置扫描到秤时是否返回多次 `allowDuplicates`
	+ 设置扫描的时间 `duration`
	+ 设置秤端显示的单位 `unit`
4. 设置连接状态的监听 `(QNBleApi)setBleConnectionChangeListener`
5. 设置测量数据的监听 `(QNBleApi)setDataListener`
6. 构建连接秤的用户对象 `(QNBleApi)buildUser:String userId,int height,String gender,Date birthday,QNResultCallback callback`
7. 连接设备 `connectDevice:QNBleDevice device,QNUser user,QNResultCallback callback`



## 注意事项
- 必须在清单文件中申请蓝牙权限、位置权限、网络权限（离线SDK不需要）
- SDK最低支持API版本为18
- SDK中使用到了v4包的资源，开发者项目中需要引入v4包的依赖
- 必须在清单文件中添加SDK需要使用到的服务：
    ``` 
  <service android:name="com.qingniu.qnble.scanner.BleScanService"/>
  <service android:name="com.qingniu.scale.measure.ble.ScaleBleService"/>
    ```    
- 如果需要兼容广播秤，需要再注册如下服务：
    ``` 
  <service android:name="com.qingniu.scale.measure.broadcast.ScaleBroadcastService"/>
    ```
- targetSdkVersion 在23及以上，需要先获取定位权限，才能扫描到设备，需要开发者自己申请
- 如果你的项目是多进程的，建议限制在主进程才进行SDK的初始化

## 常见问题

1. 初始化提示appid错误
    + 检查初始化文件和使用的appid是否匹配
    + 检查引入的SDK是否是最新的
2. 扫描设备调用成功，但是一直没有设备回调，且无错误回调
    + 检查所扫描的设备，是否已经被其他人连接
    + 部分手机需要开启GPS才能扫描到设备，请检查手机GPS是否开启
3. 连接设备一直无法成功或者成功后很快就断开连接
    + 检查设备是否被其他人连接了
    + 在系统蓝牙中查看是否当前连接的设备已经被配对,如果已经配对，需要取消配对
    + 部分手机需要先扫描才能连接成功，先扫描设备再进行连接
4. 获取到的指标与和商务洽谈的指标数不同
    + 先检查出现问题的设备，扫描时显示的名称是否正确
    + 心率秤无论是否开放了心率指标，SDK都会下发心率指标
5. 数据或者设备等监听回调，同一时间回调多次
    + 先确定是否，设置了多次监听。当监听不使用时，一定要设置为null
    + 确定是否是穿鞋测量，这个可能导致短时间内，完成多次测量的情况

**`提示`**：遇到无法定位的问题，希望开发者能第一时间提供日志，以便我们尽快找到问题    
