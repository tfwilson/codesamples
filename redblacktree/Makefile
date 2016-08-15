CC       = g++
CPPFLAGS = -Wall -g
BIN_DIR  = bin

all: $(BIN_DIR)/mapcoloring
	-@echo "--- All Built!---"

# $< is an auto variable that means "first dependency", which is alistint.cpp in this case
# $@ is an auto variable that means "target name", which is $(BIN_DIR)/alistint.o in this case


$(BIN_DIR)/mapcoloring: mapcoloring.cpp rbbst.h $(BIN_DIR)/.dirstamp	
	$(CC) $(CPPFLAGS) mapcoloring.cpp -o $@

# $^ is an auto variable that means "all dependencies",
#   which are test.cpp and $(BIN_DIR)/alistint.o in this case
# IMPORTANT: $(GTEST_LL) needs to come *after* the source files

# Rule to ensure the $(BIN_DIR) directory exist or it'll create it.
# $@ is an auto variable that means "target name", which is $(BIN_DIR)/.dirstamp in this case
$(BIN_DIR)/.dirstamp:
	mkdir -p $(BIN_DIR)
	touch $(BIN_DIR)/.dirstamp

.PHONY: $(BIN_DIR)/mapcoloring clean

mapcoloring: $(BIN_DIR)/mapcoloring

clean:
	-@rm -rf $(BIN_DIR)
	-@echo "--- All Clean!---"