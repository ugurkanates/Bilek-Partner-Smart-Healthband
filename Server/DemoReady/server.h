#ifndef SERVER_H
#define SERVER_H

#include <iostream>
#include <string>
#include <cstring>
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
#define ERROR_CODE -1
#define ERROR 0
#define DEFAULT 0
#define WS_VERSION 0x0202
#define SERVER_PORT 1379  // Serverýn Public olarak çalýþmasý için, modeminizden program çalýþtýrýldýðý zaman yazan IP'ye bu PORT'u Forwardlamanýz gerekmektedir. 
#define BUFFER_SIZE 256
#define DATE_BUFFER_SIZE 25
#define HANDSHAKE_BUFFER 1
#define DATABASE_FILENAME "BilekPartner.csv"

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

class Server
{
	public:
		Server();
		void StartServer();
		void CloseServer();
#ifdef _WIN32
		std::string GetIPAddress();
#endif		
		void ListenClients();
		static std::string GetDate();

	private:
		// OS dependent GetMessageFromClient functions
		static void VerifyClient(int clientSocket);
		static void AcceptClients(int serverSocket);
		static void HandleWristband(int clientSocket);
		static void HandleMobile(int clientSocket);
		static void FirstLoad(int clientSocket);
		static void UpdateServer(int clientSocket);
		static void UpdateDataBase(int clientSocket, std::string updateDate);

		// OS dependent serverSockets
#ifdef __linux__
		 int serverSocket;
#elif _WIN32
		SOCKET serverSocket;
#endif
		std::string ip;
		sockaddr_in serverAddress;
		int clientLimit = 10;
		static bool serverOn;
		static std::vector<std::thread> clientThreads;
		std::thread mamaThread;
};





#endif
