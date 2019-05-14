package com.yuruiyin.richeditor.span;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import com.yuruiyin.richeditor.R;
import com.yuruiyin.richeditor.enumtype.RichTypeEnum;

/**
 * Title:文章标题span
 * Description: 利用加粗修改字体大小来实现
 *
 * @author yuruiyin
 * @version 2019-04-30
 */
public class HeadlineSpan extends AbsoluteSizeSpan implements IBlockSpan {

    private String type;

    public HeadlineSpan(Context context) {
        super((int) context.getResources().getDimension(R.dimen.rich_editor_headline_text_size));
        type = RichTypeEnum.BLOCK_HEADLINE;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    @Override
    public String getType() {
        return type;
    }

}
