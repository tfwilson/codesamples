#include "ITPEnginePCH.h"
#include <algorithm>  
bool Vertex::CheckForPair(Vertex* otherVertex, Face* sharedFace) {
	for (auto& it : pairs) {
		if (it->GetV1()->GetID() == otherVertex->GetID() || it->GetV2()->GetID() == otherVertex->GetID()) { //identifiers
			it->AddSharedFace(sharedFace);
			return true;
		}
	}
	return false;
}

void Vertex::AddQ(Matrix4 K) {
	Q = Q + K;
}

void Vertex::ComparePair(Pair* otherPair, int originalSize, Vertex* otherVertex) {
	bool containsPair = false;
	for (int i = 0; i < originalSize && !containsPair; i++) {
		Pair* pair = pairs[i];
		if (!pair->IsNull()) {
			if ((pair->GetV1()->GetID() == otherPair->GetV1()->GetID()) || (pair->GetV1()->GetID() == otherPair->GetV2()->GetID()) || 
				(pair->GetV2()->GetID() == otherPair->GetV1()->GetID()) || (pair->GetV2()->GetID() == otherPair->GetV2()->GetID())) {
				//they share a vertex
				containsPair = true;
				std::vector<Face*> deletableFaces = otherPair->GetDeletableFaces();
				for (int j = 0; j < deletableFaces.size(); j++) {
					if (!deletableFaces[j]->IsNull()) {
						//if face is not in pair deletable faces, add it
						pair->AddSharedFace(deletableFaces[j]);
					}
				}
				//only want one reference to same pair
				otherPair->SetNull();
			}
		}
	}
	if (!containsPair) {
		this->AddPair(otherPair);
	}
}

void Vertex::RemovePair(Pair* minPair) {
	pairs.erase(std::remove(pairs.begin(), pairs.end(), minPair), pairs.end());
}