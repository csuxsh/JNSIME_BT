#define LOG_TAG "JNS_INPUT_ADAPTER"

#ifdef BUILD_NDK
#else
//#include <utils/Log.h>
//#include "JNIHelp.h"
//#include "android_runtime/AndroidRuntime.h"
#endif
#include <assert.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <linux/input.h>
#include "global.h"
#include "InputAdapter.h"
#include "CallBackInterface.h"
#include "com_viaplay_ime_jni_InputAdapter.h"
#include <sys/syscall.h>
#include <linux/uinput.h>


#define gettid()   syscall(__NR_gettid)


static int ovalue = 0;
static int getvalue = 0;
static bool waitForgetKey = false;
bool waitForgetJoy = false;
static int joy_num = 0;
int  uinput_fd = -1;
int  uinput_screen = -1;
struct input_event event;
struct uinput_user_dev uinp, virturl_screen;
const char *dev = "/dev/uinput";

struct fields_t {
	jmethodID onInputAdapterKeyDown;
	jmethodID onInputAdapterKeyUp;
	jmethodID onInputAdapterJoystickChange;
};

static fields_t inputCallBackField;

typedef struct tagJNiGlobale_t {
	JNIEnv *env;
	JavaVM *jvm;
} stJniGlobal_t;

static stJniGlobal_t sg_jni_global;
static jclass g_com_viaplay_ime_jni_InputAdapter_class;

//�������ź���
pthread_cond_t keyCond;
pthread_mutex_t keyMutex;
pthread_cond_t joyCond;
pthread_mutex_t joyMutex;
pthread_cond_t getDeviceCond;
pthread_mutex_t getDeviceMutex;


static void init_jni_global() {
	memset(&sg_jni_global, 0x00, sizeof(stJniGlobal_t));
}


#ifdef BUILD_NDK
class AndroidRuntime {
public:
	static JNIEnv* getJNIEnv() {
		if (NULL != sg_jni_global.jvm && NULL == sg_jni_global.env) {
			sg_jni_global.jvm->GetEnv((void **) &sg_jni_global.env,
					JNI_VERSION_1_4);
		}
		return sg_jni_global.env;
	}
};
#else
using namespace android;
#endif

struct RawKeyEvent {
	int scanCode;
	int value;
	int KeyCode;
	int deviceId;
};

struct JoyStickEvent {
	int x;
	int y;
	int z;
	int rz;
	int hat_x;
	int hat_y;
	int gas;
	int brake;
	int deviceId;
};

struct RawKeyEvent keyEvent;
struct JoyStickEvent joyStickEvent;
static void doOnKeyDown(int scanCode, int value, int deviceId)
{
	keyEvent.scanCode = scanCode;
	keyEvent.value = value;
	keyEvent.deviceId = deviceId;
}

static void doOnKeyUp(int scanCode, int value, int deviceId)
{
	keyEvent.scanCode = scanCode;
	keyEvent.value = value;
	keyEvent.deviceId = deviceId;
	//LOGE("[%s][%d] ==>  scancode = 0X%02X value = 0x%02x", __FUNCTION__, __LINE__, scanCode, value);
}

