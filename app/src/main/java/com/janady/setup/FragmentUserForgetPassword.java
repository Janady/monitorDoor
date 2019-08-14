package com.janady.setup;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.funsdkdemo.PasswordChecker;
import com.example.funsdkdemo.R;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunForgetPasswListener;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.regex.Pattern;

public class FragmentUserForgetPassword extends JBaseFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, OnFunForgetPasswListener {
    private QMUITopBarLayout mTopBar;
    private RadioGroup mRadioResetPasswMode = null;

    private EditText mEditEmail = null;
    private EditText mEditPhone = null;
    private EditText mEditVerifyCode = null;
    private Button mBtnSendVerifyCode = null;
    private EditText mEditNewPassw = null;
    private TextView mTextPasswGrade = null;
    private EditText mEditNewPassConfirm = null;
    private Button mBtnVerifyCode = null;
    private Button mBtnSubmit = null;
    private RelativeLayout phoneLayout = null;
    private RelativeLayout mailLayout = null;

    private PasswordChecker passwordChecker = null;

    private boolean isVerifyCodeConfirmed = false;

    private boolean byEmail = false;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.juser_forget_password, null);
        mTopBar = root.findViewById(R.id.topbar);
        phoneLayout = root.findViewById(R.id.layoutPhone);
        mailLayout = root.findViewById(R.id.layoutEmail);
        mRadioResetPasswMode = root.findViewById(R.id.radioForgetPasswMode);
        mRadioResetPasswMode.setOnCheckedChangeListener(this);

        mEditEmail = root.findViewById(R.id.userEmail);
        mEditPhone = root.findViewById(R.id.userPhone);
        mEditVerifyCode = root.findViewById(R.id.userVerifyCode);
        mEditNewPassw = root.findViewById(R.id.userNewPasswd);
        mTextPasswGrade = root.findViewById(R.id.passwGarde);
        mEditNewPassConfirm = root.findViewById(R.id.userNewPasswdConfirm);

        mBtnSendVerifyCode = root.findViewById(R.id.btnSendVerifyCode);
        mBtnSendVerifyCode.setOnClickListener(this);
        mBtnVerifyCode = root.findViewById(R.id.verifyBtn);
        mBtnVerifyCode.setOnClickListener(this);
        mBtnSubmit = root.findViewById(R.id.submitBtn);
        mBtnSubmit.setOnClickListener(this);

        mRadioResetPasswMode.check(R.id.radioBtnResetPwdByCellphone);
        showForgetPasswLayout(byEmail);

        //实时检测输入的密码强度
        passwordChecker = new PasswordChecker(getContext(), mEditNewPassw, mTextPasswGrade);
        FunSupport.getInstance().registerOnFunCheckPasswListener(passwordChecker);
        passwordChecker.check();

        FunSupport.getInstance().registerOnFunForgetPasswListener(this);
        initTopBar();
        return root;
    }
    @Override
    public void onDestroy() {
        FunSupport.getInstance().removeOnFunCheckPasswListener(passwordChecker);
        FunSupport.getInstance().removeOnFunForgetPasswListener(this);
        super.onDestroy();
    }
    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.guide_module_title_user_forgot_passwd);
    }

    /**
     * 以邮箱的方式还是手机方式重置密码
     * @param byEmail
     */
    private void showForgetPasswLayout(boolean byEmail) {
        if ( byEmail ) {
            phoneLayout.setVisibility(View.GONE);
            mailLayout.setVisibility(View.VISIBLE);
        } else {
            mailLayout.setVisibility(View.GONE);
            phoneLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 请求发送验证码(区分邮箱还是手机)
     */
    private void tryToSendVerifyCode() {
        if (byEmail) {
            String email = mEditEmail.getText().toString().trim();
            if (!isEmailValid(email)) {
                // 邮箱不正确
                showToast(R.string.user_login_error_email);
                return;
            }

            showWaitDialog();

            if ( !FunSupport.getInstance().requestSendEmailCodeForResetPW(email) ) {
                showToast(R.string.guide_message_error_call);
            }

        } else {
            String phoneNum = mEditPhone.getText().toString().trim();
            if ( phoneNum.length() != 11 ) {
                // 手机号不正确
                showToast(R.string.user_login_error_phone_number);
                return;
            }

            showWaitDialog();

            if ( !FunSupport.getInstance().requestSendPhoneMsgForResetPW(phoneNum) ) {
                showToast(R.string.guide_message_error_call);
            }
        }

    }

    /**
     * 校验验证码(区分邮箱还是手机)
     */
    private void tryToVerifyCode() {
        if (byEmail) {
            String email = mEditEmail.getText().toString().trim();
            String verifyCode = mEditVerifyCode.getText().toString().trim();
            showWaitDialog();
            if (!FunSupport.getInstance().requestVerifyEmailCode(email, verifyCode)) {
                showToast(R.string.guide_message_error_call);
            }
        } else {
            String phone = mEditPhone.getText().toString().trim();
            String verifyCode = mEditVerifyCode.getText().toString().trim();
            Pattern pattern = Pattern.compile("[0-9]{4}");
            if (!pattern.matcher(verifyCode).matches()) {
                showToast(R.string.user_forget_pwd_verify_code_format_erroe);
            } else {
                showWaitDialog();
                if (!FunSupport.getInstance().requestVerifyPhoneCode(phone, verifyCode)) {
                    showToast(R.string.guide_message_error_call);
                }
            }
        }
    }

    /**
     * 发送密码重置请求
     */
    private void tryToSubmit(){
        String newPassw = mEditNewPassw.getText().toString();
        String newPasswConfirm = mEditNewPassConfirm.getText().toString();
        if (!isVerifyCodeConfirmed) {
            showToast(R.string.user_forget_pwd_verify_code_first);
            return;
        }

        if (TextUtils.isEmpty(newPassw) || newPassw.length() < 8) {
            showToast(R.string.user_forget_pwd_new_password_error);
            return;
        }

        if (passwordChecker.getPasswGrade() == 1) {
            showToast(R.string.password_checker_weak_error);
        }

        if (!newPassw.equals(newPasswConfirm)) {
            showToast(R.string.user_forget_pwd_new_password_confirm_error);
            return;
        }

        showWaitDialog();

        if (byEmail) {
            String email = mEditEmail.getText().toString().trim();
            if (!FunSupport.getInstance().requestResetPasswByEmail(email, newPassw)) {
                showToast(R.string.guide_message_error_call);
            }
        } else {
            String phone = mEditPhone.getText().toString().trim();
            if (!FunSupport.getInstance().requestResetPasswByPhone(phone, newPassw)) {
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendVerifyCode:
                tryToSendVerifyCode();
                break;
            case R.id.verifyBtn:
                tryToVerifyCode();
                break;
            case R.id.submitBtn:
                tryToSubmit();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioBtnResetPwdByEmail:
                byEmail = true;
                showForgetPasswLayout(byEmail);
                break;
            case  R.id.radioBtnResetPwdByCellphone:
                byEmail = false;
                showForgetPasswLayout(byEmail);
                break;
        }
    }

    @Override
    public void onRequestCodeSuccess() {
        hideWaitDialog();
        showToast(R.string.guide_message_request_phone_msg_success);
    }

    @Override
    public void onRequestCodeFailed(Integer errCode) {
        hideWaitDialog();
        showToast(FunError.getErrorStr(errCode));
    }

    @Override
    public void onVerifyCodeSuccess() {
        isVerifyCodeConfirmed = true;
        hideWaitDialog();
        showToast(R.string.user_forget_pwd_verify_success);
    }

    @Override
    public void onVerifyFailed(Integer errCode) {
        isVerifyCodeConfirmed = false;
        hideWaitDialog();
        showToast(FunError.getErrorStr(errCode));
    }

    @Override
    public void onResetPasswSucess() {
        hideWaitDialog();
        showToast(R.string.user_forget_pwd_reset_passw_success);
    }

    @Override
    public void onResetPasswFailed(Integer errCode) {
        hideWaitDialog();
        showToast(FunError.getErrorStr(errCode));
    }
}
