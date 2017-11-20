#pragma once
#include <vector>

class Vertex;
class Face;
class Quadric;

class Pair {
public:
	Pair(Vertex* v1, Vertex* v2)
		:v1(v1)
		,v2(v2)
		,isNull(false)
	{
		float mat[4][4] =
		{
			{ 0, 0, 0, 0 },
			{ 0, 0, 0, 0 },
			{ 0, 0, 0, 0 },
			{ 0, 0, 0, 0 }
		};
		combinedQ = Matrix4(mat);
	}
	~Pair() 
	{}
	int GetID() { return ID;}
	void SetID(int identifier) { ID = identifier; }
	Vertex*& GetV1() { return v1; }
	Vertex*& GetV2() { return v2; }
	void SetV1(Vertex* newv1) { v1 = newv1; }
	void SetV2(Vertex* newv2) { v2 = newv2; }
	bool IsNull() { return isNull; }
	void SetNull() { isNull = true; }
	void AddSharedFace(Face* face) { deletableFaces.emplace_back(face); }
	float GetError() { return error; }
	Vector3 GetDestination() { return destination; }
	Matrix4 GetPairQ() { return combinedQ; }
	std::vector<Face*>& GetDeletableFaces() { return deletableFaces; }
	void UpdateQ();
	void UpdateDestinationVertex();
	void ComputeError();

	//friend bool operator == (Pair const& lhs, Pair const& rhs);
	bool operator == (Pair const& rhs)
	{
		return ((this->v1 == rhs.v1 && this->v2 == rhs.v2) || (this->v1 == rhs.v2 && this->v2 == rhs.v1));
	}
private:
	int ID;
	bool isNull;
	float error;
	Matrix4 combinedQ;
	Vertex* v1;
	Vertex* v2;
	Vector3 destination;
	std::vector<Face*> deletableFaces;
	//std::vector<Face*> keepableFaces;
};
//bool operator == (Pair const& lhs, Pair const& rhs)
//{
//	return ((lhs.v1 == rhs.v1 && lhs.v2 == rhs.v2) || (lhs.v1 == rhs.v2 && lhs.v2 == rhs.v1));
//}
