package com.example.newland.myapplication;

import android.net.Uri; //导入用于处理URI的类
import android.os.Bundle; //导入Bundle类，用于在Activity之间传递数据
import android.support.v7.app.AppCompatActivity; //导入v7包下的AppCompatActivity类，用于支持较早版本的Android系统
import android.widget.TextView; //导入用于显示文本的TextView类
import android.widget.Toast; //导入用于弹出Toast提示的类
import android.widget.VideoView; //导入用于播放视频的VideoView类

import java.text.MessageFormat; //导入用于格式化字符串的MessageFormat类

import cn.com.newland.nle_sdk.responseEntity.SensorInfo; //导入封装传感器信息的SensorInfo类
import cn.com.newland.nle_sdk.responseEntity.User; //导入封装用户信息的User类
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity; //导入封装响应信息的BaseResponseEntity类
import cn.com.newland.nle_sdk.util.NCallBack; //导入封装网络请求回调的NCallBack类
import cn.com.newland.nle_sdk.util.NetWorkBusiness; //导入封装网络请求业务的NetWorkBusiness类s


public class MainActivity extends AppCompatActivity {
    // 定义变量
    User user; // 用户实例
    String device_id = "635420"; // 设备 ID
    String apiTag = "sxt"; // API 标签
    TextView text1; // 显示 token 的文本视图
    VideoView videoView; // 显示视频的视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取 UI 控件
        text1 = findViewById(R.id.token);
        videoView = findViewById(R.id.videoView);

        // 从 Intent 中获取用户对象并获取其访问令牌
        user = (User) getIntent().getSerializableExtra("user");
        user.getAccessToken();

        // 在屏幕上显示访问令牌（仅用于调试）
        text1.setText("Access_token :  " + user.getAccessToken());

        // 设置视频播放器
        setVideoView();
    }

    /**
     * 设置视频播放器
     */
    public void setVideoView() {
        // 创建网络业务对象，传入访问令牌和 API 基础 URL
        NetWorkBusiness netWorkBusiness = new NetWorkBusiness(user.getAccessToken(), "http://api.nlecloud.com/");

        // 调用 getSensor 方法获取传感器信息
        netWorkBusiness.getSensor(device_id, apiTag, new NCallBack<BaseResponseEntity<SensorInfo>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> sensorInfoBaseResponseEntity) {
                // 从传感器信息中获取视频流信息
                String port = sensorInfoBaseResponseEntity.getResultObj().getVideoStreamPort();  //获取视频流端口
                String protocol = sensorInfoBaseResponseEntity.getResultObj().getVideoStreamProtocol();  //获取视频流协议
                String streamUrl = sensorInfoBaseResponseEntity.getResultObj().getVideoStreamUrl();  //获取视频流URL
                String username = sensorInfoBaseResponseEntity.getResultObj().getUserName();  //获取用户名
                String password = sensorInfoBaseResponseEntity.getResultObj().getPassword();  //获取密码
                String httpIp = sensorInfoBaseResponseEntity.getResultObj().getHttpIp();  //获取HTTP IP地址

                // 拼接出视频播放地址
                String camraUrl = "http://" + httpIp + port + streamUrl;
                camraUrl = MessageFormat.format(camraUrl, username, password);

                // 在屏幕上显示视频播放地址（仅用于调试）
                Toast.makeText(MainActivity.this, camraUrl, Toast.LENGTH_SHORT).show();

                // 将视频播放器绑定到该地址并开始播放
                videoView.setVideoURI(Uri.parse(camraUrl));
                videoView.start();
            }
        });

    }
}
