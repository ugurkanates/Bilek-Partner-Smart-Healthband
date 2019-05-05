#include <iostream>
#include <string>
#include <thread>
#include <stdlib.h>
#include <sys/types.h>
#include <fstream>
#include <stdlib.h>
#include <vector>
#include <fstream>
#include <ctime>
#ifdef __linux__
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <netdb.h>
#include <unistd.h>
#include <strings.h>
#include <string.h>
#elif _WIN32
#pragma once
#define WIN32_LEAN_AND_MEAN
#define errno WSAGetLastError()
#include <winsock2.h>
#include <wininet.h>
#include <WS2tcpip.h>
#include <Windows.h>
#pragma comment(lib,"Ws2_32.lib")
#endif


#define ZERO 0
#define ERROR 0
#define ERROR_CODE -1
#define DEFAULT 0
#define DEFAULT_OPTIONS 0
#define WS_VERSION 0x0202
#ifdef _WIN32
#define SERVER_IP "127.0.0.1"
#elif __linux__
#define SERVER_IP "0.0.0.0"
#endif
#define SERVER_PORT "1379"  // Serverýn Public olarak çalýþmasý için, modeminizden program çalýþtýrýldýðý zaman yazan IP'ye bu PORT'u Forwardlamanýz gerekmektedir. 
#define BUFFER_SIZE 256
#define DATE_BUFFER_SIZE 30
#define SLEEP_MILISEC 1000
#define DATABASE_FILENAME "MobileDatabase.csv"

struct WristBandDataPackage {
	float temp;
	float pulse;
	float pX;
	float pY;
	float pZ;
};

struct MobileDataPackage {
	char date[DATE_BUFFER_SIZE];
	WristBandDataPackage wbData;
};

class mobileTest{

public:
	mobileTest();
	~mobileTest();

	void ConnectToServer();
	void HandshakeWithServer();
	void FirstLoad();
	void UpdateServer();
	void UpdateDatabase();
	void GenerateData();

private:
	int serverSocket;
	std::string GetDate();
	std::string firstGenData;
	int flCount = 0;

};

