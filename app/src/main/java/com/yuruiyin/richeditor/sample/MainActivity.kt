package com.yuruiyin.richeditor.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.yuruiyin.richeditor.enumtype.FileTypeEnum
import com.yuruiyin.richeditor.enumtype.RichTypeEnum
import com.yuruiyin.richeditor.model.BlockImageSpanVm
import com.yuruiyin.richeditor.model.IBlockImageSpanObtainObject
import com.yuruiyin.richeditor.model.StyleBtnVm
import com.yuruiyin.richeditor.sample.model.DividerVm
import com.yuruiyin.richeditor.sample.model.ImageVm
import com.yuruiyin.richeditor.sample.model.VideoVm
import com.yuruiyin.richeditor.sample.utils.JsonUtil
import com.yuruiyin.richeditor.sample.utils.WindowUtil
import com.yuruiyin.richeditor.utils.FileUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_PHOTO_REQUEST_CODE = 1
        const val TAG = "MainActivity"
    }

    private val editorPaddingLeft by lazy {
        resources.getDimension(R.dimen.editor_padding_left)
    }

    private val editorPaddingRight by lazy {
        resources.getDimension(R.dimen.editor_padding_right)
    }

    private val imageWidth by lazy {
        resources.getDimension(R.dimen.editor_image_width).toInt()
    }

    private val imageMaxHeight by lazy {
        resources.getDimension(R.dimen.editor_image_max_height).toInt()
    }

    private val screenWidth by lazy {
        WindowUtil.getScreenSize(this)[0]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerEvents()
    }

    private fun registerEvents() {
        // 生成json数据，显示到TextView上
        tvCreateJson.setOnClickListener {
            val content = Gson().toJson(richEditText.content)
            val formatJsonContent = JsonUtil.getFormatJson(content)
            tvContentJson.text = formatJsonContent
            Log.d(TAG, "\n $formatJsonContent")
        }

        // 加粗
        richEditText.initStyleButton(
                StyleBtnVm(
                        RichTypeEnum.BOLD,
                        ivBold,
                        R.mipmap.icon_bold_normal,
                        R.mipmap.icon_bold_light
                )
        )

        // 斜体
        richEditText.initStyleButton(
                StyleBtnVm(
                        RichTypeEnum.ITALIC,
                        ivItalic,
                        R.mipmap.icon_italic_normal,
                        R.mipmap.icon_italic_light
                )
        )

        // 删除线
        richEditText.initStyleButton(
                StyleBtnVm(
                        RichTypeEnum.STRIKE_THROUGH,
                        ivStrikeThrough,
                        R.mipmap.icon_strikethrough_normal,
                        R.mipmap.icon_strikethrough_light
                )
        )

        // 下划线
        richEditText.initStyleButton(
                StyleBtnVm(
                        RichTypeEnum.UNDERLINE,
                        ivUnderline,
                        R.mipmap.icon_underline_normal,
                        R.mipmap.icon_underline_light
                )
        )

        // 标题
        richEditText.initStyleButton(
                StyleBtnVm(
                        RichTypeEnum.BLOCK_HEADLINE,
                        ivHeadline,
                        R.mipmap.icon_headline_normal,
                        R.mipmap.icon_headline_light
                )
        )

        // 引用
        richEditText.initStyleButton(
                StyleBtnVm(
                        RichTypeEnum.BLOCK_QUOTE,
                        ivBlockquote,
                        R.mipmap.icon_blockquote_normal,
                        R.mipmap.icon_blockquote_light
                )
        )

        // 添加图片
        ivAddImage.setOnClickListener {
            handleAddImage()
        }

        // 添加分割线
        ivAddDivider.setOnClickListener {
            handleAddDivider()
        }

    }

    private fun getEditTextWidthWithoutPadding(): Int {
        // 富文本编辑器编辑区域的宽度, 这个宽度一定要小于编辑器的宽度，否则会出现ImageSpan被绘制两边的情况
        return (screenWidth - editorPaddingLeft - editorPaddingRight - 6).toInt()
    }

    /**
     * 处理添加分割线，其实插入的也是BlockImageSpan
     */
    private fun handleAddDivider() {
        val blockImageSpanVm =
                BlockImageSpanVm(DividerVm(), getEditTextWidthWithoutPadding(), imageMaxHeight)
        richEditText.insertBlockImage(R.mipmap.image_divider_line, blockImageSpanVm, null)
    }

    /**
     * 处理插入图片
     */
    private fun handleAddImage() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GET_PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // 相册图片返回
            val selectedImageUri = data.data ?: return
            val realImagePath = FileUtil.getFileRealPath(this, selectedImageUri) ?: return
            val fileType = FileUtil.getFileType(realImagePath) ?: return
            var blockImageSpanVm = BlockImageSpanVm<IBlockImageSpanObtainObject>(this, null)
            when (fileType) {
                FileTypeEnum.STATIC_IMAGE, FileTypeEnum.GIF -> {
                    val imageVm = ImageVm(realImagePath, "2")
//                    blockImageSpanVm = BlockImageSpanVm(this, imageVm) // 不指定宽高，使用组件默认宽高
                    blockImageSpanVm = BlockImageSpanVm(imageVm, imageWidth, imageMaxHeight) // 指定宽高
                }
                FileTypeEnum.VIDEO -> {
                    // 插入视频封面
                    val videoVm = VideoVm(realImagePath, "3")
//                    blockImageSpanVm = BlockImageSpanVm(this, videoVm) // 不指定宽高，使用组件默认宽高
                    blockImageSpanVm = BlockImageSpanVm(videoVm, imageWidth, imageMaxHeight) // 指定宽高
                }
            }

            richEditText.insertBlockImage(selectedImageUri, blockImageSpanVm) { blockImageSpan ->
                val spanObtainObject = blockImageSpan.blockImageSpanVm.spanObject
                when (spanObtainObject) {
                    is ImageVm -> {
                        Toast.makeText(this, "短按了图片-当前图片路径：${spanObtainObject.path}", Toast.LENGTH_SHORT).show()
                    }
                    is VideoVm -> {
                        Toast.makeText(this, "短按了图片-当前图片路径：${spanObtainObject.path}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

}
