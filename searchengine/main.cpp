#include "main_window.h"
#include <iostream>
#include <QApplication>

using namespace std;
int main(int argc, char* argv[])
{
	QApplication app(argc, argv);
  	if(argc < 2){
    	cerr << "usage: ./search index_file...Please provide an index file" << endl;
    return 1;
  	}
	MainWindow mainWindow(argv[1]);

	mainWindow.show();

	return app.exec();
}