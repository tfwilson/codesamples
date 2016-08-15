#include "searcheng.h"
#include "util.h"
#include <fstream>
#include <set>
#include <map>
#include <string>
#include <vector>
#include <iostream>
#include "mdparser.h"

using namespace std;
SearchEng::SearchEng() {};
  SearchEng::~SearchEng()
  {
    map<string, WebPage*>::iterator fname_it;
    for (fname_it = _fnamemap.begin(); fname_it != _fnamemap.end(); fname_it++)
    {
      delete fname_it->second;
    }// go through entire map and delete webpage it->second
  }
  void SearchEng::add_parse_from_index_file(std::string index_file, PageParser* parser)
  {
    std::ifstream my_input;
  	my_input.open(index_file.c_str());
  	string fname;
    while(getline(my_input, fname))
  	{
  		add_parse_page(fname, parser);
  	}
  }
  
  void SearchEng::add_parse_page(std::string filename, PageParser* parser)
  {
    map<string, WebPage*>::iterator filename_it;
    filename_it = _fnamemap.find(filename);
    WebPage* newpage;
    if (filename_it == _fnamemap.end())
    {
      newpage = new WebPage(filename);
      _fnamemap.insert(make_pair(filename, newpage));
    }    
    else
    {
      newpage = filename_it->second;
    }
    MySet<string> templinks; //string
    MySet<string> tempwords; //string
    parser->parse(filename, tempwords, templinks); // parse webpage
    newpage->all_words(tempwords);

    set<string>::iterator set_it;
    for (set_it = tempwords.begin(); set_it != tempwords.end(); set_it++)
    {
      string n = convToLower(*set_it);
      map<string, MySet<WebPage*> >::iterator map_it;
      map_it = _searchmap.find(n);
      if (map_it == _searchmap.end()) //if not already added
      {
        MySet<WebPage*> pageset; //create webpaeset and add to it
        pageset.insert(newpage);
        _searchmap.insert(make_pair(n, pageset));
      }
      else //add to existing set
      {
        (map_it->second).insert(newpage);
      }
    }
    set<string>::iterator link_it;
    map<string, WebPage*>::iterator fname_it;
    for (link_it = templinks.begin(); link_it != templinks.end(); link_it++)
    {
      string n = *link_it;
      fname_it = _fnamemap.find(n);
      if (fname_it == _fnamemap.end())
      {
        WebPage* wp = new WebPage(n);
        _fnamemap.insert(make_pair(n, wp));
        newpage->add_outgoing_link(wp);
        wp->add_incoming_link(newpage);
      }
      else
      {
        newpage->add_outgoing_link(fname_it->second);
        (fname_it->second)->add_incoming_link(newpage);
      }

    }
  }
  
  MySet<WebPage*> SearchEng::AND_parser(std::string input, unsigned int i)
  {
    MySet<WebPage*> and_set;
    while (i < input.size())
    {
      string newword = "";
      while (input[i] != ' ' && i < input.size())
      {
        newword += input[i];
        i++;
      }

      i++;
      map<string, MySet<WebPage*> >::iterator map_it;
      map_it = _searchmap.find(convToLower(newword)); 
      if (map_it != _searchmap.end())
      {
        if (and_set.empty() == true)
        {
          and_set = map_it->second;
        }
        else
        {
          and_set = and_set.set_intersection(map_it->second);
        }
      }
      else
      {
        and_set.clear();
        return and_set;
      }
    }
    return and_set;
  }

  MySet<WebPage*> SearchEng::OR_parser(std::string input, unsigned int i)
  {
    MySet<WebPage*> or_set;
    while (i < input.size())
    {
      string newword = "";
      while (input[i] != ' ' && i < input.size())
      {
        newword += input[i];
        i++;
      }
      i++;
      map<string, MySet<WebPage*> >::iterator map_it;

      map_it = _searchmap.find(convToLower(newword));
      if (map_it != _searchmap.end())
      {
        if (or_set.empty() == true)
        {
          or_set = map_it->second;
        }
        else
        {
          or_set = or_set.set_union(map_it->second);
        }
      }
    }
    return or_set;
  }

  MySet<WebPage*> SearchEng::WORD_parser(std::string input)
  {
    MySet<WebPage*> word_set;
    map<string, MySet<WebPage*> >::iterator map_it;
    map_it = _searchmap.find(convToLower(input));
    if (map_it == _searchmap.end())
    {
      return word_set;
    }
    else
    {
      word_set = map_it->second;
      return word_set;
    }
  }

