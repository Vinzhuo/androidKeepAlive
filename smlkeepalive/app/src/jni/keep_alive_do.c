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

#define DATA_PATH_PREFIX "/data/data/"

int file_exists(char *filename);

int mypopen(char *pstrCmd) {
	FILE *fp=NULL;
    LOG_DEBUG("execute %s", pstrCmd);
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

int main(int argc, char** argv) {
	if(argc < 4){
        LOG_DEBUG("arg : %d error:mon:main", argc);
		return -1;
	}
    LOG_DEBUG("mon: I am in");

    char *interval = argv[4];
    char *action = argv[3];
    char *pstrPkgName = argv[1];
    char *classname = argv[2];
    int iInterval = atoi(interval);
    if (pstrPkgName == null || classname == null) {
        LOG_DEBUG("mon pkg : %s  class : %s", pstrPkgName, classname);

        return -1;
    }
    if (interval == null) {
        interval = "8";
    }
    if (action == null) {
       action = "null";
    }
    char strCmd[200] = {0};
     sprintf(strCmd, "am startservice -n %s/%s -a %s --user 0", pstrPkgName, classname, action);
     LOG_DEBUG("mon:strCmd %s", strCmd);
     char install_path[100] = {0};
       if (pstrPkgName != null && pstrPkgName[0] != '\0') {
           sprintf(install_path, "%s/%s", DATA_PATH_PREFIX, pstrPkgName);
       }
       LOG_DEBUG("mon: install_path : %s", install_path);
    while (1) {
        LOG_DEBUG("mon:keepalive inTerval: %d", iInterval);
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
    return 0;
}