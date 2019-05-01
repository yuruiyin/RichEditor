package com.yuruiyin.richeditor.model;

import android.widget.ImageView;

import com.yuruiyin.richeditor.enumtype.RichTypeEnum;

/**
 * Title: 样式实体
 * Description:
 *
 * @author yuruiyin
 * @version 2019-04-29
 */
public class StyleBtnVm {

    /**
     * 具体类型（包含粗体、斜体、标题等）
     */
    private @RichTypeEnum String type;

    /**
     * 是否行内样式
     */
    private boolean isInlineType;

    /**
     * 按钮ImageView
     */
    private ImageView ivButton;

    /**
     * 是否点亮
     */
    private boolean isLight;

    /**
     * 正常的资源id
     */
    private int normalResId;

    /**
     * 点亮的资源id
     */
    private int lightResId;

    public StyleBtnVm(@RichTypeEnum String type, ImageView ivButton, int normalResId, int lightResId) {
        this.type = type;
        this.ivButton = ivButton;
        this.isLight = false;
        this.normalResId = normalResId;
        this.lightResId = lightResId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageView getIvButton() {
        return ivButton;
    }

    public void setIvButton(ImageView ivButton) {
        this.ivButton = ivButton;
    }

    public boolean isLight() {
        return isLight;
    }

    public void setLight(boolean light) {
        isLight = light;
    }

    public int getNormalResId() {
        return normalResId;
    }

    public void setNormalResId(int normalResId) {
        this.normalResId = normalResId;
    }

    public int getLightResId() {
        return lightResId;
    }

    public void setLightResId(int lightResId) {
        this.lightResId = lightResId;
    }

    public boolean isInlineType() {
        return isInlineType;
    }

    public void setIsInlineType(boolean isInlineType) {
        this.isInlineType = isInlineType;
    }
}
