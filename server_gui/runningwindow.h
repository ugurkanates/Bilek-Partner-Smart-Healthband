#ifndef RUNNINGWINDOW_H
#define RUNNINGWINDOW_H

#include <QDialog>
#include <qcustomplot.h>
namespace Ui {
class runningwindow;
}

class runningwindow : public QDialog
{
    Q_OBJECT

public:
    explicit runningwindow(QWidget *parent = 0);
    ~runningwindow();
    void setupPlot(QStringList wordList);
    QStringList csvReader();

private slots:
    void on_main_clicked();

    void on_exit_clicked();

private:
    Ui::runningwindow *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;

};

#endif // RUNNINGWINDOW_H
