package com.example.newland.myapplication;

import android.content.Intent; //导入Intent类，用于Activity之间的跳转
import android.support.v7.app.AppCompatActivity; //导入AppCompatActivity类，用于创建继承自AppCompatActivity的Activity
import android.os.Bundle; //导入Bundle类，用于Activity之间传递数据
import android.view.View; //导入View类，用于视图组件的操作
import android.widget.Button; //导入Button类，用于创建按钮
import android.widget.EditText; //导入EditText类，用于创建文本框
import android.widget.Toast; //导入Toast类，用于提示信息

import cn.com.newland.nle_sdk.requestEntity.SignIn; //导入SignIn类，用于封装登录信息
import cn.com.newland.nle_sdk.responseEntity.User; //导入User类，用于封装登录成功后的用户信息
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity; //导入BaseResponseEntity类，用于封装请求返回的基础信息
import cn.com.newland.nle_sdk.util.NCallBack; //导入NCallBack类，用于处理网络请求的回调
import cn.com.newland.nle_sdk.util.NetWorkBusiness; //导入NetWorkBusiness类，用于调用新大陆云平台提供的API


public class LoginActivity extends AppCompatActivity {
    EditText user_name; //声明用户输入用户名的文本框
    EditText password; //声明用户输入密码的文本框

    Button denglu; //声明登录按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) { //重写onCreate方法，创建Activity时执行
        super.onCreate(savedInstanceState); //调用父类的onCreate方法
        setContentView(R.layout.activity_login); //设置当前Activity的布局为activity_login.xml

        user_name = findViewById(R.id.et_username); //获取布局文件中id为et_username的文本框，并赋值给user_name
        password = findViewById(R.id.et_password); //获取布局文件中id为et_password的文本框，并赋值给password
        denglu = findViewById(R.id.btn_login); //获取布局文件中id为btn_login的按钮，并赋值给denglu
        denglu.setOnClickListener(new View.OnClickListener() { //给denglu设置监听器，当denglu被点击时触发监听器的onClick方法
            @Override
            public void onClick(View view) {
                //EditText的getText()方法只能在监听事件中才能够实现，只能将其放在监听事件外面就获取不到EditText中输入的值
                signIn(user_name.getText().toString(), password.getText().toString()); //调用signIn方法进行登录
            }
        });
    }


    public void signIn(String user_name, String password) {
        // 创建 SignIn 对象，将用户名和密码传递给它
        SignIn signIn = new SignIn(user_name, password);
        // 创建 NetWorkBusiness 对象，设置服务器地址为 "http://api.nlecloud.com/"
        NetWorkBusiness netWorkBusiness = new NetWorkBusiness("", "http://api.nlecloud.com/");
        // 调用 signIn 方法进行登录，这里的 new NCallBack 是回调函数，在登录请求完成后调用 onResponse 函数
        netWorkBusiness.signIn(signIn, new NCallBack<BaseResponseEntity<User>>(getApplicationContext()) {
            /*
            Context 是一个表示应用程序环境的对象。
            它提供了访问应用程序资源和系统服务的接口。
            使用 getApplicationContext() 传递给 NCallBack 的构造函数是为了获取应用程序级别的上下文对象，而不是 Activity 级别的上下文对象。
            这是因为应用程序级别的上下文对象不会随着 Activity 的生命周期而改变，而 Activity 级别的上下文对象则可能在 Activity 销毁时被回收。
            在这种情况下，使用应用程序级别的上下文对象可以避免在 Activity 销毁时发生内存泄漏或其他问题。
            */
            @Override
            protected void onResponse(BaseResponseEntity<User> response) {
                // 创建 User 对象
                User user = new User();
                // 获取登录状态
                response.getStatus();
                // 显示登录码
                Toast.makeText(LoginActivity.this, "登录码: " + response.getStatus()+"\n"+"(登录成功时 登录码为：0)", Toast.LENGTH_SHORT).show();
                // 如果登录成功
                if (response.getStatus() == 0) {
                    // 将 accessToken 存储到 User 对象中
                    user.setAccessToken(response.getResultObj().getAccessToken());
                    // 跳转到 MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
}