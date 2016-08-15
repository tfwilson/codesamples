#include "myset.h"
#include "webpage.h"
#include <iostream>
#include <fstream>
using namespace std;

WebPage::WebPage() {}

WebPage::WebPage(std::string filename)
{
	_filename = filename;
}

WebPage::~WebPage()
{}

  /**
   * Sets the filename/URL of this webpage
   */
void WebPage::filename(std::string fname)
{
	_filename = fname;
}

  /**
   * Returns the filename/URL of this webpage
   */
std::string WebPage::filename() const
{
	return _filename;
}

  /**
   * Replace the set containing all unique words in the text
   *  with the contents of the input parameter words
   */
void WebPage::all_words(const MySet<string>& words)
{
	this->_allWords.clear();
	set<string>::iterator words_it;
	for (words_it = words.begin(); words_it != words.end() ; words_it++)
	{
		string n = *words_it;
		this->_allWords.insert(n);
	}
}

  /**
   * Returns all the unique, tokenized words in this webpage 
   */
MySet<string> WebPage::all_words() const
{
	return _allWords;
}
  /**
   * Adds a webpage that links to this page
   */
void WebPage::add_incoming_link(WebPage* wp)
{
	_inlinks.insert(wp);
}

  /**
   * Returns all webpages that link to this page
   */
MySet<WebPage*> WebPage::incoming_links() const
{
	return _inlinks;
}

  /**
   * Adds a webpage that this page links to
   */
void WebPage::add_outgoing_link(WebPage* wp)
{
	_outlinks.insert(wp);
}

  /**
   * Returns all webpages this page links to
   */
MySet<WebPage*> WebPage::outgoing_links() const
{
	return _outlinks;
}

double WebPage::rank()
{
	return _pagerank;
}
 void WebPage::set_rank(double n)
 {
 	_pagerank = n;
 }
  /**
   * Displays the webpage text to the screen 
   */
std::ostream & operator<< (std::ostream & os, const WebPage & page)
{
	ifstream my_input;
	my_input.open(page._filename.c_str());
	while(!my_input.eof())
	{
		char ch;
		my_input.get(ch);
		
		if (ch == '[')
		{
			os << ch;
			while (ch != ']' && !my_input.eof())
			{
				my_input.get(ch);
				os << ch;
			}
			my_input.get(ch);
			if (ch == '(')
			{
				while (ch != ')' && !my_input.eof())
				{
					my_input.get(ch);
				}
				my_input.get(ch);
			}
			else 
			{
				os << ch;
			}
		}
		else 
		{
			os << ch; 
		}
	}
	my_input.close();
	return os;
}

  /**** Add other desired member functions here *****/