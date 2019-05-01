#include "pch.h"
#include "server.h"

bool Server::serverOn = true;
std::vector<std::thread> Server::clientThreads;

Server::Server()
{

}

Server::~Server()
{

}

void Server::StartServer() {

#ifdef _WIN32
	//Initialize Winsock
	WSADATA wsData;
	int retval = 0;
	

	if ((WSAStartup(WS_VERSION, &wsData))) {
		std::cerr << std::endl <<"ERROR -> Can't initialize WinSock..!" << std::endl;
		exit(EXIT_FAILURE);
	}

	//Check WSA Version
	if (wsData.wVersion != WS_VERSION) {
		WSACleanup();
		std::cerr << std::endl << "ERROR -> WSA Version check failed" << std::endl;
		exit(EXIT_FAILURE);
	}


	//Create a TCP Socket
	serverSocket = socket(AF_INET, SOCK_STREAM, DEFAULT);
	if (serverSocket == INVALID_SOCKET) {
		std::cerr << std::endl << "ERROR -> Socket Creation in Server Failed." << std::endl;
		WSACleanup();
		exit(EXIT_FAILURE);
	}

	//Bind IP & Port to serverAddress
	serverAddress.sin_family = AF_INET;
	serverAddress.sin_port = htons(SERVER_PORT);
	serverAddress.sin_addr.S_un.S_addr = INADDR_ANY;  // TODO : Sorun çýkarsa inet_pton ile dene
	//inet_pton(AF_INET, "127.0.0.1", &serverAddress.sin_addr);


	if (bind(serverSocket, (sockaddr*)&serverAddress, sizeof(serverAddress))) {
		std::cerr << std::endl << "ERROR -> Failed to bind socket in server..! " << std::endl;
		closesocket(serverSocket);
		WSACleanup();
		exit(EXIT_FAILURE);
	}

	

	serverOn = true;

	ListenClients();
	

	
#elif __linux__
	//TODO : UNIX/POSIX Server here
	int set = 1;

	//Creating server socket
	if ((serverSocket = socket(AF_INET, SOCK_STREAM, DEFAULT)) == ERROR_CODE) {
		std::cerr << "\nError -> Server socket creation failed" << std::endl;
		exit(EXIT_FAILURE);
	}

	// Changing socket options to set port as reusable
	if (setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &set, sizeof(int)) == ERROR_CODE) {
		std::cerr << "\nError -> Reusable port options couldn't set\n";
		exit(EXIT_FAILURE);
	}

	//Socket address structure
	serverAddress.sin_family = AF_INET;
	socketAddress.sin_addr.s_addr = inet_addr("0.0.0.0");
	socketAddress.sin_port = htons(stoi(SERVER_PORT));

	if (bind(serverSocket, (sockaddr*)&serverAddress, sizeof(sockaddr)) == ERROR_CODE) {
		std::cerr << "\nError -> Server address binding failed\n";
		exit(EXIT_FAILURE);
	}


	if (listen(serverSocket, 1) == ERROR_CODE) {
		std::cerr << "\Error -> Server failed to listen bound socket" << std::endl;
		exit(EXIT_FAILURE);
	}

#endif
}

void Server::CloseServer() {

	Server::serverOn = false;

	std::cout << "Server shutting down\n" << std::endl;


#ifdef _WIN32
	
	if (shutdown(serverSocket, SD_SEND) == SOCKET_ERROR) {
		std::cout << "ERROR -> Server shutdown failed. Code :" << errno <<  std::endl;
		closesocket(serverSocket);
		WSACleanup();
		exit(EXIT_FAILURE);
	}

	closesocket(serverSocket);
	WSACleanup();

#elif __linux__

	shutdown(serverSocket, SHUT_RDWR);
	close(serverSocket);

#endif

	mamaThread.join();

	for (unsigned int i = 0; i < clientThreads.size(); ++i) {
		clientThreads[i].join();
	}
	
	
}

void Server::ListenClients() {
#ifdef _WIN32

	//Listen the server socket.
	if (listen(serverSocket, clientLimit)) {
		std::cerr << std::endl << "ERROR -> Failed to listen server socket " << std::endl;
		closesocket(serverSocket);
		WSACleanup();
		exit(EXIT_FAILURE);
	}


#elif __linux__
	//Wait for a client connection.
	if (listen(serverSocket, clientLimit)) {
		std::cerr << std::endl << "ERROR -> Failed to listen server socket " << std::endl;
		closesocket(serverSocket);
		WSACleanup();
		exit(EXIT_FAILURE);
	}


#endif

	std::cout << "\nServer established, waiting for client" << std::endl;

	mamaThread = std::thread(&AcceptClients, serverSocket);

}

