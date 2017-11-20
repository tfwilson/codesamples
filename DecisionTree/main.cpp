
#include <iostream>
#include <string>
#include <cstring>
#include <fstream>
#include <vector>
#include <ctime>
#include <cstdlib>
#include <math.h>
#include <map>

struct Example {
	int classification;
	std::map<std::string, char> attributes;
};

struct Attribute {
	std::string name;
	std::vector<char> types;
	int index;
};

struct Node {
	std::vector<Node*> children;
	std::vector<char> branches;
	Attribute attribute;
	int classification = -1;
};

float Classify(std::vector<Example>& testSet, std::vector<Node*>& nodes, std::vector<Attribute>& attributes);
int PluralityValue(std::vector<Example>& parentExamples);
Node* DecisionTreeLearning(std::vector<Example>& examples, std::vector<Example>& parentExamples, std::vector<Attribute>& attributes, std::vector<Node*>& nodes);
Node* BoundedDecisionTreeLearning(std::vector<Example>& examples, std::vector<Example>& parentExamples, std::vector<Attribute>& attributes, std::vector<Node*>& nodes, int currentDepth, int depthBond);
int MostImportantAttribute(std::vector<Example>& examples, std::vector<Attribute>& attributes);
float CalculateMaxImportance(int attribute, std::vector<Example>& examples, std::vector<Attribute>& attributes);
float B(float q);

int main(int argc, char* argv[]) {

	std::string className;
	std::vector<Attribute> attributes;
	std::vector<Example> examples;
	std::vector<Node*> nodes;

	//parse input file
	std::ifstream dataset(argv[1]);
	if (dataset.is_open()) {
		std::string line;
		bool isFirstLine = true;

		while (getline(dataset, line)) {
			if (isFirstLine) {
				bool isFirstWord = true;
				std::string s = "";
				for (unsigned i = 0; i <= line.length(); i++) {
					if (line[i] == ',' || i == line.length()) {
						if (isFirstWord) {
							className = s;
						}
						else {
							Attribute a;
							a.name = s;
							a.index = attributes.size();
							attributes.push_back(a);
						}
						s = "";
						isFirstWord = false;
					}
					else {
						s += line[i];
					}
				}
				isFirstLine = false;
			}
			else {
				Example e;
				e.classification = line[0] - '0';
				int a = 0;
				for (unsigned i = 1; i < line.length(); i++) {
					if (line[i] != ',') {
						e.attributes.insert(std::pair<std::string, char>(attributes[a].name, line[i]));
						a++;
					}
				}
				examples.push_back(e);
			}
		}
	}
	else {
		std::cout << "Unable to open file." << std::endl;
		return -1;
	}
	dataset.close();

	// sort into random sets (80 in one, 20 in other)
	std::vector<Example> treeSet;
	std::vector<Example> testSet;
	std::srand(time(0));
	for (unsigned i = 0; i < examples.size(); i++) {
		int r = rand() % examples.size();
		Example temp = examples[i]; examples[i] = examples[r]; examples[r] = temp;
	}

	for (unsigned i = 0; i < examples.size(); i++) {
		if (i < examples.size() * 0.8) {
			treeSet.push_back(examples[i]);
		}
		else {
			testSet.push_back(examples[i]);
		}
	}

	for (unsigned a = 0; a < attributes.size(); a++) {
		//determine all possible types
		std::string name = attributes[a].name;
		for (unsigned i = 0; i < examples.size(); i++) {
			//analyze attribute
			//if stored, increment
			//else append and increment
			bool found = false;
			for (unsigned j = 0; j < attributes[a].types.size(); j++) {
				if (examples[i].attributes[name] == attributes[a].types[j]) {
					found = true;
				}
			}
			if (!found) {
				attributes[a].types.push_back(examples[i].attributes[name]);
			}
		}
	}

	//build decision tree (praent_examples?)
	std::vector<Attribute> tempAttributes = attributes;
	DecisionTreeLearning(treeSet, treeSet, tempAttributes, nodes);

	tempAttributes.clear();
	std::cout << "---------Part1---------" << std::endl;
	//std::cout << "Number of nodes in tree: " << nodes.size() << std::endl;
	std::cout << "Training: " << Classify(treeSet, nodes, attributes) << std::endl;
	std::cout << "Testing: " << Classify(testSet, nodes, attributes) << std::endl;

	// sort into random sets (60 training, 20 validation, 20 testing)
	treeSet.clear();
	testSet.clear();
	std::vector<Example> validationSet;

	for (unsigned i = 0; i < examples.size(); i++) {
		int r = rand() % examples.size();
		Example temp = examples[i]; examples[i] = examples[r]; examples[r] = temp;
	}

	//create three sets
	for (unsigned i = 0; i < examples.size(); i++) {
		if (i < examples.size() * 0.6) {
			treeSet.push_back(examples[i]);
		}
		else if (i < examples.size() * .8) {
			validationSet.push_back(examples[i]);
		}
		else {
			testSet.push_back(examples[i]);
		}
	}
	std::cout << "---------Part2---------" << std::endl;
	int bestDepth = -1;
	float highestPercent = -1;
	for (unsigned i = 1; i < 16; i++) {
		tempAttributes = attributes;
		nodes.clear();
		BoundedDecisionTreeLearning(treeSet, treeSet, tempAttributes, nodes, 0, i);
		//std::cout << "Number of nodes in tree: " << nodes.size() << std::endl;
		float trainingPercent = Classify(treeSet, nodes, attributes);
		float validationPercent = Classify(validationSet, nodes, attributes);
		std::cout <<"Depth       Training      Validation" << std::endl;
		std::cout <<"  " << i << "          " << trainingPercent << "      " << validationPercent << std::endl;
		if (validationPercent > highestPercent) {
			highestPercent = validationPercent;
			bestDepth = i;
		}
	}
	std::cout << "Optimal depth: " << bestDepth  << std::endl;
	for (unsigned i = 0; i < validationSet.size(); i++) {
		treeSet.push_back(validationSet[i]);
	}
	tempAttributes = attributes;
	nodes.clear();
	BoundedDecisionTreeLearning(treeSet, treeSet, tempAttributes, nodes, 0, bestDepth);
	std::cout << "Optimal depth accuracy: " << Classify(testSet, nodes, attributes) << std::endl;
	
	//cleanup
	for (unsigned i = 0; i < nodes.size(); i++) {
		delete nodes[i];
	}
	nodes.clear();
	return 0;
}

