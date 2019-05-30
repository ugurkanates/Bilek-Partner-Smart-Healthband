#-------------------------------------------------
#
# Project created by QtCreator 2019-05-22T23:41:06
#
#-------------------------------------------------

QT       += core gui charts

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets printsupport

TARGET = server_gui
TEMPLATE = app
INSTALLS = target

target.path = $$[QT_INSTALL_EXAMPLES]/charts/piechart


SOURCES += main.cpp\
        mainwindow.cpp \
    walkingwindow.cpp \
    runningwindow.cpp \
    stairswindow.cpp \
    sittingwindow.cpp \
    qcustomplot.cpp \
    standingwindow.cpp \
    alldatawindow.cpp \
    stepwindow.cpp \
    monthwindow.cpp

HEADERS  += mainwindow.h \
    walkingwindow.h \
    runningwindow.h \
    stairswindow.h \
    sittingwindow.h \
    qcustomplot.h \
    standingwindow.h \
    alldatawindow.h \
    stepwindow.h \
    monthwindow.h

FORMS    += mainwindow.ui \
    walkingwindow.ui \
    runningwindow.ui \
    stairswindow.ui \
    sittingwindow.ui \
    standingwindow.ui \
    alldatawindow.ui \
    stepwindow.ui \
    monthwindow.ui

OTHER_FILES += \
    ../Desktop/background.jpg \
    read.csv
