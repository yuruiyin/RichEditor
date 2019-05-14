package com.yuruiyin.richeditor.model;

import android.support.annotation.Nullable;

import com.yuruiyin.richeditor.enumtype.RichTypeEnum;

import java.util.List;

/**
 * Title: 富文本编辑器中每个段落实体
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-13
 */
public class RichEditorBlock {

    /**
     * 段落类型：如普通文本、标题、引用、以及调用方自定义的各种类型的ImageSpan(包含图片、视频封面、其它自定义view)等。
     * 其中，文本段落都可能包含段落样式和行内样式
     */
    private String blockType;

    /**
     * 文本内容，只有文本段落才有值
     */
    private String text;

    /**
     * 段落ImageSpan包含的实体
     */
    private @Nullable
    IBlockImageSpanObtainObject blockImageSpanObtainObject;

    /**
     * 行内样式列表（一个段落可能包含多个行内样式）
     */
    private List<InlineStyleEntity> inlineStyleEntityList;

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Nullable
    public IBlockImageSpanObtainObject getBlockImageSpanObtainObject() {
        return blockImageSpanObtainObject;
    }

    public void setBlockImageSpanObtainObject(@Nullable IBlockImageSpanObtainObject blockImageSpanObtainObject) {
        this.blockImageSpanObtainObject = blockImageSpanObtainObject;
    }

    public List<InlineStyleEntity> getInlineStyleEntityList() {
        return inlineStyleEntityList;
    }

    public void setInlineStyleEntityList(List<InlineStyleEntity> inlineStyleEntityList) {
        this.inlineStyleEntityList = inlineStyleEntityList;
    }

    public static class InlineStyleEntity {
        /**
         * 行内样式：如加粗、斜体、行内ImageSpan（如@人、提及游戏、插入话题）等
         */
        private @RichTypeEnum
        String inlineType;

        /**
         * 该行内样式在段落中的偏移量
         */
        private int offset;

        /**
         * 该行内样式所占有的字符长度
         */
        private int length;

        /**
         * 该行内ImageSpan所包含的实体, 如@人、提及游戏、插入话题。若是加粗、斜体等行内样式，则可为null
         */
        private @Nullable
        IInlineImageSpanObtainObject inlineImageSpanObtainObject;

        public String getInlineType() {
            return inlineType;
        }

        public void setInlineType(String inlineType) {
            this.inlineType = inlineType;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        @Nullable
        public IInlineImageSpanObtainObject getInlineImageSpanObtainObject() {
            return inlineImageSpanObtainObject;
        }

        public void setInlineImageSpanObtainObject(@Nullable IInlineImageSpanObtainObject inlineImageSpanObtainObject) {
            this.inlineImageSpanObtainObject = inlineImageSpanObtainObject;
        }
    }

}
