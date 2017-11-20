#include "ITPEnginePCH.h"
#include "DbgAssert.h"
#include <algorithm>    // std::find, remove
#include <iostream>
#include <fstream>
#include <iomanip>
#if _WIN32 && _DEBUG
#define SIMPLIFY_DEBUG
#elif DEBUG
#define SIMPLIFY_DEBUG
#endif

SurfaceSimplifier::SurfaceSimplifier() {

}
SurfaceSimplifier::~SurfaceSimplifier() {
	for (int i = 0; i < Faces.size(); i++) {
		if (Faces[i] != nullptr) {
			delete Faces[i];
			Faces[i] = nullptr;
		}
	}
	Faces.clear();
	for (int i = 0; i < Vertices.size(); i++) {
		if (Vertices[i]->IsNull() == false) {
			Vertices[i]->SetNull(true);
			delete Vertices[i];
			Vertices[i] = nullptr;
		}
	}
	Vertices.clear();
	for (int i = 0; i < Pairs.size(); i++) {
		if (Pairs[i] != nullptr) {
			delete Pairs[i];
			Pairs[i] = nullptr;
		}
	}
	Pairs.clear();

}

void SurfaceSimplifier::SimplifyModel(const char* meshFile, int faceCount) {
	faceTarget = faceCount;
	//CreateBunnyFile();
	bool loadSuccess = LoadMesh(meshFile);
#ifdef SIMPLIFY_DEBUG
	DbgAssert(loadSuccess, "There was an issue loading the mesh file");
#endif
	InitializeStructures();
	CalculateNormals();
	InitializeQ();
	ComputePairError(Pairs);
	RemovePairs();
	OutputToFile();
}

bool SurfaceSimplifier::LoadMesh(const char* meshFile) {
	std::ifstream file(meshFile);

	if (!file.is_open())
	{
		SDL_Log("Mesh file %s not found", meshFile);
		return false;
	}

	std::stringstream fileStream;
	fileStream << file.rdbuf();
	std::string contents = fileStream.str();
	rapidjson::StringStream jsonStr(contents.c_str());
	rapidjson::Document doc;
	doc.ParseStream(jsonStr);
	if (!doc.IsObject())
	{
		SDL_Log("Mesh %s is not valid json", meshFile);
		return false;
	}
	//get vertices
	const rapidjson::Value& vertsJson = doc["vertices"];
	if (!vertsJson.IsArray() || vertsJson.Size() < 1)
	{
		SDL_Log("Mesh %s has no vertices", meshFile);
		return false;
	}

	size_t numVerts = vertsJson.Size();
	for (rapidjson::SizeType i = 0; i < vertsJson.Size(); i++)
	{
		const rapidjson::Value& vert = vertsJson[i];
		if (!vert.IsArray())
		{
			SDL_Log("Unexpected vertex format for %s", meshFile);
			return false;
		}
		Vector3 pos(vert[0].GetDouble(), vert[1].GetDouble(), vert[2].GetDouble());
		Vector3 normal(vert[3].GetDouble(), vert[4].GetDouble(), vert[5].GetDouble());
		Vector2 texcoord(vert[6].GetDouble(), vert[7].GetDouble());
		Vertex* newVertex = new Vertex(pos, normal, texcoord, i);
		Vertices.emplace_back(newVertex);
	}
	// get indices
	const rapidjson::Value& indicesJson = doc["indices"];
	if (!indicesJson.IsArray() || indicesJson.Size() < 1)
	{
		SDL_Log("Mesh %s has no indices", meshFile);
		return false;
	}
	size_t numFaces = indicesJson.Size();
	currentFaceNum = numFaces;
	for (rapidjson::SizeType i = 0; i < indicesJson.Size(); i++)
	{
		const rapidjson::Value& triangle = indicesJson[i];
		if (!triangle.IsArray() || triangle.Size() != 3)
		{
			SDL_Log("Unexpected vertex format for %s", meshFile);
			return false;
		}
		Vector3 face(triangle[0].GetInt(), triangle[1].GetInt(), triangle[2].GetInt());
		Indices.emplace_back(face);
	}
	return true;
}
//have list of vertices and indices
//for every vertex, compare to every other vertex, if that vertex is the same, chnage to tht vertex, then refill indices

