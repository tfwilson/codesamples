#include "main_window.h"
#include "webpage.h"
#include "searcheng.h"
#include "msort.h"
#include <set>
#include <sstream>
#include <fstream>
#include <vector>
#include <string>
#include <iostream>

using namespace std;
MainWindow::MainWindow(std::string config_file) : QWidget(NULL)
{
	config_parser(config_file, _configmap);
	map<string, string>::iterator config_it;
	config_it = (_configmap).find("INDEX_FILE");
	string input_files = config_it->second;
	TSearch.add_parse_from_index_file(input_files, &parser);
	
	//OTHER WINDOW
	//Message Box
	otherWindow = new QWidget;
	otherLayout = new QVBoxLayout();
	outputbox = new QTextEdit();
	otherLayout->addWidget(outputbox);
	outputbox->setReadOnly(true);

	alllinksLayout = new QHBoxLayout();
	otherLayout->addLayout(alllinksLayout);
	//Link Layout
	othersortLayout = new QVBoxLayout();
	alllinksLayout->addLayout(othersortLayout);
	//RadioButtons
	otherradioLabel = new QLabel("Sort by:");
	othersortLayout->addWidget(otherradioLabel);
	
	othersortGroupBox = new QGroupBox();
	otherfnameRadio = new QRadioButton("f&ilename");
	connect(otherfnameRadio, SIGNAL(clicked()), this, SLOT(sortOther()));
	otheroutlinkRadio = new QRadioButton("&number of outgoing links");
	connect(otheroutlinkRadio, SIGNAL(clicked()), this, SLOT(sortOther()));
	otherinlinkRadio = new QRadioButton("&number of incoming links");
	connect(otherinlinkRadio, SIGNAL(clicked()), this, SLOT(sortOther()));
	otherfnameRadio->setChecked(true);
	othersortLayout->addWidget(otherfnameRadio);
	othersortLayout->addWidget(otheroutlinkRadio);
	othersortLayout->addWidget(otherinlinkRadio);
	//othersortGroupBox->setLayout(othersortLayout); CHECK THIS
	othersortLayout->addWidget(othersortGroupBox);
	//Outlink List
	outlinksLayout = new QVBoxLayout();
	alllinksLayout->addLayout(outlinksLayout);
	outlinksLabel = new QLabel("Outgoing Links");
	outlinksLayout->addWidget(outlinksLabel);
	outlinksListWidget = new QListWidget();
	connect(outlinksListWidget, SIGNAL(currentRowChanged(int)), this, SLOT(olinkchangepage(int)));
	outlinksLayout->addWidget(outlinksListWidget);
	//Inlink List
	inlinksLayout = new QVBoxLayout();
	alllinksLayout->addLayout(inlinksLayout);
	inlinksLabel = new QLabel("Incoming Links");
	inlinksLayout->addWidget(inlinksLabel);
	inlinksListWidget = new QListWidget();
	connect(inlinksListWidget, SIGNAL(currentRowChanged(int)), this, SLOT(ilinkchangepage(int)));
	inlinksLayout->addWidget(inlinksListWidget);
	//CLOSE BUTTON
	closeButtonLayout = new QHBoxLayout();
	otherLayout->addLayout(closeButtonLayout);
	closeButton = new QPushButton("Close");
	connect(closeButton, SIGNAL(clicked()), this, SLOT(hidepopup()));
	closeButtonLayout->addWidget(closeButton);

	otherWindow->setLayout(otherLayout);

	//MAIN WINDOW

	setWindowTitle("TSearch");
	overallLayout = new QVBoxLayout();
	formLayout = new QHBoxLayout();
	overallLayout->addLayout(formLayout);
	
	//Open Search Box
	searchPromptLabel = new QLabel("Enter Search Terms:");
	formLayout->addWidget(searchPromptLabel);
	searchInput = new QLineEdit();
	formLayout->addWidget(searchInput);
	
	//Radio buttons
	radiosearchGroupBox = new QGroupBox();
	singleRadio = new QRadioButton("&Single");
	andRadio = new QRadioButton("&AND");
	orRadio = new QRadioButton("&OR");
	singleRadio->setChecked(true);
	radiosearchLayout = new QHBoxLayout();
	radiosearchLayout->addWidget(singleRadio);
	radiosearchLayout->addWidget(andRadio);
	radiosearchLayout->addWidget(orRadio);
	radiosearchGroupBox->setLayout(radiosearchLayout);

	overallLayout->addWidget(radiosearchGroupBox);
	//Search Button
	searchButtonLayout = new QHBoxLayout();
	overallLayout->addLayout(searchButtonLayout);
	searchButton = new QPushButton("TSearch");
	connect(searchInput, SIGNAL(returnPressed()), searchButton, SIGNAL(clicked()));
	connect(searchButton, SIGNAL(clicked()), this, SLOT(addWebpages()));
	searchButtonLayout->addWidget(searchButton);
	//labels for listwidget
	sortlabelLayout = new QHBoxLayout();
	overallLayout->addLayout(sortlabelLayout);
	//rankLabel = new QLabel("Page Rank    ");
	fnameLabel = new QLabel("Filename    ");
	olinkLabel = new QLabel("Outgoing Links    ");
	ilinkLabel = new QLabel("Incoming Links");
	//sortlabelLayout->addWidget(rankLabel);
	sortlabelLayout->addWidget(fnameLabel);
	sortlabelLayout->addWidget(olinkLabel);
	sortlabelLayout->addWidget(ilinkLabel);
	//Sorting Radio Buttons
	resultLayout = new QHBoxLayout();
	overallLayout->addLayout(resultLayout);
	
	mainsortLayout = new QVBoxLayout();
	mainradioLabel = new QLabel("Sort by:");
	mainsortLayout->addWidget(mainradioLabel);
	
	mainsortGroupBox = new QGroupBox();
	rankRadio = new QRadioButton("&Page Rank");
	connect(rankRadio, SIGNAL(clicked()), this, SLOT(sort()));
	fnameRadio = new QRadioButton("&Filename");
	connect(fnameRadio, SIGNAL(clicked()), this, SLOT(sort()));
	outlinkRadio = new QRadioButton("&Number of outgoing links");
	connect(outlinkRadio, SIGNAL(clicked()), this, SLOT(sort()));
	inlinkRadio = new QRadioButton("&Number of incoming links");
	connect(inlinkRadio, SIGNAL(clicked()), this, SLOT(sort()));
	rankRadio->setChecked(true);
	mainsortLayout->addWidget(rankRadio);
	mainsortLayout->addWidget(fnameRadio);
	mainsortLayout->addWidget(outlinkRadio);
	mainsortLayout->addWidget(inlinkRadio);
	mainsortGroupBox->setLayout(mainsortLayout);
	
	overallLayout->addWidget(mainsortGroupBox);
	//Webpage List
	webpageListWidget = new QListWidget();
	connect(webpageListWidget, SIGNAL(currentRowChanged(int)), this, SLOT(showpopup(int)));
	resultLayout->addWidget(webpageListWidget);

	maincloseButton = new QPushButton("Quit");
	connect(maincloseButton, SIGNAL(clicked()), this, SLOT(close()));
	overallLayout->addWidget(maincloseButton);
	setLayout(overallLayout);
	setFixedSize(sizeHint());
}
MainWindow::~MainWindow()
{
	//MAINWINDOW
	//delete overallLayout;
	delete formLayout;
	delete searchPromptLabel;
	delete searchInput;
	//delete radiosearchGroupBox;
	delete singleRadio;
	delete andRadio;
	delete orRadio;
	delete radiosearchLayout;
	delete searchButtonLayout;
	delete searchButton;
	delete resultLayout;
	delete mainsortLayout;
	delete mainradioLabel;
	//delete mainsortGroupBox;
	delete rankRadio;
	delete fnameRadio;
	delete outlinkRadio;
	delete inlinkRadio;
	delete webpageListWidget;
	//OTHERWINDOW
	//delete otherWindow;
	//delete otherLayout;
	delete outputbox;
	//delete alllinksLayout;
	delete othersortLayout;
	delete otherradioLabel;
	//delete othersortGroupBox;
	delete otherfnameRadio;
	delete otheroutlinkRadio;
	delete otherinlinkRadio;
	delete outlinksLayout;
	delete inlinksLayout;
	delete outlinksLabel;
	delete outlinksListWidget;
	delete inlinksLabel;
	delete inlinksListWidget;
	delete closeButtonLayout;
	delete closeButton;


}
void MainWindow::addWebpages() {
	//if form is empty
	if(searchInput->text().isEmpty())
	{
		return;
	}
	//get form values
	webpageListWidget->clear();
	std::string input = searchInput->text().toStdString();
	//check radiobuttons
	MySet<WebPage*> results;
	if (singleRadio->isChecked())
	{
		WebPage* temp_page;
		results = TSearch.WORD_parser(input);
		MySet<WebPage*> newword_set;
		set<WebPage*>::iterator webset_it;
		newword_set = results;
        for (webset_it = results.begin(); webset_it != results.end(); webset_it++)
        {
           temp_page = *webset_it;
           newword_set = newword_set.set_union(temp_page->outgoing_links());
           newword_set = newword_set.set_union(temp_page->incoming_links());
        }
        results = newword_set;
	}
	else if (andRadio->isChecked())
	{
		WebPage* temp_page;
		results = TSearch.AND_parser(input, 0);
		MySet<WebPage*> newand_set;
		set<WebPage*>::iterator webset_it;
		newand_set = results;
        for (webset_it = results.begin(); webset_it != results.end(); webset_it++)
        {
           temp_page = *webset_it;
           newand_set = newand_set.set_union(temp_page->outgoing_links());
           newand_set = newand_set.set_union(temp_page->incoming_links());
        }
        results = newand_set;
	}
	else if (orRadio->isChecked())
	{
		WebPage* temp_page;
		results = TSearch.OR_parser(input, 0);
		MySet<WebPage*> newor_set;
		set<WebPage*>::iterator webset_it;
		newor_set = results;
        for (webset_it = results.begin(); webset_it != results.end(); webset_it++)
        {
           temp_page = *webset_it;
           newor_set = newor_set.set_union(temp_page->outgoing_links());
           newor_set = newor_set.set_union(temp_page->incoming_links());
        }
        results = newor_set;
	}

	//initiate search based on radio buttons

	//takes webpages into vector

	WebpageVec.clear();
	set<WebPage*>::iterator it;
    for (it = results.begin(); it != results.end(); it++)
    {
    	WebpageVec.push_back(*it);
    }
    
	map<string, string>::iterator prob_it;
	map<string, string>::iterator t_it;
	prob_it = (_configmap).find("RESTART_PROBABILITY");
	t_it = (_configmap).find("STEP_NUMBER");
	double E = (double)atof((prob_it->second).c_str());
	int T = atoi((t_it->second).c_str());


	
	if (rankRadio->isChecked())
	{
		RankComp rankcomp;
		TSearch.pagerank(WebpageVec, E, T);
		mergeSort(WebpageVec, rankcomp);
	}
    
    //sort vector based on filename
	else if (fnameRadio->isChecked())
	{
		WebfnameComp fcomp;
		mergeSort(WebpageVec, fcomp);
	}
	else if (inlinkRadio->isChecked())
	{
		WebinlinksComp icomp;
		mergeSort(WebpageVec, icomp);
	}
	else if (outlinkRadio->isChecked())
	{
		WeboutlinksComp ocomp;
		mergeSort(WebpageVec, ocomp);
	}
	for (unsigned int i = 0; i < WebpageVec.size(); i++)
	{
		QString pageinfo = QString::fromStdString(WebpageVec[i]->filename());
		pageinfo += "	              ";
		pageinfo += QString::number((WebpageVec[i]->outgoing_links()).size());
		pageinfo += "	                      ";
		pageinfo += QString::number((WebpageVec[i]->incoming_links()).size());
		webpageListWidget->addItem(pageinfo);
	}
	//create a new list with webpage info sorted by radio box specification
	//clear form
	searchInput->setText("");
}
void MainWindow::sort() {
	webpageListWidget->clear();
	if (rankRadio->isChecked())
	{
		map<string, string>::iterator prob_it;
		map<string, string>::iterator t_it;
		prob_it = (_configmap).find("RESTART_PROBABILITY");
		t_it = (_configmap).find("STEP_NUMBER");
		double E = (double)atof((prob_it->second).c_str());
		int T = atoi((t_it->second).c_str());

		RankComp rankcomp;
		TSearch.pagerank(WebpageVec, E, T);
		mergeSort(WebpageVec, rankcomp);
	}
	else if (fnameRadio->isChecked())
	{
		WebfnameComp fcomp;
		mergeSort(WebpageVec, fcomp);
	}
	else if (inlinkRadio->isChecked())
	{
		WebinlinksComp icomp;
		mergeSort(WebpageVec, icomp);
	}
	else if (outlinkRadio->isChecked())
	{
		WeboutlinksComp ocomp;
		mergeSort(WebpageVec, ocomp);
	}
	for (unsigned int i = 0; i < WebpageVec.size(); i++)
	{
		QString pageinfo = QString::fromStdString(WebpageVec[i]->filename());
		pageinfo += "	              ";
		pageinfo += QString::number((WebpageVec[i]->outgoing_links()).size());
		pageinfo += "	                      ";
		pageinfo += QString::number((WebpageVec[i]->incoming_links()).size());
		webpageListWidget->addItem(pageinfo);
	}
}

