package com.yuruiyin.richeditor;

import android.content.Context;
import android.text.Editable;
import android.text.ParcelableSpan;
import android.widget.EditText;
import com.hanks.lineheightedittext.TextWatcher;

/**
 * Title: 编辑器字符变化监听器
 * Description: 如在段落图片后面输入字符时自动换行等。
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichTextWatcher implements TextWatcher {

    private Context mContext;
    private EditText mEditText;

    // 在修改前的输入框的文本长度
    private int beforeEditContentLen = 0;

    public RichTextWatcher(EditText editText) {
        mEditText = editText;
        mContext = mEditText.getContext();
    }

    /**
     * 删除字符的时候，要删除当前位置start和end相等的span
     */
    private void handleDelete() {
        Editable editable = mEditText.getEditableText();
        int cursorPos = mEditText.getSelectionStart();

        ParcelableSpan[] parcelableSpans = editable.getSpans(cursorPos, cursorPos, ParcelableSpan.class);

        for (ParcelableSpan span: parcelableSpans) {
            if (editable.getSpanStart(span) == editable.getSpanEnd(span)) {
                editable.removeSpan(span);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeEditContentLen = s.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() < beforeEditContentLen) {
            // 说明删除了字符
            handleDelete();
            return;
        }

        // TODO 输入了字符
    }
}
