#pragma once
#include "Math.h"
class Face;
class Vertex;
class Pair;

class SurfaceSimplifier {
public:
	SurfaceSimplifier();
	~SurfaceSimplifier();
	void SimplifyModel(const char* meshFile, int faceCount);
	bool LoadMesh(const char* meshFile);
	void InitializeStructures();
	void InitializeQ();
	void ComputePairError(std::vector<Pair*> pairs);
	void RemovePairs();
	void OutputToFile();
	void CreateBunnyFile();
	void CalculateNormals();
private:
	std::vector<Vector3> Indices;
	std::vector<Vertex*> Vertices;
	std::vector<Face*> Faces;
	std::vector<Pair*> Pairs;
	int currentFaceNum;
	int faceTarget;
};