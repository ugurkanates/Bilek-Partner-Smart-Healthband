#include "pch.h"
#include "server.h"

bool Server::serverOn = true;
std::vector<std::thread> Server::clientThreads;
std::string Server::fileName = std::string(DEFAULT_FILENAME);
std::queue<std::string> Server::databasePackageQueue;
std::mutex Server::databaseMutex;
std::mutex Server::logMut;
char Server::lastPackage[BUFFER_SIZE];

float GyroMeasError = PI * (40.0f / 180.0f);     // gyroscope measurement error in rads/s (start at 60 deg/s), then reduce after ~10 s to 3
float beta = sqrt(3.0f / 4.0f) * GyroMeasError;  // compute beta
float GyroMeasDrift = PI * (2.0f / 180.0f);      // gyroscope measurement drift in rad/s/s (start at 0.0 deg/s/s)
float zeta = sqrt(3.0f / 4.0f) * GyroMeasDrift;  // compute zeta, the other free parameter in the Madgwick scheme usually set to a small or zero value
//float beta = 0.041;
//float zeta = 0.015;

float pitch, yaw, roll;
float deltat = 0.4f;
float q[4] = { 1.0f, 0.0f, 0.0f, 0.0f };


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
	WriteToLog(std::string("Server established on ") + GetDate() + std::string("\n"));

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
				WriteToLog(host + std::string(" connected on port ") + service + std::string("\n"));
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

	char buffer[BP_PACK_SIZE];
	int bytesRead;
	bool clientConnected = true;

	memset(buffer, '\0', BP_PACK_SIZE);
	while (serverOn && clientConnected) {
		bytesRead = recv(clientSocket, buffer, BP_PACK_SIZE, DEFAULT);
		if (bytesRead > ZERO) {
			if (buffer[0] == 'B') {
				//Bileklik baðlandý, gerekli fn'leri çaðýrýp verileri kaydetmeye baþla
				std::cout << "~~ BilekPartner connected at " << GetDate() <<".\n\n";
				WriteToLog(std::string("BilekPartner connected at ") + GetDate() + std::string("\n"));
				if (DEBUG_DATA || DEBUG_BP) {
					std::cout << "Recieved buffer -> \n" << buffer << std::endl;
				}
				HandleWristband(clientSocket, buffer );
				WriteToLog(std::string("BilekPartner disconnected at ") + GetDate() + std::string("\n"));
				std::cout << "~~ BilekPartner Disconnected at " << GetDate() << std::endl << std::endl;
				clientConnected = false;
			}
			else if (buffer[0] == 'M') {
				// Mobil baðlandý, gerekli fn'leri çaðýrýp iletiþime baþla.
				std::cout << "\n~~ MobileApp connected at " << GetDate() << ". User -> " << (buffer + 2) << "\n\n";
				WriteToLog(std::string("MobileApp connected at ") + GetDate() + ". User -> " + (buffer + 2) + std::string("\n"));
				UpdateUser((buffer + 2));
				HandleMobile(clientSocket);
				WriteToLog(std::string("MobileApp disconnected at ") + GetDate() + std::string("\n"));
				std::cerr << "\n~~ MobileApp disconnected at " << GetDate() << std::endl;

				clientConnected = false;
			}
			else {
				std::cout << "\nUnknown client connected. Closing socket.\n\n";
				WriteToLog(std::string("Unknown Client connected at ") + GetDate() + std::string("\n"));
				if (DEBUG_DATA) {
					std::cout << buffer << std::endl;
				}
				clientConnected = false;
			}
		}
		else {
			std::cout << "\nConnection Error.\n\n";
			clientConnected = false;
		}
	}
	if (DEBUG_DATA) std::cerr << "---ClientThread finished its job.\n" << std::endl;

#ifdef _WIN32
	shutdown(clientSocket, SD_SEND);
#elif __linux___
	shutdown(clientSocket, SHUT_RDWR);
#endif
}

