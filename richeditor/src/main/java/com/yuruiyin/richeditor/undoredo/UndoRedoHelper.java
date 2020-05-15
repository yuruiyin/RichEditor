package com.yuruiyin.richeditor.undoredo;

import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.yuruiyin.richeditor.RichEditText;
import com.yuruiyin.richeditor.RichUtils;
import com.yuruiyin.richeditor.enumtype.RichTypeEnum;
import com.yuruiyin.richeditor.enumtype.UndoRedoActionTypeEnum;

import java.util.Deque;
import java.util.LinkedList;

/**
 * undo redo
 * 兼容文字样式修改，如加粗、斜体、标题等
 * 参考 https://github.com/qinci/AndroidEdit
 *
 * @modified by yry
 */
public class UndoRedoHelper {

    private final ToggleStyleObserver toggleStyleObserver = new ToggleStyleObserver();

    //操作序号(一次编辑可能对应多个操作，如替换文字，就是删除+插入)
    private int index;

    //撤销栈
    private Deque<Action> history = new LinkedList<>();
    //恢复栈
    private Deque<Action> historyBack = new LinkedList<>();

    private Editable editable;
    private RichEditText editText;
    private RichUtils richUtils;
    //自动操作标志，防止重复回调,导致无限撤销
    private boolean flag = false;

    public UndoRedoHelper(@NonNull RichEditText editText) {
        CheckNull(editText, "EditText不能为空");
        this.editable = editText.getText();
        this.editText = editText;
        this.richUtils = editText.getRichUtils();
        editText.addTextChangedListener(new UndoRedoHelper.Watcher());

        // 注册样式修改观察者到被观察中，当样式修改的时候，会通知当前对象将action插入到撤销栈中。
        richUtils.registerToggleStyleObserver(toggleStyleObserver);
    }

    protected void onEditableChanged(Editable s) {

    }

    protected void onTextChanged(Editable s) {

    }

    /**
     * 清理记录
     * Clear history.
     */
    public final void clearHistory() {
        history.clear();
        historyBack.clear();
    }

    /**
     * 撤销修改样式
     */
    private void undoRedoChangeStyle(UndoRedoHelper.Action action) {
        richUtils.toggleStyleFromUndoRedo(action.richType, action.startCursor, action.endCursor);
    }

    /**
     * 撤销
     * Undo.
     */
    public final void undo() {
        if (history.isEmpty()) {
            return;
        }

        //锁定操作
        flag = true;
        UndoRedoHelper.Action action = history.pop();
        historyBack.push(action);

        switch (action.actionType) {
            case UndoRedoActionTypeEnum.ADD:
                //撤销添加
                editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
                editText.setSelection(action.startCursor, action.startCursor);
                break;
            case UndoRedoActionTypeEnum.DELETE:
                //插销删除
                editable.insert(action.startCursor, action.actionTarget);
                editText.setSelection(action.startCursor + action.actionTarget.length());
                break;
            case UndoRedoActionTypeEnum.CHANGE:
            default:
                // 撤销样式修改
                undoRedoChangeStyle(action);
                break;
        }

        //释放操作
        flag = false;
        //判断是否是下一个动作是否和本动作是同一个操作，直到不同为止
        if (!history.isEmpty() && history.peek().index == action.index) {
            undo();
        }
    }

    /**
     * 恢复
     * Redo.
     */
    public final void redo() {
        if (historyBack.isEmpty()) {
            return;
        }

        flag = true;
        UndoRedoHelper.Action action = historyBack.pop();
        history.push(action);

        switch (action.actionType) {
            case UndoRedoActionTypeEnum.ADD:
                //恢复添加
                editable.insert(action.startCursor, action.actionTarget);
                if (action.endCursor == action.startCursor) {
                    editText.setSelection(action.startCursor + action.actionTarget.length());
                } else {
                    editText.setSelection(action.startCursor, action.endCursor);
                }
                break;
            case UndoRedoActionTypeEnum.DELETE:
                //恢复删除
                editable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
                editText.setSelection(action.startCursor, action.startCursor);
                break;
            case UndoRedoActionTypeEnum.CHANGE:
            default:
                // 恢复样式修改
                undoRedoChangeStyle(action);
                break;
        }

        flag = false;
        //判断是否是下一个动作是否和本动作是同一个操作
        if (!historyBack.isEmpty() && historyBack.peek().index == action.index) {
            redo();
        }
    }

