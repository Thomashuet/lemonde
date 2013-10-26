doc.pdf: org.pdf README.tex

%.pdf: %.tex
	xelatex $<
	xelatex $<

%.tex: %.md
	pandoc -o $@ $<

%.pdf: %.dot
	dot -Tpdf -o $@ $<

clean:
	rm -f *.aux *.log *.out README.tex

rm: clean
	rm -f *.pdf
