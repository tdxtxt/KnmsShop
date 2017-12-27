package com.knms.shop.android.activity.mine;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.knms.shop.android.R;
import com.knms.shop.android.activity.base.HeadBaseActivity;
import com.knms.shop.android.bean.ResponseBody;
import com.knms.shop.android.helper.Tst;
import com.knms.shop.android.net.RxRequestApi;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/17.
 */
public class UpdatePwdActivity extends HeadBaseActivity {
    private EditText mNewPwd, mOldPwd, mConfirmNewPwd;
    private TextView btnSave;
    private String strNewPwd, strOldPwd, strConfirmNewPwd;

    @Override
    protected int layoutResID() {
        return R.layout.activity_update_pwd;
    }

    @Override
    protected void initView() {
        mNewPwd = (EditText) findViewById(R.id.new_pwd);
        mOldPwd = (EditText) findViewById(R.id.old_pwd);
        mConfirmNewPwd = (EditText) findViewById(R.id.confirm_new_pwd);
        btnSave = (TextView) findViewById(R.id.btn_save_pwd);
    }

    @Override
    protected void initData() {
        mNewPwd.addTextChangedListener(textWatcher);
        mOldPwd.addTextChangedListener(textWatcher);
        mConfirmNewPwd.addTextChangedListener(textWatcher);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!VerificationPwd(strOldPwd) || strOldPwd.length() > 32 || strOldPwd.length() < 6) {
                    Tst.showToast("请输入6-32位字母数字组合的原密码");
                } else if (!VerificationPwd(strNewPwd) || strNewPwd.length() > 32 || strNewPwd.length() < 6) {
                    Tst.showToast("请输入6-32位字母数字组合的新密码");
                } else if (!VerificationPwd(strConfirmNewPwd) || strConfirmNewPwd.length() > 32 || strConfirmNewPwd.length() < 6) {
                    Tst.showToast("请输入6-32位字母数字组合的确认新密码");
                } else if(!strConfirmNewPwd.equals(strNewPwd)){
                    Tst.showToast("新密码与确认新密码不一致");
                }else {
                    RxRequestApi.getInstance().getApiService().updatePwd(strOldPwd, strNewPwd)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<ResponseBody>() {
                                @Override
                                public void call(ResponseBody responseBody) {
                                    Tst.showToast(responseBody.desc);
                                    if (responseBody.isSuccess()) {
                                        finshActivity();
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Tst.showToast(throwable.getMessage());
                                }
                            });
                }
            }
        });
    }

    @Override
    protected String umTitle() {
        return "修改密码";
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            strNewPwd = mNewPwd.getText() + "";
            strOldPwd = mOldPwd.getText() + "";
            strConfirmNewPwd = mConfirmNewPwd.getText() + "";
            if (VerificationPwd(strNewPwd)&&strNewPwd.length()>=6&&strNewPwd.length()<=32 && VerificationPwd(strOldPwd)
                   &&strOldPwd.length()>=6&&strOldPwd.length()<=32 && VerificationPwd(strConfirmNewPwd)
                    &&strConfirmNewPwd.length()>=6&&strConfirmNewPwd.length()<=32) {
                btnSave.setBackgroundResource(R.drawable.bg_rectangle_btn);
                btnSave.setTextColor(0xff333333);
            } else {
                btnSave.setBackgroundResource(R.drawable.bg_rectangle_gray);
                btnSave.setTextColor(0xff999999);
            }
        }
    };


    @Override
    public void setCenterTitleView(TextView tv_center) {
        tv_center.setText("修改账户密码");
    }

    private boolean VerificationPwd(String pwd) {
        String pwdRegex = "^([0-9]+[a-zA-Z]+|[a-zA-Z]+[0-9]+)[0-9a-zA-Z]*$";
        return pwd.matches(pwdRegex);
    }

}
