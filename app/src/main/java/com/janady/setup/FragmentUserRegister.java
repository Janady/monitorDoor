package com.janady.setup;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.funsdkdemo.R;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunRegisterListener;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class FragmentUserRegister extends JBaseFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, OnFunRegisterListener {
    private QMUITopBarLayout mTopBar;
    private RadioGroup mRadioRegisterMode = null;

    private EditText mEditUserName = null;
    private EditText mEditPassWord = null;
    private EditText mEditPassWordConfirm = null;
    private EditText mEditEmail = null;
    private EditText mEditPhone = null;
    private EditText mEditVerifyCode = null;
    private Button mBtnGetVerifyCode = null;
    private Button mBtnRegister = null;
    private Button mBtnUsernameCheck = null;
    private RelativeLayout phoneLayout = null;
    private RelativeLayout mailLayout = null;

    private boolean byEmail = false;
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.userRegisterBtn:
            {
                // 快速注册
                tryToRegister();
            }
            break;
            case R.id.btnGetVerifyCode:
            {
                // 获取验证码
                tryGetVerifyCode();
            }
            break;
            case R.id.btnUserNameRepeat:
                //check username repeat
                checkUsername();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.radioBtnRegisterByEmail:
            {
                byEmail = true;
                showRegisterLayout(byEmail);
            }
            break;
            case R.id.radioBtnRegisterByCellphone:
            {
                byEmail = false;
                showRegisterLayout(byEmail);
            }
            break;
        }
    }

    @Override
    public void onRequestSendCodeSuccess() {
        hideWaitDialog();
        showToast(R.string.guide_message_request_phone_msg_success);
    }

    @Override
    public void onRequestSendCodeFailed(Integer errCode) {
        hideWaitDialog();
        showToast(FunError.getErrorStr(errCode));
    }

    @Override
    public void onRegisterNewUserSuccess() {
        hideWaitDialog();
        showToast(R.string.guide_message_register_user_success);
        popBackStack();
    }

    @Override
    public void onRegisterNewUserFailed(Integer errCode) {
        hideWaitDialog();
        showToast(FunError.getErrorStr(errCode));
    }

    @Override
    public void onUserNameFine() {
        showToast(R.string.guide_message_username_fine);
    }

    @Override
    public void onUserNameUnfine(Integer errCode) {
        showToast(FunError.getErrorStr(errCode));
    }

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.juser_register, null);
        mTopBar = root.findViewById(R.id.topbar);
        phoneLayout = root.findViewById(R.id.layoutRegisterPhone);
        mailLayout = root.findViewById(R.id.layoutRegisterEmail);
        mRadioRegisterMode = root.findViewById(R.id.radioRegisterMode);
        mRadioRegisterMode.setOnCheckedChangeListener(this);

        mEditUserName = root.findViewById(R.id.userRegisterUserName);
        mEditEmail = root.findViewById(R.id.userRegisterEmail);
        mEditPhone = root.findViewById(R.id.userRegisterPhone);
        mEditVerifyCode = root.findViewById(R.id.userRegisterVerifyCode);
        mEditPassWord = root.findViewById(R.id.userRegisterPasswd);
        mEditPassWordConfirm = root.findViewById(R.id.userRegisterPasswdConfirm);

        mBtnGetVerifyCode = root.findViewById(R.id.btnGetVerifyCode);
        mBtnGetVerifyCode.setOnClickListener(this);
        mBtnRegister = root.findViewById(R.id.userRegisterBtn);
        mBtnRegister.setOnClickListener(this);
        mBtnUsernameCheck = root.findViewById(R.id.btnUserNameRepeat);
        mBtnUsernameCheck.setOnClickListener(this);

        mRadioRegisterMode.check(R.id.radioBtnRegisterByCellphone);
        showRegisterLayout(byEmail);

        // 注册监听(用户注册相关)
        FunSupport.getInstance().registerOnFunRegisterListener(this);
        initTopBar();
        return root;
    }

    @Override
    public void onDestroy() {
        // 注销监听(用户注册相关)
        FunSupport.getInstance().removeOnFunRegisterListener(this);

        super.onDestroy();
    }
    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.guide_module_title_user_register);
    }
    /**
     * 以邮箱的方式还是手机方式重置密码
     * @param byEmail
     */
    private void showRegisterLayout(boolean byEmail) {
        if ( byEmail ) {
            phoneLayout.setVisibility(View.GONE);
            mailLayout.setVisibility(View.VISIBLE);
        } else {
            mailLayout.setVisibility(View.GONE);
            phoneLayout.setVisibility(View.VISIBLE);
        }
        setVerifyCodeButton(!byEmail);
    }// check username
    private void checkUsername(){
        String userName = mEditUserName.getText().toString();
        if (userName != null && userName.length() != 0) {
            FunSupport.getInstance().checkUserName(userName);
        }
    }

    // 设置获取验证码按钮是否可用
    private void setVerifyCodeButton(boolean enabled) {
        mBtnGetVerifyCode.setEnabled(enabled);
        if ( enabled ) {
            mBtnGetVerifyCode.setTextColor(getResources().getColor(R.color.white));
            mBtnGetVerifyCode.setBackgroundResource(R.drawable.common_button_selector);
        } else {
            mBtnGetVerifyCode.setTextColor(getResources().getColor(R.color.demo_desc));
            mBtnGetVerifyCode.setBackgroundColor(getResources().getColor(R.color.bg_gray));
        }
    }

    private void tryGetVerifyCode() {
        String userName = mEditUserName.getText().toString();
        String phoneNum = mEditPhone.getText().toString().trim();
        String emailStr = mEditEmail.getText().toString().trim();

        if ( null == userName || userName.length() == 0 ) {
            // 用户名为空
            showToast(R.string.user_login_error_emptyusername);
            return;
        }

        if (byEmail) {
            if (!isEmailValid(emailStr)) {
                // 邮箱不正确
                showToast(R.string.user_login_error_email);
                return;
            }
            showWaitDialog();
            if (!FunSupport.getInstance().requestEmailCode(emailStr)) {
                showToast(R.string.guide_message_error_call);
            }

        } else {
            if ( phoneNum.length() != 11 ) {
                // 手机号不正确
                showToast(R.string.user_login_error_phone_number);
                return;
            }
            showWaitDialog();

            if ( !FunSupport.getInstance().requestPhoneMsg(userName, phoneNum) ) {
                showToast(R.string.guide_message_error_call);
            }
        }


    }
    private void tryToRegister() {
        String userName = mEditUserName.getText().toString();
        String passWord = mEditPassWord.getText().toString();
        String passWordConfirm = mEditPassWordConfirm.getText().toString();

        if ( null == userName || userName.length() == 0 ) {
            // 用户名为空
            showToast(R.string.user_login_error_emptyusername);
            return;
        }

        if ( null == passWord || passWord.length() == 0 ) {
            // 密码为空
            showToast(R.string.user_login_error_emptypassword);
            return;
        }

        if ( !passWord.equals(passWordConfirm) ) {
            showToast(R.string.user_register_error_password_unmatched);
            return;
        }

        if ( userName.length() > 16 || userName.length() < 6 ) {
            showToast(R.string.user_register_error_username_length);
            return;
        }

        if ( passWord.length() < 8 ) {
            showToast(R.string.user_register_error_password_length);
            return;
        }

        if ( R.id.radioBtnRegisterByEmail
                == mRadioRegisterMode.getCheckedRadioButtonId() ) {
            // 通过邮箱注册
            String email = mEditEmail.getText().toString().trim();
            String verifyCode = this.mEditVerifyCode.getText().toString().trim();

            if ( null == email || email.length() == 0 || !email.contains("@") || !isEmailValid(email)) {
                // 邮箱格式不正确
                showToast(R.string.user_login_error_email);
                return;
            }

            if ( null == verifyCode || verifyCode.length() == 0 ) {
                // 验证码为空
                showToast(R.string.user_login_error_emptyverifycode);
                return;
            }

            showWaitDialog();

            if ( !FunSupport.getInstance().registerByEmail(
                    userName, passWord, verifyCode, email) ) {
                showToast(R.string.guide_message_error_call);
            }

        } else {
            // 通过手机号注册
            String phoneNo = mEditPhone.getText().toString().trim();
            String verifyCode = this.mEditVerifyCode.getText().toString().trim();

            if ( phoneNo.length() != 11 ) {
                // 手机号不正确
                showToast(R.string.user_login_error_phone_number);
                return;
            }

            if ( null == verifyCode || verifyCode.length() == 0 ) {
                // 验证码为空
                showToast(R.string.user_login_error_emptyverifycode);
                return;
            }

            showWaitDialog();

            if ( !FunSupport.getInstance().registerByPhone(
                    userName, passWord, verifyCode, phoneNo) ) {
                showToast(R.string.guide_message_error_call);
            }
        }
    }
    /**
     * 验证邮箱格式是否正确
     */
    public boolean isEmailValid(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }
}
