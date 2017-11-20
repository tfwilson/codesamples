#pragma once
#include "Math.h"
#include <vector>

class Face;
class Pair;
class Vertex {
public:
	Vertex(Vector3 pos, Vector3 nor, Vector2 tex, int id)
		:position(pos)
		,normal(nor)
		,texcoord(tex)
		,identifier(id)
		,isNull(false)
	{	
		float mat[4][4] =
		{
			{ 0, 0, 0, 0 },
			{ 0, 0, 0, 0 },
			{ 0, 0, 0, 0 },
			{ 0, 0, 0, 0 }
		};
		Q = Matrix4(mat);
	}
	~Vertex() 
	{
	}
	void AddFace(Face* face) { faces.emplace_back(face); }
	void AddPair(Pair* pair) { pairs.emplace_back(pair); }
	//friend bool operator == (Vertex const& lhs, Vertex const& rhs);
	bool operator == (Vertex const& rhs){return (this->identifier == rhs.identifier);}
	Vector3 GetPosition() { return position; }
	Vector3 GetNormal() { return normal; }
	Vector2 GetTexcoord() { return texcoord; }
	void SetTexcoord(float x, float y) { texcoord.x = x; texcoord.y = y; }
	void SetNormal(Vector3 newnormal) { normal = newnormal; }
	int GetID() { return identifier; }
	void SetPosition(Vector3 pos) { position = pos; }
	const std::vector<Face*>& GetFaces() { return faces; }
	const std::vector <Pair*>& GetPairs() { return pairs; }
	Matrix4 GetQ() {return Q;}
	void SetQ(Matrix4 newQ) { Q = newQ; }
	void AddQ(Matrix4 K);
	bool IsNull() { return isNull; }
	void SetNull(bool isNull) { this->isNull = isNull; }
	bool CheckForPair(Vertex* otherVertex, Face* sharedFace);
	void ComparePair(Pair* otherPair, int originalSize, Vertex* otherVertex);
	void RemovePair(Pair* minPair);
private:
	bool isNull;
	int identifier;
	Vector3 position;
	Vector3 normal;
	Vector2 texcoord;
	Matrix4 Q;
	std::vector<Pair*> pairs;
	std::vector<Face*> faces;
};
//bool operator == (Vertex const& lhs, Vertex const& rhs)
//{
//	return (lhs.identifier == rhs.identifier);
//}