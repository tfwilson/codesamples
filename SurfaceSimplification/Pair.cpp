#include "ITPEnginePCH.h"

void Pair::UpdateQ() {
	combinedQ = v1->GetQ() + v2->GetQ();
}
void Pair::UpdateDestinationVertex() {
	float tempQ[4][4] =
	{
		{ combinedQ.mat[0][0], combinedQ.mat[0][1], combinedQ.mat[0][2], combinedQ.mat[0][3] },
		{ combinedQ.mat[1][0], combinedQ.mat[1][1], combinedQ.mat[1][2], combinedQ.mat[1][3] },
		{ combinedQ.mat[2][0], combinedQ.mat[2][1], combinedQ.mat[2][2], combinedQ.mat[2][3] },
		{ 0, 0, 0, 1 }
	};
	Matrix4 derivedMatrix(tempQ);
	//check if matrix can be inverted
	if (abs(derivedMatrix.Determinant()) > FLT_EPSILON) {
		derivedMatrix.Invert();
		destination.Set(derivedMatrix.mat[0][3], derivedMatrix.mat[1][3], derivedMatrix.mat[2][3]);
	}
	else {
		destination = (v1->GetPosition() + v2->GetPosition()) / 2;
	}

}
void Pair::ComputeError() {
	error = (combinedQ.mat[0][0] * pow(destination.x, 2)) + (2 * combinedQ.mat[0][1] * destination.x * destination.y) + (2 * combinedQ.mat[0][2] * destination.x * destination.z)
		+ (2 * combinedQ.mat[0][3] * destination.x) + (combinedQ.mat[1][1] * pow(destination.y, 2)) + (2 * combinedQ.mat[1][2] * destination.y * destination.z)
		+ (2 * combinedQ.mat[1][3] * destination.y) + (combinedQ.mat[2][2] * pow(destination.z, 2)) + (2 * combinedQ.mat[2][3] * destination.z) + (combinedQ.mat[3][3]);
	error = abs(error);
}
