package com.yuruiyin.richeditor;

import android.app.Activity;
import android.text.Editable;
import android.text.ParcelableSpan;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.widget.ImageView;
import com.yuruiyin.richeditor.enumtype.BlockTypeEnum;
import com.yuruiyin.richeditor.enumtype.InlineStyleEnum;
import com.yuruiyin.richeditor.model.InlineStyleVm;
import com.yuruiyin.richeditor.span.BoldStyleSpan;
import com.yuruiyin.richeditor.span.ItalicStyleSpan;
import com.yuruiyin.richeditor.utils.SoftKeyboardUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Title: 富文本编辑器帮助类
 * Description: 处理行内样式、段样式，图片（或视频封面）、自定义布局等。
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichUtils {

    private static final String TAG = "RichUtils";

    private RichEditText mRichEditText;

    private Activity mActivity;

    // 标记支持哪些行内样式
    private Map<String, InlineStyleVm> mInlineTypeToVmMap = new HashMap<>();

    public RichUtils(Activity activity, RichEditText richEditText) {
        mActivity = activity;
        mRichEditText = richEditText;
    }

    public void init() {
        RichTextWatcher mTextWatcher = new RichTextWatcher(mRichEditText);
        mRichEditText.addTextWatcher(mTextWatcher);

        // 监听光标位置变化
        mRichEditText.setOnSelectionChangedListener(this::handleSelectionChanged);

        // 监听删除按键
        mRichEditText.setBackspaceListener(this::handleDeleteKey);

        //为了兼容模拟器
        mRichEditText.setOnKeyListener((v, keyCode, event) -> {
            if (KeyEvent.KEYCODE_DEL == event.getKeyCode()
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && !SoftKeyboardUtil.isSoftShowing(mActivity)) {
                //监听到删除键但是软键盘没弹出，可以基本断定是用模拟器
                // TODO 也存在模拟器也会弹出软键盘的
                return handleDeleteKey();
            }
            return false;
        });
    }

    private void changeStyleBtnImage(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

    public void initInlineStyle(InlineStyleVm inlineStyleVm) {
        String inlineType = inlineStyleVm.getType();
        mInlineTypeToVmMap.put(inlineType, inlineStyleVm);
        inlineStyleVm.getIvButton().setOnClickListener(v -> toggleInlineStyle(inlineType));
    }

    private ParcelableSpan getInlineStyleSpan(Class spanClazz) {
        if (BoldStyleSpan.class == spanClazz) {
            return new BoldStyleSpan();
        } else if (ItalicStyleSpan.class == spanClazz) {
            return new ItalicStyleSpan();
        } else if (StrikethroughSpan.class == spanClazz) {
            return new StrikethroughSpan();
        } else if (UnderlineSpan.class == spanClazz) {
            return new UnderlineSpan();
        }

        return null;
    }

    /**
     * 处理行内样式的边界
     * 比如选中的区域start和end分别处于两个指定StyleSpan时间，则需要将这两端的StyleSpan切割成左右两块
     *
     * @param spanClazz 执行行内样式class类型
     */
    private void handleInlineStyleBoundary(Class spanClazz) {
        Editable editable = mRichEditText.getEditableText();
        int start = mRichEditText.getSelectionStart();
        int end = mRichEditText.getSelectionEnd();
        ParcelableSpan[] parcelableSpans = (ParcelableSpan[]) editable.getSpans(start, end, spanClazz);

        if (parcelableSpans.length <= 0) {
            return;
        }

        if (parcelableSpans.length == 1) {
            ParcelableSpan singleSpan = parcelableSpans[0];
            int singleSpanStart = editable.getSpanStart(singleSpan);
            int singleSpanEnd = editable.getSpanEnd(singleSpan);
            if (singleSpanStart < start) {
                ParcelableSpan wantAddSpan = getInlineStyleSpan(spanClazz);
                if (wantAddSpan != null) {
                    editable.setSpan(wantAddSpan, singleSpanStart, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            if (singleSpanEnd > end) {
                ParcelableSpan wantAddSpan = getInlineStyleSpan(spanClazz);
                if (wantAddSpan != null) {
                    editable.setSpan(wantAddSpan, end, singleSpanEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }

            return;
        }

        ParcelableSpan firstSpan = parcelableSpans[0];
        ParcelableSpan lastSpan = parcelableSpans[parcelableSpans.length - 1];

        int firstSpanStart = editable.getSpanStart(firstSpan);
        if (firstSpanStart < start) {
            ParcelableSpan wantAddSpan = getInlineStyleSpan(spanClazz);
            if (wantAddSpan != null) {
                editable.setSpan(wantAddSpan, firstSpanStart, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        int lastSpanEnd = editable.getSpanEnd(lastSpan);
        if (lastSpanEnd > end) {
            ParcelableSpan wantAddSpan = getInlineStyleSpan(spanClazz);
            if (wantAddSpan != null) {
                editable.setSpan(wantAddSpan, end, lastSpanEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }

    }

    /**
     * 合并所有连续的inline span（由于有切割算法）
     * 时机：需要上传富文本内容到服务端时
     */
    private void mergeAllContinuousInlineSpan() {
        // TODO
    }

    /**
     * 获取合并后的span flag
     *
     * @param mergedLeftSpanFlag
     * @param mergedRightSpanFlag
     * @return
     */
    private int getMergeSpanFlag(int mergedLeftSpanFlag, int mergedRightSpanFlag) {
        boolean isStartInclusive = false;  // 是否包括左端点
        boolean isEndInclusive = false;    // 是否包括右端点
        if (mergedLeftSpanFlag == Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                || mergedLeftSpanFlag == Spanned.SPAN_INCLUSIVE_INCLUSIVE) {
            isStartInclusive = true;
        }

        if (mergedRightSpanFlag == Spanned.SPAN_INCLUSIVE_INCLUSIVE
                || mergedRightSpanFlag == Spanned.SPAN_EXCLUSIVE_INCLUSIVE) {
            isEndInclusive = true;
        }

        if (isStartInclusive && isEndInclusive) {
            return Spanned.SPAN_INCLUSIVE_INCLUSIVE;
        }

        if (isStartInclusive) {
            return Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
        }

        if (isEndInclusive) {
            return Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
        }

        return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
    }

    /**
     * 先合并连续的inline span（由于有切割算法）
     * 时机：
     * 1. 光标在一个位置点击行内样式按钮时；
     * 2. 光标发生变化时
     */
    private void mergeContinuousInlineSpan(int leftPos, int rightPos, Class spanClazz) {
        Editable editable = mRichEditText.getEditableText();
        if (leftPos < 0 || leftPos > editable.length()
                || rightPos < 0 || rightPos > editable.length()
                || leftPos > rightPos) {
            return;
        }

        if (leftPos > 0) {
            ParcelableSpan[] leftSpans = (ParcelableSpan[]) editable.getSpans(leftPos, leftPos, spanClazz);
            if (leftSpans.length >= 2) {
                ParcelableSpan leftSpan = null;
                int resSpanStart = 0;
                int resSpanEnd = rightPos;
                for (ParcelableSpan span : leftSpans) {
                    if (editable.getSpanStart(span) < leftPos) {
                        resSpanStart = editable.getSpanStart(span);
                        leftSpan = span;
                        break;
                    }
                }
                if (leftSpan != null) {
                    int leftSpanFlags = editable.getSpanFlags(leftSpan);
                    int rightSpanFlags = Spanned.SPAN_INCLUSIVE_INCLUSIVE;
                    for (ParcelableSpan span : leftSpans) {
                        if (editable.getSpanStart(span) < leftPos) {
                            editable.removeSpan(span);
                        }
                        if (editable.getSpanStart(span) == leftPos && editable.getSpanEnd(span) == rightPos) {
                            rightSpanFlags = editable.getSpanFlags(span);
                            editable.removeSpan(span);
                        }
                    }
                    ParcelableSpan wantAddSpan = getInlineStyleSpan(spanClazz);
                    editable.setSpan(wantAddSpan, resSpanStart, resSpanEnd, getMergeSpanFlag(leftSpanFlags, rightSpanFlags));
                }
            }
        }

        if (rightPos < editable.length()) {
            ParcelableSpan[] rightSpans = (ParcelableSpan[]) editable.getSpans(rightPos, rightPos, spanClazz);
            if (rightSpans.length >= 2) {
                ParcelableSpan curRightSpan = null;
                ParcelableSpan curLeftSpan = null;
                int resSpanStart = 0;
                int resSpanEnd = 0;
                for (ParcelableSpan span : rightSpans) {
                    if (editable.getSpanEnd(span) == rightPos) {
                        curLeftSpan = span;
                        resSpanStart = editable.getSpanStart(span);
                    } else if (editable.getSpanEnd(span) > rightPos) {
                        curRightSpan = span;
                        resSpanEnd = editable.getSpanEnd(span);
                    }
                }

                if (curLeftSpan != null && curRightSpan != null) {
                    int leftSpanFlags = editable.getSpanFlags(curLeftSpan);
                    int rightSpanFlags = editable.getSpanFlags(curRightSpan);
                    for (ParcelableSpan span : rightSpans) {
                        editable.removeSpan(span);
                    }
                    ParcelableSpan wantAddSpan = getInlineStyleSpan(spanClazz);
                    editable.setSpan(wantAddSpan, resSpanStart, resSpanEnd, getMergeSpanFlag(leftSpanFlags, rightSpanFlags));
                }
            }
        }

    }

    /**
     * 通过行内样式类型获取对应Class
     *
     * @param type
     * @return
     */
    private Class getInlineSpanClassFromType(@InlineStyleEnum String type) {
        switch (type) {
            case InlineStyleEnum.BOLD:
                return BoldStyleSpan.class;
            case InlineStyleEnum.ITALIC:
                return ItalicStyleSpan.class;
            case InlineStyleEnum.STRIKE_THROUGH:
                return StrikethroughSpan.class;
            case InlineStyleEnum.UNDERLINE:
                return UnderlineSpan.class;
        }

        return null;
    }

    /**
     * 处理加粗
     * 修改选中区域的粗体样式
     */
    private void toggleInlineStyle(@InlineStyleEnum String type) {
        InlineStyleVm inlineStyleVm = mInlineTypeToVmMap.get(type);
        if (inlineStyleVm == null) {
            return;
        }

        Class spanClazz = getInlineSpanClassFromType(type);
        if (spanClazz == null) {
            return;
        }

        inlineStyleVm.setLight(!inlineStyleVm.isLight()); // 粗体状态取反
        changeStyleBtnImage(inlineStyleVm.getIvButton(),
                inlineStyleVm.isLight() ? inlineStyleVm.getLightResId() : inlineStyleVm.getNormalResId());

        Editable editable = mRichEditText.getEditableText();
        int start = mRichEditText.getSelectionStart();
        int end = mRichEditText.getSelectionEnd();

        ParcelableSpan[] parcelableSpans = (ParcelableSpan[]) editable.getSpans(start, end, spanClazz);

        // 先将两端的span进行切割
        handleInlineStyleBoundary(spanClazz);

        // 可能存在多个分段的span，需要先都移除
        for (ParcelableSpan parcelableSpan : parcelableSpans) {
            editable.removeSpan(parcelableSpan);
        }

        if (inlineStyleVm.isLight()) {
            int flags = start == end ? Spanned.SPAN_INCLUSIVE_INCLUSIVE : Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
            editable.setSpan(getInlineStyleSpan(spanClazz), start, end, flags);
            mergeContinuousInlineSpan(start, end, spanClazz);
        }
    }

    /**
     * 切换段内样式
     *
     * @param type 具体段内样式（如段落，引用等）
     */
    public void toggleBlockType(@BlockTypeEnum String type) {
        // TODO
    }

    /**
     * 修改各个按钮的状态（点亮或置灰）
     */
    private void handleStyleButtonsStatus() {
        Editable editable = mRichEditText.getEditableText();
        int cursorPos = mRichEditText.getSelectionEnd();
        String content = mRichEditText.getText().toString();

        // 先将所有按钮置灰
        for (InlineStyleVm inlineStyleVm : mInlineTypeToVmMap.values()) {
            inlineStyleVm.setLight(false);
            changeStyleBtnImage(inlineStyleVm.getIvButton(), inlineStyleVm.getNormalResId());
        }

        // TODO 段内样式也同样处理

        for (String type : mInlineTypeToVmMap.keySet()) {
            ParcelableSpan[] parcelableSpans = (ParcelableSpan[]) editable.getSpans(cursorPos, cursorPos, getInlineSpanClassFromType(type));
            if (parcelableSpans.length <= 0) {
                continue;
            }

            boolean isLight = false; //是否点亮

            for (ParcelableSpan span : parcelableSpans) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);
                int spanFlag = editable.getSpanFlags(span);
                if (spanStart < cursorPos && spanEnd > cursorPos) {
                    isLight = true;
                } else if (spanStart == cursorPos
                        && (spanFlag == Spanned.SPAN_INCLUSIVE_INCLUSIVE || spanFlag == Spanned.SPAN_INCLUSIVE_EXCLUSIVE)) {
                    isLight = true;
                } else if (spanEnd == cursorPos
                        && (spanFlag == Spanned.SPAN_INCLUSIVE_INCLUSIVE || spanFlag == Spanned.SPAN_EXCLUSIVE_INCLUSIVE)) {
                    isLight = true;
                }
            }

            if (isLight) {
                InlineStyleVm inlineStyleVm = mInlineTypeToVmMap.get(type);
                if (inlineStyleVm == null) {
                    continue;
                }
                inlineStyleVm.setLight(true);
                changeStyleBtnImage(inlineStyleVm.getIvButton(), inlineStyleVm.getLightResId());
            }
        }

        // TODO 段内样式也同样处理

    }

    /**
     * 光标发生变化的时候，若光标的位置处于某个span的右侧，则将该span的end恢复成包含（inclusive）
     * @param cursorPos 当前位置
     * @param spanClazz 具体的spanClazz
     */
    private void restoreSpanEndToInclusive(int cursorPos, Class spanClazz) {
        Editable editable = mRichEditText.getEditableText();
        ParcelableSpan[] parcelableSpans = (ParcelableSpan[]) editable.getSpans(cursorPos, cursorPos, spanClazz);

        for (ParcelableSpan span: parcelableSpans) {
            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);
            if (spanEnd == cursorPos) {
                editable.removeSpan(span);
                editable.setSpan(getInlineStyleSpan(spanClazz), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
    }

    /**
     * 处理光标的位置变化
     *
     * @param cursorPos 当前光标位置
     */
    private void handleSelectionChanged(int cursorPos) {
        // 先合并指定位置前后连续的行内样式
        for (String type : mInlineTypeToVmMap.keySet()) {
            mergeContinuousInlineSpan(cursorPos, cursorPos, getInlineSpanClassFromType(type));
            restoreSpanEndToInclusive(cursorPos, getInlineSpanClassFromType(type));
        }

        // 修改各个按钮的状态（点亮或置灰）
        handleStyleButtonsStatus();
    }

    /**
     * 处理删除按键
     * 1、删除BlockImageSpan的时候，直接将光标定位到上一行末尾
     * 2、当光标处于BlockImageSpan下一行的第一个位置（不是EditText最后一个字符）上按删除按键时,
     * 不删除字符，而是将光标定位到上一行的末尾（即BlockImageSpan的末尾）
     */
    private boolean handleDeleteKey() {
        // TODO

        return false;
    }

}
