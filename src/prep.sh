#!/bin/bash

if [ $# != 5 ]
then
  echo "$0 </bin> <frwiki-pages-articles.xml> <frwiki-langlinks.sql> <stopwords> </out>"
  exit
fi

bin=${1%%/}
out=${5%%/}

echo "Extracting links"
time $bin/link < $2 > $out/frwiki-links 2> /dev/null

echo "Extracting ids"
time $bin/id < $2 > $out/frwiki-ids 2> /dev/null

echo "Extracting redirections"
time $bin/redirect < $2 > $out/frwiki-redirections 2> /dev/null

echo "Extracting langlinks"
time $bin/langlink < $3 > $out/frwiki-en-links 2> /dev/null

echo "Canonicalizing links"
time scala -cp $bin fr.lemonde.Canonicalize $out/frwiki-redirections $4 $out/frwiki-links > $out/frwiki-links-canon

echo "Converting langlinks"
time scala -cp $bin fr.lemonde.Convert $out/frwiki-ids $out/frwiki-en-links > $out/frwiki-to-yago

echo "Building trie"
time scala -cp $bin fr.lemonde.util.Trie $out/frwiki-links-canon $out/trie-frwiki

echo "Converting trie"
time scala -cp $bin fr.lemonde.Frwiki2Yago $out/frwiki-to-yago $out/trie-frwiki > $out/trie-yago