void SearchEng::pagerank(vector<WebPage*> webpagevec, double E, int T)
{
  double vec_size = webpagevec.size();
  double initial_rank = 1.0/vec_size;

  vector<MySet<WebPage*> > can_incoming;
  vector<int> outgoing_num;
  vector<double> newranks;

  
  for (int i = 0; i < vec_size; i++)
  {
    webpagevec[i]->set_rank(initial_rank);
    newranks.push_back(initial_rank);
    MySet<WebPage*> og_ilinks;
    og_ilinks = webpagevec[i]->incoming_links();
    MySet<WebPage*>::iterator ilink_it;
    MySet<WebPage*> tempset;
    for (ilink_it = og_ilinks.begin(); ilink_it != og_ilinks.end(); ilink_it++)
    {
      for (int j = 0; j < vec_size; j++)
      {
        WebPage* itemp = *ilink_it;
        if ((itemp->filename() == webpagevec[j]->filename()) && (itemp->filename() != webpagevec[i]->filename()))
        {
          tempset.insert(itemp);
        }
      }
    }
    can_incoming.push_back(tempset);
    MySet<WebPage*> og_olinks;
    og_olinks = webpagevec[i]->outgoing_links();
    MySet<WebPage*>::iterator olink_it;
    int olinknum = 1;
    for (olink_it = og_olinks.begin(); olink_it != og_olinks.end(); olink_it++)
    {
      for (int j = 0; j < vec_size; j++)
      {
        WebPage* otemp = *olink_it;
        if ((otemp->filename() == webpagevec[j]->filename()) && (otemp->filename() != webpagevec[i]->filename()))
        {
          olinknum++;
        }
      }
    }
    outgoing_num.push_back(olinknum);
  }

// for (int i = 0; i < vec_size; i++)
// {
//   cout << "Name: " <<  webpagevec[i]->filename() << "  #outlinks:" << outgoing_num[i] << 
//   "  incoming links:"  << can_incoming[i].size() << "  pageranks:" << newranks[i] << endl;
// }
// cout << endl;

for (int t = 0; t < T; t++)
{
  for (int i = 0 ; i < vec_size ; i++)
  {
    double total_rank = 0;
    MySet<WebPage*>::iterator ilink_it;
    for (ilink_it = can_incoming[i].begin(); ilink_it != can_incoming[i].end() ; ilink_it++)
    {
      WebPage* temppage = *ilink_it;
      int index;
      for (int j = 0; j < vec_size ; j++)
      {
        if (temppage->filename() == webpagevec[j]->filename())
        {
          index = j;
        }
      }
      total_rank += (temppage->rank() / (double)outgoing_num[index]);
    }
    total_rank += (webpagevec[i]->rank() / (double)outgoing_num[i]);
    double newrank = (1 - E) * total_rank + (E * initial_rank);

    newranks[i] = newrank;
  }
  for (int l = 0; l < vec_size ; l++)
  {
    webpagevec[l]->set_rank(newranks[l]);
  }
}
// double sum = 0;
// for (int m = 0 ; m < vec_size ; m++)
// {
//   sum += newranks[m];
//   cout << webpagevec[m]->filename() << ":     " << newranks[m] << endl;
// }
// cout << "SUM:  " << sum << endl;
// for t steps
  // do math
  // for each incomign link.. get page rank
  // for each incoming link.. get its filename
  // find filename in canditadate vector
  // get index
  // use index to find otugoing links number
  // divide
  // add v.rank();
  // put into pagerank vecotr

  //once done, update webpage provate members from vector


}