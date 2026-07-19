package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.lang.reflect.Field;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.ToastUtils;
import sp.phone.task.PostCommentTask;
import gov.anzong.androidnga.common.util.NLog;

public class PostCommentDialogFragment extends BaseDialogFragment implements
        PostCommentTask.OnPostCommentFinishedListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_post_comment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        CheckBox anony = view.findViewById(R.id.anony);
        EditText input = view.findViewById(R.id.post_data);
        builder.setTitle(R.string.post_comment)
                .setView(view)
                .setPositiveButton("发送", (dialog, whichButton) -> {
                    int length = input.getText().toString().length();
                    if (length > 5 && length < 651) {
                        int tid = getArguments().getInt("tid", 0);
                        int pid = getArguments().getInt("pid", 0);
                        int fid = getArguments().getInt("fid", 0);
                        int anonymode = 0;
                        if (anony.isChecked()) {
                            anonymode = 1;
                        }
                        String prefix = getArguments().getString("prefix");
                        new PostCommentTask(fid, pid, tid, anonymode, prefix,
                                getActivity(), PostCommentDialogFragment.this)
                                .execute(input.getText().toString());
                    } else {
                        ToastUtils.showShortToast("贴条内容长度必须在6~650字节范围内");
                    }
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).setNegativeButton(android.R.string.cancel,
                (dialog, whichButton) -> {
                    dialog.dismiss();
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        return builder.create();
    }

    @Override
    public void OnPostCommentFinished(String result, boolean success) {
        ToastUtils.showShortToast(result);
        if (getActivity() != null) {
            if (success) {
                NLog.i("TAG", "SUCCESS");
                try {
                    Field field = getDialog().getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(getDialog(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getDialog().dismiss();
            }
        }
    }

}
