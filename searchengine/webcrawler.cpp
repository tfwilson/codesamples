#include "searcheng.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <map>
#include <string>
#include <vector>

using namespace std;
void DFScrawler(string newlink, vector<string>& linkvector);
void config_parser(string config_file, map<string, string>& config_map);

int main(int argc, char* argv[])
{
	
	string config_file = argv[1];
	map<string, string> config_map;
	config_parser(config_file, config_map);
	map<string, string>::iterator config_it;
	config_it = (config_map).find("INDEX_FILE");
	string crawl_index = config_it->second;
	config_it = (config_map).find("OUTPUT_FILE");
	string crawl_output = config_it->second;
	
	std::ifstream index_file;
	index_file.open((crawl_index).c_str()); 
	string rootfile;
	vector<string> crawledLinks;
	while (!index_file.eof())
	{
		index_file >> rootfile;
		DFScrawler(rootfile, crawledLinks);
	}
	ofstream output;
	output.open(crawl_output.c_str());
	for (unsigned int i = 0; i < crawledLinks.size(); i++)
	{
		output << crawledLinks[i] << endl;
	}
return 0;	
}

void DFScrawler(string newlink, vector<string>& linkvector)
{
	//check if link is in vector
	bool is_found = false;
	for (unsigned int i = 0; i < linkvector.size(); i++)
	{
		if (linkvector[i] == newlink)
		{
			is_found = true;
		}
	}
	//try to open link
	std::ifstream link_file;
	link_file.open((newlink).c_str());
	if (!link_file.fail() && !is_found)
	{
		//add link to vector
		linkvector.push_back(newlink);
		//parse through and add all outgoing links to other vector
		vector<string> outgoinglinks;
		char ch;
		string outlink;
		while (!link_file.eof())
		{
			link_file.get(ch);
			if (ch == '[')
			{
				while (ch != ']')
				{
					link_file.get(ch);
				}
				link_file.get(ch);
				if (ch == '(')
				{
					outlink = "";
					link_file.get(ch);
					while (ch != ')')
					{
						outlink += ch;
						link_file.get(ch);
					}
					outgoinglinks.push_back(outlink);
				}
			}
			else
			{

			}
		}
		link_file.close();
		//call dfs on each link in vector
		for (unsigned int i = 0 ; i < outgoinglinks.size(); i++)
		{
			DFScrawler(outgoinglinks[i], linkvector);
		}
	}
}

void config_parser(string config_file, map<string, string>& config_map)
{
  std::ifstream my_input;
  my_input.open(config_file.c_str());
  char ch;
  string v = "";
  while(!my_input.eof())
  {
    my_input.get(ch);
    if (ch == '#') //ignore comments
    {
      getline(my_input, v);
      v = "";
    }
    else if (ch != '#' && ch != ' ') //hit parameter
    {
      string parameter = "";
      string value = "";
      while ((ch != ' ' && ch != '=') && !my_input.eof())
      {
        parameter += ch;
        my_input.get(ch);
      }
      while (ch == '=' || ch == ' ')
      {
        my_input.get(ch);
      }
      while ((ch != ' ' && ch != '\n' && ch != '#') && !my_input.eof())
      {
        value += ch;
        my_input.get(ch);
      }
      config_map.insert(make_pair(parameter, value));
      if (ch != '\n')
      { 
        getline(my_input, v);
      }
      v = "";
    }
    else 
    {

    }
  }
  my_input.close();
}