void MainWindow::showpopup(int vec_index) {
	if (vec_index < 0)
	{
		return;
	}

	inlinksListWidget->clear();
	outlinksListWidget->clear();
	outlinkvec.clear();
	inlinkvec.clear();
	otherWindow->setWindowTitle(QString::fromStdString(WebpageVec[vec_index]->filename()));
	std::stringstream ss;
	ss << (*(WebpageVec[vec_index]));
	QString outputboxtext = QString::fromStdString(ss.str());
	outputbox->setText(outputboxtext);
	set<WebPage*>::iterator o_it;
	MySet<WebPage*> outlinkset = WebpageVec[vec_index]->outgoing_links();

	for (o_it = outlinkset.begin() ; o_it != outlinkset.end(); o_it++)
	{
		outlinkvec.push_back(*o_it);
	}
	set<WebPage*>::iterator i_it;
	MySet<WebPage*> inlinkset = WebpageVec[vec_index]->incoming_links();

	for (i_it = inlinkset.begin() ; i_it != inlinkset.end(); i_it++)
	{
		inlinkvec.push_back(*i_it);
	}
	for (unsigned int i = 0; i < outlinkvec.size(); i++)
	{
		QString pageoutlinks = QString::fromStdString(outlinkvec[i]->filename());
		outlinksListWidget->addItem(pageoutlinks);
	}
	for (unsigned int i = 0; i < inlinkvec.size(); i++)
	{
		QString pageinlinks = QString::fromStdString(inlinkvec[i]->filename());
		inlinksListWidget->addItem(pageinlinks);
	}

	otherWindow->show();
}

