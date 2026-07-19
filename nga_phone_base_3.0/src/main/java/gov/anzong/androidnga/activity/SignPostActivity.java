package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.http.OnHttpCallBack;
import gov.anzong.androidnga.util.ToastUtils;
import sp.phone.common.UserManagerImpl;
import sp.phone.param.SignPostParam;
import sp.phone.task.SignPostTask;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.StringUtils;

public class SignPostActivity extends BaseActivity {

    private EditText mBodyText;

    private SignPostParam mPostParam;

    private SignPostTask mPostTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sign_reply);
        initBodyText();
        mPostParam = new SignPostParam();
        mPostParam.setUid(UserManagerImpl.getInstance().getUserId());
        setTitle("更改签名");
        setupToolbar();
    }

    private void initBodyText() {
        mBodyText = (EditText) findViewById(R.id.reply_body_edittext);
        mBodyText.setSelected(true);

        Intent intent = getIntent();
        String prefix = intent.getStringExtra("prefix");
        if (prefix != null) {
            if (prefix.startsWith("[quote][pid=")
                    && prefix.endsWith("[/quote]\n")) {
                SpannableString spanString = new SpannableString(prefix);
                spanString.setSpan(new BackgroundColorSpan(-1513240), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), prefix.indexOf("[b]Post by"),
                        prefix.indexOf("):[/b]") + 5,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mBodyText.append(spanString);
            } else {
                mBodyText.append(prefix);
            }
            mBodyText.setSelection(prefix.length());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                postSign();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void postSign() {
        String bodyText = mBodyText.getText().toString();
        if (StringUtils.isEmpty(bodyText)) {
            ToastUtils.showToast("请输入内容");
        } else if (mPostTask != null && mPostTask.isRunning()) {
            ToastUtils.showToast(R.string.avoidWindfury);
        } else {
            if (mPostTask == null) {
                mPostTask = new SignPostTask();
            }
            mPostParam.setSign(FunctionUtils.ColorTxtCheck(bodyText));
            mPostTask.execute(mPostParam, new OnHttpCallBack<String>() {
                @Override
                public void onError(String text) {
                    ActivityUtils.showToast(text);
                }

                @Override
                public void onSuccess(String data) {
                    ActivityUtils.showToast(data);
                    Intent intent = new Intent();
                    intent.putExtra("sign", mPostParam.getSign());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (mPostTask != null) {
            mPostTask.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mBodyText.requestFocus();
        mBodyText.selectAll();
        super.onResume();
    }
}