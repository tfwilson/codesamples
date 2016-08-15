#include "webpage.h"
#include "searcheng.h"
#include "mdparser.h"
#include "msort.h"
#include <QWidget>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QGroupBox>
#include <QLabel>
#include <QLineEdit>
#include <QTextEdit>
#include <QPushButton>
#include <QListWidget>
#include <QRadioButton>
#include <QAbstractButton>
#include <QString>
#include <QMessageBox>
#include <QTextEdit>
#include <string>
#include <vector>


class MainWindow : public QWidget
{
	Q_OBJECT
public:
	MainWindow(std::string config_file);
	~MainWindow();
	void config_parser(std::string config_file, std::map<std::string, std::string>& config_map);

private slots:
	void addWebpages();
	void sort();
	void olinkchangepage(int o_index);
	void ilinkchangepage(int i_index);
	void showpopup(int vec_index);
	void hidepopup();
	void sortOther();

private:

	QVBoxLayout* overallLayout;
	QHBoxLayout* formLayout;
	QLabel* searchPromptLabel;
	QLineEdit* searchInput;
	QGroupBox* radiosearchGroupBox;
	QRadioButton* singleRadio;
	QRadioButton* andRadio;
	QRadioButton* orRadio;
	QHBoxLayout* radiosearchLayout;
	QHBoxLayout* searchButtonLayout;
	QPushButton* searchButton;
	QHBoxLayout* sortlabelLayout;
	QLabel* fnameLabel;
	QLabel* olinkLabel;
	QLabel* ilinkLabel;
	QHBoxLayout* resultLayout;
	QVBoxLayout* mainsortLayout;
	QLabel* mainradioLabel;
	QGroupBox* mainsortGroupBox;
	QRadioButton* rankRadio;
	QRadioButton* fnameRadio;
	QRadioButton* outlinkRadio;
	QRadioButton* inlinkRadio;
	QListWidget* webpageListWidget;
	QPushButton* maincloseButton;

	std::vector<WebPage*> WebpageVec;
	std::map<std::string, std::string> _configmap;
	std::string input_files;
	SearchEng TSearch;
	MarkDown parser;
	
	// other window
	QWidget* otherWindow;
	QTextEdit* outputbox;
	QVBoxLayout* otherLayout;
	QHBoxLayout* alllinksLayout;
	QVBoxLayout* othersortLayout;
	QLabel* otherradioLabel;
	QGroupBox* othersortGroupBox;
	QRadioButton* otherfnameRadio;
	QRadioButton* otheroutlinkRadio;
	QRadioButton* otherinlinkRadio;
	
	QVBoxLayout* outlinksLayout;
	QVBoxLayout* inlinksLayout;
	QLabel* outlinksLabel;
	QListWidget* outlinksListWidget;
	QLabel* inlinksLabel;
	QListWidget* inlinksListWidget;

	QHBoxLayout* closeButtonLayout;
	QPushButton* closeButton;
	std::vector<WebPage*> outlinkvec;
	std::vector<WebPage*> inlinkvec;
};