#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "walkingwindow.h"
#include "sittingwindow.h"
#include "stairswindow.h"
#include "standingwindow.h"
#include "alldatawindow.h"
#include "stepwindow.h"
#include "monthwindow.h"
#include "walking2window.h"
#include <QApplication>
#include <QFile>
#include <QStringList>
#include <QDebug>
MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
}
QT_CHARTS_USE_NAMESPACE
MainWindow::~MainWindow()
{
    delete ui;
}
//close the apps
void MainWindow::on_exit_clicked()
{
  close();
}
//new activity for walking
void MainWindow::on_walk_clicked()
{
    hide();
    walkingwindow ww;
    ww.setModal(true);
    ww.exec();

}
//new activity for running
void MainWindow::on_run_clicked()
{
    hide();
    runningwindow rw;
    rw.setModal(true);
    rw.exec();
}


//new activity for stairs
void MainWindow::on_stairs_clicked()
{
    hide();
    stairswindow sw;
    sw.setModal(true);
    sw.exec();
}
//new activity for sitting
void MainWindow::on_sitting_clicked()
{
    hide();
    sittingwindow sw;
    sw.setModal(true);
    sw.exec();
}


void MainWindow::on_stand_clicked()
{
    hide();
    standingwindow sw;
    sw.setModal(true);
    sw.exec();
}

void MainWindow::on_stand_2_clicked()
{
    hide();
    alldatawindow aw;
    aw.setModal(true);
    aw.exec();
}
void MainWindow::on_step_clicked()
{
    hide();
    stepwindow sw;
    sw.setModal(true);
    sw.exec();
}

void MainWindow::on_month_clicked()
{
    hide();
    monthwindow sw;
    sw.setModal(true);
    sw.exec();
}

void MainWindow::on_walk2_clicked()
{
    QStringList wordList;
    wordList=csvReader();
    setupPlot1(wordList);
}
void MainWindow::setupPlot1(QStringList wordList)
{
    QPieSeries *series = new QPieSeries();
    series->append("Pazartesi",  wordList.value(0).toInt());
    series->append("Salı", wordList.value(1).toInt());
    series->append("Çarşama", wordList.value(2).toInt());
    series->append("Perşembe", wordList.value(3).toInt());
    series->append("Cuma", wordList.value(4).toInt());
    series->append("Cumartesi", wordList.value(5).toInt());
    series->append("Pazar",wordList.value(6).toInt() );

    QPieSlice *slice = series->slices().at(2);
    slice->setExploded();
    slice->setLabelVisible();
    slice->setPen(QPen(Qt::darkGreen, 2));
    slice->setBrush(Qt::green);
    QPieSlice *slice1 = series->slices().at(0);
    slice1->setExploded();
    slice1->setLabelVisible();
    slice1->setPen(QPen(Qt::blue, 2));
    slice1->setBrush(Qt::blue);
    QPieSlice *slice2 = series->slices().at(4);
    slice2->setExploded();
    slice2->setLabelVisible();
    slice2->setPen(QPen(Qt::red, 2));
    slice2->setBrush(Qt::red);
    QChart *chart = new QChart();
    chart->addSeries(series);
    chart->setTitle("Yürüme Pie Chart");
    chart->legend()->hide();
    series->setLabelsVisible();
    series->setLabelsPosition(QPieSlice::LabelInsideHorizontal);
    for(auto slice : series->slices())
        slice->setLabel(QString("%1%").arg(100*slice->percentage(), 0, 'f', 1));
    QChartView *chartView = new QChartView(chart);
    chartView->setRenderHint(QPainter::Antialiasing);
    chartView->setMinimumWidth(600);
    chartView->setMinimumHeight(600);
    chartView->show();
}
QStringList MainWindow::csvReader()
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

