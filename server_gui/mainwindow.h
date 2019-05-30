#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <walkingwindow.h>
#include <runningwindow.h>
#include "qcustomplot.h"
#include <QtWidgets/QApplication>
#include <QtWidgets/QMainWindow>
#include <QtCharts/QChartView>
#include <QtCharts/QPieSeries>
#include <QtCharts/QPieSlice>
namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();
    void setupPlot1(QStringList wordList);
    void setupPlot2(QStringList wordList);
    void setupPlot3(QStringList wordList);
    void setupPlot4(QStringList wordList);
    void setupPlot5(QStringList wordList);
    void setupPlot6(QStringList wordList);
    QStringList csvReader();

private slots:
    void on_exit_clicked();

    void on_walk_clicked();

    void on_run_clicked();

    void on_stairs_clicked();

    void on_sitting_clicked();

    void on_stand_clicked();

    void on_stand_2_clicked();

    void on_step_clicked();

    void on_month_clicked();

    void on_walk2_clicked();

    void on_run2_clicked();

    void on_sitting2_clicked();

    void on_standing2_clicked();

    void on_step2_clicked();

    void on_stairs2_clicked();

private:
    Ui::MainWindow *ui;

};

#endif // MAINWINDOW_H


