#include "pageparser.h"
#include <string>
#include <iostream>
#include "mdparser.h"
#include <fstream>
using namespace std;

void MarkDown::parse(string filename, MySet<string>& allWords, MySet<string>& allLinks)
{
	ifstream my_input(filename.c_str());
	char ch;
	string v = "";

	while(!my_input.eof())
	{
		my_input.get(ch);
		if(isdigit(ch) || isalpha(ch))
		{
			while ((isdigit(ch) || isalpha(ch)) && !my_input.eof())
			{
				v += ch;
				my_input.get(ch);
			}
			allWords.insert(v);
			v = "";
		}
		else if(ch == '[')
		{
			string anchor = "";
			my_input.get(ch);
			while (ch != ']')
			{
				while (ch != ' ' && ch != ']')
				{
					anchor += ch;
					my_input.get(ch);
				}
				if (anchor.size() != 0)
				{
					allWords.insert(anchor);
					anchor = "";
				}
				if (ch != ']')
				{
					my_input.get(ch);
				}
			}
			my_input.get(ch);
			if (ch == '(')
			{
				string link = "";
				my_input.get(ch);
				while (ch != ')')
				{
					link += ch;
					my_input.get(ch);
				}
				ifstream link_input(link.c_str());
				if (!link_input.fail())
				{
					allLinks.insert(link);
				}
				link = "";
				link_input.close();
			}
		}
		else 
		{
			 
		}
	}
	my_input.close();
}