void MainWindow::hidepopup() {
	otherWindow->hide();
}
void MainWindow::sortOther() {
	outlinksListWidget->clear();
	inlinksListWidget->clear();
	WebfnameComp fcomp;
	WebinlinksComp icomp;
	WeboutlinksComp ocomp;
	if (otherfnameRadio->isChecked())
	{
		mergeSort(outlinkvec, fcomp);
		mergeSort(inlinkvec, fcomp);
	}
	else if (otherinlinkRadio->isChecked())
	{
		mergeSort(outlinkvec, icomp);
		mergeSort(inlinkvec, icomp);
	}
	else if (otheroutlinkRadio->isChecked())
	{
		mergeSort(outlinkvec, ocomp);
		mergeSort(inlinkvec, ocomp);
	}
	for (unsigned int i = 0; i < outlinkvec.size(); i++)
	{
		QString pageoutlinks = QString::fromStdString(outlinkvec[i]->filename());
		outlinksListWidget->addItem(pageoutlinks);
	}
	for (unsigned int i = 0; i < inlinkvec.size(); i++)
	{
		QString pageinlinks = QString::fromStdString(inlinkvec[i]->filename());
		inlinksListWidget->addItem(pageinlinks);
	}
}
void MainWindow::olinkchangepage(int o_index) {

	if (o_index < 0)
	{
		return;
	}
	otherWindow->setWindowTitle(QString::fromStdString(outlinkvec[o_index]->filename()));
	std::stringstream ss;
	ss << (*(outlinkvec[o_index]));
	QString outputboxtext = QString::fromStdString(ss.str());
	outputbox->setText(outputboxtext);
	
	set<WebPage*>::iterator o_it;
	MySet<WebPage*> outlinkset = outlinkvec[o_index]->outgoing_links();
	set<WebPage*>::iterator i_it;
	MySet<WebPage*> inlinkset = outlinkvec[o_index]->incoming_links();
	outlinkvec.clear();
	inlinkvec.clear();
	
	for (o_it = outlinkset.begin() ; o_it != outlinkset.end(); o_it++)
	{
		outlinkvec.push_back(*o_it);
	}
	for (i_it = inlinkset.begin() ; i_it != inlinkset.end(); i_it++)
	{
		inlinkvec.push_back(*i_it);
	}
	inlinksListWidget->clear();
	outlinksListWidget->clear();
	
	for (unsigned int i = 0; i < outlinkvec.size(); i++)
	{
		QString pageoutlinks = QString::fromStdString(outlinkvec[i]->filename());
		outlinksListWidget->addItem(pageoutlinks);
	}
	for (unsigned int i = 0; i < inlinkvec.size(); i++)
	{
		QString pageinlinks = QString::fromStdString(inlinkvec[i]->filename());
		inlinksListWidget->addItem(pageinlinks);
	}
}

