package com.yuruiyin.richeditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.hanks.lineheightedittext.LineHeightEditText;

/**
 * Title: 自定义EditText，可监听光标位置变化
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichEditText extends LineHeightEditText {

    private RichInputConnectionWrapper mRichInputConnection;

    private Context mContext;

    public interface OnSelectionChangedListener {
        /**
         * 光标位置改变回调
         * @param curPos 新的光标位置
         */
        void onChange(int curPos);
    }

    /**
     * EditText监听复制、粘贴、剪切事件回调的接口
     */
    public interface IClipCallback {
        /**
         * 剪切回调
         */
        void onCut();

        /**
         * 复制回调
         */
        void onCopy();

        /**
         * 粘贴回调
         */
        void onPaste();
    }

    /** 光标位置变化监听器 */
    private OnSelectionChangedListener mOnSelectionChangedListener;

    public RichEditText(Context context) {
        super(context);
        init(context);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mRichInputConnection = new RichInputConnectionWrapper(null, true);
    }

    /**
     * 设置软键盘删除按键监听器
     * @param backspaceListener 软键盘删除按键监听器
     */
    protected void setBackspaceListener(RichInputConnectionWrapper.BackspaceListener backspaceListener) {
        mRichInputConnection.setBackspaceListener(backspaceListener);
    }

    /**
     * 注册光标位置监听器
     * @param listener 光标位置变化监听器
     */
    protected void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.mOnSelectionChangedListener = listener;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mOnSelectionChangedListener != null) {
            mOnSelectionChangedListener.onChange(selEnd);
        }
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (!(mContext instanceof IClipCallback)) {
            return super.onTextContextMenuItem(id);
        }

        IClipCallback context = (IClipCallback) mContext;

        switch (id) {
            case android.R.id.cut:
                context.onCut();
                break;
            case android.R.id.copy:
                context.onCopy();
                break;
            case android.R.id.paste:
                context.onPaste();
                //粘贴特殊处理
                return true;
            default:
                break;
        }

        return super.onTextContextMenuItem(id);
    }

    /**
     * 当输入法和EditText建立连接的时候会通过这个方法返回一个InputConnection。
     * 我们需要代理这个方法的父类方法生成的InputConnection并返回我们自己的代理类。
     * */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        mRichInputConnection.setTarget(super.onCreateInputConnection(outAttrs));
        return mRichInputConnection;
    }

}
