Le Monde
========

Tools for extracting entities from news articles and locating them.

Dependencies
------------

- scala
- ocaml, ocamllex (to parse wikipedia files)

to build the documentation:

- graphviz
- pandoc
- xelatex

you can install everything with

    sudo apt-get install scala ocaml-native-compilers graphviz pandoc xelatex

Build
-----

    cd src
    make

Files
-----

- README.md : this file
- doc/ : documentation
- eval/target : manually extracted mentions for evaluation purpose
- res/isInCountry-yago : a TSV file with locations and their countries
- res/stopwords : a list of French stopwords
- src/ : source code
