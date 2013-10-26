#!/bin/bash

if [ $# != 5 ]
then
  echo "$0 </bin> <trie> <isInCountry> </articles-json> </out>"
  exit
fi

bin=${1%%/}
out=${5%%/}
conf='0.7'

echo "Extracting dates and text of articles"
mkdir $out/articles-txt
time scala -cp $bin fr.lemonde.JsonParse $4 $out/articles-txt/ $out/articles.tsv 2> /dev/null

echo "Extracting mentions"
time scala -cp $bin fr.lemonde.Main $2 $out/articles-txt $out/mentions.tsv $conf

echo "Locating articles"
time scala -cp $bin fr.lemonde.Locate $3 $out/mentions.tsv > $out/locations.tsv

