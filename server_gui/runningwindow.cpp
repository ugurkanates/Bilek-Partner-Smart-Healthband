#include "runningwindow.h"
#include "ui_runningwindow.h"
#include "mainwindow.h"
runningwindow::runningwindow(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::runningwindow)
{
    ui->setupUi(this);
    QStringList wordList;
    wordList=csvReader();
    setupPlot(wordList);
}

runningwindow::~runningwindow()
{
    delete ui;
}

void runningwindow::on_main_clicked()
{
    close();
    MainWindow *w = new MainWindow();
    w->setAttribute(Qt::WA_DeleteOnClose);
    w->setFixedSize(1000, 400);
    w->move(500,200);
    w->show();
}

void runningwindow::on_exit_clicked()
{
    close();
}
void runningwindow::setupPlot(QStringList wordList)
{
    mon=wordList.value(21).toInt();
    the=wordList.value(22).toInt();
    wed=wordList.value(23).toInt();
    thu=wordList.value(24).toInt();
    fri=wordList.value(25).toInt();
    sat=wordList.value(26).toInt();
    sun=wordList.value(27).toInt();
    QLinearGradient gradient(0, 0, 0, 400);
    gradient.setColorAt(0, QColor(90, 90, 90));
    gradient.setColorAt(0.38, QColor(105, 105, 105));
    gradient.setColorAt(1, QColor(140, 221, 255));
    ui->plot->setBackground(QBrush(gradient));

    // create empty bar chart objects:
    QCPBars *fossil = new QCPBars(ui->plot->xAxis, ui->plot->yAxis);
    fossil->setAntialiased(false);
    fossil->setStackingGap(1);
    // set names and colors:
    fossil->setName("Günlük Koşma Grafiği(dk)");
    fossil->setPen(QPen(QColor(111, 9, 176).lighter(170)));
    fossil->setBrush(QColor(111, 9, 176));
    // prepare x axis with country labels:
    QVector<double> ticks;
    QVector<QString> labels;
    ticks << 1 << 2 << 3 << 4 << 5 << 6 << 7 << 8;
     labels << "(20 Mayıs Pazartesi" << "21 Mayıs Salı" << "22 Mayıs Çarşamba" << "23 Mays Perşembe" << "17 Mayıs Cuma" << "18 Mayıs Cumartesi" << "19 Mayıs Pazar"<<"Haftalık";
    QSharedPointer<QCPAxisTickerText> textTicker(new QCPAxisTickerText);
    textTicker->addTicks(ticks, labels);
    ui->plot->xAxis->setTicker(textTicker);
    ui->plot->xAxis->setTickLabelRotation(60);
    ui->plot->xAxis->setSubTicks(false);
    ui->plot->xAxis->setTickLength(0, 4);
    ui->plot->xAxis->setRange(0, 9);
    ui->plot->xAxis->setBasePen(QPen(Qt::white));
    ui->plot->xAxis->setTickPen(QPen(Qt::white));
    ui->plot->xAxis->grid()->setVisible(true);
    ui->plot->xAxis->grid()->setPen(QPen(QColor(130, 130, 130), 0, Qt::DotLine));
    ui->plot->xAxis->setTickLabelColor(Qt::white);
    ui->plot->xAxis->setLabelColor(Qt::white);

    // prepare y axis:
    int total=mon+the+wed+thu+fri+sat+sun;
    ui->plot->yAxis->setRange(0, 200);
    ui->plot->yAxis->setPadding(5); // a bit more space to the left border
    ui->plot->yAxis->setLabel("Günlük Koşma Grafiği");
    ui->plot->yAxis->setBasePen(QPen(Qt::white));
    ui->plot->yAxis->setTickPen(QPen(Qt::white));
    ui->plot->yAxis->setSubTickPen(QPen(Qt::white));
    ui->plot->yAxis->grid()->setSubGridVisible(true);
    ui->plot->yAxis->setTickLabelColor(Qt::white);
    ui->plot->yAxis->setLabelColor(Qt::white);
    ui->plot->yAxis->grid()->setPen(QPen(QColor(130, 130, 130), 0, Qt::SolidLine));
    ui->plot->yAxis->grid()->setSubGridPen(QPen(QColor(130, 130, 130), 0, Qt::DotLine));

    // Add data:
    QVector<double> fossilData;
    fossilData  << mon << the << wed << thu << fri << sat << sun << total;
    fossil->setData(ticks, fossilData);
    // setup legend:
    ui->plot->legend->setVisible(true);
    ui->plot->axisRect()->insetLayout()->setInsetAlignment(0, Qt::AlignTop|Qt::AlignHCenter);
    ui->plot->legend->setBrush(QColor(255, 255, 255, 100));
    ui->plot->legend->setBorderPen(Qt::NoPen);
    QFont legendFont = font();
    legendFont.setPointSize(15);
    ui->plot->legend->setFont(legendFont);
    ui->plot->setInteractions(QCP::iRangeDrag | QCP::iRangeZoom);
}
QStringList runningwindow::csvReader()
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


