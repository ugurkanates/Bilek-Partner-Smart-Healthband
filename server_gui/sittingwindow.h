#ifndef SITTINGWINDOW_H
#define SITTINGWINDOW_H

#include <QDialog>
namespace Ui {
class sittingwindow;
}

class sittingwindow : public QDialog
{
    Q_OBJECT

public:
    explicit sittingwindow(QWidget *parent = 0);
    ~sittingwindow();
    void setupPlot(QStringList wordList);
    QStringList csvReader();

private slots:
    void on_main_clicked();

    void on_exit_clicked();

private:
    Ui::sittingwindow *ui;
    int mon;
    int the;
    int wed;
    int thu;
    int fri;
    int sat;
    int sun;
};

#endif // SITTINGWINDOW_H
