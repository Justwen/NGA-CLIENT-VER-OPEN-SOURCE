package sp.phone.ui.fragment.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Locale;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.TopicListActivity;

public class UrlInputDialogFragment extends BaseDialogFragment {

    private EditText mUrlAddEditText;

    @Override
    protected boolean onPositiveClick() {
        String url = mUrlAddEditText.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            mUrlAddEditText.setError("请输入URL地址");
            mUrlAddEditText.setFocusable(true);
        } else {
            url = url.toLowerCase(Locale.US).trim();
            if (url.contains("thread.php")) {
                url = url.replaceAll("(?i)[^\\[|\\]]+stid=(-?\\d+)[^\\[|\\]]*", Utils.getNGAHost() + "thread.php?stid=$1")
                        .replaceAll("(?i)[^\\[|\\]]+fid=(-?\\d+)[^\\[|\\]]*", Utils.getNGAHost() + "thread.php?fid=$1");
                Intent intent = new Intent(getContext(), TopicListActivity.class);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.contains("read.php")) {
                if (url.contains("tid") && url.contains("pid")) {
                    if (url.indexOf("tid") < url.indexOf("pid")) {
                        url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$2&tid=$1");
                    } else {
                        url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$1&tid=$2");
                    }
                } else if (url.contains("tid") && !url.contains("pid")) {
                    url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?tid=$1");
                } else if (url.contains("pid") && !url.contains("tid")) {
                    url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$1");
                }
                Intent intent = new Intent(getContext(), ArticleListActivity.class);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } else {
                mUrlAddEditText.setError("输入的地址并非NGA的板块地址或帖子地址,或缺少fid/pid/tid信息,请检查后再试");
                mUrlAddEditText.setFocusable(true);
            }
        }
        return false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_url_to, null);
        mUrlAddEditText = contentView.findViewById(R.id.et_add_url);
        mUrlAddEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event.getAction() == KeyEvent.ACTION_UP && onPositiveClick()) {
                    dismiss();
                }
                return true;
            } else {
                return false;
            }
        });

        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null) {
                CharSequence clipText = clipData.getItemAt(0).getText();
                if (!TextUtils.isEmpty(clipText)) {
                    mUrlAddEditText.setText(clipText);
                    mUrlAddEditText.selectAll();
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setTitle("由URL读取")
                .setView(contentView);
        return builder.create();
    }

}
