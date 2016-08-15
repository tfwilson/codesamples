/*Write your own template implementation of the 
Merge Sort algorithm that works with any class T. 
Put your implementation in a file msort.h 
(that is, don't make a msort.cpp file). 
Your mergeSort() function should take some kind of list 
(choose either vector<>, list<>, or deque<> based on the needs of your project). 
Your mergeSort() function should also take a comparator object (i.e., functor) 
that has an operator() defined for it.*/
#ifndef MSORT_H
#define MSORT_H
#include "webpage.h"
#include <iostream>
#include <vector>
#include <cmath>
using namespace std;
struct WebfnameComp {
	bool operator()(const WebPage* lhs, const WebPage* rhs)
	{
		return lhs->filename() <= rhs->filename();
	}
};
struct  WeboutlinksComp {
	bool operator()(const WebPage* lhs, const WebPage* rhs)
	{
		MySet<WebPage*> lhsout = lhs->outgoing_links();
		MySet<WebPage*> rhsout = rhs->outgoing_links();
		return lhsout.size() <= rhsout.size();
	}
};
struct WebinlinksComp {
	bool operator()(const WebPage* lhs, const WebPage* rhs)
	{
		MySet<WebPage*> lhsin = lhs->incoming_links();
		MySet<WebPage*> rhsin = rhs->incoming_links();
		return lhsin.size() <= rhsin.size();
	}
};
struct RankComp {
	bool operator() (WebPage* lhs, WebPage* rhs)
	{
		double lhsrank = lhs->rank();
		double rhsrank = rhs->rank();
		return lhsrank <= rhsrank;
	}
};

template <class T, class Comparator>

  	void mergeSort(std::vector<T>& myVec, Comparator comp){
  		int size = myVec.size();
  		msorthelper(myVec, 0, size - 1, comp);
  	}

template <class T, class Comparator>
	void merge(std::vector<T>& myVec, int start, int mid, int end, Comparator comp)
	{
		std::vector<T> temp;
		int i = start, j = mid+1, k = 0;
		while (i <= mid || j <= end)
		{
			if (i <= mid && (j > end || (comp(myVec[i], myVec[j])))) {
				temp.push_back(myVec[i]); i++;
			}else {
				temp.push_back(myVec[j]); j++;
			}
		}

		for (k = 0; k < end+1-start; k++)
		{
			myVec[k+start] = temp[k];
		}
	}

template <class T, class Comparator>
	void msorthelper(std::vector<T>& myVec, int start, int end, Comparator comp)
	{
		if (start < end)
		{
			int mid = (start + end)/2;
			msorthelper(myVec, start, mid, comp);
			msorthelper(myVec, mid+1, end, comp);
			merge(myVec, start, mid, end, comp);
		}
	}
#endif
