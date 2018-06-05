package com.qingniu.qnble.demo.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qingniu.qnble.demo.R;
import com.qingniu.qnble.demo.SettingActivity;
import com.qingniu.qnble.demo.bean.Config;
import com.qingniu.qnble.demo.bean.User;
import com.qingniu.qnble.demo.util.AndroidPermissionCenter;
import com.qingniu.qnble.demo.util.UserConst;
import com.qingniu.qnble.utils.QNLogUtils;
import com.yolanda.health.qnblesdk.listen.QNBleDeviceDiscoveryListener;
import com.yolanda.health.qnblesdk.listen.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBleApi;
import com.yolanda.health.qnblesdk.out.QNBleDevice;
import com.yolanda.health.qnblesdk.out.QNConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.scan_measuring)
    TextView mScanMeasuring;
    @BindView(R.id.scan_setting)
    TextView mScanSetting;
    @BindView(R.id.scan_appid)
    TextView mScanAppid;
    @BindView(R.id.scanBtn)
    Button mScanBtn;
    @BindView(R.id.stopBtn)
    Button mStopBtn;
    @BindView(R.id.scan_measuring_info)
    TextView mScanMeasuringInfo;
    @BindView(R.id.listView)
    ListView mListView;

    private QNBleApi mQNBleApi;
    private User mUser;
    private Config mConfig;
    private QNConfig mQnConfig;

    public static Intent getCallIntent(Context context, User user, Config mConfig) {
        return new Intent(context, ScanActivity.class)
                .putExtra(UserConst.CONFIG, mConfig)
                .putExtra(UserConst.USER, user);
    }

    private BaseAdapter listAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return devices.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, null);
            }
            TextView nameTv = (TextView) convertView.findViewById(R.id.nameTv);
            TextView macTv = (TextView) convertView.findViewById(R.id.macTv);
            TextView rssiTv = (TextView) convertView.findViewById(R.id.rssiTv);

            QNBleDevice scanResult = devices.get(position);

            nameTv.setText(scanResult.getName());
            macTv.setText(scanResult.getMac());
            rssiTv.setText(String.valueOf(scanResult.getRssi()));


            return convertView;
        }
    };

    private List<QNBleDevice> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mQNBleApi = QNBleApi.getInstance(this);
        //动态申请权限(Android6.0以后需要)
        AndroidPermissionCenter.verifyPermissions(this);
        initIntent();
        initData();

        mListView.setAdapter(this.listAdapter);

        mListView.setOnItemClickListener(this);

        mQNBleApi.setBleDeviceDiscoveryListener(new QNBleDeviceDiscoveryListener() {
            @Override
            public void onDeviceDiscover(QNBleDevice device) {
                devices.add(device);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onStartScan() {
                Log.d("ScanActivity","onStartScan");
            }

            @Override
            public void onStopScan() {
                Log.d("ScanActivity","onStopScan");
            }
        });

    }

    private void initData() {
//        mQnConfig = mQNBleApi.getConfig();//获取上次设置的对象
        mQnConfig = new QNConfig();
        mQnConfig.setAllowDuplicates(mConfig.isAllowDuplicates());
        mQnConfig.setDuration(mConfig.getDuration());
        mQnConfig.setUnit(mConfig.getUnit());
        mQnConfig.setOnlyScreenOn(mConfig.isOnlyScreenOn());

        mScanAppid.setText("UserId : " + mUser.getUserId());
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mUser = intent.getParcelableExtra(UserConst.USER);
            mConfig = intent.getParcelableExtra(UserConst.CONFIG);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void startScan() {
        mQNBleApi.startBleDeviceDiscovery(mQnConfig, new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
                Log.d("ScanActivity", "code:" + code + ";msg:" + msg);
            }
        });
    }

    private void stopScan() {
        mQNBleApi.stopBleDeviceDiscovery(new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0 || position >= this.devices.size()) {
            return;
        }
        stopScan();
        QNBleDevice device = this.devices.get(position);
        //连接设备
        connectDevice(device);
    }

    private void connectDevice(QNBleDevice device) {
        startActivity(ConnectActivity.getCallIntent(this, mUser, device));
    }


    @OnClick({R.id.scan_setting, R.id.scanBtn, R.id.stopBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_setting:
                startActivity(SettingActivity.getCallIntent(this));
                finish();
                break;
            case R.id.scanBtn:
                this.devices.clear();

                listAdapter.notifyDataSetChanged();
                startScan();
                break;
            case R.id.stopBtn:
                stopScan();
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AndroidPermissionCenter.REQUEST_EXTERNAL_STORAGE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