void Server::HandleWristband(int clientSocket, std::string wristBuffer) {
	char wristBandBuffer[BP_PACK_SIZE + 1];
	char printBuff[BP_PACK_SIZE + 1];

	char tempBuff[BP_PACK_SIZE];
	int bytesReadSent = 0;
	std::fstream wristBandFile;
	char* splitter, *packageSplitter, *strtok_save1;
	char* strtok_save2;
	int counter = 0;
	float pX = 0, pY = 0, pZ = 0, temp = 0, pulse = 0, gX = 0, gY = 0, gZ = 0, devreTemp = 0;
	int battery, packageCount = 0;

	memset(wristBandBuffer, '\0', BP_PACK_SIZE + 1);

	//Send confirmation message
	wristBandBuffer[0] = 'S';
	bytesReadSent = send(clientSocket, wristBandBuffer, strlen(wristBandBuffer), DEFAULT);
	if (bytesReadSent <= ZERO) {
		std::cerr << "\nFailed to sent ConfirmationMessage to BilekPartner\n";
	}
	else { //Confirmation message sent without a problem. Do the job

		//De-format message 
		memset(wristBandBuffer, '\0', BP_PACK_SIZE + 1);
		strcpy(wristBandBuffer, wristBuffer.c_str());
		 // Multiple packages at once will be read
#ifdef _WIN32
			packageSplitter = strtok_s(wristBandBuffer, "\n", &strtok_save1);
#elif __linux__
			packageSplitter = strtok_r(wristBandBuffer, "\n", &strtok_save1);
#endif

		counter = 0;
		while (packageSplitter != NULL) {

			//printf("Package -> %s ||||\n", packageSplitter);


#ifdef _WIN32
		//	splitter = strtok_s(packageSplitter, "_", &strtok_save2);
#elif __linux__
			char* strtok_save2;
			splitter = strtok_r(packageSplitter, "_", &strtok_save2);
#endif
			sscanf(packageSplitter, "B_%f_%f_%f_%f_%f_%f_%f_%f_%d_%f", &pX,&pY,&pZ,&gX,&gY,&gZ,&pulse,&temp,&battery,&devreTemp );
			
			++packageCount;
			counter = 0;

			//Put package into the queue
			sprintf(tempBuff, "%s,%f,%f,%f,%f,%f,%f,%f,%f,%d,%f\n\0", GetDate().c_str(), pX, pY, pZ, gX, gY, gZ, pulse, temp, battery, devreTemp );
			databasePackageQueue.push(tempBuff);

			/*
			gX /= 131;
			gY /= 131;
			gZ /= 131;
			*/


			MadgwickQuaternionUpdate(pX, pY, pZ, gX, gY, gZ);

			if (DEBUG_DATA || DEBUG_BP) {			
				sprintf(printBuff, "%s\naX = %f , aY = %f , aZ = %f \ngX = %f , gY = %f , gZ = %f    Pulse = %f , Temp = %f , Batt = %d , Temp2 = %f\n", GetDate().c_str(), pX,pY,pZ,gX,gY,gZ,pulse,temp,battery,devreTemp);
				std::cout << "\nBilekPartner " << tempBuff << printBuff;
			}

			yaw = atan2(2.0f * (q[1] * q[2] + q[0] * q[3]), q[0] * q[0] + q[1] * q[1] - q[2] * q[2] - q[3] * q[3]);
			pitch = -asin(2.0f * (q[1] * q[3] - q[0] * q[2]));
			roll = atan2(2.0f * (q[0] * q[1] + q[2] * q[3]), q[0] * q[0] - q[1] * q[1] - q[2] * q[2] + q[3] * q[3]);
			
			pitch *= 180.0f / PI;
			yaw *= 180.0f / PI;
			roll *= 180.0f / PI;


			if (DEBUG_DATA || DEBUG_BP) {

				sprintf(tempBuff, "Pitch (X) -> %f - Roll (Y) -> %f - Yaw (Z) -> %f", pitch, roll ,yaw);
				std::cout << tempBuff << std::endl;
			}



			//Check Data lock & then print to file.

			/******** START CRITICAL SECTION ********/
			//if (DEBUG_ACTIVITY) std::cout << "#### BilekPartner Thread entering critical section" << std::endl;
			std::unique_lock<std::mutex> lock(databaseMutex, std::defer_lock);
			lock.lock();

			wristBandFile.open(fileName, std::fstream::out | std::fstream::app);
			strcpy(tempBuff, (databasePackageQueue.front()).c_str());

			memset(lastPackage, '\0', BUFFER_SIZE);
			strcpy(lastPackage, tempBuff);


			if (DEBUG_DATA || DEBUG_BP) std::cout << "BP TO FILE -> " << tempBuff;

			databasePackageQueue.pop();
			wristBandFile.write(tempBuff, strlen(tempBuff));
			wristBandFile.flush();
			wristBandFile.close();

			//if (DEBUG_ACTIVITY) std::cout << "#### BilekPartner Thread exiting critical section" << std::endl;
			lock.unlock();
			//if (DEBUG_ACTIVITY) std::cout << "#### BilekPartner Thread exited critical section" << std::endl;

			/******** END CRITICAL SECTION ********/


#ifdef _WIN32
			packageSplitter = strtok_s(NULL, "\n", &strtok_save1);
#elif __linux__
			packageSplitter = strtok_r(NULL, "\n", &strtok_save1);
#endif
	
		}

		sprintf(wristBandBuffer, "Recieved %d packages from BilekPartner at %s\n", packageCount, GetDate().c_str());
		WriteToLog(wristBandBuffer);
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


	/*strcpy(secondBuff, "S\0");
	//Send confirmation message
	bytesReadSent = send(clientSocket, secondBuff, strlen(secondBuff), DEFAULT);
	if (bytesReadSent <= ZERO) {
		std::cerr << "\nFailed to sent ConfirmationMessage to MobileApp\n";

	}
	else { *///Confirmation message sent without a problem. Do the job

	while (serverOn && clientConnected) {
		bytesReadSent = recv(clientSocket, buffer, BUFFER_SIZE, 0);
		if (bytesReadSent <= 0) {
			clientConnected = false;
		}
		//List of commands that can be reecieved from mobilApp
		// "FL" -> "FirstLoad", sends all server dataBase to mobileApp
		// "UD" -> "UpdateDataBase", takes a date and sends all the packages recieved after the given date to mobileApp
		// "US" -> "UpdateServer", takes packages from mobileApp to UpdateServer
		// "UU *UserName*" -> "UpdateUser", takes an user name and updates server's database accordingly.
		// "GL" -> "GetLastPackage", send last package written to database to mobileApp
		// "GB Date1 Date" -> "GetBetweenDates", sends packages that are between given dates.
		else {
			if (DEBUG_DATA) std::cout << "Mobile command " << buffer << std::endl;
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
			else if (buffer[0] == 'G' && buffer[1] == 'L') {
				GetLastPackage(clientSocket);
			}
			else if (buffer[0] == 'G' && buffer[1] == 'B') {
				GetBetweenDates(clientSocket, buffer);
			}
			else {
				std::cerr << "Unknown command from MobileApp -> " << buffer << std::endl;
				clientConnected = false;
			}
		}
	}
	//}
}

void Server::FirstLoad(int clientSocket) {
	bool clientConnected = true;

	std::fstream wristBandFile;
	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread entered FirstLoad" << std::endl;

	WriteToLog(std::string("Mobile called FirstLoad at ") + GetDate() + std::string("\n"));

	/**************** START CRIT SECTION *********************/
	std::unique_lock<std::mutex> lock(databaseMutex, std::defer_lock);
	lock.lock();

	// std::ifstream wristBandFile(fileName);
	int bytesSend;
	wristBandFile.open(fileName, std::fstream::in);
	wristBandFile.clear();


	if (DEBUG_DATA) printf("Printing Database while sending it. Database name : %s\n\n", fileName.c_str());


	for (std::string line; std::getline(wristBandFile, line) && clientConnected; ) {

		line += "\n";
		if (DEBUG_DATA)	std::cout << line.c_str();


		bytesSend = send(clientSocket, line.c_str(), strlen(line.c_str()) +1, DEFAULT);
		if (bytesSend <= 0) {
			std::cerr << "Failed to sent data to Mobile on FirstLoad Function \n";
			clientConnected = false;
		}
	}
	wristBandFile.close();

	lock.unlock();
	/**************** END CRIT SECTION *********************/

	send(clientSocket, "FL FIN", 7, DEFAULT);

	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread finished FirstLoad" << std::endl;

}

void Server::UpdateServer(int clientSocket) {

	int bytesReadSent;
	bool finished = false;
	char buffer[BUFFER_SIZE];
	char* buff2;
	std::fstream wristBandFile;
	int  count = 0;
	std::string fin("US FIN");

	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread entered UpdateServer" << std::endl;
	WriteToLog(std::string("MobileApp called server at " ) + GetDate() + std::string("\n")	);


	/**************** START CRIT SECTION *********************/
	std::unique_lock<std::mutex> lock(databaseMutex, std::defer_lock);
	lock.lock();

	wristBandFile.open(fileName, std::fstream::out | std::fstream::app);

	if (DEBUG_DATA) printf("Printing all the information recieved\n\n");

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
				if (DEBUG_DATA) std::cout << "UpdateServer ->" << buffer;

				wristBandFile.write(buffer, strlen(buffer));
				wristBandFile.flush();
			}
		}
	}
	wristBandFile.close();

	lock.unlock();
	/**************** END CRIT SECTION *********************/

	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread finished UpdateServer " << std::endl;
}

