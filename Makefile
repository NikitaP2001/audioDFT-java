SRC = Application.java

all: $(SRC)
	javac $^ -d build -g

release:
	javac $^ -d build -g:none

clean:
	rm -rf build