void SurfaceSimplifier::InitializeStructures() {
	for (int i = 0; i < Vertices.size(); i++) {
		for (int j = 0; j < Vertices.size(); j++) {
			//threshold for combining double vertices
			if (i != j && (Vertices[i]->GetPosition() - Vertices[j]->GetPosition()).Length() < 0.0005f) {
				int oldID = Vertices[i]->GetID();
				int newID = Vertices[j]->GetID();
				for (int k = 0; k < Indices.size(); k++) {
					if (Indices[k].x == oldID) {
						Indices[k].x = newID;
					}
					if (Indices[k].y == oldID) {
						Indices[k].y = newID;
					}
					if (Indices[k].z == oldID) {
						Indices[k].z = newID;
					}
				}
			}
		}
	}
	int vertexID1;
	int vertexID2;
	int vertexID3;
	for (auto& index : Indices) {
		vertexID1 = index.x;
		vertexID2 = index.y;
		vertexID3 = index.z;
		Vertex* vertex1 = Vertices.at(vertexID1);
		Vertex* vertex2 = Vertices.at(vertexID2);
		Vertex* vertex3 = Vertices.at(vertexID3);
		Face* newFace = new Face(vertex1, vertex2, vertex3);
		//compute plane equation
		Vector3 p = vertex2->GetPosition() - vertex1->GetPosition();
		Vector3 q = vertex3->GetPosition() - vertex1->GetPosition();
		Vector3 normal = Cross(p, q);
		float d = -1 *((normal.x * vertex1->GetPosition().x) + (normal.y * vertex1->GetPosition().y) + (normal.z * vertex1->GetPosition().z));
		newFace->SetEquation(normal.x, normal.y, normal.z, d);
		Faces.emplace_back(newFace);
		vertex1->AddFace(newFace);
		vertex2->AddFace(newFace);
		vertex3->AddFace(newFace);
		//dont add duplicate pairs
		//if alredy exists, add deletable faces
		if (!vertex1->CheckForPair(vertex2, newFace)) {
			Pair* pair = new Pair(vertex1, vertex2);
			pair->AddSharedFace(newFace);
			Pairs.emplace_back(pair);
			vertex1->AddPair(pair);
			vertex2->AddPair(pair);
		}
		if (!vertex1->CheckForPair(vertex3, newFace)) {
			Pair* pair = new Pair(vertex1, vertex3);
			pair->AddSharedFace(newFace);
			Pairs.emplace_back(pair);
			vertex1->AddPair(pair);
			vertex3->AddPair(pair);
		}
		if (!vertex2->CheckForPair(vertex3, newFace)) {
			Pair* pair = new Pair(vertex2, vertex3);
			pair->AddSharedFace(newFace);
			Pairs.emplace_back(pair);
			vertex2->AddPair(pair);
			vertex3->AddPair(pair);
		}
	}
}

void SurfaceSimplifier::CalculateNormals() {
	//for bunny
	for (auto& face : Faces) {
		Vector3 U = face->GetV2()->GetPosition() - face->GetV1()->GetPosition();
		Vector3 V = face->GetV3()->GetPosition() - face->GetV1()->GetPosition();
		Vector3 normal = Cross(U, V);
		Vertices[face->GetV1()->GetID()]->SetNormal(face->GetV1()->GetNormal() + normal);
		Vertices[face->GetV2()->GetID()]->SetNormal(face->GetV2()->GetNormal() + normal);
		Vertices[face->GetV3()->GetID()]->SetNormal(face->GetV3()->GetNormal() + normal);
	}
	for (auto& vertex : Vertices) {
		Vector3 normal = vertex->GetNormal();
		normal.Normalize();
		vertex->SetNormal(normal);
	}
}

void SurfaceSimplifier::InitializeQ() {
	for (auto& vertex : Vertices) {
		for (auto& face : vertex->GetFaces()) {
			vertex->AddQ(face->ComputeK());
		}
	}
}
// for heap
bool CompareError(Pair* lhs, Pair* rhs) {
	return lhs->GetError() <= rhs->GetError() ? false : true;
}
//updating pairs initially and post contraction
void SurfaceSimplifier::ComputePairError(std::vector<Pair*> pairs) {
	for (auto& pair : pairs) {
		if (!pair->IsNull()) {
			pair->UpdateQ();
			pair->UpdateDestinationVertex();
			pair->ComputeError();
		}
	}
}

void SurfaceSimplifier::RemovePairs() {
	while (currentFaceNum > faceTarget) {
		//pop
		std::make_heap(Pairs.begin(), Pairs.end(), &CompareError);
		std::pop_heap(Pairs.begin(), Pairs.end(), &CompareError);
		Pair* minPair = Pairs.back();
		Vertex* v1 = minPair->GetV1();
		Vertex* v2 = minPair->GetV2();
		//update position and Q and normals and texcoord 
		v1->SetPosition(minPair->GetDestination());
		v1->SetTexcoord((v1->GetTexcoord().x + v2->GetTexcoord().x) / 2.0f, (v1->GetTexcoord().y + v2->GetTexcoord().y) / 2.0f);
		v1->SetNormal((v1->GetNormal() + v2->GetNormal()) / 2);
		v1->SetQ(minPair->GetPairQ());
		//delete shared faces
		for (auto& face : minPair->GetDeletableFaces()) {
			if (!face->IsNull()) {
				Faces.erase(std::remove(Faces.begin(), Faces.end(), face), Faces.end());
				face->SetNull();
				currentFaceNum--;
			}
		}
		//delete pair
		//update v1 with v2 data then poitn v2 to v1
		v1->RemovePair(minPair);
		v2->RemovePair(minPair);
		minPair->SetNull();
		int length = v1->GetPairs().size();
		for (auto pair : v2->GetPairs()) {
			if (pair != minPair && pair->IsNull() == false) {
				//if v1 is already paired with the thing v2 was paired, combine deletable faces (so long as they arent null) "delete shared pairs"
				//other wise add em straight up , will chnage v2 pointer afetr
				v1->ComparePair(pair, length, v2);
			}
		}
		for (int i = 0; i < Vertices.size(); i++) {
			if (Vertices[i]->GetID() == v2->GetID()) {
				Vertices[i] = Vertices[v1->GetID()];
			}
		}
		for (int i = 0; i < Pairs.size(); i++) {
			if (Pairs[i]->GetV1()->GetID() == v2->GetID()) {
				Pairs[i]->SetV1(Vertices[v1->GetID()]);
			}
			if (Pairs[i]->GetV2()->GetID() == v2->GetID()) {
				Pairs[i]->SetV2(Vertices[v1->GetID()]);
			}
		}
		//update all pairs of v1
		for (int i = 0; i < Faces.size(); i++) {
			if (Faces[i]->IsNull()) {
				Faces.erase(std::remove(Faces.begin(), Faces.end(), Faces[i]), Faces.end());
				i--;
			}
		}
		for (int i = 0; i < Pairs.size(); i++) {
			if (Pairs[i]->IsNull()) {
				Pairs.erase(std::remove(Pairs.begin(), Pairs.end(), Pairs[i]), Pairs.end());
				i--;
			}
		}
		ComputePairError(v1->GetPairs());
	}
 }