void Server::UpdateDataBase(int clientSocket, std::string updateDate) {
	int bytesSend;
	char s_year[5], s_month[3], s_day[3], s_hour[3], s_min[3], s_sec[3];
	char year[5], month[3], day[3], hour[3], min[3], sec[3];
	char date1[BUFFER_SIZE], date2[BUFFER_SIZE];
	int res;
	bool found = false;
	int packageCount = 0;
	bool clientConnected = true;

	sscanf(updateDate.c_str(), "UD %4s-%2s-%2s %2s:%2s:%2s", year, month, day, hour, min, sec);
	sprintf(date1, "%s%s%s%s%s%s", year, month, day, hour, min, sec);

	if (DEBUG_ACTIVITY) printf("#### Mobile Thread entered UpdateDataBase, requested date %s %s %s %s:%s:%s\n", year, month, day, hour, min, sec);
	WriteToLog(std::string("MobileApp called UpdateDataBase at ") + GetDate() + std::string(". Requested date ") + updateDate);


	/**************** START CRIT SECTION *********************/
	std::unique_lock<std::mutex> lock(databaseMutex, std::defer_lock);
	lock.lock();


	std::ifstream wristBandFile(fileName);

	for (std::string line; getline(wristBandFile, line) && clientConnected;) {

		if (found) {
			//SEND INFORMATION TO MOBILE
			line += "\n";
			++packageCount;
			bytesSend = send(clientSocket, line.c_str(), strlen(line.c_str())+1, DEFAULT);
			if (bytesSend <= ZERO) {
				printf("Failed to send information to Mobile in UpdateDataBase function.\n");
				clientConnected = false;
			}
			if (DEBUG_DATA) printf("UpdateDatabase -> %s", line.c_str());
		}
		else {
			sscanf(line.c_str(), "%4s-%2s-%2s %2s:%2s:%2s", s_year, s_month, s_day, s_hour, s_min, s_sec);
			sprintf(date2, "%s%s%s%s%s%s", s_year, s_month, s_day, s_hour, s_min, s_sec);
			res = strcmp(date1, date2);
			if (res == -1) found = true;
		}
	}

	if(send(clientSocket, "UD FIN", 7, DEFAULT) <= ERROR ) {
		std::cout << "Failed to send UD Confirmation message to MobileApp\n";
	}

	sprintf(date1, "Server sent %d packages to MobileApp\n", packageCount);
	WriteToLog(date1);

	wristBandFile.close();
	lock.unlock();
	/**************** END CRIT SECTION *********************/
	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread finished UpdateDataBase" << std::endl;
}

