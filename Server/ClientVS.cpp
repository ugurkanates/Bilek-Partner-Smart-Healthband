#include "pch.h"
#include <iostream>

#include "client.h"

int main()
{
	BilekPartner testClient = BilekPartner();


	testClient.ConnectToServer();
	printf("Connected to server\n");

	printf("Sending Data to Server\n");
	testClient.SendDataToServer();

}