void Server::AcceptClients(int serverSocket){
#ifdef _WIN32
	//Accept client
	SOCKET clientSocket;
	sockaddr_in clientAddress;

	while (serverOn) {

		int clientSize = sizeof(clientAddress);
		if ((clientSocket = accept(serverSocket, (sockaddr*)&clientAddress, &clientSize)) == INVALID_SOCKET) {
			if (errno != WSAEINTR) {
				std::cerr << std::endl << "ERROR -> Failed to accept client. Code : " << errno <<  std::endl;
			}else { // Interrupted, high possibnility of server shutdown. Do necessary stuff

			}
		}else {
			//Print Client Information
			char host[NI_MAXHOST];
			char service[NI_MAXSERV];

			ZeroMemory(host, NI_MAXHOST);
			ZeroMemory(service, NI_MAXSERV);

			if (getnameinfo((sockaddr*)&clientAddress, sizeof(clientAddress), host, NI_MAXHOST, service, NI_MAXSERV, 0) == 0) {
				std::cout << host << " connected on port " << service << std::endl;
			}
			else {
				inet_ntop(AF_INET, &clientAddress.sin_addr, host, NI_MAXHOST);
				std::cout << host << " connected on port " << ntohs(clientAddress.sin_port) << std::endl;
			}
			
			//GetMessageFromClient(clientSocket);
			std::thread clientServantThread(VarifyClient, clientSocket);
			clientThreads.push_back(std::move(clientServantThread));
			std::cout << "\n ~~ Accepted client, waiting for a new one ~~\n" << std::endl;
		}

	}




#elif __linux__

	int clientSocket;

	while (serverOn) {
		if (clientSocket = accept(serverSocket, nullptr, nullptr) == ERROR_CODE) {
			if (errno != EINVAL) {
				std::cerr << "\nError -> Connection attempt with client failed. ~ " << errno << std::endl;
			}
		}else {
			//GetMessageFromClient(clientSocket);
			std::thread clientServantThread(GetMessageFromClient, clientSocket);
			clientThreads.push_back(std::move(clientServantThread));
			}
	}


#endif

}

void Server::HandleWristband(int clientSocket) {
	char buffer[BUFFER_SIZE];
	int bytesReadSent = 0;
	bool clientConnected = true;
	memset(buffer, '\0', BUFFER_SIZE);
	buffer[0] = 'S';
	std::fstream wristBandFile;

	//Send confirmation message
	bytesReadSent = send(clientSocket, buffer, strlen(buffer),DEFAULT);
	if (bytesReadSent <= 0) {
		std::cerr << "\nFailed to sent ConfirmationMessage to Wristband\n";

	}
	else { //Confirmation message sent without a problem. Do the job

		wristBandFile.open("BilekPartner.txt", std::fstream::out | std::fstream::app );
		
		//Write given data to files
		while (serverOn && clientConnected) {

			bytesReadSent = recv(clientSocket, buffer, BUFFER_SIZE, DEFAULT);
			if (bytesReadSent <= 0) { //BilekPartner disconnected.
				clientConnected = false;
				wristBandFile.close();
				std::cout << "\n ~ BilekPartner disconnected.\n";
			}else {
				std::cout << "\nWristband ->" << buffer << std::endl;

				wristBandFile.write(buffer, strlen(buffer));
				wristBandFile.write("\n", 1);
				wristBandFile.flush();

				//TODO: Remove this
				memset(buffer, '\0', BUFFER_SIZE);

			}

		}
	}
}


void Server::HandleMobile(int clientSocket){
	char buffer[BUFFER_SIZE];
	int bytesReadSent = 0;
	bool clientConnected = true;
	memset(buffer, '\0', BUFFER_SIZE);
	buffer[0] = 'S';
	std::fstream wristbandFile;

	//Send confirmation message
	bytesReadSent = send(clientSocket, buffer, strlen(buffer), DEFAULT);
	if (bytesReadSent <= 0) {
		std::cerr << "\nFailed to sent ConfirmationMessage to MobileApp\n";

	}else { //Confirmation message sent without a problem. Do the job

	}
}