void Server::UpdateUser(std::string newFileName) {
	bool condCheck = false;
	std::fstream source;
	std::fstream dest;
	char temp[BUFFER_SIZE];
	bool fileExists = false;

	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread entered UpdateUser" << std::endl;
	if (DEBUG_DATA) std::cout << "Update User called with the user name -> " << newFileName << std::endl;
	WriteToLog(std::string("MobileApp called UpdateUser at ") + GetDate() + std::string(". Requested UserName ") + newFileName);

	sscanf(newFileName.c_str(), "%s\n", temp);
	
	/**************** START CRIT SECTION *********************/
	std::unique_lock<std::mutex> lock(databaseMutex, std::defer_lock);
	lock.lock();

	newFileName = temp;
	newFileName += ".csv";
	fileName = newFileName;

#ifdef _WIN32
	if (std::filesystem::exists(DEFAULT_FILENAME) && !std::filesystem::exists(fileName)) fileExists = true;
	
#elif __linux__
	if (std::experimental::filesystem::exists(DEFAULT_FILENAME) && !std::experimental::filesystem::exists(fileName)) fileExists = true; // File does not exits and defaultStorage file exists.
#endif

	if(fileExists){
			// File does not exits and defaultStorage file exists.


		source.open(DEFAULT_FILENAME, std::fstream::in);
		dest.open(fileName, std::fstream::out);

		if (DEBUG_DATA) std::cout << "FILE SWAP" << std::endl;

		std::istreambuf_iterator<char> begin_source(source);
		std::istreambuf_iterator<char> end_source;
		std::ostreambuf_iterator<char> begin_dest(dest);
		copy(begin_source, end_source, begin_dest);

		source.close();
		dest.close();
		remove(DEFAULT_FILENAME);
	}

	std::cout << "FileSwap FIN" << std::endl;
	lock.unlock();
	/**************** END CRIT SECTION *********************/
	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread finished UpdateUser" << std::endl;
}