void SurfaceSimplifier::OutputToFile() {
	std::ofstream resultfile;
	std::ifstream configFile("Assets/Meshes/Config.itpmesh2");
	std::stringstream configBuffer;
	configBuffer << configFile.rdbuf();
	resultfile.open("Assets/Meshes/Result.itpmesh2", std::ios::trunc);
	if (resultfile.is_open()) {
		std::ostringstream vertices;
		std::ostringstream indices;
		int vert_count = 0;
		int face_count = 0;
		for (auto& vertex : Vertices) {
			std::string vposx = std::to_string(vertex->GetPosition().x);
			std::string vposy = std::to_string(vertex->GetPosition().y);
			std::string vposz = std::to_string(vertex->GetPosition().z);
			std::string vnorx = std::to_string(vertex->GetNormal().x);
			std::string vnory = std::to_string(vertex->GetNormal().y);
			std::string vnorz = std::to_string(vertex->GetNormal().z);
			std::string vtexx = std::to_string(vertex->GetTexcoord().x);
			std::string vtexy = std::to_string(vertex->GetTexcoord().y);
			vertices << "[" << vposx << "," << vposy << "," << vposz << "," << vnorx << "," << vnory << "," << vnorz << "," << vtexx << "," << vtexy << "]";
			if (vert_count < Vertices.size() - 1) {
				vertices << ",\n";
			}
			else {
				vertices << "\n" << "],\n" << "\"indices\":[\n";
			}
			vert_count++;
		}
		for (auto& face : Faces) {
			//get position strings
			indices << "[" << std::to_string(face->GetV1()->GetID()) << "," << std::to_string(face->GetV2()->GetID()) << "," << std::to_string(face->GetV3()->GetID()) << "]";
			if (face_count < Faces.size() - 1) {
				indices << ",\n";
			}
			else {
				indices << "\n" << "]\n" << "}";
			}
			face_count++;
		}
		SDL_Log(std::to_string(face_count).c_str());
		resultfile << configBuffer.str() << vertices.str() << indices.str();
		resultfile.close();
	}
}

void SurfaceSimplifier::CreateBunnyFile() {
	std::ifstream configFile("Assets/Meshes/Config.itpmesh2");
	std::stringstream configBuffer;
	configBuffer << configFile.rdbuf();
	std::ifstream bunnyFile("Assets/Meshes/Bunny.itpmesh2");
	std::ofstream bunnyFileOutput;
	std::string line;
	std::ostringstream fileFormat;
	bool reachedFaces = true;
	while (std::getline(bunnyFile, line)) {
		std::istringstream iss(line);
		char type;
		float x, y, z;
		int f1, f2, f3;
		if (iss >> type) {
			if (type == 'v') {
				iss >> x >> y >> z;
				if (!reachedFaces) {
					fileFormat << ",\n";
				}
				fileFormat << "[" << std::to_string(x) << "," << std::to_string(y) << "," << std::to_string(z) << "," << 0 << "," << 0 << "," << 0 << "," << 0 << "," << 0 << "]";
				reachedFaces = false;
			}
			else {
				iss >> f1 >> f2 >> f3;
				f1--;
				f2--;
				f3--;
				if (!reachedFaces) {
					fileFormat << "\n" << "],\n" << "\"indices\":[\n";
					reachedFaces = true;
					fileFormat << "[" << std::to_string(f1) << "," << std::to_string(f2) << "," << std::to_string(f3) << "]";
				}
				else {
					fileFormat << ",\n";
					fileFormat << "[" << std::to_string(f1) << "," << std::to_string(f2) << "," << std::to_string(f3) << "]";
				}
			}
		}
	}
	fileFormat << "\n" << "]\n" << "}";
	bunnyFileOutput.open("Assets/Meshes/Bunny2.itpmesh2", std::ios::trunc);
	bunnyFileOutput << configBuffer.str() << fileFormat.str();
	bunnyFileOutput.close();
}