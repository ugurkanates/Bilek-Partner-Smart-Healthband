#ifndef MONTHWINDOW_H
#define MONTHWINDOW_H

#include <QDialog>
#include <qcustomplot.h>
namespace Ui {
class monthwindow;
}

class monthwindow : public QDialog
{
    Q_OBJECT

public:
    explicit monthwindow(QWidget *parent = 0);
    ~monthwindow();
    void setupPlot(QStringList wordList);
    QStringList csvReader();

private slots:
    void on_main_clicked();

    void on_exit_clicked();

private:
    Ui::monthwindow *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;
};

#endif // MONTHWINDOW_H