void MainWindow::ilinkchangepage(int i_index) {

	if (i_index < 0)
	{
		return;
	}
	otherWindow->setWindowTitle(QString::fromStdString(inlinkvec[i_index]->filename()));
	std::stringstream ss;
	ss << (*(inlinkvec[i_index]));
	QString outputboxtext = QString::fromStdString(ss.str());
	outputbox->setText(outputboxtext);
	
	set<WebPage*>::iterator o_it;
	MySet<WebPage*> outlinkset = inlinkvec[i_index]->outgoing_links();
	set<WebPage*>::iterator i_it;
	MySet<WebPage*> inlinkset = inlinkvec[i_index]->incoming_links();
	outlinkvec.clear();
	inlinkvec.clear();
	
	for (o_it = outlinkset.begin() ; o_it != outlinkset.end(); o_it++)
	{
		outlinkvec.push_back(*o_it);
	}
	for (i_it = inlinkset.begin() ; i_it != inlinkset.end(); i_it++)
	{
		inlinkvec.push_back(*i_it);
	}
	inlinksListWidget->clear();
	outlinksListWidget->clear();
	for (unsigned int i = 0; i < outlinkvec.size(); i++)
	{
		QString pageoutlinks = QString::fromStdString(outlinkvec[i]->filename());
		outlinksListWidget->addItem(pageoutlinks);
	}
	for (unsigned int i = 0; i < inlinkvec.size(); i++)
	{
		QString pageinlinks = QString::fromStdString(inlinkvec[i]->filename());
		inlinksListWidget->addItem(pageinlinks);
	}
}

void MainWindow::config_parser(string config_file, map<string, string>& config_map)
{
  ifstream my_input(config_file.c_str());
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