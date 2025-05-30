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

.PHONY: clean build run
clean:
	@echo "Cleaning project"
	rm -fr $(BUILD)
