package com.yuruiyin.richeditor.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yuruiyin.richeditor.enumtype.FileTypeEnum
import com.yuruiyin.richeditor.enumtype.RichTypeEnum
import com.yuruiyin.richeditor.model.BlockImageSpanVm
import com.yuruiyin.richeditor.model.IBlockImageSpanObtainObject
import com.yuruiyin.richeditor.model.RichEditorBlock
import com.yuruiyin.richeditor.model.StyleBtnVm
import com.yuruiyin.richeditor.sample.enumtype.BlockImageSpanType
import com.yuruiyin.richeditor.sample.model.*
import com.yuruiyin.richeditor.sample.utils.JsonUtil
import com.yuruiyin.richeditor.sample.utils.WindowUtil
import com.yuruiyin.richeditor.utils.FileUtil
import com.yuruiyin.richeditor.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_PHOTO_REQUEST_CODE = 1
        const val TAG = "MainActivity"

        /**
         * 草稿SharePreferences的名字
         */
        const val SP_DRAFT_NAME = "rich_editor"

        /**
         * 保存在SharePreferences中的草稿json数据key
         */
        const val KEY_DRAFT_JSON = "key_draft_json"
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

    private val gameItemHeight by lazy {
        resources.getDimension(R.dimen.editor_game_height).toInt()
    }

    private val gameIconSize by lazy {
        resources.getDimension(R.dimen.editor_game_icon_size).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerEvents()
    }

    private fun registerEvents() {
        // 生成json数据，显示到TextView上
        btnCreateJson.setOnClickListener {
            val draftEditorBlockList = convertEditorContent(richEditText.content)
            showJson(draftEditorBlockList)
        }

        // 清空内容
        btnClearContent.setOnClickListener {
            richEditText.clearContent()
        }

        // 保存草稿
        btnSaveDraft.setOnClickListener {
            handleSaveDraft()
        }

        // 恢复草稿
        btnRestoreDraft.setOnClickListener {
            handleRestoreDraft()
        }

        // 清空草稿
        btnClearDraft.setOnClickListener {
            handleClearDraft()
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

        // 添加游戏（自定义布局的一种）
        ivAddGame.setOnClickListener {
            handleAddGame()
        }

        ivUndo.setOnClickListener { richEditText.undo() }

        ivRedo.setOnClickListener { richEditText.redo() }
    }

    private fun convertEditorContent(editorBlockList: List<RichEditorBlock>): List<DraftEditorBlock> {
        // 先将对象进行转换，让里头blockImageSpanObtainObject具体到各自类型的实体上（如ImageVm）
        val draftEditorBlockList = mutableListOf<DraftEditorBlock>()
        editorBlockList.forEach {
            val draftEditorBlock = DraftEditorBlock()
            draftEditorBlock.blockType = it.blockType
            draftEditorBlock.text = it.text
            draftEditorBlock.inlineStyleEntities = it.inlineStyleEntityList
            when (it.blockType) {
                BlockImageSpanType.IMAGE -> {
                    draftEditorBlock.image = it.blockImageSpanObtainObject as? ImageVm
                }
                BlockImageSpanType.VIDEO -> {
                    draftEditorBlock.video = it.blockImageSpanObtainObject as? VideoVm
                }
                BlockImageSpanType.GAME -> {
                    draftEditorBlock.game = it.blockImageSpanObtainObject as? GameVm
                }
                BlockImageSpanType.DIVIDER -> {
                    draftEditorBlock.divider = it.blockImageSpanObtainObject as? DividerVm
                }
            }
            draftEditorBlockList.add(draftEditorBlock)
        }

        return draftEditorBlockList
    }

    private fun showJson(draftEditorBlockList: List<DraftEditorBlock>) {
        val content = Gson().toJson(draftEditorBlockList)
        val formatJsonContent = JsonUtil.getFormatJson(content)
        tvContentJson.text = formatJsonContent
        Log.d(TAG, "\n $formatJsonContent")
    }

    private fun handleClearDraft() {
        val sp = getSharedPreferences(SP_DRAFT_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()

        Toast.makeText(this, "清空草稿成功", Toast.LENGTH_SHORT).show()
    }

    /**
     * 遍历段落恢复草稿，即一段一段的插入到编辑器中
     */
    private fun restoreDraft(draftEditorBlockList: List<DraftEditorBlock>) {
        richEditText.clearContent()
        draftEditorBlockList.forEach {
            when (it.blockType) {
                RichTypeEnum.BLOCK_NORMAL_TEXT, RichTypeEnum.BLOCK_HEADLINE, RichTypeEnum.BLOCK_QUOTE -> {
                    val richEditorBlock = RichEditorBlock()
                    richEditorBlock.blockType = it.blockType
                    richEditorBlock.text = it.text
                    richEditorBlock.inlineStyleEntityList = it.inlineStyleEntities
                    richEditText.insertBlockText(richEditorBlock)
                }
                // 以下就是用户自定义的blockType，可能是图片、视频、自定义类型等
                BlockImageSpanType.IMAGE -> {
                    val imageVm= it.image ?: return@forEach
                    doAddBlockImageSpan(imageVm.path, imageVm, true)
                }
                BlockImageSpanType.VIDEO -> {
                    val videoVm = it.video ?: return@forEach
                    doAddBlockImageSpan(videoVm.path, videoVm, true)
                }
                BlockImageSpanType.DIVIDER -> {
                    handleAddDivider(true)
                }
                BlockImageSpanType.GAME -> {
                    val gameVm = it.game ?: return@forEach
                    doAddGame(gameVm, true)
                }
            }
        }
    }

    /**
     * 恢复草稿
     */
    private fun handleRestoreDraft() {
        val sp = getSharedPreferences(SP_DRAFT_NAME, Context.MODE_PRIVATE)
        val jsonContent = sp.getString(KEY_DRAFT_JSON, "")
        if (TextUtils.isEmpty(jsonContent)) {
            Toast.makeText(this, "没有草稿内容", Toast.LENGTH_SHORT).show()
            return
        }

        val editorBlockList = Gson().fromJson<List<DraftEditorBlock>>(
                jsonContent,
                object : TypeToken<List<DraftEditorBlock>>() {}.type
        )

        showJson(editorBlockList)
        restoreDraft(editorBlockList)
    }

    /**
     * 保存草稿
     */
    private fun handleSaveDraft() {
        val richEditorBlockList = richEditText.content
        // 先将对象进行转换，让里头blockImageSpanObtainObject具体到各自类型的实体上（如ImageVm）
        val draftEditorBlockList= convertEditorContent(richEditorBlockList)

        val jsonContent = Gson().toJson(draftEditorBlockList)
        val sp = getSharedPreferences(SP_DRAFT_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_DRAFT_JSON, jsonContent)
//        editor.commit() // commit是同步写，可能会阻塞主线程，因此不建议
        editor.apply()

        Toast.makeText(this, "保存草稿成功", Toast.LENGTH_SHORT).show()
    }

    private fun getEditTextWidthWithoutPadding(): Int {
        // 富文本编辑器编辑区域的宽度, 这个宽度一定要小于编辑器的宽度，否则会出现ImageSpan被绘制两边的情况
        return (screenWidth - editorPaddingLeft - editorPaddingRight - 6).toInt()
    }

    private fun doAddGame(gameVm: GameVm, isFromDraft: Boolean = false) {
        val gameItemView = layoutInflater.inflate(R.layout.editor_game_item, null)
        val ivGameIcon = gameItemView.findViewById<ImageView>(R.id.ivGameIcon)
        val tvGameName = gameItemView.findViewById<TextView>(R.id.tvGameName)
        ivGameIcon.setImageResource(R.mipmap.icon_game_zhuoyao)
        tvGameName.text = gameVm.name

        ivGameIcon.layoutParams.width = gameIconSize
        ivGameIcon.layoutParams.height = gameIconSize

        val gameItemWidth = getEditTextWidthWithoutPadding()
        ViewUtil.layoutView(gameItemView, gameItemWidth, gameItemHeight)

        val blockImageSpanVm = BlockImageSpanVm(gameVm, gameItemWidth, imageMaxHeight)
        blockImageSpanVm.isFromDraft = isFromDraft
        richEditText.insertBlockImage(ViewUtil.getBitmap(gameItemView), blockImageSpanVm) { blockImageSpan ->
            val retGameVm = blockImageSpan.blockImageSpanVm.spanObject as GameVm
            // 点击游戏item
            Toast.makeText(this, "短按了游戏：${retGameVm.name}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 插入游戏
     */
    private fun handleAddGame() {
        val gameVm = GameVm(1, "一起来捉妖")
        doAddGame(gameVm)
    }

    /**
     * 处理添加分割线，其实插入的也是BlockImageSpan
     */
    private fun handleAddDivider(isFromDraft: Boolean = false) {
        val blockImageSpanVm =
                BlockImageSpanVm(DividerVm(), getEditTextWidthWithoutPadding(), imageMaxHeight)
        blockImageSpanVm.isFromDraft = isFromDraft
        richEditText.insertBlockImage(R.mipmap.image_divider_line, blockImageSpanVm, null)
    }

    /**
     * 处理插入图片
     */
    private fun handleAddImage() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GET_PHOTO_REQUEST_CODE)
    }

    private fun doAddBlockImageSpan(
            realImagePath: String, blockImageSpanObtainObject: IBlockImageSpanObtainObject, isFromDraft: Boolean = false
    ) {
//        val blockImageSpanVm = BlockImageSpanVm(this, imageVm) // 不指定宽高，使用组件默认宽高
        val blockImageSpanVm =
                BlockImageSpanVm(blockImageSpanObtainObject, imageWidth, imageMaxHeight) // 指定宽高
        blockImageSpanVm.isFromDraft = isFromDraft
        richEditText.insertBlockImage(realImagePath, blockImageSpanVm) { blockImageSpan ->
            val spanObtainObject = blockImageSpan.blockImageSpanVm.spanObject
            when (spanObtainObject) {
                is ImageVm -> {
                    Toast.makeText(this, "短按了图片-当前图片路径：${spanObtainObject.path}", Toast.LENGTH_SHORT).show()
                }
                is VideoVm -> {
                    Toast.makeText(this, "短按了视频-当前视频路径：${spanObtainObject.path}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // 相册图片返回
            val selectedImageUri = data.data ?: return
            val realImagePath = FileUtil.getFileRealPath(this, selectedImageUri) ?: return
            val fileType = FileUtil.getFileType(realImagePath) ?: return
            when (fileType) {
                FileTypeEnum.STATIC_IMAGE, FileTypeEnum.GIF -> {
                    val imageVm = ImageVm(realImagePath, "2")
                    doAddBlockImageSpan(realImagePath, imageVm)
                }
                FileTypeEnum.VIDEO -> {
                    // 插入视频封面
                    val videoVm = VideoVm(realImagePath, "3")
                    doAddBlockImageSpan(realImagePath, videoVm)
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
        handleSaveDraft()
    }

}
