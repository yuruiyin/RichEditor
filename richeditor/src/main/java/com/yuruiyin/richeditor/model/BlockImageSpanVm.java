package com.yuruiyin.richeditor.model;

import android.content.Context;

import com.yuruiyin.richeditor.R;
import com.yuruiyin.richeditor.config.AppConfig;

/**
 * Title: BlockImageSpan 相关数据
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-09
 */
public class BlockImageSpanVm<T extends IBlockImageSpanObtainObject> {

    /**
     * 图片宽
     */
    private int width;

    /**
     * 图片最大高度
     */
    private int maxHeight;

    /**
     * BlockImageSpan中包含的用户自定义的对象
     */
    private T spanObject;

    /**
     * 是否是视频
     */
    private boolean isVideo;

    /**
     * 是否gif
     */
    private boolean isGif;

    /**
     * 是否长图
     */
    private boolean isLong;

    /**
     * 是否为相册图片（用于判断是否给ImageSpan添加圆角）
     */
    private boolean isPhoto;

    /**
     * 是否来自草稿，（若来自草稿，则插入BlockImageSpan的时候，不在图片前面插入换行符'\n'）
     */
    private boolean isFromDraft;

    public BlockImageSpanVm(Context context, T spanObject) {
        this.width = (int) context.getResources().getDimension(R.dimen.rich_editor_default_image_width);
        this.maxHeight = (int) (this.width * AppConfig.IMAGE_MAX_HEIGHT_WIDTH_RATIO);
        this.spanObject = spanObject;
    }

    public BlockImageSpanVm(T spanObject, int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.spanObject = spanObject;
    }

    public BlockImageSpanVm(T spanObject, int width) {
        this.width = width;
        this.maxHeight = (int) (width * AppConfig.IMAGE_MAX_HEIGHT_WIDTH_RATIO);
        this.spanObject = spanObject;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public T getSpanObject() {
        return spanObject;
    }

    public void setSpanObject(T spanObject) {
        this.spanObject = spanObject;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean isGif) {
        this.isGif = isGif;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean isLong) {
        this.isLong = isLong;
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setPhoto(boolean isPhoto) {
        this.isPhoto = isPhoto;
    }

    public boolean isFromDraft() {
        return isFromDraft;
    }

    public void setFromDraft(boolean isFromDraft) {
        this.isFromDraft = isFromDraft;
    }
}
