package com.yuruiyin.richeditor.model;

import android.content.Context;

import com.yuruiyin.richeditor.R;

/**
 * Title: BlockImageSpan 相关数据
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-09
 */
public class BlockImageSpanVm<T extends IBlockImageSpanObtainObject> {

    private int width;
    private int maxHeight;
    private T spanObject;

    // 插入的ImageSpan是否为视频封面，用来确定是否显示视频图标标识
    private boolean isVideo;

    public BlockImageSpanVm(Context context, T spanObject) {
        this.width = (int) context.getResources().getDimension(R.dimen.rich_editor_image_width);
        this.maxHeight = (int) context.getResources().getDimension(R.dimen.rich_editor_image_max_height);
        this.spanObject = spanObject;
    }

    public BlockImageSpanVm(Context context, T spanObject, boolean isVideo) {
        this(context, spanObject);
        this.isVideo = isVideo;
    }

    public BlockImageSpanVm(T spanObject, int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.spanObject = spanObject;
    }

    public BlockImageSpanVm(T spanObject, int width, int maxHeight, boolean isVideo) {
        this(spanObject, width, maxHeight);
        this.isVideo = isVideo;
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

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
