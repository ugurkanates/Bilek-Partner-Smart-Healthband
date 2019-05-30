#include "alldatawindow.h"
#include "ui_alldatawindow.h"
#include "mainwindow.h"
alldatawindow::alldatawindow(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::alldatawindow)
{
    ui->setupUi(this);
    QStringList wordList;
    wordList=csvReader();
    setupChart(wordList);
}

alldatawindow::~alldatawindow()
{
    delete ui;
}
void alldatawindow::setupChart(QStringList wordList){
    // set dark background gradient:
    QLinearGradient gradient(0, 0, 0, 400);
    gradient.setColorAt(0, QColor(90, 90, 90));
    gradient.setColorAt(0.38, QColor(105, 105, 105));
    gradient.setColorAt(1, QColor(15, 19, 255));
    ui->plot->setBackground(QBrush(gradient));

    // create empty bar chart objects:
    QCPBars *walking = new QCPBars(ui->plot->xAxis, ui->plot->yAxis);
    QCPBars *stairs = new QCPBars(ui->plot->xAxis, ui->plot->yAxis);
    QCPBars *sitting= new QCPBars(ui->plot->xAxis, ui->plot->yAxis);
    QCPBars *running= new QCPBars(ui->plot->xAxis, ui->plot->yAxis);
    QCPBars *standing= new QCPBars(ui->plot->xAxis, ui->plot->yAxis);
    walking->setAntialiased(false); // gives more crisp, pixel aligned bar borders
    stairs->setAntialiased(false);
    sitting->setAntialiased(false);
    running->setAntialiased(false);
    standing->setAntialiased(false);
    walking->setStackingGap(1);
    stairs->setStackingGap(1);
    sitting->setStackingGap(1);
    running->setStackingGap(1);
    standing->setStackingGap(1);
    // set names and colors:
    sitting->setName("Yürüme");
    sitting->setPen(QPen(QColor(111, 9, 176).lighter(170)));
    sitting->setBrush(QColor(111, 9, 176));
    stairs->setName("Merdiven");
    stairs->setPen(QPen(QColor(250, 170, 20).lighter(150)));
    stairs->setBrush(QColor(250, 170, 20));
    walking->setName("Oturma");
    walking->setPen(QPen(QColor(0, 168, 140).lighter(130)));
    walking->setBrush(QColor(0, 168, 140));
    running->setName("Koşma");
    running->setPen(QPen(QColor(255, 19, 109).lighter(130)));
    running->setBrush(QColor(255, 19, 109));
    standing->setName("Ayakta Durma");
    standing->setPen(QPen(QColor(140, 221, 255).lighter(130)));
    standing->setBrush(QColor(140, 221, 255));
    // stack bars on top of each other:
    stairs->moveAbove(sitting);
    walking->moveAbove(stairs);
    standing->moveAbove(walking);
    running->moveAbove(standing);
    // prepare x axis with country labels:
    QVector<double> ticks;
    QVector<QString> labels;
    ticks << 1 << 2 << 3 << 4 << 5 << 6 << 7;
    labels << "(20 Mayıs Pazartesi" << "21 Mayıs Salı" << "22 Mayıs Çarşamba" << "23 Mays Perşembe" << "17 Mayıs Cuma" << "18 Mayıs Cumartesi" << "19 Mayıs Pazar"<<"Haftalık";
    QSharedPointer<QCPAxisTickerText> textTicker(new QCPAxisTickerText);
    textTicker->addTicks(ticks, labels);
    ui->plot->xAxis->setTicker(textTicker);
    ui->plot->xAxis->setTickLabelRotation(60);
    ui->plot->xAxis->setSubTicks(false);
    ui->plot->xAxis->setTickLength(0, 6);
    ui->plot->xAxis->setRange(0, 8);
    ui->plot->xAxis->setBasePen(QPen(Qt::white));
    ui->plot->xAxis->setTickPen(QPen(Qt::white));
    ui->plot->xAxis->grid()->setVisible(true);
    ui->plot->xAxis->grid()->setPen(QPen(QColor(130, 130, 130), 0, Qt::DotLine));
    ui->plot->xAxis->setTickLabelColor(Qt::white);
    ui->plot->xAxis->setLabelColor(Qt::white);

    // prepare y axis:
    ui->plot->yAxis->setRange(0,600);
    ui->plot->yAxis->setPadding(5); // a bit more space to the left border
    ui->plot->yAxis->setLabel("Haftalık Veri(%)");
    ui->plot->yAxis->setBasePen(QPen(Qt::white));
    ui->plot->yAxis->setTickPen(QPen(Qt::white));
    ui->plot->yAxis->setSubTickPen(QPen(Qt::white));
    ui->plot->yAxis->grid()->setSubGridVisible(true);
    ui->plot->yAxis->setTickLabelColor(Qt::white);
    ui->plot->yAxis->setLabelColor(Qt::white);
    ui->plot->yAxis->grid()->setPen(QPen(QColor(130, 130, 130), 0, Qt::SolidLine));
    ui->plot->yAxis->grid()->setSubGridPen(QPen(QColor(130, 130, 130), 0, Qt::DotLine));

    // Add data:
    QVector<double> sittingData, stairsData, walkingData,runningData,standingData;
    walkingData  << wordList.value(0).toInt() << wordList.value(1).toInt()<<wordList.value(2).toInt()<<wordList.value(3).toInt()<<wordList.value(4).toInt()<<wordList.value(5).toInt()<<wordList.value(6).toInt();
    stairsData << wordList.value(7).toInt() << wordList.value(8).toInt() << wordList.value(9).toInt()<< wordList.value(10).toInt()<< wordList.value(11).toInt()<< wordList.value(12).toInt()<< wordList.value(13).toInt();

    sittingData   <<wordList.value(14).toInt() <<wordList.value(15).toInt()<< wordList.value(16).toInt()<< wordList.value(17).toInt() <<wordList.value(18).toInt() <<wordList.value(19).toInt() <<wordList.value(20).toInt();

    runningData << wordList.value(21).toInt() << wordList.value(22).toInt() <<wordList.value(23).toInt()<<wordList.value(24).toInt()<< wordList.value(25).toInt()<<wordList.value(26).toInt()<<wordList.value(27).toInt();

    standingData <<wordList.value(28).toInt() <<wordList.value(29).toInt()<<wordList.value(30).toInt()<< wordList.value(31).toInt() <<wordList.value(32).toInt() <<wordList.value(33).toInt()<< wordList.value(34).toInt();

    sitting->setData(ticks, sittingData);
    stairs->setData(ticks, stairsData);
    walking->setData(ticks, walkingData);
    running->setData(ticks, runningData);
    standing->setData(ticks, standingData);
    // setup legend:
    ui->plot->legend->setVisible(true);
    ui->plot->axisRect()->insetLayout()->setInsetAlignment(0, Qt::AlignTop|Qt::AlignHCenter);
    ui->plot->legend->setBrush(QColor(255, 255, 255, 100));
    ui->plot->legend->setBorderPen(Qt::NoPen);
    QFont legendFont = font();
    legendFont.setPointSize(10);
    ui->plot->legend->setFont(legendFont);
    ui->plot->setInteractions(QCP::iRangeDrag | QCP::iRangeZoom);
}

void alldatawindow::on_main_clicked()
{
    close();
    MainWindow *w = new MainWindow();
    w->setAttribute(Qt::WA_DeleteOnClose);
    w->setFixedSize(1000, 400);
    w->move(500,200);
    w->show();
}

void alldatawindow::on_exit_clicked()
{
    close();
}
QStringList alldatawindow::csvReader()
{
       QFile file("../read.csv");
       if (!file.open(QIODevice::ReadOnly)) {
           qDebug() << file.errorString();
           exit(0);
       }
       QStringList wordList;
       while (!file.atEnd()) {
           QByteArray line = file.readLine();
          wordList.append(line.split(',').value(0));
          wordList.append(line.split(',').value(1));
          wordList.append(line.split(',').value(2));
          wordList.append(line.split(',').value(3));
          wordList.append(line.split(',').value(4));
          wordList.append(line.split(',').value(5));
          wordList.append(line.split(',').value(6));

       }
       return wordList;
}
