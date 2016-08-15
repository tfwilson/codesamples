#include "rbbst.h"
#include <iostream>
#include <string>
#include <fstream>
#include <map>
#include <set>

using namespace std;

void printadj(bool** array, int row, int col);
void printinput(char** array, int row, int col);
bool isValid(bool** adj, int row, RedBlackTree<char, int>& rbbst, char* list, int size, int color);
bool colorMapHelper(bool** adj, RedBlackTree<char,int>& rbbst, char* list, int size, int row);
void colorMap(bool** adj, RedBlackTree<char, int>& rbbst, char* list, int size);

int main(int argc, char* argv[]){
	ifstream my_input;
	my_input.open(argv[1]);
	if (!my_input) {
		cout << "Invalid file!" << endl;
		return -1;
	}
	int countries;
	int rows;
	int cols;
	my_input >> countries;
	my_input >> rows;
	my_input >> cols;
	if (my_input.fail()) {
		cout << "File formatted incorrectly" << endl;
		return -1;
	}
	char** inputarray = new char*[rows]; // to delete
	for (int i = 0; i < rows; i++) {
		inputarray[i] = new char[cols];
	}
	char ch;
	set<char> countryset;
	
	for (int i = 0; i < rows; i++) { //make array from input
		for (int j = 0; j < cols; j++) {
			my_input >> ch;
			countryset.insert(ch);
			inputarray[i][j] = ch;
		}
	}
	
	bool** adjmatrix = new bool*[countries]; //adj matrix to delete
	for (int i = 0; i < countries; i++) {
		adjmatrix[i] = new bool[countries];
	}

	for (int i = 0; i < countries; i++) { //initialize false
		for (int j = 0; j < countries; j++) {
			adjmatrix[i][j] = false;
		}
	}

	set<char>::iterator it;
	int counter = 0;
	char* list = new char[countries]; //make list to delete
	for (it = countryset.begin(); it != countryset.end(); it++) {
		char name = *it;
		list[counter] = name;
		counter++;
	}

	int home;
	int away;
	for (int i = 0; i < rows; i++) { //fill in adj matrix
		for (int j = 0; j < cols; j++) {
			char value = inputarray[i][j];
			if (i != 0) {
				if (value != inputarray[i-1][j]) { // check below
					for (int k = 0; k < countries; k++) {
						if (list[k] == value) {
							home = k;
						}
						if (list[k] == inputarray[i-1][j]) {
							away = k;
						}
					}
					adjmatrix[home][away] = true;
					adjmatrix[away][home] = true;
				}
			}
			if (i != rows-1) {
				if (value != inputarray[i+1][j]) { //check above
					for (int k = 0; k < countries; k++) {
						if (list[k] == value) {
							home = k;
						}
						if (list[k] == inputarray[i+1][j]) {
							away = k;
						}
					}
					adjmatrix[home][away] = true;
					adjmatrix[away][home] = true;
				}
			}
			if (j != 0) {
				if (value != inputarray[i][j-1]) { //check left
					for (int k = 0; k < countries; k++) {
						if (list[k] == value) {
							home = k;
						}
						if (list[k] == inputarray[i][j-1]) {
							away = k;
						}
					}
					adjmatrix[home][away] = true;
					adjmatrix[away][home] = true;
				}
			}
			if (j != cols-1) {
				if (value != inputarray[i][j+1]) { //check right
					for (int k = 0; k < countries; k++) {
						if (list[k] == value) {
							home = k;
						}
						if (list[k] == inputarray[i][j+1]) {
							away = k;
						}
					}
					adjmatrix[home][away] = true;
					adjmatrix[away][home] = true;
				}
			}
		}
	}
	//printadj(adjmatrix, countries, countries);
	//printinput(inputarray, rows, cols);
	RedBlackTree<char, int> rbbst;

	for (it = countryset.begin(); it != countryset.end(); it++) { // initialize colors
		char name = *it;
		rbbst.insert(std::make_pair<const char, int>(name, 0));
	}
	//rbbst.rbbstprint();
	colorMap(adjmatrix, rbbst, list, countries); //begin backtracking

	RedBlackTree<char, int>::iterator mapit(NULL); //output colors
	for (mapit = rbbst.begin(); mapit != rbbst.end(); ++mapit)
	{
		std::cout << mapit->first << " " << mapit->second << std::endl;
	}
	
	for (int i = 0; i < rows; i++)  //deletes
	{
    	delete [] inputarray[i];
 	}
	delete [] inputarray;

	for (int i = 0; i < countries; i++) 
	{
    	delete [] adjmatrix[i];
 	}
	delete [] adjmatrix;

	delete [] list;

	return 0;
}

void printadj(bool** array, int row, int col)
{
	for (int i = 0; i < row; i++) {
		for (int j = 0 ; j < col; j++) {
			cout << array[i][j];
		}
		cout << endl;
	}
}
void printinput(char** array, int row, int col)
{
	for (int i = 0; i < row; i++) {
		for (int j = 0 ; j < col; j++) {
			cout << array[i][j];
		}
		cout << endl;
	}	
}

bool isValid(bool** adj, int row, RedBlackTree<char, int>& rbbst, char* list, int size, int color)
{
	int othercolor;
	for (int j = 0; j < size; j++) { // go through countries
		othercolor = rbbst.find(list[j])->second; // get other color
		if (adj[row][j] == true && color == othercolor) { //if border and color is same
			return false;
		}
	}
	return true;
}
// 
bool colorMapHelper(bool** adj, RedBlackTree<char,int>& rbbst, char* list, int size, int row) 
{
	char ch = list[row];
	int nextrow;
	for (int i = 1; i < 5; i++) { //for each color
		if (isValid(adj, row , rbbst, list, size, i)) {
			rbbst.insert(std::make_pair<const char, int>(ch, i)); //if ok, insert
			nextrow = row;
			nextrow++;
			if (nextrow == size) { //at end
				return true;
			}
			if (colorMapHelper(adj, rbbst, list, size, nextrow)) {
				return true;
			}
		}
	}
	rbbst.insert(std::make_pair<const char, int>(ch, 0)); //reset
	return false;	
}

void colorMap(bool** adj, RedBlackTree<char, int>& rbbst, char* list, int size)
{
	colorMapHelper(adj, rbbst, list, size, 0);
}