# RichEditor
基于原生EditText+span实现的Android富文本编辑器

## 组件描述
该组件是基于原生EditText+span的方式实现的，旨在提供一个功能齐全且使用方便的Android富文本编辑器。主要支持了加粗斜体等行内样式、标题引用等段内样式以及插入图片视频甚至自定义View等。

## 功能列表
- [x] 支持加粗、斜体、删除线、下划线行内样式
- [x] 支持插入标题、引用段内样式
- [x] 支持插入段落图片、视频
- [x] 支持插入段落自定义布局
- [x] 支持视频、gif和长图标记
- [x] 支持图片圆角
- [ ] 支持行内ImageSpan，如类似微博@xxx，#话题名#
- [ ] undo redo
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
	implementation 'com.github.yuruiyin:RichEditor:0.0.1'
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

#### 2) 针对加粗、斜体、标题等需要修改图标样式的的按（不包括插入图片按钮），如加粗，处理如下：
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
    // 添加图片
    ivAddImage.setOnClickListener {
        // 首先打开相册（若app中有自定义的相册，请更改如下代码）
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GET_PHOTO_REQUEST_CODE)
    }

    // 选中相册图片回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_PHOTO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // 相册图片返回
            val selectedImageUri = data.data ?: return
            val realImagePath = FileUtil.getFileRealPath(this, selectedImageUri) ?: return
            val fileType = FileUtil.getFileType(realImagePath) ?: return
            var blockImageSpanVm = BlockImageSpanVm<IBlockImageSpanObtainObject>(this, null)

            // 构造BlockImageSpan所需实体，主要包含要显示出来的图片宽高（也可使用组件默认的）、以及图片视频路径或自定义布局中的实体信息等。
            when (fileType) {
                FileTypeEnum.STATIC_IMAGE, FileTypeEnum.GIF -> {
                    // 图片（静态图或gif）
                    val imageVm = ImageVm(realImagePath, "2") // 要保存的绑定到ImageSpan上的实体，未来取编辑器内容时会用到
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

            // 以uri的方式插入
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

            // 以path的方式插入
            richEditText.insertBlockImage(realImagePath, blockImageSpanVm) { blockImageSpan ->
                // 同上
            }

        }
    }       
    
```

#### 4) 插入自定义布局