void Server::GetLastPackage(int clientSocket){

	int bytesSent = 0;

	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread entered GetLastPackage";
	if (DEBUG_DATA) std::cout << "LastPackage -> " << lastPackage << std::endl;
	
	WriteToLog(std::string("Mobile called GetLastPackage at ") + GetDate() + std::string("\n"));

	bytesSent = send(clientSocket, lastPackage, sizeof(lastPackage), DEFAULT);
	if (bytesSent <= 0) {
		std::cout << "Error sending Last Package to mobile app." << std::endl;;
	}

	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread finished GetLastPackage" << std::endl;
}

void Server::WriteToLog(std::string currLog){

	std::fstream logFile;

	/******************** START CRITICAL SECTION ****************************/
	std::unique_lock<std::mutex> lock(logMut, std::defer_lock);
	lock.lock();

	logFile.open(LOG_FILENAME, std::fstream::in | std::fstream::app);

	logFile.write(currLog.c_str(), strlen(currLog.c_str()));
	logFile.flush();

	logFile.close();

	lock.unlock();
	/********************** END CRITICAL SECTION *****************************/

}

void Server::GetBetweenDates(int clientSocket, std::string betweenDate){

	int bytesSend;
	char s_year[5], s_month[3], s_day[3], s_hour[3], s_min[3], s_sec[3];
	char e_year[5], e_month[3], e_day[3], e_hour[3], e_min[3], e_sec[3];
	char year[5], month[3], day[3], hour[3], min[3], sec[3];
	char date1[BUFFER_SIZE], date2[BUFFER_SIZE], date3[BUFFER_SIZE];
	int res;
	bool found = false;
	int packageCount = 0;

	std::cout << "Request -> " << betweenDate;

	sscanf(betweenDate.c_str(), "GB %4s-%2s-%2s %2s:%2s:%2s %4s-%2s-%2s %2s:%2s:%2s", year, month, day, hour, min, sec, e_year, e_month, e_day, e_hour, e_min, e_sec);
	sprintf(date1, "%s%s%s%s%s%s\0", year, month, day, hour, min, sec);
	sprintf(date2, "%s%s%s%s%s%s\0", e_year, e_month, e_day, e_hour, e_min, e_sec);

	if (DEBUG_ACTIVITY) printf("#### Mobile Thread entered GetBetwenDates, requested date scope %s %s %s %s:%s:%s <-> %s %s %s %s:%s:%s \n", year, month, day, hour, min, sec, e_year, e_month, e_day, e_hour, e_min, e_sec);
	WriteToLog(std::string("MobileApp called GetBetweenDates at ") + GetDate() + std::string(". Requested date scope ") + date1 + " <-> " + date2);


	/**************** START CRIT SECTION *********************/
	std::unique_lock<std::mutex> lock(databaseMutex, std::defer_lock);
	lock.lock();

	std::ifstream wristBandFile(fileName);

	for (std::string line; getline(wristBandFile, line);) {

		sscanf(line.c_str(), "%4s-%2s-%2s %2s:%2s:%2s", s_year, s_month, s_day, s_hour, s_min, s_sec);
		sprintf(date3, "%s%s%s%s%s%s", s_year, s_month, s_day, s_hour, s_min, s_sec);
		res = strcmp(date1, date3);
		if (res == -1) found = true;
		res = strcmp(date2, date3);
		if (res == -1) found = false;

		if (found) {
			//SEND INFORMATION TO MOBILE
			line += "\n";
			++packageCount;
			bytesSend = send(clientSocket, line.c_str(), strlen(line.c_str()) + 1, DEFAULT);
			if (bytesSend <= ZERO) {
				printf("Failed to send information to Mobile in GetBetweenDates function.\n");
			}
			if (DEBUG_DATA) printf("GetBetweenDates -> %s", line.c_str());

		}
	}

	if (send(clientSocket, "GB FIN", 7, DEFAULT) <= ERROR) {
		std::cout << "Failed to send UD Confirmation message to MobileApp\n";
	}

	sprintf(date1, "Server sent %d packages to MobileApp\n", packageCount);
	WriteToLog(date1);

	wristBandFile.close();
	lock.unlock();
	/**************** END CRIT SECTION *********************/
	if (DEBUG_ACTIVITY) std::cout << "#### Mobile Thread finished GetBetweenDates" << std::endl;



}

