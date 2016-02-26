#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <android/log.h>

#define LOG_TAG "jni_keepAlive"

#ifdef ALI_LOG
#define LOG_INFO(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOG_DEBUG(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOG_WARN(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOG_ERROR(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#define LOG_INFO(...)
#define LOG_DEBUG(...)
#define LOG_WARN(...)
#define LOG_ERROR(...)
#endif

#define null 0
#define INTENT_CLASS_NAME "android/content/Intent"
#define COMPONENT_CLASS_NAME "android/content/ComponentName"
#define DATA_PATH_PREFIX "/data/data/"

int file_exists(char *filename);
void send_heartbeat(char * pstrPkgName, char * classname, char * action, char * interval);
int check_string(char *str);
void copyfile(char *oldp, char *newp);
JNIEXPORT jint JNICALL Java_com_sml_zhuolin_smlkeepalive_library_KeepAlive_startKeepAlive
  (JNIEnv *env, jclass object, jobject intent, jstring ms) {
  LOG_DEBUG("startKeepAlive");
  if (intent == null) {
      return -1;
  }

  jclass intent_class = (*env)->FindClass(env, INTENT_CLASS_NAME);
  LOG_DEBUG(" find intent success");
  jboolean result = (*env)->IsInstanceOf(env, intent, intent_class);
  if (result != JNI_TRUE) {
    (*env)->DeleteLocalRef(env, intent_class);
     return -1;
  }
   LOG_DEBUG("is intent yes");
  jmethodID com_methodid = (*env)->GetMethodID(env, intent_class, "getComponent", "()Landroid/content/ComponentName;");
  jmethodID action_methodid = (*env)->GetMethodID(env, intent_class, "getAction", "()Ljava/lang/String;");
  jobject comp = (*env)->CallObjectMethod(env, intent, com_methodid);
  jstring action = (*env)->CallObjectMethod(env, intent, action_methodid);
     LOG_DEBUG("get methodid and object");
  if (comp == null && action == null) {
     return -1;
  }
  char * str_action = "";
  if (action != null) {
     str_action = (*env)->GetStringUTFChars(env, action, null);
  }
  LOG_DEBUG("mon:action %s ",  str_action);

  char * iternal_ms = "";
  if (ms != null) {
     iternal_ms = (*env)->GetStringUTFChars(env, ms, null);
  }
  jclass com_class = null;
  jmethodID package_methodid, class_methodid;
  jstring package_name = null;
  jstring class_name = null;
  if (comp != null) {
     com_class = (*env)->FindClass(env, COMPONENT_CLASS_NAME);
     package_methodid = (*env)->GetMethodID(env, com_class, "getPackageName", "()Ljava/lang/String;");
     class_methodid = (*env)->GetMethodID(env, com_class, "getClassName", "()Ljava/lang/String;");
     package_name = (*env)->CallObjectMethod(env, comp, package_methodid);
     class_name = (*env)->CallObjectMethod(env, comp, class_methodid);
  }
  char * str_pkgname = "";
  char * str_clsname = "";
  if (package_name != null) {
     str_pkgname = (*env)->GetStringUTFChars(env, package_name, null);
  }
  if (class_name != null) {
     str_clsname = (*env)->GetStringUTFChars(env, class_name, null);
  }

  LOG_DEBUG("package_name %s class_name %s",  str_pkgname, str_clsname);


  char old_path[100] = {0};
    if (str_pkgname != null && str_pkgname[0] != '\0') {
        sprintf(old_path, "%s%s/lib/libkeepalive.so", DATA_PATH_PREFIX, str_pkgname);
  }

//  if (!file_exists(old_path)) {
//    LOG_DEBUG(" %s isn't exist", old_path);
//  }


  char install_path[100] = {0};
  if (str_pkgname != null && str_pkgname[0] != '\0') {
      sprintf(install_path, "%s%s/libkeepalive", DATA_PATH_PREFIX, str_pkgname);
  }

//  if (!file_exists(install_path)) {
//      copyfile(old_path, install_path);
//      chmod(install_path, 500);
//  }


  pid_t pid = fork();
  if (pid < 0) {
    return -1;
  } else if (pid == 0) {
     int i = 0;
     while (i < 50) {
        execlp(install_path, "libkeepalive", str_pkgname, str_clsname, str_action, iternal_ms, (char *)NULL);
        LOG_DEBUG("mon:main execl failure");
        usleep(3000);
        i++;
     }
     send_heartbeat(str_pkgname, str_clsname, str_action, iternal_ms);
  } else {
       if (iternal_ms != null && iternal_ms[0] != '\0') {
            LOG_DEBUG("release string");
            (*env)->ReleaseStringUTFChars(env, ms, iternal_ms);
            LOG_DEBUG("release string end");
       }
      if (str_action != null && str_action[0] != '\0') {
            LOG_DEBUG("release string");
           (*env)->ReleaseStringUTFChars(env, action, str_action);
                 LOG_DEBUG("release string end");
        }  else {
           (*env)->DeleteLocalRef(env , action);
        }
        if (str_pkgname != null && str_pkgname[0] != '\0') {
            (*env)->ReleaseStringUTFChars(env, package_name, str_pkgname);
        } else {
            (*env)->DeleteLocalRef(env , package_name);
        }
        if (str_clsname != null && str_clsname[0] != '\0') {
            (*env)->ReleaseStringUTFChars(env, class_name, str_clsname);
        } else {
            (*env)->DeleteLocalRef(env , class_name);
        }
        (*env)->DeleteLocalRef(env, intent_class);
        if (com_class != null) {
            (*env)->DeleteLocalRef(env, com_class);
        }
          LOG_DEBUG("release resoures");
      return pid;
  }
}

