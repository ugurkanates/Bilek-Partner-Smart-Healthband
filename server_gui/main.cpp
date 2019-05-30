#include "mainwindow.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    MainWindow w;
    //sets the size of window
    w.setFixedSize(1000, 400);
    w.move(500,200);
    w.show();
    return a.exec();
}