void Server::MadgwickQuaternionUpdate(double ax, double ay, double az, double gx, double gy, double gz){
	float q1 = q[0], q2 = q[1], q3 = q[2], q4 = q[3];         // short name local variable for readability
	float norm;                                               // vector norm
	float f1, f2, f3;                                         // objetive funcyion elements
	float J_11or24=0, J_12or23=0, J_13or22=0, J_14or21=0, J_32=0, J_33=0; // objective function Jacobian elements
	float qDot1=0, qDot2=0, qDot3=0, qDot4=0;
	float hatDot1=0, hatDot2=0, hatDot3=0, hatDot4=0;
	float gerrx=0, gerry=0, gerrz=0, gbiasx = 0, gbiasy = 0, gbiasz = 0;        // gyro bias error

	// Auxiliary variables to avoid repeated arithmetic
	float _halfq1 = 0.5f * q1;
	float _halfq2 = 0.5f * q2;
	float _halfq3 = 0.5f * q3;
	float _halfq4 = 0.5f * q4;
	float _2q1 = 2.0f * q1;
	float _2q2 = 2.0f * q2;
	float _2q3 = 2.0f * q3;
	float _2q4 = 2.0f * q4;
	float _2q1q3 = 2.0f * q1 * q3;
	float _2q3q4 = 2.0f * q3 * q4;

	// Normalise accelerometer measurement
	norm = sqrt(ax * ax + ay * ay + az * az);
	if (norm == 0.0f) return; // handle NaN
	norm = 1.0f / norm;
	ax *= norm;
	ay *= norm;
	az *= norm;

	// Compute the objective function and Jacobian
	f1 = _2q2 * q4 - _2q1 * q3 - ax;
	f2 = _2q1 * q2 + _2q3 * q4 - ay;
	f3 = 1.0f - _2q2 * q2 - _2q3 * q3 - az;
	J_11or24 = _2q3;
	J_12or23 = _2q4;
	J_13or22 = _2q1;
	J_14or21 = _2q2;
	J_32 = 2.0f * J_14or21;
	J_33 = 2.0f * J_11or24;

	// Compute the gradient (matrix multiplication)
	hatDot1 = J_14or21 * f2 - J_11or24 * f1;
	hatDot2 = J_12or23 * f1 + J_13or22 * f2 - J_32 * f3;
	hatDot3 = J_12or23 * f2 - J_33 * f3 - J_13or22 * f1;
	hatDot4 = J_14or21 * f1 + J_11or24 * f2;

	// Normalize the gradient
	norm = sqrt(hatDot1 * hatDot1 + hatDot2 * hatDot2 + hatDot3 * hatDot3 + hatDot4 * hatDot4);
	hatDot1 /= norm;
	hatDot2 /= norm;
	hatDot3 /= norm;
	hatDot4 /= norm;

	// Compute estimated gyroscope biases
	gerrx = _2q1 * hatDot2 - _2q2 * hatDot1 - _2q3 * hatDot4 + _2q4 * hatDot3;
	gerry = _2q1 * hatDot3 + _2q2 * hatDot4 - _2q3 * hatDot1 - _2q4 * hatDot2;
	gerrz = _2q1 * hatDot4 - _2q2 * hatDot3 + _2q3 * hatDot2 - _2q4 * hatDot1;

	// Compute and remove gyroscope biases
	gbiasx += gerrx * deltat * zeta;
	gbiasy += gerry * deltat * zeta;
	gbiasz += gerrz * deltat * zeta;
	gx -= gbiasx;
	gy -= gbiasy;
	gz -= gbiasz;

	// Compute the quaternion derivative
	qDot1 = -_halfq2 * gx - _halfq3 * gy - _halfq4 * gz;
	qDot2 = _halfq1 * gx + _halfq3 * gz - _halfq4 * gy;
	qDot3 = _halfq1 * gy - _halfq2 * gz + _halfq4 * gx;
	qDot4 = _halfq1 * gz + _halfq2 * gy - _halfq3 * gx;
	// Compute then integrate estimated quaternion derivative
	q1 += (qDot1 - (beta * hatDot1)) * deltat;
	q2 += (qDot2 - (beta * hatDot2)) * deltat;
	q3 += (qDot3 - (beta * hatDot3)) * deltat;
	q4 += (qDot4 - (beta * hatDot4)) * deltat;

	// Normalize the quaternion
	norm = sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);    // normalise quaternion
	norm = 1.0f / norm;
	q[0] = q1 * norm;
	q[1] = q2 * norm;
	q[2] = q3 * norm;
	q[3] = q4 * norm;
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
	date = std::to_string(1900 + ti->tm_year) + "-";
	//Month
	if (ti->tm_mon + 1 < 10) {
		date += "0" + std::to_string(ti->tm_mon + 1) + "-";
	}
	else {
		date += std::to_string(ti->tm_mon + 1) + "-";
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



/*
if (splitter != NULL)	std::cout << splitter << std::endl;

// pX - pY - pZ - gX - gY - gZ - pulse - temp - battery(int) - devreSıcak

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
	case 3:	//gX
		gX = atof(splitter);
		break;
	case 4:	//gY
		gY= atof(splitter);
		break;
	case 5:	//gZ
		gZ = atof(splitter);
		break;
	case 6: //pulse
		pulse = atof(splitter);
		break;
	case 7: //temp
		temp = atof(splitter);
		break;
	case 8: //battery
		battery = atoi(splitter);
		break;
	case 9: //devreTemp
		devreTemp = atof(splitter);
		break;
	default:
		printf("Error on reading data from BilekPartner\n");
		printf("Error Buffer -> %s - %d~~\n~~\n", splitter, counter);
		break;
	}
	++counter;

#ifdef _WIN32
				splitter = strtok_s(NULL, "_", &strtok_save2);Kapa
				if (splitter != NULL)	std::cout << splitter << std::endl;
#elif __linux__
				splitter = strtok_r(NULL, "_", &strtok_save2);
#endif
			}*/