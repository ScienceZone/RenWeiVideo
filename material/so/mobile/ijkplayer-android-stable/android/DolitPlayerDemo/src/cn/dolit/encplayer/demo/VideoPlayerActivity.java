//=======================================================================================
//                 点量软件，致力于做最专业的通用软件库，节省您的开发时间
//
//	Copyright:	Copyright (c) Dolit.cn
//  版权所有：	点量软件有限公司
//              如果您是个人作为非商业目的使用，您可以自由、免费的使用点量软件库和演示程序，
//              也期待收到您反馈的意见和建议，共同改进点量产品
//              如果您是商业使用，那么您需要联系作者申请产品的商业授权。
//              点量软件库所有演示程序的代码对外公开，软件库的代码只限付费用户个人使用。
//  官方网站：  http://www.dolit.cn   http://blog.dolit.cn
//
//=======================================================================================

package cn.dolit.encplayer.demo;

import cn.dolit.encplayer.demo.R;
import cn.dolit.media.player.widget.MediaController;
import cn.dolit.media.player.widget.VideoView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

public class VideoPlayerActivity extends Activity {
	private VideoView mVideoView;
	private View mBufferingIndicator;
	private MediaController mMediaController;

	private String mVideoPath;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);

		//mVideoPath = "http://10.0.0.15/001-enc.flv";
		mVideoPath = "http://flv.bn.netease.com/tvmrepo/2012/7/C/7/E868IGRC7-mobile.mp4";

		Intent intent = getIntent();
		String intentAction = intent.getAction();
		if (!TextUtils.isEmpty(intentAction)
				&& intentAction.equals(Intent.ACTION_VIEW)) {
			mVideoPath = intent.getDataString();
		}

		mBufferingIndicator = findViewById(R.id.buffering_indicator);
		mMediaController = new MediaController(this);

		mVideoView = (VideoView) findViewById(R.id.video_view);
		mVideoView.setMediaController(mMediaController);
		mVideoView.setMediaBufferingIndicator(mBufferingIndicator);
		mVideoView.setVideoPath(mVideoPath);
		mVideoView.requestFocus();
		mVideoView.start();
	}
}
