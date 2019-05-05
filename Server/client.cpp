#include "pch.h"
#include "client.h"


BilekPartner::BilekPartner() {
}

BilekPartner::~BilekPartner() {
}

void BilekPartner::ConnectToServer() {
	WSADATA wsaData;
	serverSocket = INVALID_SOCKET;
	struct addrinfo *result = NULL,
		*ptr = NULL,
		hints;

	int iResult;
	int recvbuflen = BUFFER_SIZE;

	// Initialize Winsock
	iResult = WSAStartup(MAKEWORD(2, 2), &wsaData);
	if (iResult != 0) {
		printf("WSAStartup failed with error: %d\n", iResult);
		exit(EXIT_FAILURE);
	}

	ZeroMemory(&hints, sizeof(hints));
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_protocol = IPPROTO_TCP;

	// Resolve the server address and port
	iResult = getaddrinfo(SERVER_IP, SERVER_PORT, &hints, &result);
	if (iResult != 0) {
		printf("getaddrinfo failed with error: %d\n", iResult);
		WSACleanup();
		exit(EXIT_FAILURE);
	}

	// Attempt to connect to an address until one succeeds
	for (ptr = result; ptr != NULL; ptr = ptr->ai_next) {

		// Create a SOCKET for connecting to server
		serverSocket = socket(ptr->ai_family, ptr->ai_socktype,
			ptr->ai_protocol);
		if (serverSocket == INVALID_SOCKET) {
			printf("socket failed with error: %ld\n", WSAGetLastError());
			WSACleanup();
			// Attempt to connect to an address until one succeeds
			for (ptr = result; ptr != NULL; ptr = ptr->ai_next) {

				// Create a SOCKET for connecting to server
				serverSocket = socket(ptr->ai_family, ptr->ai_socktype,
					ptr->ai_protocol);
				if (serverSocket == INVALID_SOCKET) {
					printf("socket failed with error: %ld\n", WSAGetLastError());
					WSACleanup();
					exit(EXIT_FAILURE);
				}

				// Connect to server.
				iResult = connect(serverSocket, ptr->ai_addr, (int)ptr->ai_addrlen);
				if (iResult == SOCKET_ERROR) {
					closesocket(serverSocket);
					serverSocket = INVALID_SOCKET;
					continue;
				}
				break;
			}

			freeaddrinfo(result);

			if (serverSocket == INVALID_SOCKET) {
				printf("Unable to connect to server!\n");
				WSACleanup();
				exit(EXIT_FAILURE);
			};
		}

		// Connect to server.
		iResult = connect(serverSocket, ptr->ai_addr, (int)ptr->ai_addrlen);
		if (iResult == SOCKET_ERROR) {
			closesocket(serverSocket);
			serverSocket = INVALID_SOCKET;
			continue;
		}
		break;
	}

	freeaddrinfo(result);

	if (serverSocket == INVALID_SOCKET) {
		printf("Unable to connect to server!\n");
		WSACleanup();
		exit(EXIT_FAILURE);
	}



}

void BilekPartner::SendDataToServer() {
	int bytesReadSent = 0;
	char buffer[BUFFER_SIZE];
	srand(time(0));

	float t = (rand() % 300) + 1;
	float p = (rand() % 300) + 1;
	float px = (rand() % 300) + 1;
	float py = (rand() % 300) + 1;
	float pz = (rand() % 300) + 1;

	WristBandDataPackage wristPackage = { t,p,px,py,pz };

	//Send Confirmation Char
	bytesReadSent = send(serverSocket, "B", 1, 0);
	if (bytesReadSent == SOCKET_ERROR) {
		printf("send failed with error: %d\n", WSAGetLastError());
		closesocket(serverSocket);
		WSACleanup();
		exit(EXIT_FAILURE);
	}

	bytesReadSent = recv(serverSocket, buffer, 1, 0);
	if (bytesReadSent == SOCKET_ERROR || buffer[0] != 'S') {
		printf("recv failed with error\n");
		closesocket(serverSocket);
		WSACleanup();
		exit(EXIT_FAILURE);
	}

	printf("Handshake with server is successfull, sending data\n");


	for (int i = 0; i < 30; ++i) {
		t = (rand() % 300) + 1;
		p = (rand() % 300) + 1;
		px = (rand() % 300) + 1;
		py = (rand() % 300) + 1;
		pz = (rand() % 300) + 1;

		wristPackage = { t,p,px,py,pz };

		printf("WristPackage -> %f , %f , %f , %f, %f\n", wristPackage.temp, wristPackage.pulse, wristPackage.pX, wristPackage.pY, wristPackage.pZ);

#ifdef _WIN32
		Sleep(SLEEP_MILISEC);
#else
		usleep(SLEEP_MILISEC * 1000); // takes microseconds
#endif

		bytesReadSent = send(serverSocket, (char*)&wristPackage, sizeof(wristPackage), 0);
		if (bytesReadSent == INVALID_SOCKET) {
			printf("send failed with error: %d\n", WSAGetLastError());
			closesocket(serverSocket);
			WSACleanup();
			exit(EXIT_FAILURE);
		}
	}

	printf("\n All Data is sent. Disconnecting WristBand\n");
	closesocket(serverSocket);
	WSACleanup();
}
