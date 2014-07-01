#include "jni.h"
#define BUILD_NDK
#ifdef BUILD_NDK
#include "android/log.h"
#include <pthread.h>
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)
#endif
#ifndef _Included_com_viaplay_ime_jni_InputAdapter
#define _Included_com_viaplay_ime_jni_InputAdapter
#ifdef __cplusplus
extern "C" {
#endif
#undef com_viaplay_ime_jni_InputAdapter_SELECT_SCANCODE
#define com_viaplay_ime_jni_InputAdapter_SELECT_SCANCODE 314L
#undef com_viaplay_ime_jni_InputAdapter_START_SCANCODE
#define com_viaplay_ime_jni_InputAdapter_START_SCANCODE 315L

extern  pthread_cond_t getDeviceCond;
extern  pthread_mutex_t getDeviceMutex;

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    init
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_init
  (JNIEnv *, jclass);

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    start
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_start
  (JNIEnv *, jclass);

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    stop
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_stop
  (JNIEnv *, jclass);

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    setAxis
 * Signature: (Lcom/viaplay/ime/jni/RawEvent;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_setAxis
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    setButton
 * Signature: (Lcom/viaplay/ime/jni/RawEvent;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_setButton
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    getKey
 * Signature: (Lcom/viaplay/ime/jni/RawEvent;)V
 */
JNIEXPORT void JNICALL Java_com_viaplay_ime_jni_InputAdapter_getKey
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    getJoyStick
 * Signature: (Lcom/viaplay/ime/jni/JoyStickEvent;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_getJoyStick
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    getDeviceList
 * Signature: ()Ljava/util/List;
 */
JNIEXPORT jobject JNICALL Java_com_viaplay_ime_jni_InputAdapter_getDeviceList
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
