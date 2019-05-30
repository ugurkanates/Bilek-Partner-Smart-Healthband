#ifndef WALKING2WINDOW_H
#define WALKING2WINDOW_H

#include <QDialog>
#include "qcustomplot.h"
#include <QtWidgets/QApplication>
#include <QtWidgets/QMainWindow>
#include <QtCharts/QChartView>
#include <QtCharts/QPieSeries>
#include <QtCharts/QPieSlice>
namespace Ui {
class walking2window;
}

class walking2window : public QDialog
{
    Q_OBJECT

public:
    explicit walking2window(QWidget *parent = nullptr);
    ~walking2window();
    void setupPlot(QStringList wordList);
    QStringList csvReader();

private:
    Ui::walking2window *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;
};

#endif // WALKING2WINDOW_H
