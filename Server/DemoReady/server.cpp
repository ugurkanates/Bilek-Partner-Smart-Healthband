#include "pch.h"
#include "server.h"

bool Server::serverOn = true;
std::vector<std::thread> Server::clientThreads;
std::string Server::fileName = std::string(DEFAULT_FILENAME);
std::queue<std::string> Server::databasePackageQueue;
std::mutex Server::mut;

Server::Server() {
}

void Server::StartServer() {

#ifdef _WIN32
	//Initialize Winsock
	WSADATA wsData;
	int retval = 0;


	if ((WSAStartup(WS_VERSION, &wsData))) {
		std::cerr << std::endl << "ERROR -> Can't initialize WinSock..!" << std::endl;
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
	serverAddress.sin_addr.s_addr = inet_addr("0.0.0.0");
	serverAddress.sin_port = htons(SERVER_PORT);

	if (bind(serverSocket, (sockaddr*)&serverAddress, sizeof(sockaddr)) == ERROR_CODE) {
		std::cerr << "\nError -> Server address binding failed\n";
		exit(EXIT_FAILURE);
	}


	if (listen(serverSocket, clientLimit) == ERROR_CODE) {
		std::cerr << "\Error -> Server failed to listen bound socket" << std::endl;
		exit(EXIT_FAILURE);
	}

#endif

	serverOn = true;
	ListenClients();

}

void Server::CloseServer() {

	Server::serverOn = false;

	std::cout << "Server shutting down\n" << std::endl;


#ifdef _WIN32

	if (shutdown(serverSocket, SD_SEND) == SOCKET_ERROR) {
		std::cout << "ERROR -> Server shutdown failed. Code :" << errno << std::endl;
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

	mamaThread.~thread();

	mamaThread.join();

	for (unsigned int i = 0; i < clientThreads.size(); ++i) {
		clientThreads[i].join();
	}

	std::cout << "Server shutdown..!" << std::endl;
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
		close(serverSocket);
		exit(EXIT_FAILURE);
	}


#endif
	std::cerr << "\nServer established on " << GetDate() << " , waiting for clients" << std::endl;

	mamaThread = std::thread(&AcceptClients, serverSocket);

}

void Server::AcceptClients(int serverSocket) {
	sockaddr_in clientAddress;
#ifdef _WIN32
	//Accept client
	SOCKET clientSocket;

	while (serverOn) {

		int clientSize = sizeof(clientAddress);
		if ((clientSocket = accept(serverSocket, (sockaddr*)&clientAddress, &clientSize)) == INVALID_SOCKET) {
			if (errno != WSAEINTR) {
				std::cerr << std::endl << "ERROR -> Failed to accept client. Code : " << errno << std::endl;
			}
			else { // Interrupted, high possibnility of server shutdown. Do necessary stuff

			}
		}
		else {
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
			std::thread clientServantThread(VerifyClient, clientSocket);
			clientThreads.push_back(std::move(clientServantThread));
		}

	}

#elif __linux__

	int clientSocket;

	while (serverOn) {
		if ((clientSocket = accept(serverSocket, nullptr, nullptr)) == ERROR_CODE) {
			if (errno != EINVAL) {
				std::cerr << "\nError -> Connection attempt with client failed. ~ " << errno << std::endl;
			}
		}
		else {
			std::thread clientServantThread(VerifyClient, clientSocket);
			clientThreads.push_back(std::move(clientServantThread));
		}

	}


#endif

}

void Server::VerifyClient(int clientSocket) {

	char buffer[BUFFER_SIZE];
	int bytesRead;
	bool clientConnected = true;

	memset(buffer, '\0', BUFFER_SIZE);
	while (serverOn && clientConnected) {
		bytesRead = recv(clientSocket, buffer, BUFFER_SIZE - 1, DEFAULT);
		if (bytesRead > ZERO) {
			if (buffer[0] == 'B') {
				//Bileklik baðlandý, gerekli fn'leri çaðýrýp verileri kaydetmeye baþla
				std::cout << "\n ~ BilekPartner connected.\n\n";
				if (DEBUG) {
					std::cout << buffer << std::endl;
				}
				HandleWristband(clientSocket, buffer + 2);
				clientConnected = false;
			}
			else if (buffer[0] == 'M') {
				// Mobil baðlandý, gerekli fn'leri çaðýrýp iletiþime baþla.
				std::cout << "\n ~ MobileApp connected. User -> " << (buffer+2) << "\n\n";
				UpdateUser((buffer + 2));
				HandleMobile(clientSocket);
				clientConnected = false;
			}
			else {
				std::cout << "\nUnknown client connected. Closing socket.\n\n" << buffer << std::endl;
				clientConnected = false;
			}

		}
		else {
			std::cout << "\nConnection Error.\n\n";
			clientConnected = false;
		}
	}
	std::cerr << "\nClientThread finished its job. Returning to Mama Process\n" << std::endl;
#ifdef _WIN32
	shutdown(clientSocket, SD_SEND);
#elif __linux___
	shutdown(clientSocket, SHUT_RDWR);
#endif
}

void Server::HandleWristband(int clientSocket, std::string wristBuffer) {
	char wristBandBuffer[BUFFER_SIZE + 1];
	int bytesReadSent = 0;
	std::fstream wristBandFile;
	char* splitter;
	int counter = 0;
	float pX = 0, pY = 0, pZ = 0, temp = 0, pulse = 0;

	memset(wristBandBuffer, '\0', BUFFER_SIZE + 1);

	//Send confirmation message
	wristBandBuffer[0] = 'S';
	bytesReadSent = send(clientSocket, wristBandBuffer, strlen(wristBandBuffer), DEFAULT);
	if (bytesReadSent <= ZERO) {
		std::cerr << "\nFailed to sent ConfirmationMessage to Wristband\n";
	}
	else { //Confirmation message sent without a problem. Do the job


		//De-format message 

		strcpy(wristBandBuffer, wristBuffer.c_str());
		splitter = strtok(wristBandBuffer, "_");

		while (splitter != NULL) {
			switch (counter) {

			case 0: // pX
				pX = atof(splitter);
				break;
			case 1: //pY
				pY = atof(splitter);
				break;
			case 2:	//pZ
				pZ = atof(splitter);
				break;
			case 3:	//Temp
				temp = atof(splitter);
				break;
			case 4:	//Pulse
				pulse = atof(splitter);
				break;
			default:
				printf("Error on reading data from Wristband\n");
				break;
			}

			++counter;
			splitter = strtok(NULL, "_");

		}

		//Put package into the queue

		sprintf(wristBandBuffer, "%s,%f,%f,%f,%f,%f\n\0", GetDate().c_str(), temp, pulse, pX, pY, pZ);

		databasePackageQueue.push(wristBandBuffer);

		//TODO: Remove This

		std::cout << wristBandBuffer << std::endl;

		if (DEBUG) {
			std::cout << "Wristband ->" << wristBandBuffer;
		}

		//Check Data lock & then print to file.
		
		/******** START CRITICAL SECTION ********/
		//std::unique_lock<std::mutex> lock(mut, std::defer_lock);
		//lock.lock();

		wristBandFile.open(fileName, std::fstream::out | std::fstream::app);
		strcpy(wristBandBuffer, (databasePackageQueue.front()).c_str());

		databasePackageQueue.pop();
		wristBandFile.write(wristBandBuffer, strlen(wristBandBuffer));
		wristBandFile.flush();

		wristBandFile.close();
		//lock.unlock();
		/******** END CRITICAL SECTION ********/

	}
}



void Server::HandleMobile(int clientSocket) {
	char buffer[BUFFER_SIZE];
	char secondBuff[BUFFER_SIZE];
	int bytesReadSent = 0;
	bool clientConnected = true;
	memset(buffer, '\0', BUFFER_SIZE);
	buffer[0] = 'S';
	std::fstream wristbandFile;

	strcpy(secondBuff, "S\0");
	//Send confirmation message
	bytesReadSent = send(clientSocket, secondBuff, strlen(secondBuff), DEFAULT);
	if (bytesReadSent <= ZERO) {
		std::cerr << "\nFailed to sent ConfirmationMessage to MobileApp\n";

	}
	else { //Confirmation message sent without a problem. Do the job

		while (serverOn && clientConnected) {
			bytesReadSent = recv(clientSocket, buffer, BUFFER_SIZE, 0);
			if (bytesReadSent <= 0) {
				std::cerr << "\n ~ MobileApp disconnected.\n";
				clientConnected = false;
			}
			//List of commands that can be reecieved from mobilApp
			// "FL" -> "FirstLoad", sends all server dataBase to mobileApp
			// "UD" -> "UpdateDataBase", takes a date and sends all the packages recieved after the given date to mobileApp
			// "US" -> "UpdateServer", takes packages from mobileApp to UpdateServer
			// "UU *UserName*" -> "UpdateUser", takes an user name and updates server's database accordingly.
			else {
				if (buffer[0] == 'F' && buffer[1] == 'L') {
					FirstLoad(clientSocket);
				}
				else if (buffer[0] == 'U' && buffer[1] == 'S') {
					UpdateServer(clientSocket);
				}
				else if (buffer[0] == 'U' && buffer[1] == 'D') {
					UpdateDataBase(clientSocket, buffer);
				}
				else if (buffer[0] == 'U' && buffer[1] == 'U') {
					UpdateUser((buffer + 3));
				}
				else {
					std::cerr << "Unknown command from MobileApp -> " << buffer << std::endl;
				}
			}
		}
	}
}

void Server::FirstLoad(int clientSocket) {

	std::fstream wristBandFile;
	printf("FirstLoad Called\n");

	/**************** START CRIT SECTION *********************/
	//std::unique_lock<std::mutex> lock(mut,std::defer_lock);
	//lock.lock();

	// std::ifstream wristBandFile(fileName);
	int bytesSend;
	wristBandFile.open(fileName, std::fstream::in);
	wristBandFile.clear();


	std::cout << "FirstLoad -> " << fileName << " - " << wristBandFile.good() <<  std::endl;
	if (DEBUG) {
		printf("Printing Database while sending it\n\n");
	}

	for (std::string line; std::getline(wristBandFile, line); ) {

		line += "\n";
		if (DEBUG) {
			std::cout << line.c_str();
		}

		bytesSend = send(clientSocket, line.c_str(), BUFFER_SIZE, DEFAULT);
		if (bytesSend <= 0) {
			std::cerr << "Failed to sent data to Mobile on FirstLoad Function \n";
		}
	}
	wristBandFile.close();

	//lock.unlock();	
	/**************** END CRIT SECTION *********************/

	send(clientSocket, "FL FIN", BUFFER_SIZE, DEFAULT);


	printf("\nFirst Load finished\n\n");
}

void Server::UpdateServer(int clientSocket) {

	int bytesReadSent;
	bool finished = false;
	char buffer[BUFFER_SIZE];
	char* buff2;
	MobileDataPackage mobDataPack;
	std::fstream wristBandFile;
	int  count = 0;
	std::string fin("US FIN");


	printf("UpdateServer Called\n");
	/**************** START CRIT SECTION *********************/
	//std::unique_lock<std::mutex> lock(mut, std::defer_lock);
	//lock.lock();

	wristBandFile.open(fileName, std::fstream::out | std::fstream::app);


	if (DEBUG) {
		printf("Printing all the information recieved\n\n");
	}
	while (!finished) {
		bytesReadSent = recv(clientSocket, buffer, BUFFER_SIZE, DEFAULT);
		if (bytesReadSent <= ERROR) {
			std::cerr << "\nCouldn't read from MobileApp in UpdateDataBase.\n ";
			finished = true;
		}
		else {
			if (fin.compare(buffer) == 0) {
				finished = true;
			}
			else {

				// Write MobDataPack into server.
				if (DEBUG) {
					std::cout << "UpdateServer ->" << buffer;
				}

				wristBandFile.write(buffer, strlen(buffer));
				wristBandFile.flush();
			}
		}
	}
	wristBandFile.close();

	//lock.unlock();
	/**************** END CRIT SECTION *********************/

	printf("\nExiting UpdateServer\n\n");
}

void Server::UpdateDataBase(int clientSocket, std::string updateDate) {
	int bytesSend;
	char s_year[5], s_month[3], s_day[3], s_hour[3], s_min[3], s_sec[3];
	char year[5], month[3], day[3], hour[3], min[3], sec[3];
	char date1[BUFFER_SIZE], date2[BUFFER_SIZE];
	int res;
	bool found = false;


	std::cout << updateDate << std::endl;

	sscanf(updateDate.c_str(), "UD %s %s %s %2s:%2s:%2s", year, month, day, hour, min, sec);
	sprintf(date1, "%s%s%s%s%s%s", year, month, day, hour, min, sec);

	printf("UpdateDatabase Called, requested date %s %s %s %s:%s:%s\n\n", year, month, day, hour, min, sec);
	
	/**************** START CRIT SECTION *********************/
	//std::unique_lock<std::mutex> lock(mut, std::defer_lock);
	//lock.lock();

	std::ifstream wristBandFile(fileName);

	for (std::string line; getline(wristBandFile, line);) {

		if (found) {
			//SEND INFORMATION TO MOBILE
			line += "\n";
			bytesSend = send(clientSocket, line.c_str(), BUFFER_SIZE, DEFAULT);
			if (bytesSend <= ZERO) {
				printf("Failed to send information to Mobile in UpdateDataBase function.\n");
			}
			if (DEBUG) {
				printf("UpdateDatabase -> %s", line.c_str());
			}
		}
		else {
			sscanf(line.c_str(), "%s %s %s %2s:%2s:%2s", s_year, s_month, s_day, s_hour, s_min, s_sec);
			sprintf(date2, "%s%s%s%s%s%s", s_year, s_month, s_day, s_hour, s_min, s_sec);
			res = strcmp(date1, date2);
			if (res == -1) found = true;
		}
	}

	send(clientSocket, "UD FIN", BUFFER_SIZE, DEFAULT);

	wristBandFile.close();
	//lock.unlock();
	/**************** END CRIT SECTION *********************/

	printf("Exiting UpdateDataBase\n");
}

void Server::UpdateUser(std::string newFileName) {

	std::cout << "Update User called with the user name -> " << newFileName << std::endl;

	/**************** START CRIT SECTION *********************/
	//std::unique_lock<std::mutex> lock(mut, std::defer_lock);
	//lock.lock();
	newFileName += ".csv";
	std::fstream source;
	std::fstream dest;

	char copyChar;

	fileName = newFileName;
	if (std::filesystem::exists(DEFAULT_FILENAME) && !std::filesystem::exists(fileName)) { // File does not exits and defaultStorage file exists.


		source.open(DEFAULT_FILENAME, std::fstream::in);
		dest.open(fileName, std::fstream::out);

		if(DEBUG) std::cout << "FILE SWAP" << std::endl;
		
		std::istreambuf_iterator<char> begin_source(source);
		std::istreambuf_iterator<char> end_source;
		std::ostreambuf_iterator<char> begin_dest(dest);
		copy(begin_source, end_source, begin_dest);

		source.close();
		dest.close();	
		remove(DEFAULT_FILENAME);
	}

	//lock.unlock();
	/**************** END CRIT SECTION *********************/
	std::cout << "Update User Function finished" << std::endl;
}

#ifdef _WIN32
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

	std::cout << "\nIP Address -> " << ip << std::endl;
	return ip;
}
#endif

std::string Server::GetDate() {
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