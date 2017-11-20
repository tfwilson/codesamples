#include "ITPEnginePCH.h"

Matrix4 Face::ComputeK() {
	float mat[4][4] =
	{
		{ a*a, a*b, a*c, a*d },
		{ a*b, b*b, b*c, b*d },
		{ a*c, b*c, c*c, c*d },
		{ a*d, b*d, c*d, d*d }
	};
	return Matrix4(mat);
}