/*
#ifdef _WIN32
void Server::GetMessageFromClient(SOCKET clientSocket) {
	
	char buffer[BUFFER_SIZE];
	char serverBuff[BUFFER_SIZE];
	int bytesRead;
	
	while (serverOn) {

		ZeroMemory(serverBuff, BUFFER_SIZE);
		ZeroMemory(buffer, BUFFER_SIZE);
		//Recieve message from client
		bytesRead = recv(clientSocket, buffer, BUFFER_SIZE, DEFAULT);
		if (bytesRead == SOCKET_ERROR) {
			if (errno != WSAECONNABORTED) {
				std::cout << std::endl << "Couldn't read from client. Code :" << errno << std::endl;
				closesocket(clientSocket);
				exit(EXIT_FAILURE);
			}
			else { // Server shutting down, do stuff warn client etc.

			}
		}
		else if (bytesRead == ZERO) {
			std::cout << "\nClient disconnected." << std::endl;
			serverOn = false;
		}
		else {

			std::cout << "\nClient Said -> " << buffer << "(" << strlen(buffer) << ")" << std::endl;


			//TODO : Make package for client messages and write them into a file.

		}
	}
	std::cout << "ClientServantThread finished its job. Returning to Mama Process\n" << std::endl;
	shutdown(clientSocket, SD_SEND);

}*/

void Server::VarifyClient(int clientSocket) {

	char buffer[BUFFER_SIZE];
	char serverBuff[BUFFER_SIZE];
	int bytesRead;
	bool clientConnected = true;

	memset(buffer, '\0', BUFFER_SIZE);
	memset(serverBuff, '\0', BUFFER_SIZE);

	while (serverOn && clientConnected) {
		bytesRead = recv(clientSocket, buffer, BUFFER_SIZE, DEFAULT);
		if (bytesRead > ZERO) {
			std::cout << "\nNew Client -> " << buffer << std::endl;
			if (buffer[0] == 'B') {
				//Bileklik baðlandý, gerekli fn'leri çaðýrýp verileri kaydetmeye baþla
				std::cout << "\n ~ BilekPartner connected.\n";
				HandleWristband(clientSocket);
			}
			else if (buffer[0] == 'M') {
				// Mobil baðlandý, gerekli fn'leri çaðýrýp iletiþime baþla.
				std::cout << "\n ~ MobileApp connected\n";
				HandleMobile(clientSocket);
			}else {
				std::cout << "\nUnknown client connected. Closing socket.\n";
				clientConnected = false;

			}

		} 
	}
	std::cout << "ClientServantThread finished its job. Returning to Mama Process\n" << std::endl;
#ifdef _WIN32
	shutdown(clientSocket, SD_SEND);
#elif __linux___
	shutdown(clientSocket, SHUT_RDWR);
#endif
}


std::string Server::GetIPAddress() {
	using namespace std;
	string line;
	ifstream IPFile;
	int offset;
	const char* search = "IPv4 Address. . . . . . . . . . . :"; // search pattern
	system("ipconfig > ip.txt");
	IPFile.open("ip.txt");
	if (IPFile.is_open()) {
		while (!IPFile.eof()) {
			getline(IPFile, line);
			if ((offset = line.find(search, 0)) != string::npos) {
				line.erase(0, 39);
				ip = line;
				IPFile.close();
			}
		}
	}

	return ip;
}
/*
int clientSize = sizeof(clientAddress);
	if ((clientSocket = accept(serverSocket, (sockaddr*)&clientAddress, &clientSize)) == INVALID_SOCKET) {
		std::cerr << std::endl << "ERROR -> Failed to accept on server." << std::endl;
		closesocket(serverSocket);
		WSACleanup();
		exit(EXIT_FAILURE);
	}




		// Modify message and send it back to client
		sprintf_s(serverBuff, "Server says -> %s\0", buffer);
		bytesRead = send(clientSocket, serverBuff, strlen(serverBuff), DEFAULT);
		if (bytesRead <= ERROR) {
			std::cout << std::endl << "Couldn't send message to client" << std::endl;
			std::cout << std::endl << "ERROR -> " << WSAGetLastError() << std::endl;
			exit(EXIT_FAILURE);
		}
	}


*/