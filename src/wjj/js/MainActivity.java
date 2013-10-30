package wjj.js;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class MainActivity extends Activity {
	WebView wv;
	private Button mButton;
	private EditText mEditText;
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mButton = (Button) findViewById(R.id.button01);
		mEditText = (EditText) findViewById(R.id.edittext01);
		wv = ((WebView) findViewById(R.id.webView1));
		WebSettings ws = wv.getSettings();
		ws.setSupportZoom(true);
		ws.setBuiltInZoomControls(true);
		// ws.setLoadWithOverviewMode(true);
		// ws.setUseWideViewPort(true);
		ws.setDefaultTextEncodingName("utf-8");
		ws.setJavaScriptEnabled(true);
		// 自定义的客户端js处理
		wv.addJavascriptInterface(new MyJavaScriptInterface(), "myjs");
		// ws.setJavaScriptCanOpenWindowsAutomatically(true);// 允许弹出窗口
		// 映射到javascript的window.open
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});

		// 客户端js处理
		// wv.setWebChromeClient(new MyWebChromeClient());//js处理器1
		wv.setWebChromeClient(new MyWebChromeClient2(MainActivity.this));// js处理器2

		wv.loadUrl("file:///android_asset/showtime.html");// 测试页1
		// wv.loadUrl("file:///android_asset/dialog.html");// 测试页2
		// wv.loadUrl("javascript:alert('lala')");//直接测试

		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// 取得编辑框中输入的内容
					String url = mEditText.getText().toString();
					// 判断输入的内容是不是网址
					if (URLUtil.isNetworkUrl(url)) {
						// 装载网址
						wv.loadUrl(url);
					} else {
						mEditText.setText("输入网址错误");
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (wv.canGoBack())) {
			// 返回前一个页面
			wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			wv.loadUrl("javascript:setDate('" + bundle.getString("dateString")
					+ "','" + bundle.getString("eleId") + "');");
			// wv.loadUrl("javascript:setDate('2011-11-11','123');");要加上单引号，否则设置失败
		};

	};

	// targetSdkVersion>=17时，需要加上@JavascriptInterface，否则报错Uncaught TypeError:
	// Object [object Object] has no method
	// 'toString'。该标记为4.2之后引入，所以target=android-17或更高
	// 如果仅target低于17则出现矛盾：需要引入JavascriptInterface类 然而低版本jar中又没有该类！js无法运行
	// 仍然报错Uncaught TypeError: Object [object Object] has no method 'toString'
	// 反之，如果仅targetSdkVersion低于17，那么不用加@JavascriptInterface，当然加上也行，因为当target>=17时的jar中有这个类
	// 如果均低于17，不用加！当然了，没有这个类，想加也加不了
	// 所以，只要targetSdkVersion<17时就不用加。如果target>=17,jar包中有这个类，随便加不加。如果target<17,加不了也不用加
	class MyJavaScriptInterface {
		@JavascriptInterface
		public void chooseDate(String date, final String eleId) {
			String[] dateParts = date.split("-");
			new DatePickerDialog(
					MainActivity.this,
					new OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// Toast.makeText(MainActivity.this,
							// year + "-" + monthOfYear + "-" + dayOfMonth, 0)
							// .show();
							String dateString = year
									+ "-"
									+ ((monthOfYear + 1) < 10 ? "0"
											+ (monthOfYear + 1)
											: (monthOfYear + 1))
									+ "-"
									+ (dayOfMonth < 10 ? "0" + dayOfMonth
											: dayOfMonth);
							// 不支持非UI线程调用
							// wv.loadUrl("javascript:setDate(" + dateString +
							// "," + eleId
							// + ")");
							Message msg = new Message();

							Bundle bundle = new Bundle();
							bundle.putString("dateString", dateString);
							bundle.putString("eleId", eleId);

							msg.setData(bundle);
							handler.sendMessage(msg);

						}
					}, Integer.parseInt(dateParts[0]),
					Integer.parseInt(dateParts[1]) - 1, Integer
							.parseInt(dateParts[2])).show();

		}
	}
}
