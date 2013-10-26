Setup
=====

I assume you have downloaded frwiki-pages-articles.xml and frwiki-langlinks.sql ([here is the latest version of these files](http://dumps.wikimedia.org/frwiki/latest/)) in a `wikipedia` directory, the articles in JSON format are in the `articles` directory (without subdirectories) and you want the result files to be in the `results` directory.

The extraction can be done in two steps. The first step can be done only once and the second steps needs to be done each time you have new articles to process.

Processing Wikipedia files
==========================

command line for the default setup:

    src/prep.sh src/ wikipedia/frwiki-pages-articles.xml wikipedia/frwiki-langlinks.sql res/stopwords results/

The script src/prep.sh takes five arguments:

- the directory where the binaries are (this is `src/`);
- the `frwiki-pages-articles.xml` file;
- the `frwiki-langlinks.sql` file;
- a list of stopwords (a list of French stopwords is available in the `res/` directory);
- the directory for the result files.

It produces the following files:

- `frwiki-en-links` : A table whith the id of French Wikipedia pages (col. 1) and the names of the corresponding English Wikipedia pages (col. 2).
- `frwiki-ids` : A table with the id of French Wikipedia pages (col. 1) and their names (col. 2).
- `frwiki-links` : A table with for each link in the French Wikipedia, the name of the origin page (col. 1), the anchor text (col. 2) and the name of the target page (col. 3).
- `frwiki-redirections` : A table with the names of French Wikipedia pages (col. 1) and the names of the pages they redirect to (col. 2).
- `frwiki-to-yago` : A table with the names of French Wikipedia pages (col. 1) and the corresponding yago entities (col. 2).
- `frwiki-links-canon` : Same as frwiki-links but with canonicalized page names (i.e. after following all redirections, capitalized and without spaces) and after removing links with stopwords as anchor text.
- `trie-frwiki` : A table with phrases (col. 1), French Wikipedia pages (col. 2) and the number of times a phrase refers to a page (col. 3). In pairs that appear less than five times, the page name is replaced by the empty string.
- `trie-yago` : Same as trie-frwiki but names of French Wikipedia pages are replaced by yago entities.

Processing articles
===================

command line for the default setup:

    src/run.sh src/ results/trie-yago res/isInCountry-yago articles/ results/

The script src/run.sh takes five arguments:

- the directory where the binaries are (this is `src/`);
- the `trie-yago` file created by the first step;
- a table of locations and their countries (you can use the file provided in the res/` directory);
- the `articles` directory that contains all the articles in JSON format;
- the directory for the result files.

It produces the following files:

- `articles-txt/` : A directory containing the articles in raw text format.
- `articles.tsv` : A table with the ids of the articles (col. 1) and their publication date (col. 2).
- `mentions.tsv` : A table with the ids of the articles (col. 1), the entities mentioned (col. 2) and a number wich corresponds to the number of times an entity is mentioned in an article and with which confidence (col. 3).
- `locations.tsv` : A table with the ids of the articles (col. 1), the countries corresponding to the articles (col. 2) and a magic number wich corresponds to the confidence of this location (col. 3).

Structure of the source code
==================================

`id.mll`, `langlink.mll`, `link.mll` and `redirect.mll`
-------------------------------------------------------

Ocamllex files to parse data from Wikipedia.

`article.scala`, `date.scala` and `jsonparse.scala`
-------------------------------------

The Jsonparse class provides a main method to parse JSON files and output the raw text of the article and the pair article id, publication date. It takes a directory of JSON files and produces a directory of txt files and a file with the ids and publication dates. The parsing itself is done by the constructor of the Article class using the standard library.

`canonicalize.scala`
--------------------

This file contains several classes to convert files (e.g. from French titles to YAGO entities). It contains the classes Canonicalize, Convert and Frwiki2Yago.

`locate.scala`
--------------

Compute locations of articles given the mentions they contain. The country with the most mentions is selected.

`main.scala` and `tag.scala`
----------------------------

The Main class is a wrapper for the functions of the Tag class. The Tag class scan the articles to find entities.

`trie.scala`
------------

The Trie class provides functions to manipulate tries (multimaps whith key of type `String`).