static void doOnJoystickDataChange(int scanCode, int value, int deviceId)
{

	joyStickEvent.deviceId = deviceId;
	switch(scanCode)
	{
	case ABS_X:
		joyStickEvent.x = value;
		break;
	case ABS_Y:
		joyStickEvent.y = value;
		break;
	case ABS_Z:
		joyStickEvent.z = value;
		break;
	case ABS_RZ:
		joyStickEvent.rz = value;
		break;
	case ABS_HAT0X:
		joyStickEvent.hat_x = value;
		break;
	case ABS_HAT0Y:
		joyStickEvent.hat_y = value;
		break;
	case ABS_GAS:
		joyStickEvent.gas = value;
		break;
	case ABS_BRAKE:
		joyStickEvent.brake = value;
		break;
	}
}
void delay(int i)
{
	while(i)
	{
		i--;
	}
}
int setup_virtural_screen()
{
	// Temporary variable
	int i=0;
	// Open the input device
	uinput_screen = open("/dev/uinput", O_WRONLY | O_NDELAY);

	if(!uinput_fd)
	{
		LOGE("Unable to open /dev/uinput/ for virtural screen");
		return -1;
	}
	memset(&virturl_screen,0,sizeof(uinp)); // Intialize the uInput device to NULL
	strncpy(virturl_screen.name, "STEVEN TOUCH TEST", UINPUT_MAX_NAME_SIZE);

	uinp.id.version = 1;
	uinp.id.bustype = BUS_USB;
	virturl_screen
}
int setup_uinput_device()
{
	// Temporary variable
	int i=0;

	// Open the input device
	uinput_fd = open("/dev/uinput", O_WRONLY | O_NDELAY);
	if(!uinput_fd)
	{
		LOGE("Unable to open /dev/uinput/n");
		return -1;
	}

	memset(&uinp,0,sizeof(uinp)); // Intialize the uInput device to NULL
	strncpy(uinp.name, "STEVEN TEST", UINPUT_MAX_NAME_SIZE);
	uinp.id.version = 1;

	uinp.id.bustype = BUS_USB;

	uinp.absmax[0] = 255;
	uinp.absmax[1] = 255;
	uinp.absmax[2] = 255;
	uinp.absmax[5] = 255;
	uinp.absmax[9] = 255;
	uinp.absmax[10] = 255;
	uinp.absmax[16] = 1;
	uinp.absmax[17] = 1;
	uinp.absmin[16] = -1;
	uinp.absmin[17] = -1;

	// Setup the uinput device


	ioctl(uinput_fd, UI_SET_EVBIT, EV_ABS);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_X);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_Y);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_Z);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_RZ);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_BRAKE);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_GAS);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_HAT0X);
	ioctl(uinput_fd, UI_SET_ABSBIT, ABS_HAT0Y);

	ioctl(uinput_fd, UI_SET_EVBIT, EV_KEY);
	for(i = 0; i < 247; i++)
		ioctl(uinput_fd, UI_SET_KEYBIT, i);


	for(i = 0x130; i < 0x13e; i++)
		ioctl(uinput_fd, UI_SET_KEYBIT, i);

	ioctl(uinput_fd, UI_SET_KEYBIT, 353);

	/* Create input device into input sub-system */
	if(write(uinput_fd, &uinp, sizeof(uinp)) != sizeof(uinp))
		LOGE("WRITE UINPUT FAILED");
	if (ioctl(uinput_fd, UI_DEV_CREATE))
	{
		LOGE("Unable to create UINPUT device.");
		return -1;
	}
	return 1;
}


class InputAdapterCallBack : public CallBackInterface {
public:
	int keyProcess(const RawEvent *rawEvent) {

		int scanCode = rawEvent->scanCode;
		int value = rawEvent->value;
		int deviceId = rawEvent->deviceId;

		while(waitForgetKey)
		{
			LOGE("[%s][%d] ==> waitForgetKey", __FUNCTION__, __LINE__);
		}
		pthread_mutex_lock(&keyMutex);
		if (value == 1) {
			doOnKeyDown(scanCode, value, deviceId);
		} else if(value == 0){
			doOnKeyUp(scanCode, value, deviceId);
		}
		//delay(1000);
		pthread_cond_signal(&keyCond);
		pthread_mutex_unlock(&keyMutex);

		return 1; 
	};

	int joystickProcess(const RawEvent *rawEvent) { 
		int scanCode = rawEvent->scanCode;
		int value = rawEvent->value;
		int deviceId = rawEvent->deviceId;


		if(rawEvent->type != 0)
			doOnJoystickDataChange(scanCode, value, deviceId);
		//LOGE("[%s][%d] ==> notify getjoy ", __FUNCTION__, __LINE__);
		//delay(1000);
		return 1; 
	};
};

#ifdef BUILD_NDK
static InputAdapter* mInputAdapter = NULL;
static InputAdapterCallBack* mInputAdapterCallBack;
#else
static sp<InputAdapter> mInputAdapter = NULL;
static sp<InputAdapterCallBack> mInputAdapterCallBack;
#endif

JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_init(JNIEnv *env, jclass clazz) {
	init_jni_global();
	jclass inputAdapter = env->FindClass("com/viaplay/ime/jni/InputAdapter");
	jfieldID uinputmode = env->GetStaticFieldID (inputAdapter, "uInputMode", "Z");
	env->GetJavaVM(&sg_jni_global.jvm);
	pthread_mutex_init(&keyMutex, NULL);
	pthread_cond_init(&keyCond, NULL);
	pthread_mutex_init(&joyMutex, NULL);
	pthread_cond_init(&joyCond, NULL);
	pthread_mutex_init(&getDeviceMutex, NULL);
	pthread_cond_init(&getDeviceCond, NULL);
	mInputAdapter = InputAdapter::create();
	mInputAdapterCallBack = new InputAdapterCallBack();
	mInputAdapter->getKeyManager()->registerCallBackInterface(mInputAdapterCallBack);
	mInputAdapter->getJoystick()->registerCallBackInterface(mInputAdapterCallBack);
	joyStickEvent.x = 0x7f;
	joyStickEvent.y = 0x7f;
	joyStickEvent.z = 0x7f;
	joyStickEvent.rz = 0x7f;
	if(!setup_uinput_device())
		LOGE("[%s][%d] ==> UINPUT FAILED", __FUNCTION__, __LINE__);
	else
	{
		jboolean b = 1;
		env->SetStaticBooleanField(inputAdapter, uinputmode, (jboolean)1);
	}
	return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_start(JNIEnv *env, jclass clazz) {
	if (mInputAdapter == NULL) {
		LOGE("[%s][%d] ==> mInputAdapter is NULL", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
	LOGE("[%s][%d] ==> mInputAdapter is start", __FUNCTION__, __LINE__);
	mInputAdapter->start();
	return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_stop(JNIEnv *env, jclass clazz) {
	if (mInputAdapter == NULL) {
		LOGE("[%s][%d] ==> mInputAdapter is NULL", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
	pthread_mutex_unlock(&keyMutex);
	pthread_mutex_unlock(&joyMutex);
	mInputAdapter->stop();
	ioctl(uinput_fd, UI_DEV_DESTROY);
	close(uinput_fd);
	return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_com_viaplay_ime_jni_InputAdapter_getKey (JNIEnv *env, jclass clazz, jobject rawEvent) {
	jclass jclazz = env->FindClass("com/viaplay/ime/jni/RawEvent");
	if (jclazz == NULL) {
		//LOGE("[%s][%d] ==> could't find the com/jnselectronic/ime/jni/RawEvent class", __FUNCTION__, __LINE__);
		return;
	}
	// ֪ͨ����������
	pthread_mutex_lock(&keyMutex);
	waitForgetKey = false;
	pthread_cond_wait(&keyCond, &keyMutex);
	jfieldID scanCode = env->GetFieldID(jclazz, "scanCode", "I");
	jfieldID value = env->GetFieldID(jclazz, "value", "I");
	jfieldID deviceId = env->GetFieldID(jclazz, "deviceId", "I");
	env->SetIntField(rawEvent, scanCode, keyEvent.scanCode);
	env->SetIntField(rawEvent, value, keyEvent.value);
	env->SetIntField(rawEvent, deviceId, keyEvent.deviceId);
	waitForgetKey =  true;
	pthread_mutex_unlock(&keyMutex);
}

JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_getJoyStick
(JNIEnv *env, jclass clazz, jobject joyEvent)
{
	//	LOGE("[%s][%d] ==> enter getjoy", __FUNCTION__, __LINE__);

	pthread_mutex_lock(&joyMutex);
	//	LOGE("[%s][%d] ==> getjoy wait", __FUNCTION__, __LINE__);
	waitForgetJoy = false;
	pthread_cond_wait(&joyCond, &joyMutex);
	if(mJoystickChanged)
	{
		mJoystickChanged = 0;
		jclass jclazz = env->FindClass("com/viaplay/ime/jni/JoyStickEvent");
		if (jclazz == NULL) {
			//LOGE("[%s][%d] ==> could't find the com/jnselectronic/ime/jni/JoyStickEvent class", __FUNCTION__, __LINE__);
			return false;
		}
		jfieldID x = env->GetFieldID(jclazz, "x", "I");
		jfieldID y = env->GetFieldID(jclazz, "y", "I");
		jfieldID z = env->GetFieldID(jclazz, "z", "I");
		jfieldID rz = env->GetFieldID(jclazz, "rz", "I");
		jfieldID hat_x = env->GetFieldID(jclazz, "hat_x", "I");
		jfieldID hat_y = env->GetFieldID(jclazz, "hat_y", "I");
		jfieldID gas = env->GetFieldID(jclazz, "gas", "I");
		jfieldID brake = env->GetFieldID(jclazz, "brake", "I");
		jfieldID deviceId = env->GetFieldID(jclazz, "deviceId", "I");

		env->SetIntField(joyEvent, x, joyStickEvent.x);
		env->SetIntField(joyEvent, y, joyStickEvent.y);
		env->SetIntField(joyEvent, z, joyStickEvent.z);
		env->SetIntField(joyEvent, rz, joyStickEvent.rz);
		env->SetIntField(joyEvent, hat_x, joyStickEvent.hat_x);
		env->SetIntField(joyEvent, hat_y, joyStickEvent.hat_y);
		env->SetIntField(joyEvent, gas, joyStickEvent.gas);
		env->SetIntField(joyEvent, brake, joyStickEvent.brake);
		env->SetIntField(joyEvent, deviceId, joyStickEvent.deviceId);
		pthread_mutex_unlock(&joyMutex);
		waitForgetJoy = true;
		return true;
	}
	pthread_mutex_unlock(&joyMutex);
	waitForgetJoy = true;
	return false;
}
JNIEXPORT jobject JNICALL Java_com_viaplay_ime_jni_InputAdapter_getDeviceList
(JNIEnv * env, jclass clazz)
{
	jclass list_cls = env->FindClass("java/util/ArrayList");//���ArrayList������
	jmethodID list_costruct = env->GetMethodID(list_cls , "<init>","()V"); //��õù��캯��Id
	jobject list_obj = env->NewObject(list_cls , list_costruct); //����һ��Arraylist���϶���
	//���Arraylist���е� add()����ID���䷽��ԭ��Ϊ�� boolean add(Object object) ;
	jmethodID list_add  = env->GetMethodID(list_cls,"add","(Ljava/lang/Object;)Z");
	//LOGE("[%s][%d] ==> get devicelist", __FUNCTION__, __LINE__);
	for(int i = 0 ; i < 32; i++)
	{

		if(devicelist[i] == NULL)
			return list_obj;
		jstring dvicename = env->NewStringUTF(devicelist[i]);
		env->CallBooleanMethod(list_obj , list_add , dvicename); //ִ��Arraylist��ʵ����add���������һ��String
	}
	return list_obj ;
}
/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    setAxis
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_setAxis
(JNIEnv * env , jclass clazz, jobject e)
{
	jclass cls = env->GetObjectClass(e);
	jfieldID type = env->GetFieldID(cls, "type", "I");
	jfieldID code = env->GetFieldID(cls, "scanCode", "I");
	jfieldID value = env->GetFieldID(cls, "value", "I");

	memset(&event, 0, sizeof(event));
	event.type =  (unsigned char) env->GetIntField( e, type);
	event.code =   (unsigned char)env->GetIntField(e, code);
	if((event.code == ABS_HAT0X) || (event.code == ABS_HAT0Y))
		event.value = env->GetIntField(e, value);
	else
		event.value =  (unsigned char)env->GetIntField(e, value);
	LOGE("[%s][%d] ==> code=[%d],type=[%d],value[%d]", __FUNCTION__, __LINE__,event.code,event.type,event.value);
	write(uinput_fd, &event, sizeof(event));
}

/*
 * Class:     com_viaplay_ime_jni_InputAdapter
 * Method:    setButton
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_viaplay_ime_jni_InputAdapter_setButton
(JNIEnv * env , jclass clazz, jobject e)
{
	jclass cls = env->GetObjectClass(e);
	jfieldID type = env->GetFieldID(cls, "type", "I");
	jfieldID code = env->GetFieldID(cls, "scanCode", "I");
	jfieldID value = env->GetFieldID(cls, "value", "I");

	memset(&event, 0, sizeof(event));
	event.type =   env->GetIntField( e, type);
	event.code =   env->GetIntField(e, code);
	event.value =  env->GetIntField( e, value);
	LOGE("[%s][%d] ==> code=[%d],type=[%d],value[%d]", __FUNCTION__, __LINE__,event.code,event.type,event.value);
	write(uinput_fd, &event, sizeof(event));
}
