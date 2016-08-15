#ifndef MySet_H
#define MySet_H

#include <set>
#include <string>

// avoid circular reference
class WebPage;

template <typename T>
class MySet : public std::set<T> 
{
public:
  MySet();
  MySet<T> set_intersection(const MySet<T>& other);
  MySet<T> set_union(const MySet<T>& other);
};

template <typename T>
MySet<T>::MySet() : std::set<T>() {}

template <typename T>
MySet<T> MySet<T>::set_intersection(const MySet<T>& other) {
	MySet<T> newSet;
	typename MySet<T>::iterator other_it;
	typename MySet<T>::iterator this_it;
	for (other_it = other.begin(); other_it != other.end() ; other_it++)
	{
		T n = *other_it;
		for (this_it = this->begin(); this_it != this->end() ; this_it++)
		{
			T m = *this_it;
			if (n == m) 
			{
				newSet.insert(n);
			}
		} 
	}
	return newSet;

}
template <typename T>
MySet<T> MySet<T>::set_union(const MySet<T>& other) {
	typename MySet<T>::iterator other_it;
	for (other_it = other.begin(); other_it != other.end() ; other_it++)
	{
		T n = *other_it;
		this->insert(n);
	}
	return *this;
}

#endif
