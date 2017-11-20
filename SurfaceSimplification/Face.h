#pragma once
class Vertex;
class Face {
public:
	Face(Vertex* v1, Vertex* v2, Vertex* v3)
		:v1(v1)
		,v2(v2)
		,v3(v3)
		,isNull(false)
	{}
	void SetEquation(float a, float b, float c, float d) { this->a = a; this->b = b; this->c = c; this->d = d; }
	Vertex*& GetV1() {return v1;}
	Vertex*& GetV2() {return v2;}
	Vertex*& GetV3() {return v3;}
	bool IsNull() { return isNull; }
	void SetNull() { isNull = true; }
	Matrix4 ComputeK();
	~Face()
	{}
private:
	bool isNull;
	float a;
	float b;
	float c;
	float d;
	Vertex* v1;
	Vertex* v2;
	Vertex* v3;
};