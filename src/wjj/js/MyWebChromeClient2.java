package wjj.js;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

public class MyWebChromeClient2 extends WebChromeClient {
	Context context;

	public MyWebChromeClient2(Context context) {
		this.context = context;
	}

	// 以下3种是系统定义的映射。我们还可以定义其他映射。
	// 处理javascript中的alert
	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			final JsResult result) {
		// 构架一个builder来显示网页中的对话框
		Builder builder = new Builder(context);
		builder.setTitle("Alert 弹窗框");
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击确定按钮之后，继续执行网页中的操作
						result.confirm();
					}
				});
		builder.setCancelable(false);
		builder.show();
		return true;
	}

	// 处理javascript中的confirm
	@Override
	public boolean onJsConfirm(WebView view, String url, String message,
			final JsResult result) {
		Builder builder = new Builder(context);
		builder.setTitle("Confirm 确认框");
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();// 返回null
					}
				});
		builder.setCancelable(false);
		builder.show();
		return true;
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message,
			String defaultValue, final JsPromptResult result) {
		// 自定义一个带输入的对话框由TextView和Edittext构成
		final LayoutInflater factory = LayoutInflater.from(context);
		final View dialogView = factory.inflate(R.layout.prom_dialog, null);
		// 设置TextView对应网页中的提示信息
		((TextView) dialogView.findViewById(R.id.TextView_PROM))
				.setText(message);
		// 设置EditText对应网页中的输入框
		((EditText) dialogView.findViewById(R.id.EditText_PROM))
				.setText(defaultValue);

		Builder builder = new Builder(context);
		builder.setTitle("Prompt 输入框" + url);
		builder.setView(dialogView);
		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 点击确定之后，取得输入的值，传给网页处理
						String value = ((EditText) dialogView
								.findViewById(R.id.EditText_PROM)).getText()
								.toString();
						result.confirm(value);// 返回值为value
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();// 返回值为null
					}
				});
		builder.setOnCancelListener(new AlertDialog.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				result.cancel();
			}
		});
		builder.show();
		return true;
	}

	// 设置网页的加载的进度条
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		((Activity) context).getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
				newProgress);
		super.onProgressChanged(view, newProgress);
	}

	// 设置应用程序的标题title
	@Override
	public void onReceivedTitle(WebView view, String title) {
		((Activity) context).setTitle(title);
		super.onReceivedTitle(view, title);
	}
}
