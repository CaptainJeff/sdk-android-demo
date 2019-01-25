package com.qingniu.qnble.demo.wrist.utils;

import android.util.Log;

import com.qingniu.qnble.demo.bean.WristSettingItem;
import com.yolanda.health.qnblesdk.bean.QNAlarm;
import com.yolanda.health.qnblesdk.bean.QNBandBaseConfig;
import com.yolanda.health.qnblesdk.bean.QNBandInfo;
import com.yolanda.health.qnblesdk.bean.QNBandMetrics;
import com.yolanda.health.qnblesdk.bean.QNCleanInfo;
import com.yolanda.health.qnblesdk.bean.QNHealthData;
import com.yolanda.health.qnblesdk.bean.QNRemindMsg;
import com.yolanda.health.qnblesdk.bean.QNSitRemind;
import com.yolanda.health.qnblesdk.constant.CheckStatus;
import com.yolanda.health.qnblesdk.listener.QNObjCallback;
import com.yolanda.health.qnblesdk.listener.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBandManager;
import com.yolanda.health.qnblesdk.out.QNUser;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author: hekang
 * @description:发送手环命令
 * @date: 2019/1/22 14:56
 */
public class WristSendUtils {
    private static final String TAG = "WristSendUtils";

    private QNBandManager mBandManager;

    public WristSendUtils(QNBandManager qnBandManager) {
        this.mBandManager = qnBandManager;
    }

    /**
     * 绑定手环
     */
    public Observable<WristSettingItem> bindBand(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.bindBand(new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 解绑手环
     */
    public Observable<WristSettingItem> unbindBand(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.cancelBind(new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 校验手环绑定的手机信息
     */
    public Observable<WristSettingItem> checkSameBindPhone(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.checkSameBindPhone(new QNObjCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean data, int code, String msg) {
                        item.setChecked(data);
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 获取手环的信息
     */
    public Observable<WristSettingItem> fetchBandInfo(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.fetchBandInfo(new QNObjCallback<QNBandInfo>() {
                    @Override
                    public void onResult(QNBandInfo data, int code, String msg) {
                        Log.d(TAG, "QNBandInfo:" + data);
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置手环的时间
     */
    public Observable<WristSettingItem> syncBandTime(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncBandTime(new Date(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置闹钟
     */
    public Observable<WristSettingItem> syncAlarm(final QNAlarm qnAlarm, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncAlarm(qnAlarm, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置目标
     */
    public Observable<WristSettingItem> syncGoal(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                int stepGoal = 0;
                try {
                    stepGoal = Integer.parseInt(item.getValue());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (stepGoal == 0) {
                    item.setErrorCode(-1);
                    item.setErrorMsg("设置的步数目标数据错误");
                    e.onNext(item);
                    e.onComplete();
                    return;
                }
                mBandManager.syncGoal(stepGoal, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置用户
     */
    public Observable<WristSettingItem> syncUser(final QNUser qnUser, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncUser(qnUser, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置手环度量
     */
    public Observable<WristSettingItem> syncMetrics(final QNBandMetrics qnBandMetrics, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncMetrics(qnBandMetrics, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置久坐提醒
     */
    public Observable<WristSettingItem> syncSitRemind(final QNSitRemind qnSitRemind, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncSitRemind(qnSitRemind, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置心率监测模式
     */
    public Observable<WristSettingItem> syncHeartRateObserverMode(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncHeartRateObserverMode(item.isChecked(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置寻找手机模式
     */
    public Observable<WristSettingItem> syncFindPhone(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncFindPhone(item.isChecked(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置拍照模式
     */
    public Observable<WristSettingItem> syncCameraMode(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncCameraMode(item.isChecked(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置抬腕识别是否开启
     */
    public Observable<WristSettingItem> syncHandRecognizeMode(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncHandRecognizeMode(item.isChecked(), new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 设置清除手环设置
     */
    public Observable<WristSettingItem> reset(final QNCleanInfo cleanInfo, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.reset(cleanInfo, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 重启手环
     */
    public Observable<WristSettingItem> reboot(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.reboot(new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 快捷设置手环基础配置
     */
    public Observable<WristSettingItem> syncFastSetting(final QNBandBaseConfig baseConfig, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncFastSetting(baseConfig, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 同步今天健康数据
     */
    public Observable<WristSettingItem> syncTodayHealthData(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncTodayHealthData(new QNObjCallback<QNHealthData>() {
                    @Override
                    public void onResult(QNHealthData data, int code, String msg) {
                        Log.d(TAG, "syncTodayHealthData:" + data);
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 同步历史健康数据
     */
    public Observable<WristSettingItem> syncHistoryHealthData(final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.syncHistoryHealthData(new QNObjCallback<List<QNHealthData>>() {
                    @Override
                    public void onResult(List<QNHealthData> datas, int code, String msg) {
                        Log.d(TAG, "syncHistoryHealthData:" + datas);
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }
                });
            }
        });

        return observable;
    }

    /**
     * 来电提醒
     */
    public Observable<WristSettingItem> callRemind(final String userPhone, final String userName, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.callRemind(userName, userPhone, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }

                });
            }
        });

        return observable;
    }

    /**
     * 消息提醒
     */
    public Observable<WristSettingItem> msgRemind(final QNRemindMsg remindMsg, final WristSettingItem item) {
        Observable<WristSettingItem> observable = Observable.create(new ObservableOnSubscribe<WristSettingItem>() {
            @Override
            public void subscribe(final ObservableEmitter<WristSettingItem> e) throws Exception {
                mBandManager.msgRemind(remindMsg, new QNResultCallback() {
                    @Override
                    public void onResult(int code, String msg) {
                        item.setChecked(code == CheckStatus.OK.getCode());
                        item.setErrorCode(code);
                        item.setErrorMsg(msg);
                        e.onNext(item);
                        e.onComplete();
                    }

                });
            }
        });

        return observable;
    }

}
