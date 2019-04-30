package com.yuruiyin.richeditor;

import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

/**
 * Title: 解决editText无法通过setOnKeyListener来监听部分输入法的按键的问题
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class RichInputConnectionWrapper extends InputConnectionWrapper {

    private BackspaceListener mBackspaceListener;

    public interface BackspaceListener {
        /**
         * @return true: 消费了退格键，false：未消费退格键
         */
        boolean onBackspace();
    }

    /**
     * Initializes a wrapper.
     * <p>
     * <p><b>Caveat:</b> Although the system can accept {@code (InputConnection) null} in some
     * places, you cannot emulate such a behavior by non-null {@link InputConnectionWrapper} that
     * has {@code null} in {@code target}.</p>
     *
     * @param target  the {@link InputConnection} to be proxied.
     * @param mutable set {@code true} to protect this object from being reconfigured to target
     *                another {@link InputConnection}.  Note that this is ignored while the target is {@code null}.
     */
    public RichInputConnectionWrapper(InputConnection target, boolean mutable) {
        super(target, mutable);
    }

    public void setBackspaceListener(BackspaceListener backspaceListener) {
        this.mBackspaceListener = backspaceListener;
    }

    /**
     * 当软键盘删除文本之前，会调用这个方法通知输入框，我们可以重写这个方法并判断是否拦截这个事件
     * 在谷歌输入法上，点击退格键的时候不会调用{@link #sendKeyEvent(KeyEvent event)}，
     * 而是直接回调这个方法，所以需要在这个方法上做拦截。
     * @param beforeLength
     * @param afterLength
     * @return
     */
    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return mBackspaceListener != null && mBackspaceListener.onBackspace() ||
                super.deleteSurroundingText(beforeLength, afterLength);
    }

    /**
     * 当在软件盘上点击某些按钮（比如退格键，数字键，回车键等），该方法可能会被触发（取决于输入法的开发者），
     * 所以也可以重写该方法并拦截这些事件，这些事件就不会被分发到输入框了
     * @param event
     * @return
     */
    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mBackspaceListener != null && mBackspaceListener.onBackspace()) {
                return true;
            }
        }
        return super.sendKeyEvent(event);
    }

}
