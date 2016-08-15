#ifndef SEARCHENG_H
#define SEARCHENG_H
#include <map>
#include <vector>
#include <string>
#include <iostream>
#include "webpage.h"
#include "myset.h"
#include "pageparser.h"

class SearchEng {
 public:
  SearchEng();
  ~SearchEng();
  void add_parse_from_index_file(std::string index_file, 
				 PageParser* parser);
  void add_parse_page(std::string filename, 
		      PageParser* parser);
  MySet<WebPage*> AND_parser(std::string input, unsigned int i);
  MySet<WebPage*> OR_parser(std::string input, unsigned int i);
  MySet<WebPage*> WORD_parser(std::string input);
  void pagerank(std::vector<WebPage*> webpagevec, double E, int T);

  /**** Add other desired member functions here *****/


 private:
 /**** Add other desired data members here *****/
 	std::map<std::string, MySet<WebPage*> > _searchmap;
 	std::map<std::string, WebPage*> _fnamemap;
  


};

#endif
