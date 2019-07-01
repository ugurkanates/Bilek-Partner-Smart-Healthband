#ifndef  WALKINGWINDOW_H
#define WALKINGWINDOW_H

#include <QDialog>
#include <mainwindow.h>
#include "qcustomplot.h"
namespace Ui {
class walkingwindow;
}

class walkingwindow : public QDialog
{
    Q_OBJECT

public:
    explicit walkingwindow(QWidget *parent = 0);
    ~walkingwindow();
    void setupPlot(QStringList wordList);
    QStringList csvReader();


private slots:
    void on_exit_clicked();

    void on_main_clicked();



private:
    Ui::walkingwindow *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;
};

#endif // WALKINGWINDOW_H
