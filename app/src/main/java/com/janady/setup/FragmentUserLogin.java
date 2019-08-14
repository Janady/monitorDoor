package com.janady.setup;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.common.DialogSavedUsers;
import com.example.common.UIFactory;
import com.example.funsdkdemo.ActivityGuideDeviceList;
import com.example.funsdkdemo.ActivityGuideUserForgetPassw;
import com.example.funsdkdemo.ActivityGuideUserRegister;
import com.example.funsdkdemo.R;
import com.janady.HomeActivity;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunLoginListener;
import com.lib.funsdk.support.models.FunLoginType;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class FragmentUserLogin extends JBaseFragment implements View.OnClickListener, OnFunLoginListener {
    private QMUITopBarLayout mTopBar;

    private EditText mEditUserName = null;
    private EditText mEditPassWord = null;
    private ImageButton mBtnLoginHistory = null;
    private Button mBtnLogin = null;
    private Button mBtnForgotPasswd = null;
    private Button mBtnLoginByWeibo = null;
    private Button mBtnLoginByQQ = null;
    private Button mBtnRegister = null;

    private Button mBtnCheckSavePasswd = null;
    private Button mBtnCheckAutoLogin = null;
    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.juser_login, null);
        mTopBar = root.findViewById(R.id.topbar);

        mEditUserName = root.findViewById(R.id.userLoginUserName);
        mEditPassWord = root.findViewById(R.id.userLoginPasswd);
        mBtnLogin = root.findViewById(R.id.userLoginBtn);
        mBtnLogin.setOnClickListener(this);

        mBtnForgotPasswd = root.findViewById(R.id.userloginForgotPasswd);
        mBtnForgotPasswd.setOnClickListener(this);

        mBtnLoginHistory = root.findViewById(R.id.btnLoginHistory);
        mBtnLoginHistory.setOnClickListener(this);

        mBtnLoginByWeibo = root.findViewById(R.id.userLoginByWeibo);
        mBtnLoginByQQ = root.findViewById(R.id.userLoginByQQ);
        UIFactory.setLeftDrawable(getContext(), mBtnLoginByWeibo,
                R.drawable.user_icon_other_login_weibo, 24, 24);
        UIFactory.setLeftDrawable(getContext(), mBtnLoginByQQ,
                R.drawable.user_icon_other_login_qq, 24, 24);
        mBtnLoginByWeibo.setOnClickListener(this);
        mBtnLoginByQQ.setOnClickListener(this);

        mBtnCheckSavePasswd = root.findViewById(R.id.checkboxSavePassword);
        UIFactory.setLeftDrawable(getContext(), mBtnCheckSavePasswd,
                R.drawable.icon_check, 24, 24);
        mBtnCheckAutoLogin = root.findViewById(R.id.checkboxAutoLogin);
        UIFactory.setLeftDrawable(getContext(), mBtnCheckAutoLogin,
                R.drawable.icon_check, 24, 24);
        mBtnCheckSavePasswd.setOnClickListener(this);
        mBtnCheckAutoLogin.setOnClickListener(this);

        mBtnCheckSavePasswd.setSelected(FunSupport.getInstance().getSavePasswordAfterLogin());
        mBtnCheckAutoLogin.setSelected(FunSupport.getInstance().getAutoLogin());

        mBtnRegister = root.findViewById(R.id.userRegister);
        mBtnRegister.setOnClickListener(this);

        // 显示上一次保存的用户名和密码
        mEditUserName.setText(FunSupport.getInstance().getSavedUserName());
        mEditPassWord.setText(FunSupport.getInstance().getSavedPassword());

        // 用户相关的操作,必须切换网络访问方式
        FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);

        // 注册监听(用户登录相关)
        FunSupport.getInstance().registerOnFunLoginListener(this);

        initTopBar();
        return root;
    }
    @Override
    public void onDestroy() {
        // 注销监听(用户登录相关)
        FunSupport.getInstance().removeOnFunLoginListener(this);

        super.onDestroy();
    }
    private void initTopBar() {
        mTopBar.setTitle(R.string.guide_module_title_user_login);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.userLoginByWeibo:
            case R.id.userLoginByQQ:
            {
                showToast("暂不开放第三方账号登录");
            }
            break;
            case R.id.btnLoginHistory:
            {
                // 显示登录历史
                showLoginHistory();
            }
            break;
            case R.id.userLoginBtn:
            {
                tryToLogin();
            }
            break;
            case R.id.userloginForgotPasswd:
            {
                enterForgotPassword();
            }
            break;
            case R.id.userRegister:
            {
                enterUserRegister();
            }
            break;
            case R.id.checkboxSavePassword:
            {
                if ( mBtnCheckSavePasswd.isSelected() ) {
                    mBtnCheckSavePasswd.setSelected(false);
                    FunSupport.getInstance().setSavePasswordAfterLogin(false);
                } else {
                    mBtnCheckSavePasswd.setSelected(true);
                    FunSupport.getInstance().setSavePasswordAfterLogin(true);
                }
            }
            break;
            case R.id.checkboxAutoLogin:
            {
                if ( mBtnCheckAutoLogin.isSelected() ) {
                    mBtnCheckAutoLogin.setSelected(false);
                    FunSupport.getInstance().setAutoLogin(false);
                } else {
                    mBtnCheckAutoLogin.setSelected(true);
                    FunSupport.getInstance().setAutoLogin(true);
                }
            }
            break;
        }
    }
    private void showLoginHistory() {
        DialogSavedUsers dialog = new DialogSavedUsers(getContext(),
                FunSupport.getInstance().getSavedUserNames(),
                new DialogSavedUsers.OnSavedUserSelectListener() {

                    @Override
                    public void onSavedUserSelected(String userName) {
                        String passWord = FunSupport.getInstance().getSavedPassword(userName);
                        if ( null != passWord
                                && null != mEditUserName
                                && null != mEditPassWord ) {
                            mEditUserName.setText(userName);
                            mEditPassWord.setText(passWord);
                            mBtnLogin.requestFocus();
                        }
                    }
                });
        dialog.show();
    }

    private void tryToLogin() {
        String userName = mEditUserName.getText().toString();
        String passWord = mEditPassWord.getText().toString();

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

        showWaitDialog();

        if ( !FunSupport.getInstance().login(userName, passWord) ) {
            showToast(R.string.guide_message_error_call);
        }
    }

    private void enterForgotPassword() {
        startFragment(new FragmentUserForgetPassword());
    }

    private void enterUserRegister() {
        startFragment(new FragmentUserRegister());
    }
    public void showUserInfo() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        getActivity().finish();
    }
    @Override
    public void onLoginSuccess() {
        hideWaitDialog();
        showToast(R.string.user_register_login_success);

        // 显示用户信息
        showUserInfo();
    }

    @Override
    public void onLoginFailed(Integer errCode) {
        hideWaitDialog();
        showToast(FunError.getErrorStr(errCode));
    }

    @Override
    public void onLogout() {

    }
}
