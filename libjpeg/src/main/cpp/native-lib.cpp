#include <jni.h>
#include <string>

#include <android/bitmap.h>
#include <stdio.h>
#include <setjmp.h>
#include <math.h>
#include <time.h>
#include <csetjmp>
#include "include/turbojpeg.h"
#include "include/jpeglib.h"
#include <android/log.h>
#include <zconf.h>
#include <sys/stat.h>
#include <string.h>

#include <malloc.h>

typedef uint8_t BYTE;
#define TAG "jpegturbodemo"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,TAG,FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,TAG,FORMAT,##__VA_ARGS__);
#define LOGW(FORMAT, ...) __android_log_print(ANDROID_LOG_WARN,TAG,FORMAT,##__VA_ARGS__);
#define LOGD(FORMAT, ...) __android_log_print(ANDROID_LOG_DEBUG,TAG,FORMAT,##__VA_ARGS__);

// 错误输出结构体
struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};


typedef struct my_error_mgr *my_error_ptr;

// 方法
METHODDEF(void)
my_error_exit(j_common_ptr
              cinfo) {
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    LOGW("jpeg_message_table[%d]:%s",
         myerr->pub.msg_code, myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
    longjmp(myerr
                    ->setjmp_buffer, 1);
};


extern "C" JNIEXPORT jstring JNICALL
Java_com_github_jpegturbodemo_NativeUtils_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
int generateJPEG(BYTE *data, int w, int h, jint quality, const char *location, jboolean optimize) {
    int nComponent = 3;
    struct jpeg_compress_struct jcs;
    //自定义的error
    struct my_error_mgr jem;

    jcs.err = jpeg_std_error(&jem.pub);

    if (setjmp(jem.setjmp_buffer)) {
        return 0;
    }
    //为JPEG对象分配空间并初始化
    jpeg_create_compress(&jcs);
    //获取文件信息 只允许写数据
    FILE *f = fopen(location, "wb");
    if (f == NULL) {
        return 0;
    }

    //指定压缩数据源
    jpeg_stdio_dest(&jcs, f);
    jcs.image_width = w;
    jcs.image_height = h;

    jcs.arith_code = false;
    jcs.input_components = nComponent;
    jcs.in_color_space = JCS_RGB;

    jpeg_set_defaults(&jcs);
    jcs.optimize_coding = quality;

    //为压缩设定参数，包括图像大小，颜色空间
    jpeg_set_quality(&jcs, quality, true);
    //开始压缩
    jpeg_start_compress(&jcs, true);
    JSAMPROW row_point[1];
    int row_stride;
    row_stride = jcs.image_width * nComponent;
    while (jcs.next_scanline < jcs.image_height) {
        row_point[0] = &data[jcs.next_scanline * row_stride];
        jpeg_write_scanlines(&jcs, row_point, 1);
    }
    //  关键代码
    if (jcs.optimize_coding) {
        LOGD("哈夫曼算法完成压缩");
    } else {
        LOGD("未使用哈夫曼算法");
    }
    //压缩完毕
    jpeg_finish_compress(&jcs);
    //释放资源
    jpeg_destroy_compress(&jcs);
    fclose(f);
    return 1;
}

/**
 *
 */
char *ConvertJByteArrayToChars(JNIEnv *env, jbyteArray byteArray) {
    char *chars = NULL;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(byteArray, 0);
    int chars_len = env->GetArrayLength(byteArray);
    chars = new char[chars_len + 1];
    memset(chars, 0, chars_len + 1);
    memcpy(chars, bytes, chars_len);
    chars[chars_len] = 0;
    env->ReleaseByteArrayElements(byteArray, bytes, 0);
    return chars;
}

/**
 *
 * @param env
 * @param jstr
 * @return
 */
const char *jstringToString(JNIEnv *env, jstring jstr) {
    char *ret;
    const char *tempStr = env->GetStringUTFChars(jstr, NULL);
    jsize len = env->GetStringUTFLength(jstr);
    if (len > 0) {
        ret = (char *) malloc(len + 1);
        memcpy(ret, tempStr, len);
        ret[len] = 0;
    }
    env->ReleaseStringUTFChars(jstr, tempStr);
    return ret;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_github_jpegturbodemo_NativeUtils_compressBitmap(
        JNIEnv *env, jobject bitmap,
        jint quality,
        jbyteArray fileByteArray,
        jboolean optimize) {

    // 结构体 指针
    AndroidBitmapInfo androidBitmapInfo;
    // 像素指针
    BYTE *pixelsBitmap;
    //
    int ret;
    //
    BYTE *data;
    // 定义临时变量
    BYTE *tempData;
    // 文件路径
    char *file = ConvertJByteArrayToChars(env, fileByteArray);
    //装载bitmap解析
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &androidBitmapInfo)) < 0) {
        LOGE("解析bitmap失败");
        return env->NewStringUTF("0");
    }
    // 锁定画布 指向指针的数组  int 4个字节
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, reinterpret_cast<void **>(&pixelsBitmap))) < 0) {
        LOGE("加载bitmap失败");
    }

    BYTE r, g, b;
    int color;

    int w, h, format;
    w = androidBitmapInfo.width;
    h = androidBitmapInfo.height;
    format = androidBitmapInfo.format;
    // 只处理 RGBA_8888编码
    if (format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGD("AndroidBitmapInfo  format  is not ANDROID_BITMAP_FORMAT_RGBA_8888 error=%d", ret);
        return env->NewStringUTF("-1");
    }

    data = static_cast<BYTE *>(malloc(static_cast<size_t>(w * h * 3)));
    //
    tempData = data;
    // 遍历像素矩阵 bitmap转为RGB
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            color = *((int *) pixelsBitmap);
            // 高8位
            r = static_cast<BYTE>((color & 0x00FF0000) >> 16);
            // 中8位
            g = static_cast<BYTE>((color & 0x0000FF00) >> 8);
            // 低8位
            b = static_cast<BYTE>(color & 0x000000FF);
            data += 3;
            pixelsBitmap += 4;

        }
    }
    // 释放画布
    AndroidBitmap_unlockPixels(env, bitmap);
    // 调用libjpeg函数
    int resultCode = generateJPEG(tempData, w, h, quality, file, optimize);
    // 释放
    free(tempData);
    if (resultCode == 0) {
        return env->NewStringUTF("0");
    }
    return env->NewStringUTF("1");

}