int mypopen(char *pstrCmd) {
	FILE *fp=NULL;
	fp = popen(pstrCmd, "r");
	if (fp) {
		pclose(fp);
	}
	return 0;
}

int file_exists(char *file_path) {
    if (file_path == null || file_path[0] == '\0') {
        return 0;
    }
    int result = access(file_path, 0);
    return (result == 0);
}

void send_heartbeat(char * pstrPkgName, char * classname, char * action, char * interval) {
    if (pstrPkgName == null || classname == null) {
        return;
    }
    if (interval == null) {
        interval = "8";
    }
    if (action == null) {
        action = "null";
    }
     char strCmd[200] = {0};
     int iInterval = atoi(interval);
     sprintf(strCmd, "am startservice -n %s/%s -a %s --user 0", pstrPkgName, classname, action);
     LOG_DEBUG("mon:strCmd %s", strCmd);
     char install_path[100] = {0};
     if (pstrPkgName != null && pstrPkgName[0] != '\0') {
               sprintf(install_path, "%s/%s", DATA_PATH_PREFIX, pstrPkgName);
      }
      LOG_DEBUG("mon: install_path : %s", install_path);
      while (1) {
            LOG_DEBUG("mon:keepalive intenal : %d ", iInterval);
            sleep(iInterval);
            if (!file_exists(install_path)) {
                LOG_DEBUG("app uninstall");
                break;
            }
            pid_t ppid = getppid();
            if (ppid == 1) {
               mypopen(strCmd);
               LOG_DEBUG("sucide");
               break;
            }
       }
       pid_t mypid = getpid();
       kill(mypid, SIGKILL);
       return ;
}

/**
 *
 */
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    return JNI_VERSION_1_4;
}

//void copyfile(char *oldp, char *newp) {
//    if (!check_string(oldp) || !check_string(newp)) {
//        return;
//    }
//    rename(oldp, newp);
//}
//
//int check_string(char *str) {
//    if (str == null || str[0] == '\0') {
//        return 0;
//    }
//    return 1;
//}
//
//int file_exists(char *file_path) {
//    if (file_path == null || file_path[0] == '\0') {
//        return 0;
//    }
//    int result = access(file_path, 0);
//    return (result == 0);
//}