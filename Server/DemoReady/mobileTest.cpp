#include "mobileTest.h"


mobileTest::mobileTest()
{
}


mobileTest::~mobileTest()
{
}

void mobileTest::ConnectToServer(){

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

	// Connecting to server
	if (connect(serverSocket, (struct sockaddr*)&serverAddress, sizeof(serverAddress)) == ERROR_CODE) {
		std::cerr << "\nSystem Error\nConnection to server socket failed" << std::endl;
		exit(EXIT_FAILURE);
	}

#endif

}

void mobileTest::HandshakeWithServer(){
	int bytesReadSent = 0;
	char buffer[BUFFER_SIZE];
	//Send Confirmation Char
	buffer[0] ='M';
	//memset(buffer,'M',BUFFER_SIZE);
	bytesReadSent = send(serverSocket, buffer, BUFFER_SIZE, 0);
	if (bytesReadSent == ERROR_CODE) {
#ifdef _WIN32
		printf("send failed with error: %d\n", WSAGetLastError());
		closesocket(serverSocket);
		WSACleanup();
#elif __linux__
		printf("send failed with error\n");
		close(serverSocket);
#endif
		exit(EXIT_FAILURE);
	}

	bytesReadSent = recv(serverSocket, buffer, BUFFER_SIZE, 0);
	if (bytesReadSent == ERROR_CODE || buffer[0] != 'S') {
		printf("recv failed with error\n");
#ifdef _WIN32
		closesocket(serverSocket);
		WSACleanup();
#elif __linux__
		close(serverSocket);
#endif
		exit(EXIT_FAILURE);
	}


}

void mobileTest::FirstLoad(){
	char buffer[BUFFER_SIZE];
	int bytesRead;
	std::fstream wristbandFile;
	bool done = false;
	wristbandFile.open(DATABASE_FILENAME, std::fstream::out );

	bytesRead = send(serverSocket, "FL", 2, DEFAULT);

	printf("\n************\n\n");
	while (!done) {

		bytesRead = recv(serverSocket, buffer, BUFFER_SIZE, DEFAULT);
		if (bytesRead <= 0) {
			printf("Couldn't read in firstLoad\n");
		}else{
			if (strcmp(buffer, "FL FIN") == 0) {
				done = true;
			}
			else {
				printf("Recieved buffer -> %s", buffer);
				wristbandFile.write(buffer, strlen(buffer));
				wristbandFile.flush();
				++flCount;
				if (flCount == 5) {
					firstGenData = buffer;
				}

			}
		}
	}
	wristbandFile.close();
	printf("\n************\n\n");

}

void mobileTest::UpdateServer(){
	std::ifstream wristBandFile(DATABASE_FILENAME);
	int bytesSend;
	int count = 0;
	send(serverSocket, "US", BUFFER_SIZE, DEFAULT);
	

	printf("\n************\n\n");


	for (std::string line; getline(wristBandFile, line);) {

		if (count >= flCount) {
			line += "\n";
			std::cout << line.c_str();
			bytesSend = send(serverSocket, line.c_str(), BUFFER_SIZE, DEFAULT);
			if (bytesSend <= 0) {
				std::cerr << "Failed to sent data to Mobile on FirstLoad Function \n";
			}
		}

		++count;
	}

	bytesSend = send(serverSocket, "US FIN", BUFFER_SIZE, DEFAULT);
	if (bytesSend <= 0) {
		std::cerr << "Failed to send UpdateServer End signal to server\n";
	}

	wristBandFile.close();

	printf("\n************\n\n");

}

void mobileTest::UpdateDatabase(){
	std::fstream wristbandFile;
	char buffer[BUFFER_SIZE];
	int bytesRead;
	bool done = false;
	
	
	sprintf(buffer, "UD %s", firstGenData.c_str());
	send(serverSocket, buffer, BUFFER_SIZE, DEFAULT);


	printf("\n************\n\n");
	wristbandFile.open(DATABASE_FILENAME, std::fstream::out);

	while (!done) {

		bytesRead = recv(serverSocket, buffer, BUFFER_SIZE, DEFAULT);
		if (bytesRead <= 0) {
			printf("Couldn't read in firstLoad\n");
		}
		else {
			if (strcmp(buffer, "UD FIN") == 0) {
				done = true;
			}
			else {
				printf("Recieved buffer -> %s", buffer);
				wristbandFile.write(buffer, strlen(buffer));
				wristbandFile.flush();
				++flCount;
			}
		}
	}
	wristbandFile.close();

	printf("\n************\n\n");


}

void mobileTest::GenerateData(){
	char buffer[BUFFER_SIZE];
	std::fstream wristBandFile;
	
	srand(time(0));

	float t = (rand() % 300) + 1;
	float p = (rand() % 300) + 1;
	float px = (rand() % 300) + 1;
	float py = (rand() % 300) + 1;
	float pz = (rand() % 300) + 1;

	WristBandDataPackage wristPackage = { t,p,px,py,pz };

	wristBandFile.open(DATABASE_FILENAME, std::fstream::out | std::fstream::app);

	printf("\n************\n\n");

	for (int i = 0; i < 10; ++i) {
		t = (rand() % 300) + 1;
		p = (rand() % 300) + 1;
		px = (rand() % 300) + 1;
		py = (rand() % 300) + 1;
		pz = (rand() % 300) + 1;

		wristPackage = { t,p,px,py,pz };

		sprintf(buffer, "%s,%.2f,%.2f,%.2f,%.2f,%.2f\n", GetDate().c_str(), wristPackage.temp, wristPackage.pulse, wristPackage.pX, wristPackage.pY, wristPackage.pZ);
		std::cout << "Generated Data ->" << buffer ;


		wristBandFile.write(buffer, strlen(buffer));
		wristBandFile.flush();	
	}
	
	printf("\n************\n\n");

}

std::string mobileTest::GetDate() {
	std::string date;
	char dateBuff[DATE_BUFFER_SIZE];

	time_t tt;
	struct tm *ti;

	time(&tt);
	ti = localtime(&tt);

	//YEAR
	date = std::to_string(1900 + ti->tm_year) + " ";
	//Month
	if (ti->tm_mon + 1 < 10) {
		date += "0" + std::to_string(ti->tm_mon + 1) + " ";
	}
	else {
		date += std::to_string(ti->tm_mon + 1) + " ";
	}
	//Day
	if (ti->tm_mday < 10) {
		date += "0" + std::to_string(ti->tm_mday) + " ";
	}
	else {
		date += std::to_string(ti->tm_mday) + " ";
	}
	//Hour
	if (ti->tm_hour < 10) {
		date += "0" + std::to_string(ti->tm_hour) + ":";
	}
	else {
		date += std::to_string(ti->tm_hour) + ":";
	}
	//Minute
	if (ti->tm_min < 10) {
		date += "0" + std::to_string(ti->tm_min) + ":";
	}
	else {
		date += std::to_string(ti->tm_min) + ":";
	}
	//Second
	if (ti->tm_sec < 10) {
		date += "0" + std::to_string(ti->tm_sec);
	}
	else {
		date += std::to_string(ti->tm_sec);
	}

	return date;
}
