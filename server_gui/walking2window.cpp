#include "walking2window.h"
#include "ui_walking2window.h"
#include <QFile>
#include <QStringList>
#include <QDebug>
#include <QtWidgets/QApplication>
#include <QtWidgets/QMainWindow>
#include <QtCharts/QChartView>
#include <QtCharts/QPieSeries>
#include <QtCharts/QPieSlice>
walking2window::walking2window(QWidget *parent) :
    QDialog(parent),
    ui(new Ui::walking2window)
{
    ui->setupUi(this);
    QStringList wordList;
    wordList=csvReader();
    setupPlot(wordList);
}
QT_CHARTS_USE_NAMESPACE
walking2window::~walking2window()
{
    delete ui;
}
void walking2window::setupPlot(QStringList wordList)
{
    QPieSeries *series = new QPieSeries();
    series->append("Pazartesi",  wordList.value(0).toInt());
    series->append("Salı", wordList.value(1).toInt());
    series->append("Çarşama", wordList.value(2).toInt());
    series->append("Perşembe", wordList.value(3).toInt());
    series->append("Cuma", wordList.value(4).toInt());
    series->append("Cumartesi", wordList.value(5).toInt());
    series->append("Pazar",wordList.value(6).toInt() );

    QPieSlice *slice = series->slices().at(1);
    slice->setExploded();
    slice->setLabelVisible();
    slice->setPen(QPen(Qt::darkGreen, 2));
    slice->setBrush(Qt::green);
    QPieSlice *slice1 = series->slices().at(0);
    slice1->setExploded();
    slice1->setLabelVisible();
    slice1->setPen(QPen(Qt::darkGreen, 2));
    slice1->setBrush(Qt::green);
    QChart *chart = new QChart();
    chart->addSeries(series);
    chart->setTitle("Yürüme Pie Chart");
    chart->legend()->hide();

    QChartView *chartView = new QChartView(chart);
    chartView->setRenderHint(QPainter::Antialiasing);
    chartView->show();
    close();
}
QStringList walking2window::csvReader()
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
