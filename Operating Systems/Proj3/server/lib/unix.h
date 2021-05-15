#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include "constants.h"

#define UNIXSTR_PATH  "/tmp/s.unixstr"
#define UNIXDG_PATH  "/tmp/s.unixdgx"
#define UNIXDG_TMP   "/tmp/dgXXXXXXX"

typedef struct file {
    int fd;
    int mode;
} file;

typedef struct client {
    uid_t uid;
    int socket;
    file openFile[MAX_OPEN_FILES];
} client;