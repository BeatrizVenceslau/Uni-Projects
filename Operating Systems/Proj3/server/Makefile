# Makefile, versao 1
# Sistemas Operativos, DEI/IST/ULisboa 2019-20

SOURCES = main.c lib/fs.c lib/sync.c lib/bst.c 
SOURCES += lib/hash.c lib/inodes.c 
OBJS_RWLOCK = $(SOURCES:%.c=%-rwlock.o)
OBJS = $(OBJS_RWLOCK)
CC   = gcc
LD   = gcc
CFLAGS =-Wall -std=gnu99 -I../ -g
LDFLAGS=-lm -pthread
TARGETS = tecnicofs-rwlock

.PHONY: all clean

all: $(TARGETS)

$(TARGETS):
	$(LD) $(CFLAGS) $^ -o $@ $(LDFLAGS)

## RWLOCK ###
lib/bst-rwlock.o: CFLAGS+=-DRWLOCK
lib/bst-rwlock.o: lib/bst.c lib/bst.h

lib/hash-rwlock.o: CFLAGS+=-DRWLOCK
lib/hash-rwlock.o: lib/hash.c lib/hash.h

lib/inodes-rwlock.o: CFLAGS+=-DRWLOCK
lib/inodes-rwlock.o: lib/inodes.c lib/inodes.h

lib/fs-rwlock.o: CFLAGS+=-DRWLOCK
lib/fs-rwlock.o: lib/fs.c lib/fs.h lib/bst.h lib/hash.h

lib/sync-rwlock.o: CFLAGS+=-DRWLOCK
lib/sync-rwlock.o: lib/sync.c lib/sync.h lib/constants.h

main-rwlock.o: CFLAGS+=-DRWLOCK
main-rwlock.o: main.c lib/fs.h lib/bst.h lib/hash.h lib/constants.h lib/timer.h lib/sync.h
tecnicofs-rwlock: lib/bst-rwlock.o lib/hash-rwlock.o lib/inodes-rwlock.o lib/fs-rwlock.o lib/sync-rwlock.o main-rwlock.o


%.o:
	$(CC) $(CFLAGS) -c -o $@ $<

clean:
	@echo Cleaning...
	rm -f $(OBJS) $(TARGETS)