Node* DecisionTreeLearning(std::vector<Example>& examples, std::vector<Example>& parentExamples, std::vector<Attribute>& attributes, std::vector<Node*>& nodes) {
	if (examples.empty()) {
		//std::cout << "1" << std::endl;
		Node* newNode = new Node();
		nodes.push_back(newNode);
		newNode->classification = PluralityValue(parentExamples);
		return newNode;
	}
	// check if all examples have same classification
	bool allSame = true;
	int c = examples[0].classification;
	for (unsigned i = 1; i < examples.size(); i++) {
		if (c != examples[i].classification) {
			allSame = false;
			break;
		}
	}
	if (allSame) {
		//std::cout << "2" << std::endl;
		Node* newNode = new Node();
		nodes.push_back(newNode);
		newNode->classification = c;
		return newNode;
	}
	else if (attributes.empty()) {
		//std::cout << "3: " << examples.size() << std::endl;
		Node* newNode = new Node();
		nodes.push_back(newNode);
		newNode->classification = PluralityValue(examples);
		return newNode;
	}
	else {
		//Calculate importance
		int a = MostImportantAttribute(examples, attributes);
		Node* newNode = new Node();
		newNode->attribute = attributes[a];
		nodes.push_back(newNode);
		for (unsigned i = 0; i < newNode->attribute.types.size(); i++) {
			std::vector<Example> exs;
			std::string name = newNode->attribute.name;
			for (unsigned j = 0; j < examples.size(); j++) {
				if (examples[j].attributes[name] == newNode->attribute.types[i]) {
					exs.push_back(examples[j]);
					//std::cout << "Attribute: " << newNode->attribute.name << " Type: " << newNode->attribute.types[i] <<std::endl;
				}
			}

			std::vector<Attribute> attributesCopy = attributes;
			if (!attributesCopy.empty()) {
				attributesCopy.erase(attributesCopy.begin() + a);
			}
			newNode->children.push_back(DecisionTreeLearning(exs, examples, attributesCopy, nodes));
			newNode->branches.push_back(newNode->attribute.types[i]);
		}
		//std::cout << "Attribute: " << newNode->attribute.name << std::endl;
		return newNode;
	}
}

int PluralityValue(std::vector<Example>& parentExamples) {
	int p = 0;
	int n = 0;
	for (unsigned i = 0; i < parentExamples.size(); i++) {
		if (parentExamples[i].classification == 0) {
			n++;
		}
		else {
			p++;
		}
	}
	if (n > p) {
		return 0;
	}
	else if (p > n) {
		return 1;
	}
	else {
		return rand() % 2;
	}
}

float CalculateMaxImportance(int attribute, std::vector<Example>& examples, std::vector<Attribute>& attributes) {
	std::vector<int> trueTally;
	std::vector<int> falseTally;
	int totalTrue = 0;
	for (unsigned i = 0; i < attributes[attribute].types.size(); i++) {
		trueTally.push_back(0);
		falseTally.push_back(0);
	}
	//loop through examples
	std::string name = attributes[attribute].name;
	for (unsigned i = 0; i < examples.size(); i++) {
		for (unsigned j = 0; j < attributes[attribute].types.size(); j++) {
			if (examples[i].attributes[name] == attributes[attribute].types[j]) {
				if (examples[i].classification == 0) {
					falseTally[j]++;
				}
				else {
					trueTally[j]++;
					totalTrue++;
				}
			}
		}
	}

	//calculate importance
	float gain = (float)totalTrue / (float)examples.size();
	gain = B(gain);
	float summation = 0;
	for (unsigned i = 0; i < attributes[attribute].types.size(); i++) {
		int count = falseTally[i] + trueTally[i];
		if (count != 0) {
			gain -= ((float)count / (float)examples.size()) * B((float)trueTally[i] / (float)count);
		}
	}
	return gain;
}

