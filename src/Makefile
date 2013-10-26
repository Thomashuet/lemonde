EXE=link redirect id langlink

all: $(EXE) scala

%: %.ml
	ocamlopt -o $@ $<
%.ml: %.mll
	ocamllex $<

scala:
	fsc *.scala

clean:
	rm -rf *.cm[iox] *.o *.ml

rm: clean
	rm -rf fr $(EXE)
