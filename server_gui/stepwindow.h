#ifndef STEPWINDOW_H
#define STEPWINDOW_H

#include <QDialog>
#include "qcustomplot.h"
namespace Ui {
class stepwindow;
}

class stepwindow : public QDialog
{
    Q_OBJECT

public:
    explicit stepwindow(QWidget *parent = 0);
    ~stepwindow();
    void setupPlot(QStringList wordList);
    QStringList csvReader();

private slots:
    void on_exit_clicked();

    void on_main_clicked();

private:
    Ui::stepwindow *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;
};

#endif // STEPWINDOW_H
