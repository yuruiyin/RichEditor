# RichEditor
基于原生EditText+span实现的Android富文本编辑器

## 组件描述
该组件是基于原生EditText+span的方式实现的，旨在提供一个功能齐全且使用方便的Android富文本编辑器。主要支持了加粗斜体等行内样式、标题引用等段内样式以及插入图片视频甚至自定义View等。

## 功能演示
![Demo](./image/demo.gif)

## 功能列表
- [x] 支持加粗、斜体、删除线、下划线行内样式
- [x] 支持插入标题、引用段内样式
- [x] 支持插入段落图片、视频
- [x] 支持插入段落自定义布局
- [x] 支持视频、gif和长图标记
- [x] 支持图片圆角
- [ ] 支持行内ImageSpan，如类似微博@xxx，#话题名#
- [x] undo redo
- [ ] 支持清除样式
- [ ] 编辑器内部复制粘贴ImageSpan（任意以ImageSpan方式插入的的类型，如图片、视频、自定义view等）

## 如何使用
### gradle

Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the dependency in your app build.gradle:
```groovy
dependencies {
	implementation 'com.github.yuruiyin:RichEditor:latest-version'
}
```

### 参数定义
<table>
   <tdead>
    <tr>
      <th align="center">自定义属性名字</th>
      <th align="center">参数含义</th>
    </tr>
  </tdead>
  <tbody>
    <tr>
      <td align="center">editor_show_video_mark</td>
      <td align="center">是否显示视频标识图标</td>
    </tr>
    <tr>
      <td align="center">editor_video_mark_resource_id</td>
      <td align="center">视频图标资源id</td>
    </tr>
    <tr>
      <td align="center">editor_show_gif_mark</td>
      <td align="center">是否显示gif标识图标</td>
    </tr>
    <tr>
      <td align="center">editor_show_long_image_mark</td>
      <td align="center">是否显示长图标识</td>
    </tr>
    <tr>
      <td align="center">editor_image_radius</td>
      <td align="center">图片和视频圆角大小</td>
    </tr> 
    <tr>
      <td align="center">editor_headline_text_size</td>
      <td align="center">标题字体大小</td>
    </tr>        
  </tbody>
</table>

### 代码演示 
说明：各个样式按钮的layout由调用方自行完成
#### 1) 首先在xml中引用RichEditText：
```xml
<com.yuruiyin.richeditor.RichEditText
    android:id="@+id/richEditText"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="20dp"
    android:background="#ffffff"
    android:gravity="top|left"
    android:hint="请输入..."
    android:inputType="textMultiLine"
    android:lineSpacingExtra="5dp"
    android:maxLength="20000"
    android:minHeight="350dp"
    android:paddingBottom="70dp"
    android:paddingLeft="15dp"
    android:paddingRight="15dp"
    android:paddingTop="23dp"
    android:textColor="#171717"
    android:textColorHint="#aaaaaa"
    android:textCursorDrawable="@null"
    android:textSize="16dp"
    app:editor_video_mark_resource_id="@mipmap/editor_video_mark_icon"
    app:editor_image_radius="3dp"
    app:editor_show_gif_mark="true"
    app:editor_show_video_mark="true"
    app:editor_show_long_image_mark="true"
    />
```

#### 2) 针对加粗、斜体、标题等需要修改图标样式的按钮（不包括插入图片按钮），如加粗，处理如下：
```kotlin
    // 加粗
    richEditText.initStyleButton(
            StyleBtnVm(
                    RichTypeEnum.BOLD,
                    ivBold,
                    R.mipmap.icon_bold_normal,
                    R.mipmap.icon_bold_light
            )
    )
```
说明：其中ivBold为加粗ImageView，由调用方在layout中定义；R.mipmap.icon_bold_normal和R.mipmap.icon_bold_light是加粗按钮正常状态和点亮状态图片的资源id。

#### 3）插入图片或视频
```kotlin
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
        val blockImageSpanVm = BlockImageSpanVm(blockImageSpanObtainObject) // 不指定宽高，使用图片原始大小（但组件内对最大宽和最大高还是有约束的）
    //        val blockImageSpanVm = BlockImageSpanVm(blockImageSpanObtainObject, imageWidth, imageMaxHeight) // 指定宽高
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

```

#### 4) 插入自定义布局
```kotlin

    /**
     * 插入游戏
     */
    private fun handleAddGame() {
        val gameVm = GameVm(1, "一起来捉妖")
        doAddGame(gameVm)
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

```
说明：插入自定义布局最终也是通过bitmap以ImageSpan的形式插入到编辑器中的。

#### 5）获取数据
```kotlin
    // 返回的编辑器实体是一个list，list中每个元素代表一个段落block，具体block参数可以参考RichEditorBlock, 
    // 但是若需要保存草稿功能，则需要对该list进行转换成自己的实体，否则List<RichEditorBlock>序列化后反序列化会丢失数据,可以参考demo
    val conntent: List<RichEditorBlock> = richEditText.content
```

### 具体使用请参考[demo](https://github.com/yuruiyin/RichEditor/blob/master/app/src/main/java/com/yuruiyin/richeditor/sample/MainActivity.kt)

## 相关引用
1) 设置EditText的光标高度: [LineHeightEditText](https://github.com/hanks-zyh/LineHeightEditText)
2) 设置图片圆角: [RoundedImageView](https://github.com/vinc3m1/RoundedImageView)
3) undo redo: [AndroidEdit](https://github.com/qinci/AndroidEdit)

## 最后
您的star是我把组件做到完美的动力~
