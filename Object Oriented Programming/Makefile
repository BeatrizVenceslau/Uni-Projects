
.PHONY: all clean run test jar

all: lib/po-uilib.jar
	@clear
	javac -cp lib/po-uilib.jar:. `find m19 -name *.java`

run: all
	@java -Dimport=tests/A-99-99-M-ok.import -cp lib/po-uilib.jar:. m19.app.App

graph: clean all
	@java -Dimport=tests/A-99-99-M-ok.import -Dui=swing -cp lib/po-uilib.jar:. m19.app.App

runy: all
	@java -cp lib/po-uilib.jar:. m19.app.App
clean:
	@rm -rf `find m19 -name *.class` tests/*.outhyp tests/testsEI/*.outhypEI tests/testsEF/*.outhypEF tests/testsEI/*.diffEI tests/testsEF/*.diffEF tests/*.diff proj2.jar proj.jar 0 1 2 3 4 5 6 7 8 9 10 s requisicao cumpridor faltoso user work works tests/*.out2 notificacao tests.zip myLastRun.outhyp myReport.html

individual: all
	java -Dimport=tests/A-99-95-M-ok.import -cp lib/po-uilib.jar:. m19.app.App

test: all
	@./runtests.sh @./runtestsMINE.sh 

jar:
	@jar -cvf proj.jar `find m19 -name "*.java"`

zipTestes:
	zip tests.zip tests/00.md tests/*.in tests/*.out tests/*.import

analyse:
	#pmd -f html -d proj.jar -R category/java/bestpractices.xml -ignorelist ignorer.txt -auxclasspath lib/ > myReport.html
	#pmd -f html -d proj.jar -R rulesets/java/quickstart.xml -ignorelist ignorer.txt -auxclasspath lib/ > myReport.html
