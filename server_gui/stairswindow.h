#ifndef STAIRSWINDOW_H
#define STAIRSWINDOW_H

#include <QDialog>

namespace Ui {
class stairswindow;
}

class stairswindow : public QDialog
{
    Q_OBJECT

public:
    explicit stairswindow(QWidget *parent = 0);
    ~stairswindow();
     void setupPlot(QStringList wordList);
     QStringList csvReader();

private slots:
    void on_main_clicked();

    void on_exit_clicked();
private:
    Ui::stairswindow *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;
};

#endif // STAIRSWINDOW_H