void MainWindow::on_run2_clicked()
{
    QStringList wordList;
    wordList=csvReader();
    setupPlot2(wordList);
}
void MainWindow::setupPlot2(QStringList wordList){
    QPieSeries *series = new QPieSeries();
    series->append("Pazartesi",  wordList.value(21).toInt());
    series->append("Salı", wordList.value(22).toInt());
    series->append("Çarşama", wordList.value(23).toInt());
    series->append("Perşembe", wordList.value(24).toInt());
    series->append("Cuma", wordList.value(25).toInt());
    series->append("Cumartesi", wordList.value(26).toInt());
    series->append("Pazar",wordList.value(27).toInt() );

    QPieSlice *slice = series->slices().at(2);
    slice->setExploded();
    slice->setLabelVisible();
    slice->setPen(QPen(Qt::darkGreen, 2));
    slice->setBrush(Qt::green);
    QPieSlice *slice1 = series->slices().at(0);
    slice1->setExploded();
    slice1->setLabelVisible();
    slice1->setPen(QPen(Qt::blue, 2));
    slice1->setBrush(Qt::blue);
    QPieSlice *slice2 = series->slices().at(4);
    slice2->setExploded();
    slice2->setLabelVisible();
    slice2->setPen(QPen(Qt::red, 2));
    slice2->setBrush(Qt::red);
    QChart *chart = new QChart();
    chart->addSeries(series);
    chart->setTitle("Koşma Pie Chart");
    chart->legend()->hide();
    series->setLabelsVisible();
    series->setLabelsPosition(QPieSlice::LabelInsideHorizontal);
    for(auto slice : series->slices())
        slice->setLabel(QString("%1%").arg(100*slice->percentage(), 0, 'f', 1));
    QChartView *chartView = new QChartView(chart);
    chartView->setRenderHint(QPainter::Antialiasing);
    chartView->setMinimumWidth(600);
    chartView->setMinimumHeight(600);
    chartView->show();
}
void MainWindow::setupPlot3(QStringList wordList){
    QPieSeries *series = new QPieSeries();
    series->append("Pazartesi",  wordList.value(14).toInt());
    series->append("Salı", wordList.value(15).toInt());
    series->append("Çarşama", wordList.value(16).toInt());
    series->append("Perşembe", wordList.value(17).toInt());
    series->append("Cuma", wordList.value(18).toInt());
    series->append("Cumartesi", wordList.value(19).toInt());
    series->append("Pazar",wordList.value(20).toInt() );

    QPieSlice *slice = series->slices().at(2);
    slice->setExploded();
    slice->setLabelVisible();
    slice->setPen(QPen(Qt::darkGreen, 2));
    slice->setBrush(Qt::green);
    QPieSlice *slice1 = series->slices().at(0);
    slice1->setExploded();
    slice1->setLabelVisible();
    slice1->setPen(QPen(Qt::blue, 2));
    slice1->setBrush(Qt::blue);
    QPieSlice *slice2 = series->slices().at(4);
    slice2->setExploded();
    slice2->setLabelVisible();
    slice2->setPen(QPen(Qt::red, 2));
    slice2->setBrush(Qt::red);
    QChart *chart = new QChart();
    chart->addSeries(series);
    chart->setTitle("Oturma Pie Chart");
    chart->legend()->hide();
    series->setLabelsVisible();
    series->setLabelsPosition(QPieSlice::LabelInsideHorizontal);
    for(auto slice : series->slices())
        slice->setLabel(QString("%1%").arg(100*slice->percentage(), 0, 'f', 1));
    QChartView *chartView = new QChartView(chart);
    chartView->setRenderHint(QPainter::Antialiasing);
    chartView->setMinimumWidth(600);
    chartView->setMinimumHeight(600);
    chartView->show();
}
void MainWindow::setupPlot4(QStringList wordList){
    QPieSeries *series = new QPieSeries();
    series->append("Pazartesi",  wordList.value(28).toInt());
    series->append("Salı", wordList.value(29).toInt());
    series->append("Çarşama", wordList.value(30).toInt());
    series->append("Perşembe", wordList.value(31).toInt());
    series->append("Cuma", wordList.value(32).toInt());
    series->append("Cumartesi", wordList.value(33).toInt());
    series->append("Pazar",wordList.value(34).toInt() );

    QPieSlice *slice = series->slices().at(2);
    slice->setExploded();
    slice->setLabelVisible();
    slice->setPen(QPen(Qt::darkGreen, 2));
    slice->setBrush(Qt::green);
    QPieSlice *slice1 = series->slices().at(0);
    slice1->setExploded();
    slice1->setLabelVisible();
    slice1->setPen(QPen(Qt::blue, 2));
    slice1->setBrush(Qt::blue);
    QPieSlice *slice2 = series->slices().at(4);
    slice2->setExploded();
    slice2->setLabelVisible();
    slice2->setPen(QPen(Qt::red, 2));
    slice2->setBrush(Qt::red);
    QChart *chart = new QChart();
    chart->addSeries(series);
    chart->setTitle("Ayakta Durma Pie Chart");
    chart->legend()->hide();
    series->setLabelsVisible();
    series->setLabelsPosition(QPieSlice::LabelInsideHorizontal);
    for(auto slice : series->slices())
        slice->setLabel(QString("%1%").arg(100*slice->percentage(), 0, 'f', 1));
    QChartView *chartView = new QChartView(chart);
    chartView->setRenderHint(QPainter::Antialiasing);
    chartView->setMinimumWidth(600);
    chartView->setMinimumHeight(600);
    chartView->show();
}
void MainWindow::setupPlot5(QStringList wordList){
    QPieSeries *series = new QPieSeries();
    series->append("Pazartesi",  wordList.value(35).toInt());
    series->append("Salı", wordList.value(36).toInt());
    series->append("Çarşama", wordList.value(37).toInt());
    series->append("Perşembe", wordList.value(38).toInt());
    series->append("Cuma", wordList.value(39).toInt());
    series->append("Cumartesi", wordList.value(40).toInt());
    series->append("Pazar",wordList.value(41).toInt() );

    QPieSlice *slice = series->slices().at(2);
    slice->setExploded();
    slice->setLabelVisible();
    slice->setPen(QPen(Qt::darkGreen, 2));
    slice->setBrush(Qt::green);
    QPieSlice *slice1 = series->slices().at(0);
    slice1->setExploded();
    slice1->setLabelVisible();
    slice1->setPen(QPen(Qt::blue, 2));
    slice1->setBrush(Qt::blue);
    QPieSlice *slice2 = series->slices().at(4);
    slice2->setExploded();
    slice2->setLabelVisible();
    slice2->setPen(QPen(Qt::red, 2));
    slice2->setBrush(Qt::red);
    QChart *chart = new QChart();
    chart->addSeries(series);
    chart->setTitle("Adım Sayısı Pie Chart");
    chart->legend()->hide();
    series->setLabelsVisible();
    series->setLabelsPosition(QPieSlice::LabelInsideHorizontal);
    for(auto slice : series->slices())
        slice->setLabel(QString("%1%").arg(100*slice->percentage(), 0, 'f', 1));
    QChartView *chartView = new QChartView(chart);
    chartView->setRenderHint(QPainter::Antialiasing);
    chartView->setMinimumWidth(600);
    chartView->setMinimumHeight(600);
    chartView->show();
}
void MainWindow::setupPlot6(QStringList wordList){
    QPieSeries *series = new QPieSeries();
    series->append("Pazartesi",  wordList.value(7).toInt());
    series->append("Salı", wordList.value(8).toInt());
    series->append("Çarşama", wordList.value(9).toInt());
    series->append("Perşembe", wordList.value(10).toInt());
    series->append("Cuma", wordList.value(11).toInt());
    series->append("Cumartesi", wordList.value(12).toInt());
    series->append("Pazar",wordList.value(13).toInt() );

    QPieSlice *slice = series->slices().at(2);
    slice->setExploded();
    slice->setLabelVisible();
    slice->setPen(QPen(Qt::darkGreen, 2));
    slice->setBrush(Qt::green);
    QPieSlice *slice1 = series->slices().at(0);
    slice1->setExploded();
    slice1->setLabelVisible();
    slice1->setPen(QPen(Qt::blue, 2));
    slice1->setBrush(Qt::blue);
    QPieSlice *slice2 = series->slices().at(4);
    slice2->setExploded();
    slice2->setLabelVisible();
    slice2->setPen(QPen(Qt::red, 2));
    slice2->setBrush(Qt::red);
    QChart *chart = new QChart();

    chart->setTitle("Merdiven Pie Chart");
    chart->legend()->hide();
    series->setLabelsVisible();
    series->setLabelsPosition(QPieSlice::LabelInsideHorizontal);
    chart->addSeries(series);
    for(auto slice : series->slices())
        slice->setLabel(QString("%1%").arg(100*slice->percentage(), 0, 'f', 1));

    QChartView *chartView = new QChartView(chart);
    chartView->setRenderHint(QPainter::Antialiasing);
    chartView->setMinimumWidth(600);
    chartView->setMinimumHeight(600);
    chartView->show();
}
void MainWindow::on_sitting2_clicked()
{
    QStringList wordList;
    wordList=csvReader();
    setupPlot3(wordList);
}

void MainWindow::on_standing2_clicked()
{
    QStringList wordList;
    wordList=csvReader();
    setupPlot4(wordList);
}

void MainWindow::on_step2_clicked()
{
    QStringList wordList;
    wordList=csvReader();
    setupPlot5(wordList);
}

void MainWindow::on_stairs2_clicked()
{
    QStringList wordList;
    wordList=csvReader();
    setupPlot6(wordList);
}