int MostImportantAttribute(std::vector<Example>& examples, std::vector<Attribute>& attributes) {
	float max = -1;
	int a = -1;
	for (unsigned attribute = 0; attribute < attributes.size(); attribute++) {
		float importance = CalculateMaxImportance(attribute, examples, attributes);
		//std::cout << "Attribute : " << attributes[attribute].name << " Importance: " << importance << " Num attributes: " << attributes.size() << std::endl;
		if (importance >= max) {
			max = importance;
			a = attribute;
		}
	}
	//std::cout << "*****Most important: " << attributes[a].name << std::endl;
	return a;
}

float B(float q) {
	if (q == 1 || q == 0) {
		return 0;
	}
	return -1 * (q * log2(q) + (1 - q) * log2(1 - q));
}

float Classify(std::vector<Example>& testSet, std::vector<Node*>& nodes, std::vector<Attribute>& attributes) {
	int successes = 0;
	//run each example in test set through tree
	for (unsigned i = 0; i < testSet.size(); i++) {
		//std::cout << "New" << std::endl;
		Node* node = nodes[0];
		while (node->classification == -1) {
			//std::cout << "Attribute: " << node->attribute.name << " ";
			int a = node->attribute.index;
			std::string name = attributes[a].name;
			//std::cout << "Type: " << testSet[i].attributes[a] << " Branches: ";
			//std::cout << "Children: " << node->children.size() << std::endl;
			for (unsigned j = 0; j < node->branches.size(); j++) {
				//std::cout << "Test type: " << testSet[i].attributes[a] << std::endl;
				//std::cout << node->branches[j] << " ";

				if (testSet[i].attributes[name] == node->branches[j]) {
					node = node->children[j];
					break;
				}
			}
			//std::cout << std::endl;
		}
		if (testSet[i].classification == node->classification) {
			successes++;
			//std::cout << "Success" << std::endl;
		}
	}
	return (float)successes / testSet.size();
}

Node* BoundedDecisionTreeLearning(std::vector<Example>& examples, std::vector<Example>& parentExamples,
	std::vector<Attribute>& attributes, std::vector<Node*>& nodes,
	int currentDepth, int depthBound) {
	
	if (examples.empty()) {
		//std::cout << "1" << std::endl;
		Node* newNode = new Node();
		nodes.push_back(newNode);
		newNode->classification = PluralityValue(parentExamples);
		return newNode;
	}
	// check if all examples have same classification
	bool allSame = true;
	int c = examples[0].classification;
	for (unsigned i = 1; i < examples.size(); i++) {
		if (c != examples[i].classification) {
			allSame = false;
			break;
		}
	}
	if (allSame) {
		//std::cout << "2" << std::endl;
		Node* newNode = new Node();
		nodes.push_back(newNode);
		newNode->classification = c;
		return newNode;
	}
	else if (attributes.empty()) {
		//std::cout << "3: " << examples.size() << std::endl;
		Node* newNode = new Node();
		nodes.push_back(newNode);
		newNode->classification = PluralityValue(examples);
		return newNode;
	}
	else if (currentDepth == depthBound) {
		Node* newNode = new Node();
		nodes.push_back(newNode);
		newNode->classification = PluralityValue(examples);
		return newNode;
	}
	else {
		//Calculate importance
		int a = MostImportantAttribute(examples, attributes);
		Node* newNode = new Node();
		newNode->attribute = attributes[a];
		currentDepth++;
		nodes.push_back(newNode);
		for (unsigned i = 0; i < newNode->attribute.types.size(); i++) {
			std::vector<Example> exs;
			std::string name = newNode->attribute.name;
			for (unsigned j = 0; j < examples.size(); j++) {
				if (examples[j].attributes[name] == newNode->attribute.types[i]) {
					exs.push_back(examples[j]);
					//std::cout << "Attribute: " << newNode->attribute.name << " Type: " << newNode->attribute.types[i] <<std::endl;
				}
			}

			std::vector<Attribute> attributesCopy = attributes;
			if (!attributesCopy.empty()) {
				attributesCopy.erase(attributesCopy.begin() + a);
			}
			newNode->children.push_back(BoundedDecisionTreeLearning(exs, examples, attributesCopy,
				nodes, currentDepth, depthBound));
			newNode->branches.push_back(newNode->attribute.types[i]);
		}
		//std::cout << "Attribute: " << newNode->attribute.name << std::endl;
		return newNode;
	}
}