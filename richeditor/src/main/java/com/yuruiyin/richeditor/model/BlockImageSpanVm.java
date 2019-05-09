package com.yuruiyin.richeditor.model;

/**
 * Title: BlockImageSpan 相关数据
 * Description:
 *
 * @author yuruiyin
 * @version 2019-05-09
 */
public class BlockImageSpanVm {

    private String type;

    private int width;

    private int maxHeight;

    private Object spanObject;

    public BlockImageSpanVm(String type, int width, int maxHeight, Object spanObject) {
        this.type = type;
        this.width = width;
        this.maxHeight = maxHeight;
        this.spanObject = spanObject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Object getSpanObject() {
        return spanObject;
    }

    public void setSpanObject(Object spanObject) {
        this.spanObject = spanObject;
    }
}
