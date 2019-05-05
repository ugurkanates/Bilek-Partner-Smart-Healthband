#include "pch.h"
#include "mobileTest.h"

int main(){
 
	mobileTest testMobile = mobileTest();

	testMobile.ConnectToServer();
	printf("Connected to server \n");

	testMobile.HandshakeWithServer();
	printf("Handshake with server is successful\n");

	printf("Calling FirstLoad from Server\n");
	testMobile.FirstLoad();
	printf("FirstLoad successful\n");
	printf("Generating additional data from App\n");
	testMobile.GenerateData();
	printf("Generation completed\n");

	printf("Calling UpdateServer from Server\n");
	testMobile.UpdateServer();
	printf("UpdateServer successful\n");

	printf("Calling UpdateDatabase from Server\n");
	testMobile.UpdateDatabase();
	printf("UpdateDatabase Successful, closing MobilApp\n");

}
