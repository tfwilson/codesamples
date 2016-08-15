#include "pageparser.h"
#include "myset.h"

class MarkDown : public PageParser {
public:
	void parse(std::string filename, MySet<std::string>& allWords, MySet<std::string>& allLinks);
};
