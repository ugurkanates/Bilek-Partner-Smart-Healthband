#ifndef STANDINGWINDOW_H
#define STANDINGWINDOW_H

#include <QDialog>
#include "qcustomplot.h"
namespace Ui {
class standingwindow;
}

class standingwindow : public QDialog
{
    Q_OBJECT

public:
    explicit standingwindow(QWidget *parent = 0);
    ~standingwindow();
    void setupPlot(QStringList wordList);
    QStringList csvReader();

private slots:
    void on_exit_clicked();

    void on_main_clicked();

private:
    Ui::standingwindow *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;
};

#endif // STANDINGWINDOW_H