    /**
     * 首次设置文本
     * Set default text.
     */
    public final void setDefaultText(CharSequence text) {
        clearHistory();
        flag = true;
        editable.replace(0, editable.length(), text);
        flag = false;
    }

    /**
     * 当样式发生变化回调
     */
    private void onStyleChanged(Action action) {
        action.setIndex(++index);
        history.push(action);
        historyBack.clear();
    }

    private class Watcher implements TextWatcher {

        /**
         * Before text changed.
         *
         * @param s     the s
         * @param start the start 起始光标
         * @param count the endCursor 选择数量
         * @param after the after 替换增加的文字数
         */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            int end = start + count;
            if (end > start && end <= s.length()) {
                CharSequence charSequence = s.subSequence(start, end);
                //删除了文字
                if (charSequence.length() > 0) {
                    UndoRedoHelper.Action action = new Action(
                            charSequence, start, UndoRedoActionTypeEnum.DELETE
                    );

                    if (count > 1) {
                        //如果一次超过一个字符，说名用户选择了，然后替换或者删除操作
                        action.setSelectCount(count);
                    } else if (count == 1 && count == after) {
                        //一个字符替换
                        action.setSelectCount(count);
                    }

                    // todo 还有一种情况:选择一个字符,然后删除(暂时没有考虑这种情况)

                    history.push(action);
                    historyBack.clear();
                    action.setIndex(++index);
                }
            }
        }

        /**
         * On text changed.
         *
         * @param s      the s
         * @param start  the start 起始光标
         * @param before the before 选择数量
         * @param count  the endCursor 添加的数量
         */
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
            if (flag) return;
            int end = start + count;
            if (end > start) {
                CharSequence charSequence = s.subSequence(start, end);
                //添加文字
                if (charSequence.length() > 0) {
                    UndoRedoHelper.Action action = new Action(
                            charSequence, start, UndoRedoActionTypeEnum.ADD);

                    history.push(action);
                    historyBack.clear();
                    if (before > 0) {
                        //文字替换（先删除再增加），删除和增加是同一个操作，所以不需要增加序号
                        action.setIndex(index);
                    } else {
                        action.setIndex(++index);
                    }
                }
            }
        }

        @Override
        public final void afterTextChanged(Editable s) {
            if (flag) return;
            if (s != editable) {
                editable = s;
                onEditableChanged(s);
            }
            UndoRedoHelper.this.onTextChanged(s);
        }

    }

    public static class Action {
        /**
         * 改变字符.
         */
        CharSequence actionTarget;

        /**
         * 光标位置.
         */
        int startCursor;
        int endCursor;

        /**
         * 类型：增加、删除、修改样式
         */
        @UndoRedoActionTypeEnum String actionType;

        @RichTypeEnum String richType;

        /**
         * 操作序号.
         */
        int index;

        public Action(CharSequence actionTag, int startCursor, String actionType) {
            this.actionTarget = actionTag;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.actionType = actionType;
        }

        public Action(int startCursor, int endCursor, @RichTypeEnum String richType) {
            this.startCursor = startCursor;
            this.endCursor = endCursor;
            this.actionType = UndoRedoActionTypeEnum.CHANGE;
            this.richType = richType;
        }

        public void setSelectCount(int count) {
            this.endCursor = endCursor + count;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    private static void CheckNull(Object o, String message) {
        if (o == null) throw new IllegalStateException(message);
    }

    public class ToggleStyleObserver {
        public void onChange(Action action) {
            onStyleChanged(action);
        }
    }

}
