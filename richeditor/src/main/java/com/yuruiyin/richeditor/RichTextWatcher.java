package com.yuruiyin.richeditor;

import android.content.Context;
import android.text.Editable;
import android.text.ParcelableSpan;
import com.hanks.lineheightedittext.TextWatcher;
import com.yuruiyin.richeditor.span.BlockImageSpan;

/**
 * Title: 编辑器字符变化监听器
 * Description: 如在段落图片后面输入字符时自动换行等。
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichTextWatcher implements TextWatcher {

    private Context mContext;
    private RichEditText mEditText;

    // 在修改前的输入框的文本长度
    private int beforeEditContentLen = 0;

    // 需要插入回车符的位置，场景：在imageSpan后面输入文字时候需要换行
    private int needInsertBreakLinePos = -1;

    // 上次的输入框内容
    private String lastEditTextContent = "";

    private RichUtils mRichUtils;

    // 是否删除了回车符
    private boolean isDeleteEnterStr;

    public RichTextWatcher(RichEditText editText) {
        mEditText = editText;
        mContext = mEditText.getContext();
        mRichUtils = mEditText.getRichUtils();
    }

    /**
     * 删除字符的时候，要删除当前位置start和end相等的span
     */
    private void handleDelete() {
        Editable editable = mEditText.getEditableText();
        int cursorPos = mEditText.getSelectionStart();

        ParcelableSpan[] parcelableSpans = editable.getSpans(cursorPos, cursorPos, ParcelableSpan.class);

        for (ParcelableSpan span : parcelableSpans) {
            if (editable.getSpanStart(span) == editable.getSpanEnd(span)) {
                editable.removeSpan(span);
            }
        }

        if (isDeleteEnterStr) {
            // 删除了回车符，如果回车前后两行只要有一行是block样式，就要合并
            mRichUtils.mergeBlockSpanAfterDeleteEnter();
        }
    }

    /**
     * 修改上一行的段样式或者上一行末尾的N个行内样式的flag为end exclusive，即当前行与上一行断开连接
     */
    private void changeLastBlockOrInlineSpanFlag() {
        mRichUtils.changeLastBlockOrInlineSpanFlag();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        isDeleteEnterStr = after == 0 && s.charAt(start) == '\n';
        beforeEditContentLen = s.length();
        Editable editable = mEditText.getText();
        int curPos = mEditText.getSelectionStart();
        if (curPos == 0) {
            needInsertBreakLinePos = -1;
            return;
        }

        BlockImageSpan[] blockImageSpans = editable.getSpans(curPos - 1, curPos, BlockImageSpan.class);
        if (blockImageSpans.length > 0) {
            //说明当前光标处于imageSpan的后面，如果在当前位置输入文字，需要另起一行
            needInsertBreakLinePos = curPos;
        } else {
            needInsertBreakLinePos = -1;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() < beforeEditContentLen) {
            // 说明删除了字符
            handleDelete();
            lastEditTextContent = s.toString();
            return;
        }

        int cursorPos = mEditText.getSelectionStart();
        String editContent = s.toString();
        if (needInsertBreakLinePos != -1 &&
                cursorPos > 0 && editContent.charAt(cursorPos - 1) != '\n') {
            //在imageSpan后面输入了文字（除了'\n'），则需要换行
            s.insert(needInsertBreakLinePos, "\n");
        }

        if (cursorPos > 0 && editContent.charAt(cursorPos - 1) == '\n' && !editContent.equals(lastEditTextContent)) {
            // 输入了回车, 需要断开上一行的样式（包括inline和block）
            lastEditTextContent = s.toString();
            changeLastBlockOrInlineSpanFlag();
        }

        lastEditTextContent = s.toString();
    }
}
