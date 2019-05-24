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
    private int needInsertBreakLinePosAfterImage = -1;
    // 是否需要在ImageSpan之前插入换行，场景：在imageSpan前面输入文字时候需要换行
    private boolean isNeedInsertBreakLineBeforeImage;

    // 上次的输入框内容
    private String lastEditTextContent = "";

    // 是否删除了回车符
    private boolean isDeleteEnterStr;

    public RichTextWatcher(RichEditText editText) {
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

        for (ParcelableSpan span : parcelableSpans) {
            if (editable.getSpanStart(span) == editable.getSpanEnd(span)) {
                editable.removeSpan(span);
            }
        }

        if (isDeleteEnterStr) {
            // 删除了回车符，如果回车前后两行只要有一行是block样式，就要合并
            mEditText.getRichUtils().mergeBlockSpanAfterDeleteEnter();
        }
    }

    /**
     * 修改上一行的段样式或者上一行末尾的N个行内样式的flag为end exclusive，即当前行与上一行断开连接
     */
    private void changeLastBlockOrInlineSpanFlag() {
        mEditText.getRichUtils().changeLastBlockOrInlineSpanFlag();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        isDeleteEnterStr = after == 0 && s.length() > 0 && s.charAt(start) == '\n';
        beforeEditContentLen = s.length();
        Editable editable = mEditText.getText();
        int curPos = mEditText.getSelectionStart();

        // 判断是否在图片后输入
        if (curPos == 0) {
            needInsertBreakLinePosAfterImage = -1;
        } else {
            // 判断是否在图片后面输入
            BlockImageSpan[] blockImageSpansAfter = editable.getSpans(curPos - 1, curPos, BlockImageSpan.class);
            if (blockImageSpansAfter.length > 0) {
                //说明当前光标处于imageSpan的后面，如果在当前位置输入文字，需要另起一行
                needInsertBreakLinePosAfterImage = curPos;
            } else {
                needInsertBreakLinePosAfterImage = -1;
            }
        }

        // 判断是否在图片前面输入
        BlockImageSpan[] blockImageSpansBefore = editable.getSpans(curPos, curPos + 1, BlockImageSpan.class);
        // 说明当前光标在ImageSpan的前面
        isNeedInsertBreakLineBeforeImage = blockImageSpansBefore.length > 0;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() < beforeEditContentLen) {
            // 说明删除了字符
            if (s.length() > 0) {
                handleDelete();
            }
            lastEditTextContent = s.toString();
            return;
        }

        int cursorPos = mEditText.getSelectionStart();
        String editContent = s.toString();
        if (needInsertBreakLinePosAfterImage != -1 &&
                cursorPos > 0 && editContent.charAt(cursorPos - 1) != '\n') {
            //在imageSpan后面输入了文字（除了'\n'），则需要换行
            s.insert(needInsertBreakLinePosAfterImage, "\n");
        }

        if (isNeedInsertBreakLineBeforeImage && cursorPos >= 0) {
            // 在ImageSpan前输入回车, 则需要将光标移动到上一个行
            // 在ImageSpan前输入文字（除了'\n'），则需要先换行，在将光标移动到上一行
            if (editContent.charAt(cursorPos - 1) != '\n') {
                s.insert(cursorPos, "\n");
            }
            mEditText.setSelection(cursorPos);
        }

        if (cursorPos > 0 && editContent.charAt(cursorPos - 1) == '\n' && !editContent.equals(lastEditTextContent)) {
            // 输入了回车, 需要断开上一行的样式（包括inline和block）
            lastEditTextContent = s.toString();
            changeLastBlockOrInlineSpanFlag();
        }

        lastEditTextContent = s.toString();
    }
}
