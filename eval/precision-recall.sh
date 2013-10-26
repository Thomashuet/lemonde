#!/bin/bash

if [ $# != 2 ]
then
  echo "$0 <real> <target>"
  exit
fi

a=`cut -f 1,2 $1 | grep -x -f $2 | wc -l`
b=`cat $1 | wc -l`
c=`cat $2 | wc -l`

precision=`bc -l <<< "$a/$b"`
recall=`bc -l <<< "$a/$c"`

echo "precision :	$precision"
echo "recall :	$recall"
