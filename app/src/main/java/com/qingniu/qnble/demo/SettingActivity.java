package com.qingniu.qnble.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.qingniu.qnble.demo.bean.Config;
import com.qingniu.qnble.demo.bean.User;
import com.qingniu.qnble.demo.picker.DatePickerDialog;
import com.qingniu.qnble.demo.picker.HeightPickerDialog;
import com.qingniu.qnble.demo.util.DateUtils;
import com.qingniu.qnble.demo.util.ToastMaker;
import com.qingniu.qnble.demo.view.ScanActivity;
import com.qingniu.qnble.demo.view.SystemScanActivity;
import com.yolanda.health.qnblesdk.constant.QNInfoConst;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: yolanda-XY
 * date: 2018/3/23
 * package_name: com.qingniu.qnble.demo
 * description: ${设置用户信息界面}
 */

public class SettingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @BindView(R.id.user_shape_spinner)
    Spinner shapeSpn;
    @BindView(R.id.user_goal_spinner)
    Spinner goalSpn;

    @BindView(R.id.user_id_edt)
    EditText mUserIdEdt;
    @BindView(R.id.user_male_rb)
    RadioButton mUserMaleRb;
    @BindView(R.id.user_female_rb)
    RadioButton mUserFemaleRb;
    @BindView(R.id.user_height_tv)
    TextView mUserHeightTv;
    @BindView(R.id.user_birthday_tv)
    TextView mUserBirthdayTv;
    @BindView(R.id.ble_scan_first_time)
    RadioButton mBleScanFirstTime;
    @BindView(R.id.ble_scan_every_time)
    RadioButton mBleScanEveryTime;
    @BindView(R.id.user_unit_kg)
    RadioButton mUserUnitKg;
    @BindView(R.id.user_unit_lb)
    RadioButton mUserUnitLb;
    @BindView(R.id.user_unit_jin)
    RadioButton mUserUnitJin;
    @BindView(R.id.user_unit_st)
    RadioButton mUserUnitSt;
    @BindView(R.id.user_gender_grp)
    RadioGroup mUserGenderGrp;
    @BindView(R.id.user_calc_grp)
    RadioGroup mUserCalcGrp;
    @BindView(R.id.ble_scan_grp)
    RadioGroup mBleScanGrp;
    @BindView(R.id.user_unit_grp)
    RadioGroup mUserUnitGrp;
    @BindView(R.id.btn_sure)
    Button mSure;
    @BindView(R.id.btn_system_scan)
    Button btn_system_scan;

    @BindView(R.id.scan_timeEt)
    EditText mScanEt;
    @BindView(R.id.scan_out_timeEt)
    EditText mScanOutEt;
    @BindView(R.id.connect_out_timeEt)
    EditText mConnectOutEt;

    private Config mBleConfig; //蓝牙配置对象
    private String mGender = "male";//用户性别
    private int mHeight = 172; //用户身高
    private Date mBirthday = null; //用户生日

    private User mUser;

    public static Intent getCallIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initData() {
        mBleConfig = new Config();
        mUser = new User();
    }


    private void initView() {
        mBirthday = DateUtils.getDate(1990, 1, 1);
        mUserBirthdayTv.setText(DateUtils.getBirthdayString(mBirthday, SettingActivity.this));
    }


    private void initListener() {
        mUserGenderGrp.setOnCheckedChangeListener(this);
        mUserCalcGrp.setOnCheckedChangeListener(this);
        mBleScanGrp.setOnCheckedChangeListener(this);
        mUserUnitGrp.setOnCheckedChangeListener(this);
        mUserHeightTv.setOnClickListener(this);
        mUserBirthdayTv.setOnClickListener(this);
        mSure.setOnClickListener(this);
        btn_system_scan.setOnClickListener(this);

        shapeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUser.setChoseShape(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mUser.setChoseShape(0);

            }
        });

        goalSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUser.setChoseGoal(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mUser.setChoseGoal(0);

            }
        });

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.user_male_rb:
                mGender = QNInfoConst.GENDER_MAN;
                break;
            case R.id.user_female_rb:
                mGender = QNInfoConst.GENDER_WOMAN;
                break;
            case R.id.normal_calc_rb:
                mUser.setAthleteType(QNInfoConst.CALC_NORMAL);
                break;
            case R.id.athlete_calc_rb:
                mUser.setAthleteType(QNInfoConst.CALC_ATHLETE);
                break;
            case R.id.ble_scan_first_time:
                //每个设备单次扫描只返回一次
                mBleConfig.setAllowDuplicates(false);
                break;
            case R.id.ble_scan_every_time:
                //每个设备单次扫描回调多次,不过信号强度不同
                mBleConfig.setAllowDuplicates(true);
                break;
            case R.id.user_unit_kg:
                mBleConfig.setUnit(0);
                break;
            case R.id.user_unit_lb:
                mBleConfig.setUnit(1);
                break;
            case R.id.user_unit_jin:
                mBleConfig.setUnit(2);
                break;
            case R.id.user_unit_st:
                mBleConfig.setUnit(3);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_height_tv:
                HeightPickerDialog.Builder heightBuilder = new HeightPickerDialog.Builder().heightChooseListener(new HeightPickerDialog.HeightChooseListener() {
                    @Override
                    public void onChoose(int height) {
                        mHeight = height;
                        mUserHeightTv.setText(height + "cm");
                    }
                });
                heightBuilder.defaultHeight(172);
                heightBuilder.maxHeight(240);
                heightBuilder.minHeight(40);
                heightBuilder.themeColor(getResources().getColor(R.color.themeColor_0fbfef)).context(SettingActivity.this).build().show();
                break;
            case R.id.user_birthday_tv:
                DatePickerDialog.Builder dateBuilder = new DatePickerDialog.Builder()
                        .dateChooseListener(new DatePickerDialog.DateChooseListener() {

                            @Override
                            public void onChoose(Date date) {
                                if (DateUtils.getAge(date) < 10) {
                                    ToastMaker.show(SettingActivity.this, getResources().getString(R.string.RegisterViewController_lowAge10));
                                }

                                mBirthday = date;
                                mUserBirthdayTv.setText(DateUtils.getBirthdayString(date, SettingActivity.this));
                            }
                        });

                dateBuilder.defaultYear(1990);
                dateBuilder.defaultMonth(1);
                dateBuilder.defaultDay(1);
                dateBuilder.context(SettingActivity.this)
                        .themeColor(getResources().getColor(R.color.themeColor_0fbfef))
                        .build().show();
                break;
            case R.id.btn_sure:
                if (checkInfo()) return;

                startActivity(ScanActivity.getCallIntent(this, mUser, mBleConfig));
                finish();
                break;
            case R.id.btn_system_scan:
                if (checkInfo()) return;

                startActivity(SystemScanActivity.getCallIntent(this, mUser, mBleConfig));
                finish();
                break;
        }
    }

    private boolean checkInfo() {
        String userId = mUserIdEdt.getText().toString().trim();
        int scanTime = 0;
        try {
            scanTime = Integer.parseInt(mScanEt.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            ToastMaker.show(this, "请填写正确的扫描时间");
            return true;
        }
        long scanOutTime = 0L;
        try {
            scanOutTime = Long.parseLong(mScanOutEt.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            ToastMaker.show(this, "请填写正确的扫描超时时间");
            return true;
        }
        long connectOutTime = 0L;
        try {
            connectOutTime = Long.parseLong(mConnectOutEt.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            ToastMaker.show(this, "请填写正确的连接超时时间");
            return true;
        }

        if (userId.isEmpty()) {
            ToastMaker.show(this, "用户id不能为空");
            return true;
        } else if (mUserGenderGrp.getCheckedRadioButtonId() == -1) {
            ToastMaker.show(this, "请选择性别");
            return true;
        } else if (mHeight == 0) {
            ToastMaker.show(this, "请输入身高");
            return true;
        } else if (mBirthday == null) {
            ToastMaker.show(this, "请输入出生日期");
            return true;
        } else if (mBleScanGrp.getCheckedRadioButtonId() == -1) {
            ToastMaker.show(this, "请选择蓝牙扫描模式");
            return true;
        } else if (mUserUnitGrp.getCheckedRadioButtonId() == -1) {
            ToastMaker.show(this, "请选择重量单位");
            return true;
        }

        mUser.setUserId(userId);
        mUser.setHeight(mHeight);
        mUser.setGender(mGender);
        mUser.setBirthDay(mBirthday);

        mBleConfig.setDuration(scanTime);
        mBleConfig.setScanOutTime(scanOutTime);
        mBleConfig.setConnectOutTime(connectOutTime);
        return false;
    }

}
