JC := javac
JR := java
SRC := Projeto_COO/src/*.java
MAIN := Main
BUILD := bin

run: build
	@echo "Running the project"
	@$(JR) -cp $(BUILD) $(MAIN)

build: $(BUILD) $(SRC)
	@echo "Compiling the project"
	@$(JC) -d $(BUILD) $(SRC)

$(BUILD):
	@echo "Creating output folder"
	@mkdir -p $@

clean:
	@echo "Cleaning project"
	rm -fr $(BUILD)

help:
	@echo "Available targets:"
	@echo "   run        : Run the application"
	@echo "   build      : Compile the Java project"
	@echo "   clean      : Remove compiled projects"
	@echo "   help       : Show this help"



.PHONY: clean build run help
