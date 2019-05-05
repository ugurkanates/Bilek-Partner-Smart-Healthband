#include "bileklikTest.h"


BilekPartner::BilekPartner() {
}

BilekPartner::~BilekPartner() {
}

void BilekPartner::ConnectToServer() {
#ifdef _WIN32
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
				if (iResult == ERROR) {
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
#elif __linux__
	struct hostent *server;
	struct sockaddr_in serverAddress;

	// Creating server socket for connection
	serverSocket = socket(AF_INET, SOCK_STREAM, DEFAULT_OPTIONS);
	if (serverSocket == ERROR_CODE) {
		std::cerr << "\nSystem Error\nServer socket creation failed\n";
		exit(EXIT_FAILURE);
	}

	// Filling server address structure
	
server = gethostbyname(SERVER_IP);
	bcopy((char *)server->h_addr, (char*)&serverAddress.sin_addr.s_addr, sizeof(serverAddress));
	serverAddress.sin_family = AF_INET;
	serverAddress.sin_port = htons(atoi(SERVER_PORT));


	/*
	serverAddress.sin_addr.s_addr = inet_addr(SERVER_IP);
	serverAddress.sin_family = AF_INET;
	serverAddress.sin_port = htons(atoi(SERVER_PORT));
	*/

	// Connecting to server
	if (connect(serverSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress)) == ERROR_CODE) {
		std::cerr << "\nSystem Error\nConnection to server socket failed" << std::endl;
		exit(EXIT_FAILURE);
	}
#endif


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
	buffer[0] ='B';
	memset(buffer,'B',BUFFER_SIZE);
	//Send Confirmation Char
	bytesReadSent = send(serverSocket, buffer, BUFFER_SIZE, 0);


	if (bytesReadSent == ERROR) {
#ifdef _WIN32
		printf("send failed with error: %d\n", WSAGetLastError());
		closesocket(serverSocket);
		WSACleanup();
#elif __linux__
		printf("send failed with error: %d\n", errno);
		close(serverSocket);
		exit(EXIT_FAILURE);
#endif
	}

	bytesReadSent = recv(serverSocket, buffer, BUFFER_SIZE, 0);
printf("bytesReadSent -> %d\n",bytesReadSent);
	if (bytesReadSent == ERROR || buffer[0] != 'S') {
		printf("recv failed with error\n");
#ifdef _WIN32
		closesocket(serverSocket);
		WSACleanup();
#elif __linux__
		close(serverSocket);
		exit(EXIT_FAILURE);
#endif
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
		if (bytesReadSent == ERROR_CODE) {
#ifdef _WIN32
			printf("send failed with error: %d\n", WSAGetLastError());
			closesocket(serverSocket);
			WSACleanup();
#elif __linux__
			printf("send failed with error: %d\n", errno);
			close(serverSocket);
			exit(EXIT_FAILURE);
#endif
			exit(EXIT_FAILURE);
		}
	}

	printf("\n All Data is sent. Disconnecting WristBand\n");

#ifdef _WIN32
	closesocket(serverSocket);
	WSACleanup();
#elif __linux__
	close(serverSocket);
	exit(EXIT_FAILURE);
#endif
}
