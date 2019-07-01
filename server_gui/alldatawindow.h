#ifndef ALLDATAWINDOW_H
#define ALLDATAWINDOW_H

#include <QDialog>
#include <qcustomplot.h>
namespace Ui {
class alldatawindow;
}

class alldatawindow : public QDialog
{
    Q_OBJECT

public:
    explicit alldatawindow(QWidget *parent = 0);
    ~alldatawindow();
    void setupChart(QStringList wordList);
    QStringList csvReader();

private slots:
    void on_main_clicked();

    void on_exit_clicked();

private:
    Ui::alldatawindow *ui;

};

#endif // ALLDATAWINDOW_H
