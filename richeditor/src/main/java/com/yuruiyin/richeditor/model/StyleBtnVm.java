package com.yuruiyin.richeditor.model;

import androidx.annotation.ColorInt;
import android.view.View;
import android.widget.ImageView;

import android.widget.TextView;
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
     * 是否点亮
     */
    private boolean isLight;

    /**
     * 按钮ImageView
     */
    private ImageView ivIcon;

    /**
     * 正常的资源id
     */
    private int iconNormalResId;

    /**
     * 点亮的资源id
     */
    private int iconLightResId;

    /**
     * 被点击的view
     */
    private View clickedView;

    /**
     * 标题文本（如粗体、斜体、标题等）
     */
    private TextView tvTitle;

    /**
     * 标题文本正常的颜色
     */
    private @ColorInt int titleNormalColor;

    /**
     * 标题文本点亮的颜色
     */
    private @ColorInt int titleLightColor;

    public StyleBtnVm(Builder builder) {
        this.type = builder.type;
        this.ivIcon = builder.ivIcon;
        this.isLight = false;
        this.iconNormalResId = builder.iconNormalResId;
        this.iconLightResId = builder.iconLightResId;
        this.clickedView = builder.clickedView;
        this.tvTitle = builder.tvTitle;
        this.titleNormalColor = builder.titleNormalColor;
        this.titleLightColor = builder.titleLightColor;
    }

    public String getType() {
        return type;
    }

    public ImageView getIvIcon() {
        return ivIcon;
    }

    public boolean isLight() {
        return isLight;
    }

    public void setLight(boolean light) {
        isLight = light;
    }

    public int getNormalResId() {
        return iconNormalResId;
    }

    public int getLightResId() {
        return iconLightResId;
    }

    public boolean isInlineType() {
        return isInlineType;
    }

    public void setIsInlineType(boolean isInlineType) {
        this.isInlineType = isInlineType;
    }

    public View getClickedView() {
        return clickedView;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public int getTitleNormalColor() {
        return titleNormalColor;
    }

    public int getTitleLightColor() {
        return titleLightColor;
    }

    public static class Builder {
        /**
         * 具体类型（包含粗体、斜体、标题等）
         */
        private @RichTypeEnum String type;

        /**
         * 按钮ImageView
         */
        private ImageView ivIcon;

        /**
         * 正常的资源id
         */
        private int iconNormalResId;

        /**
         * 点亮的资源id
         */
        private int iconLightResId;

        /**
         * 被点击的view
         */
        private View clickedView;

        /**
         * 标题文本（如粗体、斜体、标题等）
         */
        private TextView tvTitle;

        /**
         * 标题文本正常的颜色
         */
        private @ColorInt int titleNormalColor;

        /**
         * 标题文本点亮的颜色
         */
        private @ColorInt int titleLightColor;

        public Builder setType(@RichTypeEnum String type) {
            this.type = type;
            return this;
        }

        public Builder setIvIcon(ImageView ivIcon) {
            this.ivIcon = ivIcon;
            return this;
        }

        public Builder setIconNormalResId(int iconNormalResId) {
            this.iconNormalResId = iconNormalResId;
            return this;
        }

        public Builder setIconLightResId(int iconLightResId) {
            this.iconLightResId = iconLightResId;
            return this;
        }

        public Builder setTvTitle(TextView tvTitle) {
            this.tvTitle = tvTitle;
            return this;
        }

        public Builder setTitleNormalColor(@ColorInt int titleNormalColor) {
            this.titleNormalColor = titleNormalColor;
            return this;
        }

        public Builder setTitleLightColor(@ColorInt int titleLightColor) {
            this.titleLightColor = titleLightColor;
            return this;
        }

        public Builder setClickedView(View clickedView) {
            this.clickedView = clickedView;
            return this;
        }

        public StyleBtnVm build() {
            return new StyleBtnVm(this);
        }
    }
}
