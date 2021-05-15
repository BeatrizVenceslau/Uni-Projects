#!/bin/bash

inputdir=$1
outputdir=$2
maxThreads=$3
NumberBuckets=$4

for test in "$inputdir"/*.txt
do
   if [ $maxThreads -gt 0 ]
   then
      echo InputFile = `basename "$test"`  NumThreads=​1
      ./tecnicofs-nosync "$test" "$outputdir/`basename "${test%.*}"`-1.txt" "1" "1" | tail -n 1

      for thread in $(seq 2 $maxThreads)
      do
         echo InputFile = `basename "$test"`  NumThreads=​$thread
         ./tecnicofs-mutex "$test" "$outputdir/`basename "${test%.*}"`-$thread.txt" "$thread" "$NumberBuckets" | tail -n 1
      done
   else
      echo "Invalid maxThreads"
      exit 1
   fi

done