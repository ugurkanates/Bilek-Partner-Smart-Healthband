#include "pch.h"
#include <iostream>
#include "server.h"


int main(int argc, char** argv){
	Server test = Server();
	int end;
	

#ifdef _WIN32
	test.GetIPAddress();
#endif
	test.StartServer();

	std::cout << "\n*********************\n To shutdown server input any string\n*********************\n";
	std::cin >> end;

	test.CloseServer();
	
	return 0;
}
